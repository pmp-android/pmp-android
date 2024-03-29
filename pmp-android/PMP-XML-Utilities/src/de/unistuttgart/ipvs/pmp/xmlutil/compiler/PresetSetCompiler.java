/*
 * Copyright 2012 pmp-android development team
 * Project: PMP-XML-UTILITIES
 * Project-Site: http://code.google.com/p/pmp-android/
 *
 * ---------------------------------------------------------------------
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.unistuttgart.ipvs.pmp.xmlutil.compiler;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.unistuttgart.ipvs.pmp.xmlutil.common.XMLConstants;
import de.unistuttgart.ipvs.pmp.xmlutil.compiler.common.XMLAttribute;
import de.unistuttgart.ipvs.pmp.xmlutil.compiler.common.XMLCompiler;
import de.unistuttgart.ipvs.pmp.xmlutil.compiler.common.XMLNode;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.IPreset;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.IPresetAssignedApp;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.IPresetAssignedPrivacySetting;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.IPresetPSContext;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.IPresetSet;

/**
 * 
 * @author Marcus Vetter
 * 
 */
public class PresetSetCompiler extends BasicISCompiler {
    
    /**
     * Compile an {@link IPresetSet} and return the xml input stream
     * 
     * @param presetSet
     *            {@link IPresetSet} to compile
     * @return xml input stream
     */
    public InputStream compile(IPresetSet presetSet) {
        // Instantiate the root node
        XMLNode presetSetNode = new XMLNode(XMLConstants.PRESET_SET);
        
        // Add preset childs
        for (XMLNode presetNode : createPresetNodes(presetSet)) {
            presetSetNode.addChild(presetNode);
        }
        
        // Compile and return the result
        InputStream result = null;
        try {
            result = XMLCompiler.compileStream(presetSetNode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    
    /**
     * Create the XMLNode list of the Preset-Nodes
     * 
     * @param presetSet
     *            the PresetSet
     * @return created nodelist
     */
    private List<XMLNode> createPresetNodes(IPresetSet presetSet) {
        List<XMLNode> nodes = new ArrayList<XMLNode>();
        
        // Iterate through all presets
        for (IPreset preset : presetSet.getPresets()) {
            // Instantiate the XML-Node
            XMLNode presetNode = new XMLNode(XMLConstants.PRESET);
            
            // Add the attributes
            presetNode.addAttribute(new XMLAttribute(XMLConstants.IDENTIFIER_ATTR, preset.getIdentifier()));
            presetNode.addAttribute(new XMLAttribute(XMLConstants.CREATOR_ATTR, preset.getCreator()));
            presetNode.addAttribute(new XMLAttribute(XMLConstants.NAME_ATTR, preset.getName()));
            presetNode.addAttribute(new XMLAttribute(XMLConstants.DESCRIPTION_ATTR, preset.getDescription()));
            
            // Add the assigned apps
            XMLNode assignedApps = new XMLNode(XMLConstants.ASSIGNED_APPS);
            for (IPresetAssignedApp app : preset.getAssignedApps()) {
                XMLNode appNode = new XMLNode(XMLConstants.APP);
                appNode.addAttribute(new XMLAttribute(XMLConstants.IDENTIFIER_ATTR, app.getIdentifier()));
                assignedApps.addChild(appNode);
            }
            
            // Add the assigned privacy settings
            XMLNode assignedPrivacySettings = new XMLNode(XMLConstants.ASSIGNED_PRIVACY_SETTINGS);
            for (XMLNode psNode : createPresetAssignedPSs(preset)) {
                assignedPrivacySettings.addChild(psNode);
            }
            
            // Now finally add the childs to the presetNode and add this one to the result list
            presetNode.addChild(assignedApps);
            presetNode.addChild(assignedPrivacySettings);
            nodes.add(presetNode);
        }
        
        return nodes;
        
    }
    
    
    /**
     * Create the XMLNode list of the assigned privacy settings
     * 
     * @param preset
     *            the Preset
     * @return created nodelist
     */
    private List<XMLNode> createPresetAssignedPSs(IPreset preset) {
        List<XMLNode> nodes = new ArrayList<XMLNode>();
        
        // Iterate through all privacy settings
        for (IPresetAssignedPrivacySetting ps : preset.getAssignedPrivacySettings()) {
            // Instantiate the XML-Node
            XMLNode privacySettingNode = new XMLNode(XMLConstants.PRIVACY_SETTING);
            
            // Transform the rg revision
            String rgRevision = ps.getRgRevision();
            try {
                Date date = new Date(Long.valueOf(rgRevision));
                rgRevision = XMLConstants.REVISION_DATE_FORMAT.format(date);
            } catch (NumberFormatException nfe) {
                // Ignore it. Something went wrong and the min revision was not an integer.
            }
            
            // Add the attributes
            privacySettingNode.addAttribute(new XMLAttribute(XMLConstants.RG_IDENTIFIER_ATTR, ps.getRgIdentifier()));
            privacySettingNode.addAttribute(new XMLAttribute(XMLConstants.RG_REVISION_ATTR, rgRevision));
            privacySettingNode.addAttribute(new XMLAttribute(XMLConstants.PS_IDENTIFIER_ATTR, ps.getPsIdentifier()));
            
            // Add the value
            if (ps.getValue() != null) {
                XMLNode valueNode = new XMLNode(XMLConstants.VALUE);
                valueNode.setCDATAContent(ps.getValue());
                privacySettingNode.addChild(valueNode);
            }
            
            // Add the contexts
            for (IPresetPSContext context : ps.getContexts()) {
                XMLNode contextNode = new XMLNode(XMLConstants.CONTEXT);
                contextNode.addAttribute(new XMLAttribute(XMLConstants.CONTEXT_TYPE_ATTR, context.getType()));
                contextNode.addAttribute(new XMLAttribute(XMLConstants.CONTEXT_CONDITION_ATTR, context.getCondition()));
                
                // Add the empty condition attribute
                if (context.isEmptyCondition()) {
                    contextNode.addAttribute(new XMLAttribute(XMLConstants.CONTEXT_EMPTY_CONDITION_ATTR, "true"));
                }
                
                /*
                 *  Create the overrideValue-Node
                 */
                XMLNode overrideValueNode = new XMLNode(XMLConstants.CONTEXT_OVERRIDE_VALUE);
                overrideValueNode.setCDATAContent(context.getOverrideValue());
                
                // Add the empty override value attribute
                if (context.isEmptyOverrideValue()) {
                    overrideValueNode.addAttribute(new XMLAttribute(XMLConstants.CONTEXT_EMPTY_OVERRIDE_VALUE_ATTR,
                            "true"));
                }
                
                // Add the override value child to the context
                contextNode.addChild(overrideValueNode);
                
                // Add it to his parent
                privacySettingNode.addChild(contextNode);
            }
            
            // Now finally add it to the result list
            nodes.add(privacySettingNode);
        }
        
        return nodes;
    }
    
}
