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

import de.unistuttgart.ipvs.pmp.xmlutil.ais.AISRequiredResourceGroup;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.AISServiceFeature;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.IAIS;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.IAISRequiredPrivacySetting;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.IAISRequiredResourceGroup;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.IAISServiceFeature;
import de.unistuttgart.ipvs.pmp.xmlutil.common.XMLConstants;
import de.unistuttgart.ipvs.pmp.xmlutil.compiler.common.XMLAttribute;
import de.unistuttgart.ipvs.pmp.xmlutil.compiler.common.XMLCompiler;
import de.unistuttgart.ipvs.pmp.xmlutil.compiler.common.XMLNode;

/**
 * 
 * @author Marcus Vetter
 * 
 */
public class AISCompiler extends BasicISCompiler {
    
    /**
     * Compile an {@link IAIS} and return the xml input stream
     * 
     * @param ais
     *            {@link IAIS} to compile
     * @return xml input stream
     */
    public InputStream compile(IAIS ais) {
        
        // Instantiate the root and two main child nodes
        XMLNode aisNode = new XMLNode(XMLConstants.AIS);
        XMLNode aiNode = new XMLNode(XMLConstants.AI);
        XMLNode sfsNode = new XMLNode(XMLConstants.SFS);
        
        // Add appInformation childs
        for (XMLNode nameDescrNode : createNameDescriptionNodes(ais)) {
            aiNode.addChild(nameDescrNode);
        }
        
        // Add serviceFeature childs
        for (XMLNode sfNode : createSFNodes(ais)) {
            sfsNode.addChild(sfNode);
        }
        
        // Add appInformation and serviceFeature to AIS
        aisNode.addChild(aiNode);
        aisNode.addChild(sfsNode);
        
        // Compile and return the result
        InputStream result = null;
        try {
            result = XMLCompiler.compileStream(aisNode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    
    /**
     * Create the {@link AISServiceFeature} node list
     * 
     * @param ais
     *            {@link IAIS}
     * @return {@link AISServiceFeature} node list
     */
    private List<XMLNode> createSFNodes(IAIS ais) {
        List<XMLNode> nodeList = new ArrayList<XMLNode>();
        
        for (IAISServiceFeature sf : ais.getServiceFeatures()) {
            XMLNode sfNode = new XMLNode(XMLConstants.SF);
            
            // Add identifier
            sfNode.addAttribute(new XMLAttribute(XMLConstants.IDENTIFIER_ATTR, sf.getIdentifier()));
            
            // Add the name and description nodes
            for (XMLNode nameDescrNode : createNameDescriptionNodes(sf)) {
                sfNode.addChild(nameDescrNode);
            }
            
            // Add required resource group nodes
            for (XMLNode rrgNode : createRRGNodes(sf)) {
                sfNode.addChild(rrgNode);
            }
            
            // Add SF to nodeList
            nodeList.add(sfNode);
        }
        
        return nodeList;
        
    }
    
    
    /**
     * Create the {@link AISRequiredResourceGroup} node list
     * 
     * @param sf
     *            {@link AISServiceFeature} of the {@link AISRequiredResourceGroup}s
     * @return node list of {@link AISRequiredResourceGroup}s
     */
    private List<XMLNode> createRRGNodes(IAISServiceFeature sf) {
        List<XMLNode> nodeList = new ArrayList<XMLNode>();
        
        for (IAISRequiredResourceGroup rrg : sf.getRequiredResourceGroups()) {
            XMLNode rrgNode = new XMLNode(XMLConstants.RRG);
            
            // Add identifier
            rrgNode.addAttribute(new XMLAttribute(XMLConstants.IDENTIFIER_ATTR, rrg.getIdentifier()));
            
            // Add minRevision and use the simple date formatter
            String minRevision = rrg.getMinRevision();
            
            try {
                Date date = new Date(Long.valueOf(minRevision));
                minRevision = XMLConstants.REVISION_DATE_FORMAT.format(date);
                // Bugfix for Android 2.1.1: Timezone MEZ is not a valid timezone for this android version.
                // So replace MEZ with GMT+01:00.
                minRevision = minRevision.replace("MEZ", "GMT+01:00");
                minRevision = minRevision.replace("MESZ", "GMT+02:00");
                
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
                // Ignore it. Something went wrong and the min revision was not an integer.
            }
            rrgNode.addAttribute(new XMLAttribute(XMLConstants.MINREVISION_ATTR, minRevision));
            
            // Add required privacy settings
            for (IAISRequiredPrivacySetting rps : rrg.getRequiredPrivacySettings()) {
                XMLNode rpsNode = new XMLNode(XMLConstants.RPS);
                rpsNode.addAttribute(new XMLAttribute(XMLConstants.IDENTIFIER_ATTR, rps.getIdentifier()));
                if (rps.getValue() != null) {
                    rpsNode.setCDATAContent(rps.getValue());
                }
                // Add the empty value attribute
                if (rps.isEmptyValue()) {
                    rpsNode.addAttribute(new XMLAttribute(XMLConstants.EMPTY_VALUE_ATTR, "true"));
                }
                
                // add the child
                rrgNode.addChild(rpsNode);
            }
            
            // Add the rrgNode to the nodeList
            nodeList.add(rrgNode);
        }
        return nodeList;
    }
}
