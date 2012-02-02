/*
 * Copyright 2011 pmp-android development team
 * Project: PMP
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
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.unistuttgart.ipvs.pmp.xmlutil.ais.AIS;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.PrivacySetting;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.RequiredResourceGroup;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.ServiceFeature;
import de.unistuttgart.ipvs.pmp.xmlutil.common.exception.ParserException;
import de.unistuttgart.ipvs.pmp.xmlutil.common.exception.ParserException.Type;

/**
 * This XML Parser parses a given xml (for an app) and creates a app information
 * set
 * 
 * @author Marcus Vetter
 * 
 */
public class AISParser extends AbstractParser {

	/**
	 * AppInformationSet
	 */
	private AIS ais = new AIS();

	/**
	 * This method parses a given xml (by the xml url) and returns a created app
	 * information set
	 * 
	 * @return created app information set
	 */
	public AIS parse(InputStream xmlStream) {
		// Initialize
		initParser(xmlStream);

		// Check, if the root node is named correctly
		if (!this.doc.getDocumentElement().getNodeName()
				.equals("appInformationSet")) {
			throw new ParserException(Type.BAD_ROOT_NODE_NAME,
					"The name of the root node is invalid.");
		}

		// The main nodes "appInformation" and "serviceFeatures" are
		// required once.
		NodeList appInformation = this.doc
				.getElementsByTagName("appInformation");
		NodeList serviceFeatures = this.doc
				.getElementsByTagName("serviceFeatures");

		// Check, if there is exactly one appInformation and one service
		// features node
		if (appInformation.getLength() < 1) {
			throw new ParserException(Type.NODE_MISSING,
					"The node appInformation is missing!");
		} else if (appInformation.getLength() > 1) {
			throw new ParserException(Type.NODE_OCCURRED_TOO_OFTEN,
					"The node appInformation occurred too often!");
		}
		if (serviceFeatures.getLength() < 1) {
			throw new ParserException(Type.NODE_MISSING,
					"The node serviceFeatures is missing!");
		} else if (serviceFeatures.getLength() > 1) {
			throw new ParserException(Type.NODE_OCCURRED_TOO_OFTEN,
					"The node serviceFeatures occurred too often!");
		}

		// Check, if there are only 2 child nodes of appInformationSet
		checkNumberOfNodes(2,
				(Element) this.doc.getElementsByTagName("appInformationSet")
						.item(0));

		// Parse the app information nodes
		parseNameDescriptionNodes((Element) appInformation.item(0), this.ais);
		parseServiceFeaturesNode((Element) serviceFeatures.item(0));

		return this.ais;
	}

	/**
	 * This method parses the service features element
	 * 
	 * @param serviceFeaturesElement
	 *            starting with this root element
	 */
	private void parseServiceFeaturesNode(Element serviceFeaturesElement) {
		NodeList serviceFeaturesNodeList = serviceFeaturesElement
				.getElementsByTagName("serviceFeature");

		// Parse the defined Service Features
		for (int itr = 0; itr < serviceFeaturesNodeList.getLength(); itr++) {
			Element serviceFeatureElement = (Element) serviceFeaturesNodeList
					.item(itr);

			// Get the identifier
			String identifier = serviceFeatureElement
					.getAttribute("identifier");

			// Instantiate the service feature and add it to the AIS
			ServiceFeature sf = new ServiceFeature(identifier);
			this.ais.addServiceFeature(sf);

			// Parse name and descriptions
			parseNameDescriptionNodes(serviceFeatureElement, sf);

			// Get the node list of the required resource groups
			NodeList rrgNodeList = serviceFeatureElement
					.getElementsByTagName("requiredResourceGroup");

			// Parse all required resource groups
			for (int rrgItr = 0; rrgItr < rrgNodeList.getLength(); rrgItr++) {
				Element rrgElement = (Element) rrgNodeList.item(rrgItr);

				// Instantiate the required resource group and add the
				// identifier
				RequiredResourceGroup rrg = new RequiredResourceGroup(
						rrgElement.getAttribute("identifier"));

				// Add the required resource group to the service feature
				sf.addRequiredResourceGroup(rrg);

				// Parse the required resource group
				List<String[]> privacySettingList = parseNodes(rrgElement,
						"privacySetting", "identifier");

				// Add to the app information set (building objects)
				for (String[] privacySettingArray : privacySettingList) {
					// Add identifier and value
					rrg.addPrivacySetting(new PrivacySetting(
							privacySettingArray[1], privacySettingArray[0]));
				}
			}
		}
	}

}