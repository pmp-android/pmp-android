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
package de.unistuttgart.ipvs.pmp.xmlutil.common.informationset;

import java.util.Locale;

import de.unistuttgart.ipvs.pmp.xmlutil.validator.issue.IssueLocation;

/**
 * 
 * @author Marcus Vetter
 * 
 */
public class LocalizedString extends IssueLocation {
    
    /**
     * Serial
     */
    private static final long serialVersionUID = -8792729039613615548L;
    
    /**
     * The name
     */
    private String string = "";
    
    /**
     * Locale
     */
    private Locale locale;
    
    
    /**
     * Get the name
     * 
     * @return name
     */
    public String getString() {
        return this.string;
    }
    
    
    /**
     * Set the name
     * 
     * @param name
     *            name to set
     */
    public void setString(String name) {
        this.string = name;
    }
    
    
    /**
     * Get the locale
     * 
     * @return locale
     */
    public Locale getLocale() {
        return this.locale;
    }
    
    
    /**
     * Set the locale
     * 
     * @param locale
     *            locale to set
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    
    @Override
    public void clearIssuesAndPropagate() {
        super.getIssues().clear();
    }
    
}