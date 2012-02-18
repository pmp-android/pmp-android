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
package de.unistuttgart.ipvs.pmp.xmlutil.ais;

import java.util.List;

import de.unistuttgart.ipvs.pmp.xmlutil.common.IIdentifierIS;

/**
 * 
 * @author Marcus Vetter
 * 
 */
public interface IAISRequiredResourceGroup extends IIdentifierIS {
    
    /**
     * Get the min revision
     * 
     * @return the min revision
     */
    public abstract String getMinRevision();
    
    
    /**
     * Set the min revision
     * 
     * @param minRevision
     *            min revision to set
     */
    public abstract void setMinRevision(String minRevision);
    
    
    /**
     * Get all privacy settings of the required resource group
     * 
     * @return list of privacy settings
     */
    public abstract List<IAISRequiredPrivacySetting> getRequiredPrivacySettings();
    
    
    /**
     * Add a privacy setting to the required resource group
     * 
     * @param privacySetting
     *            privacySetting to add
     */
    public abstract void addRequiredPrivacySetting(IAISRequiredPrivacySetting privacySetting);
    
    
    /**
     * Remove a privacy setting from the required resource group
     * 
     * @param privacySetting
     *            privacySetting to remove
     */
    public abstract void removeRequiredPrivacySetting(IAISRequiredPrivacySetting privacySetting);
    
    
    /**
     * Get a required privacy setting for a given identifier. Null, if no
     * required privacy setting exists for the given identifier.
     * 
     * @param identifier
     *            identifier of the required privacy setting
     * @return required privacy setting with given identifier, null if none
     *         exists.
     */
    public abstract IAISRequiredPrivacySetting getRequiredPrivacySettingForIdentifier(String identifier);
    
}
