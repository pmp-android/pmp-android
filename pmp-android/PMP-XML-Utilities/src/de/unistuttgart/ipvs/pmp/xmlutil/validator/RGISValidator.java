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
package de.unistuttgart.ipvs.pmp.xmlutil.validator;

import java.util.ArrayList;
import java.util.List;

import de.unistuttgart.ipvs.pmp.xmlutil.common.IIdentifierIS;
import de.unistuttgart.ipvs.pmp.xmlutil.rgis.IRGIS;
import de.unistuttgart.ipvs.pmp.xmlutil.rgis.IRGISPrivacySetting;
import de.unistuttgart.ipvs.pmp.xmlutil.validator.issue.IIssue;
import de.unistuttgart.ipvs.pmp.xmlutil.validator.issue.Issue;
import de.unistuttgart.ipvs.pmp.xmlutil.validator.issue.IssueType;

/**
 * Validator for {@link IRGIS}
 * 
 * @author Marcus Vetter
 * 
 */
public class RGISValidator extends AbstractValidator {
    
    /**
     * Validate the whole {@link IRGIS}
     * 
     * @param rgis
     *            the {@link IRGIS}
     * @param attachData
     *            set this flag true, if the given data should be attached with the {@link IIssue}s
     * @return List with {@link IIssue}s as result of the validation
     */
    public List<IIssue> validateRGIS(IRGIS rgis, boolean attachData) {
        List<IIssue> issueList = new ArrayList<IIssue>();
        
        // Clear the attached issues, if the issues should be attached
        if (attachData) {
            rgis.clearIssues();
        }
        
        /*
         * Validate the app information and the service features 
         */
        issueList.addAll(validateRGInformation(rgis, attachData));
        issueList.addAll(validatePrivacySettings(rgis, attachData));
        
        return issueList;
    }
    
    
    /**
     * Validate the resource group information
     * 
     * @param rgis
     *            the {@link IRGIS}
     * @param attachData
     *            set this flag true, if the given data should be attached with the {@link IIssue}s
     * @return List with {@link IIssue}s as result of the validation
     */
    public List<IIssue> validateRGInformation(IRGIS rgis, boolean attachData) {
        List<IIssue> issueList = new ArrayList<IIssue>();
        
        // Clear the attached issues, if the issues should be attached
        if (attachData) {
            rgis.clearRGInformationIssues();
        }
        
        /*
         * Validate names and descriptions
         */
        issueList.addAll(validateNames(rgis));
        issueList.addAll(validateDescriptions(rgis));
        
        /*
         * Validate, if the identifier is set
         */
        if (!checkValueSet(rgis.getIdentifier())) {
            issueList.add(new Issue(IssueType.IDENTIFIER_MISSING, rgis));
        }
        
        /*
         * Validate, if the icon is set
         */
        if (!checkValueSet(rgis.getIconLocation())) {
            issueList.add(new Issue(IssueType.ICON_MISSING, rgis));
        }
        /*
         * Validate, if the class name is set
         */
        if (!checkValueSet(rgis.getClassName())) {
            issueList.add(new Issue(IssueType.CLASSNAME_MISSING, rgis));
        }
        
        // Attach data
        attachData(issueList, attachData);
        
        return issueList;
    }
    
    
    /**
     * Validate all {@link IRGISPrivacySetting}s of the given {@link IRGIS}
     * 
     * @param rgis
     *            the {@link IRGIS}
     * @param attachData
     *            set this flag true, if the given data should be attached with the {@link IIssue}s
     * @return List with {@link IIssue}s as result of the validation
     */
    public List<IIssue> validatePrivacySettings(IRGIS rgis, boolean attachData) {
        List<IIssue> issueList = new ArrayList<IIssue>();
        
        // Clear the attached issues, if the issues should be attached
        if (attachData) {
            rgis.clearPSIssues();
        }
        
        /*
         * Validate the occurrences of identifier of privacy settings
         */
        // Convert
        List<IIdentifierIS> superPSList = new ArrayList<IIdentifierIS>();
        for (IRGISPrivacySetting ps : rgis.getPrivacySettings()) {
            superPSList.add(ps);
        }
        // Validate
        for (String identifierFail : validateOccurrenceOfIdentifier(superPSList)) {
            Issue issue = new Issue(IssueType.PS_IDENTIFIER_OCCURRED_TOO_OFTEN, rgis);
            issue.addParameter(identifierFail);
            issueList.add(issue);
        }
        
        /*
         * Validate, if any privacy setting exists
         */
        if (rgis.getPrivacySettings().size() == 0) {
            Issue issue = new Issue(IssueType.NO_PS_EXISTS, rgis);
            issueList.add(issue);
        }
        
        // Attach data
        attachData(issueList, attachData);
        
        /*
         * Validate all privacy settings
         */
        for (IRGISPrivacySetting ps : rgis.getPrivacySettings()) {
            issueList.addAll(validatePrivacySetting(ps, attachData));
        }
        
        return issueList;
    }
    
    
    /**
     * Validate the given {@link IRGISPrivacySetting}
     * 
     * @param ps
     *            the {@link IRGISPrivacySetting}
     * @param attachData
     *            set this flag true, if the given data should be attached with the {@link IIssue}s
     * @return List with {@link IIssue}s as result of the validation
     */
    public List<IIssue> validatePrivacySetting(IRGISPrivacySetting ps, boolean attachData) {
        List<IIssue> issueList = new ArrayList<IIssue>();
        
        // Clear the attached issues, if the issues should be attached
        if (attachData) {
            ps.clearIssues();
        }
        
        /*
         * Validate names, descriptions and change descriptions
         */
        issueList.addAll(validateNames(ps));
        issueList.addAll(validateDescriptions(ps));
        issueList.addAll(validateChangeDescriptions(ps));
        
        /*
         * Validate, if the identifier is set
         */
        if (!checkValueSet(ps.getIdentifier())) {
            issueList.add(new Issue(IssueType.IDENTIFIER_MISSING, ps));
        }
        
        /*
         * Validate, if the valid value description is set
         */
        if (!checkValueSet(ps.getValidValueDescription())) {
            issueList.add(new Issue(IssueType.VALID_VALUE_DESCRIPTION_MISSING, ps));
        }
        
        // Attach data
        attachData(issueList, attachData);
        
        return issueList;
    }
    
    
    /**
     * Clear all {@link IIssue}s, begin with the given {@link IRGIS} and propagate
     * 
     * @param rgis
     *            the {@link IRGIS}
     */
    public void clearIssuesAndPropagate(IRGIS rgis) {
        rgis.clearIssues();
        for (IRGISPrivacySetting ps : rgis.getPrivacySettings()) {
            ps.clearIssues();
        }
    }
    
}
