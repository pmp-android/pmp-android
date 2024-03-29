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
package de.unistuttgart.ipvs.pmp.xmlutil.parser;

import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.unistuttgart.ipvs.pmp.xmlutil.common.XMLConstants;
import de.unistuttgart.ipvs.pmp.xmlutil.parser.common.ParsedNode;
import de.unistuttgart.ipvs.pmp.xmlutil.parser.common.ParserException;
import de.unistuttgart.ipvs.pmp.xmlutil.parser.common.ParserException.Type;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.IPreset;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.IPresetAssignedPrivacySetting;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.IPresetSet;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.Preset;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.PresetAssignedApp;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.PresetAssignedPrivacySetting;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.PresetPSContext;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.PresetSet;

/**
 * This class parses a given xml file and creates a {@link IPresetSet}
 * 
 * @author Marcus Vetter
 * 
 */
public class PresetSetParser extends AbstractParser {
    
    /**
     * The {@link IPresetSet}
     */
    private IPresetSet presetSet;
    
    
    /**
     * This method parses a given xml (by the xml url) and returns a created {@link IPresetSet}
     * 
     * @return created {@link IPresetSet}
     */
    public IPresetSet parse(InputStream xmlStream) {
        // Initialize
        initParser(xmlStream);
        
        // Create new AIS
        this.presetSet = new PresetSet();
        
        // Check, if the root node is named correctly
        if (!this.doc.getDocumentElement().getNodeName().equals(XMLConstants.PRESET_SET)) {
            throw new ParserException(Type.BAD_ROOT_NODE_NAME, "The name of the root node is invalid.");
        }
        
        // The main nodes called "preset", parse them
        NodeList presetNodes = this.doc.getElementsByTagName(XMLConstants.PRESET);
        for (int itr = 0; itr < presetNodes.getLength(); itr++) {
            parsePreset((Element) presetNodes.item(itr));
        }
        
        // Check, if there are a maximum of maxValid child nodes of the root node
        checkMaxNumberOfNodes(presetNodes.getLength(), (Element) this.doc.getElementsByTagName(XMLConstants.PRESET_SET)
                .item(0));
        
        return this.presetSet;
    }
    
    
    /**
     * Parse one {@link IPreset}
     * 
     * @param presetElement
     */
    private void parsePreset(Element presetElement) {
        
        // Get the attributes
        String identifier = presetElement.getAttribute(XMLConstants.IDENTIFIER_ATTR);
        String creator = presetElement.getAttribute(XMLConstants.CREATOR_ATTR);
        String name = presetElement.getAttribute(XMLConstants.NAME_ATTR);
        String description = presetElement.getAttribute(XMLConstants.DESCRIPTION_ATTR);
        
        // Instantiate new Preset
        Preset preset = new Preset(identifier, creator, name, description);
        
        // Get the assignedApps-NodeList and parse the elements
        NodeList assignedAppsNodeList = presetElement.getElementsByTagName(XMLConstants.ASSIGNED_APPS);
        if (assignedAppsNodeList.getLength() == 1) {
            // Parse the assignedAppsElement
            parseAssignedApps((Element) assignedAppsNodeList.item(0), preset);
        } else if (assignedAppsNodeList.getLength() > 1) {
            throw new ParserException(Type.NODE_OCCURRED_TOO_OFTEN, "The node " + XMLConstants.ASSIGNED_APPS
                    + " occurred too often!");
        }
        
        // Get the assignedPrivacySettings-NodeList and parse the elements
        NodeList assignedPSsNodeList = presetElement.getElementsByTagName(XMLConstants.ASSIGNED_PRIVACY_SETTINGS);
        if (assignedPSsNodeList.getLength() == 1) {
            // Parse the assignedPrivacySettingsElement
            parseAssignedPSs((Element) assignedPSsNodeList.item(0), preset);
        } else if (assignedPSsNodeList.getLength() > 1) {
            throw new ParserException(Type.NODE_OCCURRED_TOO_OFTEN, "The node "
                    + XMLConstants.ASSIGNED_PRIVACY_SETTINGS + " occurred too often!");
        }
        
        // Add preset to Preset Set
        this.presetSet.addPreset(preset);
    }
    
    
    /**
     * Parse assigned apps
     * 
     * @param assignedAppElement
     *            the element of the assigned apps
     * @param preset
     *            the preset
     */
    private void parseAssignedApps(Element assignedAppElement, IPreset preset) {
        // Get the list of all assigned apps
        List<ParsedNode> assignedAppsNodes = parseNodes(assignedAppElement, XMLConstants.APP,
                XMLConstants.IDENTIFIER_ATTR);
        
        // Add all assigned apps to the preset
        for (ParsedNode appNode : assignedAppsNodes) {
            preset.addAssignedApp(new PresetAssignedApp(appNode.getAttribute(XMLConstants.IDENTIFIER_ATTR)));
        }
    }
    
    
    /**
     * Parse {@link IPresetAssignedPrivacySetting}s
     * 
     * @param assignedPSsElement
     *            the element of the {@link IPresetAssignedPrivacySetting}s
     * @param preset
     *            the {@link IPreset}
     */
    private void parseAssignedPSs(Element assignedPSsElement, IPreset preset) {
        // Get the list of all assigned privacy settings
        List<ParsedNode> assignedPSNodes = parseNodes(assignedPSsElement, XMLConstants.PRIVACY_SETTING,
                XMLConstants.RG_IDENTIFIER_ATTR, XMLConstants.RG_REVISION_ATTR, XMLConstants.PS_IDENTIFIER_ATTR);
        
        // Add all assigned privacy settings to the preset and parse the contexts
        int psItr = 0;
        for (ParsedNode psNode : assignedPSNodes) {
            // The PS element
            Element psElement = (Element) assignedPSsElement.getElementsByTagName(XMLConstants.PRIVACY_SETTING).item(
                    psItr);
            
            // Get the attributes
            String rgIdentifier = psNode.getAttribute(XMLConstants.RG_IDENTIFIER_ATTR);
            String rgRevision = psNode.getAttribute(XMLConstants.RG_REVISION_ATTR);
            String psIdentifier = psNode.getAttribute(XMLConstants.PS_IDENTIFIER_ATTR);
            
            // Transforme the rgRevision
            try {
                Date date = XMLConstants.REVISION_DATE_FORMAT.parse(rgRevision);
                rgRevision = String.valueOf(date.getTime());
            } catch (ParseException e) {
                // The parse exception can be ignored.
                // If the time was in another format, the validator will find it.
            }
            
            // Instantiate and add the PresetAssignedPrivacySetting
            PresetAssignedPrivacySetting assignedPS = new PresetAssignedPrivacySetting(rgIdentifier, rgRevision,
                    psIdentifier, "");
            preset.addAssignedPrivacySetting(assignedPS);
            
            // Get the value
            int maxValid = 0;
            List<ParsedNode> valueList = parseNodes(psElement, XMLConstants.VALUE, XMLConstants.EMPTY_VALUE_ATTR);
            
            if (valueList.size() == 1) {
                // The valueNode
                ParsedNode valueNode = valueList.get(0);
                
                // Set the value
                assignedPS.setValue(valueNode.getValue());
                
                // Add the empty value attribute
                String emptyValueAttr = valueNode.getAttribute(XMLConstants.EMPTY_VALUE_ATTR);
                
                if (emptyValueAttr.toLowerCase().equals("true")) {
                    assignedPS.setEmptyValue(true);
                    // Set the value to "", if it is null
                    if (assignedPS.getValue() == null) {
                        assignedPS.setValue("");
                    }
                } else if (!emptyValueAttr.toLowerCase().equals("false") && !emptyValueAttr.equals("")) {
                    throw new ParserException(Type.EMPTY_CONDITION_BOOLEAN_EXCEPTION,
                            "The value of the attribute \"emptyValue\" of a assigned privacy setting is not a boolean.");
                }
                
                maxValid++;
            } else if (valueList.size() > 1) {
                throw new ParserException(Type.NODE_OCCURRED_TOO_OFTEN, "The node " + XMLConstants.VALUE
                        + " occurred too often!");
            }
            
            // Parse the contexts
            int numberOfParsedContexts = parseContexts(psElement, assignedPS);
            
            // Check, if there are more nodes then expected
            maxValid += numberOfParsedContexts;
            checkMaxNumberOfNodes(maxValid, psElement);
            
            psItr++;
        }
    }
    
    
    /**
     * Parse a context
     * 
     * @param psElement
     *            the element of the {@link IPresetAssignedPrivacySetting}
     * @param assignedPS
     *            the assigned {@link IPresetAssignedPrivacySetting} object
     */
    private int parseContexts(Element psElement, IPresetAssignedPrivacySetting assignedPS) {
        // Get the list of all assigned privacy settings
        List<ParsedNode> contextLists = parseNodes(psElement, XMLConstants.CONTEXT, XMLConstants.CONTEXT_TYPE_ATTR,
                XMLConstants.CONTEXT_CONDITION_ATTR, XMLConstants.CONTEXT_EMPTY_CONDITION_ATTR);
        
        // Add all contexts
        int contextItr = 0;
        for (ParsedNode contextNode : contextLists) {
            // The context element
            Element contextElement = (Element) psElement.getElementsByTagName(XMLConstants.CONTEXT).item(contextItr);
            
            // Get the attributes
            String type = contextNode.getAttribute(XMLConstants.CONTEXT_TYPE_ATTR);
            String condition = contextNode.getAttribute(XMLConstants.CONTEXT_CONDITION_ATTR);
            
            // Instantiate and add the PresetPSContext
            PresetPSContext context = new PresetPSContext(type, condition, "");
            assignedPS.addContext(context);
            
            // Add the empty condition attribute
            String emptyConditionAttr = contextNode.getAttribute(XMLConstants.CONTEXT_EMPTY_CONDITION_ATTR);
            if (emptyConditionAttr.toLowerCase().equals("true")) {
                context.setEmptyCondition(true);
                // Set the condition to "", if it is null
                if (context.getCondition() == null) {
                    context.setCondition("");
                }
            } else if (!emptyConditionAttr.toLowerCase().equals("false") && !emptyConditionAttr.equals("")) {
                throw new ParserException(Type.EMPTY_CONDITION_BOOLEAN_EXCEPTION,
                        "The value of the attribute \"emptyCondition\" of a context is not a boolean.");
            }
            
            // Get the override value
            List<ParsedNode> overrideValueList = parseNodes(contextElement, XMLConstants.CONTEXT_OVERRIDE_VALUE,
                    XMLConstants.CONTEXT_EMPTY_OVERRIDE_VALUE_ATTR);
            int maxValid = 0;
            if (overrideValueList.size() == 1) {
                // Set the override value
                context.setOverrideValue(overrideValueList.get(0).getValue());
                maxValid++;
                
                // Add the empty override value attribute
                String emptyOverrideValueAttr = overrideValueList.get(0).getAttribute(
                        XMLConstants.CONTEXT_EMPTY_OVERRIDE_VALUE_ATTR);
                if (emptyOverrideValueAttr.toLowerCase().equals("true")) {
                    context.setEmptyOverrideValue(true);
                    // Set the override value to "", if it is null
                    if (context.getOverrideValue() == null) {
                        context.setOverrideValue("");
                    }
                    
                } else if (!emptyOverrideValueAttr.toLowerCase().equals("false") && !emptyOverrideValueAttr.equals("")) {
                    throw new ParserException(Type.EMPTY_OVERRIDE_VALUE_BOOLEAN_EXCEPTION,
                            "The value of the attribute \"emptyOverrideValue\" of a override value is not a boolean.");
                }
            } else if (overrideValueList.size() > 1) {
                throw new ParserException(Type.NODE_OCCURRED_TOO_OFTEN, "The node "
                        + XMLConstants.CONTEXT_OVERRIDE_VALUE + " occurred too often!");
            }
            checkMaxNumberOfNodes(maxValid, contextElement);
            
            contextItr++;
        }
        
        return contextItr;
    }
    
}
