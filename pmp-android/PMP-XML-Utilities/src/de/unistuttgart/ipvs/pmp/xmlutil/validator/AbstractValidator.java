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
package de.unistuttgart.ipvs.pmp.xmlutil.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.unistuttgart.ipvs.pmp.xmlutil.common.BasicIS;
import de.unistuttgart.ipvs.pmp.xmlutil.common.IBasicIS;
import de.unistuttgart.ipvs.pmp.xmlutil.common.IIdentifierIS;
import de.unistuttgart.ipvs.pmp.xmlutil.common.ILocalizedString;
import de.unistuttgart.ipvs.pmp.xmlutil.common.LocalizedString;
import de.unistuttgart.ipvs.pmp.xmlutil.validator.issue.IIssue;
import de.unistuttgart.ipvs.pmp.xmlutil.validator.issue.IIssueLocation;
import de.unistuttgart.ipvs.pmp.xmlutil.validator.issue.Issue;
import de.unistuttgart.ipvs.pmp.xmlutil.validator.issue.IssueLocation;
import de.unistuttgart.ipvs.pmp.xmlutil.validator.issue.IssueType;

/**
 * AbstractValidator for AIS, RGIS and PresetSet
 * 
 * @author Marcus Vetter
 * 
 */
public class AbstractValidator {
    
    /**
     * Validate the names of a given {@link IssueLocation}
     * 
     * @param location
     *            location of the issue
     * @return List with issues as result of the validation
     */
    protected List<Issue> validateNames(IIssueLocation location) {
        List<Issue> issueList = new ArrayList<Issue>();
        
        // Check, if this location has names and descriptions (extends from BasicIS)
        if (location instanceof BasicIS) {
            boolean englishLocaleExists = false;
            List<String> localesOccurred = new ArrayList<String>();
            
            for (LocalizedString name : ((IBasicIS) location).getNames()) {
                
                // Instantiate possible issues
                Issue localeMissing = new Issue(IssueType.LOCALE_MISSING, name);
                Issue localeInvalid = new Issue(IssueType.LOCALE_INVALID, name);
                Issue nameEmpty = new Issue(IssueType.EMPTY_VALUE, name);
                
                // Flag, if the locale is missing
                boolean localeAvailable = true;
                
                // Check, if the locale is set
                if (name.getLocale() == null || !checkValueSet(name.getLocale().getLanguage())) {
                    issueList.add(localeMissing);
                    localeAvailable = false;
                    
                } else if (!checkLocale(name.getLocale())) {
                    // if the locale is invalid
                    issueList.add(localeInvalid);
                    // Add the information of the locale to the name issue
                    localeInvalid.addParameter(name.getLocale().getLanguage());
                } else {
                    // Check, if its the english attribute
                    if (checkLocaleAttributeEN(name.getLocale())) {
                        englishLocaleExists = true;
                    }
                    
                    String locale = name.getLocale().getLanguage();
                    if (!localesOccurred.contains(locale)) {
                        localesOccurred.add(locale);
                    } else {
                        // Check, if this issue is already added to the issuelist
                        boolean issueAlreadyExists = false;
                        for (IIssue issueExisting : issueList) {
                            if (issueExisting.getType().equals(IssueType.NAME_LOCALE_OCCURRED_TOO_OFTEN)
                                    && (issueExisting).getLocation().equals(location)
                                    && issueExisting.getParameters().size() > 0
                                    && issueExisting.getParameters().get(0).equals(locale)) {
                                issueAlreadyExists = true;
                            }
                        }
                        if (!issueAlreadyExists) {
                            Issue localesOccurredTooOften = new Issue(IssueType.NAME_LOCALE_OCCURRED_TOO_OFTEN,
                                    location);
                            localesOccurredTooOften.addParameter(locale);
                            issueList.add(localesOccurredTooOften);
                        }
                        
                    }
                }
                
                // Check, if the name is set
                if (!checkValueSet(name.getString())) {
                    issueList.add(nameEmpty);
                    // Add the information of the locale to the name issue
                    if (localeAvailable) {
                        nameEmpty.addParameter(name.getLocale().getLanguage());
                    }
                } else {
                    // Add the information of the name to the locale issue
                    localeMissing.addParameter(name.getString());
                }
            }
            
            // Add an issue: the English locale is missing
            if (!englishLocaleExists) {
                Issue localeEnMissing = new Issue(IssueType.NAME_LOCALE_EN_MISSING, location);
                issueList.add(localeEnMissing);
            }
            
        }
        
        return issueList;
        
    }
    
    
    /**
     * Validate the description of a given {@link IssueLocation}
     * 
     * @param location
     *            the IssueLocation
     * @return List with issues as result of the validation
     */
    protected List<IIssue> validateDescriptions(IIssueLocation location) {
        List<IIssue> issueList = new ArrayList<IIssue>();
        
        // Check, if this location has names and descriptions (extends from BasicIS)
        if (location instanceof BasicIS) {
            boolean englishLocaleExists = false;
            List<String> localesOccurred = new ArrayList<String>();
            
            for (ILocalizedString description : ((IBasicIS) location).getDescriptions()) {
                
                // Instantiate possible issues
                Issue localeMissing = new Issue(IssueType.LOCALE_MISSING, description);
                Issue localeInvalid = new Issue(IssueType.LOCALE_INVALID, description);
                Issue descriptionEmpty = new Issue(IssueType.EMPTY_VALUE, description);
                
                // Flag, if the locale is missing
                boolean localeAvailable = true;
                
                // Check, if the locale is set
                if (description.getLocale() == null || !checkValueSet(description.getLocale().getLanguage())) {
                    issueList.add(localeMissing);
                    localeAvailable = false;
                    
                } else if (!checkLocale(description.getLocale())) {
                    // if the locale is invalid
                    issueList.add(localeInvalid);
                    // Add the information of the locale to the name issue
                    localeInvalid.addParameter(description.getLocale().getLanguage());
                } else {
                    // Check, if its the english attribute
                    if (checkLocaleAttributeEN(description.getLocale())) {
                        englishLocaleExists = true;
                    }
                    
                    String locale = description.getLocale().getLanguage();
                    if (!localesOccurred.contains(locale)) {
                        localesOccurred.add(locale);
                    } else {
                        // Check, if this issue is already added to the issuelist
                        boolean issueAlreadyExists = false;
                        for (IIssue issueExisting : issueList) {
                            if (issueExisting.getType().equals(IssueType.DESCRIPTION_LOCALE_OCCURRED_TOO_OFTEN)
                                    && (issueExisting).getLocation().equals(location)
                                    && issueExisting.getParameters().size() > 0
                                    && issueExisting.getParameters().get(0).equals(locale)) {
                                issueAlreadyExists = true;
                            }
                        }
                        if (!issueAlreadyExists) {
                            Issue localesOccurredTooOften = new Issue(IssueType.DESCRIPTION_LOCALE_OCCURRED_TOO_OFTEN,
                                    location);
                            localesOccurredTooOften.addParameter(locale);
                            issueList.add(localesOccurredTooOften);
                        }
                        
                    }
                }
                
                // Check, if the description is set
                if (!checkValueSet(description.getString())) {
                    issueList.add(descriptionEmpty);
                    // Add the information of the locale to the name issue
                    if (localeAvailable) {
                        descriptionEmpty.addParameter(description.getLocale().getLanguage());
                    }
                } else {
                    // Add the information of the description to the locale issue
                    localeMissing.addParameter(description.getString());
                }
            }
            
            // Add an issue: the English locale is missing
            if (!englishLocaleExists) {
                Issue localeEnMissing = new Issue(IssueType.DESCRIPTION_LOCALE_EN_MISSING, location);
                issueList.add(localeEnMissing);
            }
            
        }
        
        return issueList;
        
    }
    
    
    /**
     * Validate the occurrences of identifier.
     * 
     * @param locationWithIdentifier
     *            the data to validate (with an identifier)
     * @return a list with all identifier, which occurred at least twice
     */
    protected List<String> validateOccurrenceOfIdentifier(List<IIdentifierIS> locationWithIdentifier) {
        List<String> idList = new ArrayList<String>();
        for (IIdentifierIS identifier : locationWithIdentifier) {
            String id = identifier.getIdentifier();
            for (IIdentifierIS identifierCompare : locationWithIdentifier) {
                String idCompare = identifierCompare.getIdentifier();
                if ((!identifier.equals(identifierCompare)) && id.equals(idCompare) && !idList.contains(id)) {
                    idList.add(id);
                }
            }
        }
        return idList;
    }
    
    
    /**
     * Check, if the lang attribute value of a given lang attribute equals "en"
     * 
     * @param locale
     *            the locale to validate
     */
    protected boolean checkLocaleAttributeEN(Locale locale) {
        return locale.getLanguage().equals("en");
    }
    
    
    /**
     * Check, if the given locale is valid.
     * 
     * @param givenLocale
     *            locale to check
     * @return flag, if the given local is valid or not.
     */
    protected boolean checkLocale(Locale givenLocale) {
        for (String locale : Locale.getISOLanguages()) {
            if (locale.equals(givenLocale.getLanguage())) {
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * The method validates, if a given value is set
     * 
     * @param value
     *            value to validate
     * @return flag, if the value is set or not
     */
    protected boolean checkValueSet(String value) {
        return !(value == null || value.equals(""));
    }
    
    
    /**
     * Attach the issues to the data
     * 
     * @param issueList
     *            given issues
     * @param attachData
     *            true, if the data should be attached with the issues
     */
    protected void attachData(List<IIssue> issueList, boolean attachData) {
        if (attachData) {
            for (IIssue issue : issueList) {
                IIssueLocation location = issue.getLocation();
                location.addIssue(issue);
            }
        }
    }
    
}
