/*
 * Copyright 2012 pmp-android development team
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
package de.unistuttgart.ipvs.pmp.model.element.missing;

import de.unistuttgart.ipvs.pmp.model.element.privacysetting.PrivacySetting;

/**
 * Object to inform about missing {@link PrivacySetting} values.
 * 
 * @author Tobias Kuhn
 * 
 */
public class MissingPrivacySettingValue {
    
    private final String rg, ps, value;
    
    
    public MissingPrivacySettingValue(String rg, String ps, String value) {
        this.rg = rg;
        this.ps = ps;
        this.value = value;
    }
    
    
    public String getResourceGroup() {
        return this.rg;
    }
    
    
    public String getPrivacySetting() {
        return this.ps;
    }
    
    
    public String getValue() {
        return this.value;
    }
    
    
    @Override
    public String toString() {
        return String.format("%s [%s,%s,'%s']", super.toString(), this.rg, this.ps, this.value);
    }
    
}
