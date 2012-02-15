/*
 * Copyright 2011 pmp-android development team
 * Project: SwitchesResourceGroup
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
package de.unistuttgart.ipvs.pmp.resource.privacysetting.library;

import android.content.Context;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.DefaultPrivacySetting;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.IPrivacySettingView;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.PrivacySettingValueException;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.view.BooleanView;

/**
 * {@link DefaultPrivacySetting} for {@link Boolean}.
 * 
 * @author Tobias Kuhn, Jakob Jarosch
 * 
 */
public class BooleanPrivacySetting extends DefaultPrivacySetting<Boolean> {
    
    private BooleanView view = null;
    
    
    @Override
    public Boolean parseValue(String value) throws PrivacySettingValueException {
        if (value == null) {
            return false;
        }
        
        boolean result = Boolean.valueOf(value);
        
        if (!result && !value.equalsIgnoreCase(Boolean.FALSE.toString())) {
            throw new PrivacySettingValueException();
        }
        
        return result;
    }
    
    
    @Override
    public String valueToString(Object value) {
        if (value == null || !(value instanceof Boolean)) {
            return null;
        }
        Boolean b = (Boolean) value;
        return b.toString();
    }
    
    
    @Override
    public IPrivacySettingView<Boolean> getView(Context context) {
        if (this.view == null) {
            this.view = new BooleanView(context);
        }
        return this.view;
    }
    
}
