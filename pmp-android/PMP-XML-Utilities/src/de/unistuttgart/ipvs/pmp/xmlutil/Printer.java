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
package de.unistuttgart.ipvs.pmp.xmlutil;

import java.util.List;

import de.unistuttgart.ipvs.pmp.xmlutil.ais.IAIS;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.IAISRequiredPrivacySetting;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.IAISRequiredResourceGroup;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.IAISServiceFeature;
import de.unistuttgart.ipvs.pmp.xmlutil.common.informationset.ILocalizedString;
import de.unistuttgart.ipvs.pmp.xmlutil.common.informationset.LocalizedString;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.Preset;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.PresetAssignedApp;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.PresetAssignedPrivacySetting;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.PresetPSContext;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.PresetSet;
import de.unistuttgart.ipvs.pmp.xmlutil.rgis.IRGIS;
import de.unistuttgart.ipvs.pmp.xmlutil.rgis.IRGISPrivacySetting;
import de.unistuttgart.ipvs.pmp.xmlutil.validator.issue.IIssue;
import de.unistuttgart.ipvs.pmp.xmlutil.validator.issue.IIssueLocation;

/**
 * This Class provides static methods to print a given AIS, RGIS and PresetSet.
 * 
 * @author Marcus Vetter
 * 
 */
public class Printer {
    
    /**
     * Method for printing
     * 
     * @param s
     *            String to print
     */
    private static void p(String s) {
        System.out.println(s);
    }
    
    
    /**
     * Print the issues
     * 
     * @param location
     *            location of the issues
     * @param preString
     *            the string before the print out
     */
    private static void printIssues(IIssueLocation location, String preString) {
        for (IIssue issue : location.getIssues()) {
            StringBuilder sb = new StringBuilder(preString + "!! Issue: " + issue.getType().toString());
            if (issue.getParameters().size() > 0) {
                sb.append(" (Parameter: ");
                for (int itr = 0; itr < issue.getParameters().size(); itr++) {
                    sb.append(issue.getParameters().get(itr));
                    if (itr < issue.getParameters().size() - 1)
                        sb.append(", ");
                }
                sb.append(")");
            }
            p(sb.toString());
        }
    }
    
    
    /**
     * Print the app information set to the console
     * 
     * @param ais
     *            the app information set for printing
     */
    public static void printAIS(IAIS ais) {
        p("------------------------------------");
        p("- Printout of the AIS --------------");
        p("------------------------------------");
        printIssues(ais, "> ");
        p("App information:");
        p("> Names:");
        for (LocalizedString name : ais.getNames()) {
            p("   > " + name.getLocale().getLanguage() + ": " + name.getString());
            printIssues(name, "   > ");
        }
        p("> Descriptions:");
        for (LocalizedString descr : ais.getDescriptions()) {
            p("   > " + descr.getLocale().getLanguage() + ": " + descr.getString());
            printIssues(descr, "   > ");
        }
        for (IAISServiceFeature sf : ais.getServiceFeatures()) {
            p("");
            p("Service Feature:");
            printIssues(sf, "> ");
            p("> Identifier: " + sf.getIdentifier());
            p("> Names:");
            for (LocalizedString name : sf.getNames()) {
                p("   > " + name.getLocale().getLanguage() + ": " + name.getString());
                printIssues(name, "   > ");
            }
            p("> Descriptions:");
            for (LocalizedString descr : sf.getDescriptions()) {
                p("   > " + descr.getLocale().getLanguage() + ": " + descr.getString());
                printIssues(descr, "   > ");
            }
            for (IAISRequiredResourceGroup rrg : sf.getRequiredResourceGroups()) {
                p("> Required Resource Group:");
                printIssues(rrg, "   > ");
                p("   > Identifier: " + rrg.getIdentifier());
                p("   > Min Revision: " + rrg.getMinRevision());
                p("   > Privacy Settings:");
                for (IAISRequiredPrivacySetting rps : rrg.getRequiredPrivacySettings()) {
                    p("      > " + rps.getIdentifier() + ": " + rps.getValue());
                    printIssues(rps, "      > ");
                }
            }
        }
        p("------------------------------------");
    }
    
    
    /**
     * Print the rgis set to the console
     * 
     * @param rgis
     *            the rg information set for printing
     */
    public static void printRGIS(IRGIS rgis) {
        p("------------------------------------");
        p("- Printout of the RGIS -------------");
        p("------------------------------------");
        p("Resourcegroup information:");
        printIssues(rgis, "> ");
        p("> Identifier: " + rgis.getIdentifier());
        p("> IconLocation: " + rgis.getIconLocation());
        p("> Class Name: " + rgis.getClassName());
        p("> Names:");
        for (LocalizedString name : rgis.getNames()) {
            p("   > " + name.getLocale().getLanguage() + ": " + name.getString());
            printIssues(name, "   > ");
        }
        p("> Descriptions:");
        for (LocalizedString descr : rgis.getDescriptions()) {
            p("   > " + descr.getLocale().getLanguage() + ": " + descr.getString());
            printIssues(descr, "   > ");
        }
        for (IRGISPrivacySetting ps : rgis.getPrivacySettings()) {
            p("");
            p("Privacy Setting:");
            printIssues(ps, "> ");
            p("> Identifier: " + ps.getIdentifier());
            p("> Valid value description: " + ps.getValidValueDescription());
            p("> Names:");
            for (LocalizedString name : ps.getNames()) {
                p("   > " + name.getLocale().getLanguage() + ": " + name.getString());
                printIssues(name, "   > ");
            }
            p("> Descriptions: ");
            for (LocalizedString descr : ps.getDescriptions()) {
                p("   > " + descr.getLocale().getLanguage() + ": " + descr.getString());
                printIssues(descr, "   > ");
            }
            p("> Change descriptions: ");
            for (ILocalizedString changeDescr : ps.getChangeDescriptions()) {
                p("   > " + changeDescr.getLocale().getLanguage() + ": " + changeDescr.getString());
                printIssues(changeDescr, "   > ");
            }
        }
        p("------------------------------------");
    }
    
    
    /**
     * Print the presetSet set to the console
     * 
     * @param presetSet
     *            the presetSet set for printing
     */
    public static void printPresetSet(PresetSet presetSet) {
        p("------------------------------------");
        p("- Printout of the PresetSet --------");
        p("------------------------------------");
        for (Preset preset : presetSet.getPresets()) {
            p("Preset information:");
            p("> Identifier: " + preset.getIdentifier());
            p("> Creator: " + preset.getCreator());
            p("> Name: " + preset.getName());
            p("> Description: " + preset.getDescription());
            p("");
            p("Assigned Apps:");
            for (PresetAssignedApp app : preset.getAssignedApps()) {
                p("> " + app.getIdentifier());
            }
            p("");
            for (PresetAssignedPrivacySetting ps : preset.getAssignedPrivacySettings()) {
                p("Assigned Privacy Setting:");
                p("> RG: " + ps.getRgIdentifier() + " (Revision: " + ps.getRgRevision() + ")");
                p("> PS: " + ps.getPsIdentifier());
                p("> Value: " + ps.getValue());
                for (PresetPSContext context : ps.getContexts()) {
                    p("> Context:");
                    p("   > Type: " + context.getType());
                    p("   > Condition: " + context.getCondition());
                    p("   > Override-Value: " + context.getOverrideValue());
                }
                p("");
            }
            p("------------------------------------");
        }
        p("------------------------------------");
    }
    
    
    public static void printIssues(List<IIssue> issueList) {
        p("------------------------------------");
        p("- Printout of the issue list -------");
        p("------------------------------------");
        for (IIssue issue : issueList) {
            p("> Location: " + issue.getLocation());
            p("> Type: " + issue.getType().toString());
            for (String parameter : issue.getParameters()) {
                p("> Parameter: " + parameter);
            }
            p("");
        }
        p("------------------------------------");
    }
    
}
