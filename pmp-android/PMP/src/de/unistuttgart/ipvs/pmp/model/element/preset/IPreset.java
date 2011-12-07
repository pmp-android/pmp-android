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
package de.unistuttgart.ipvs.pmp.model.element.preset;

import de.unistuttgart.ipvs.pmp.model.element.IModelElement;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.IResourceGroup;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.IServiceFeature;

/**
 * {@link IPreset} is the chain link between the {@link IResourceGroup}, its {@link IPrivacySetting}s and the
 * {@link IApp} . It grants the assigned {@link IApp} access to the assigned {@link IPrivacySetting}s.
 * 
 * @author Jakob Jarosch
 */
public interface IPreset extends IModelElement {
    
    /**
     * @return Returns the <b>unique</b> identifier of the {@link IPreset}.
     */
    @Override
    public String getIdentifier();
    
    
    /**
     * 
     * @return the unique identifier for creator
     */
    public String getLocalIdentifier();
    
    
    /**
     * Returns whether this preset was bundled with an {@link IApp} or {@link IResourceGroup} or whether it was created
     * by the user.
     * 
     * @return false, if the preset was created by the user, true, if it was bundled with an {@link IApp} or
     *         {@link IResourceGroup}.
     */
    public boolean isBundled();
    
    
    /**
     * Returns the creator element of this {@link IPreset}. This can be either an {@link IApp} or {@link IResourceGroup}
     * , if the preset was delivered with them, or the user, in which case this method gracefully refers to the user as
     * "null".
     * 
     * @return the {@link IApp} or {@link IResourceGroup} with which this preset was bundled, or null, if it was not
     *         bundled
     */
    public IModelElement getCreator();
    
    
    /**
     * @return Returns the name of the {@link IPreset}.
     */
    public String getName();
    
    
    /**
     * Sets the name of this {@link IPreset} to name.
     * 
     * @param name
     */
    public void setName(String name);
    
    
    /**
     * @return Returns the description of the {@link IPreset}.
     */
    public String getDescription();
    
    
    /**
     * Sets the description of this {@link IPreset} to description.
     * 
     * @param description
     */
    public void setDescription(String description);
    
    
    /**
     * @return Returns the used {@link IPrivacySetting}s by this preset.
     */
    public IPrivacySetting[] getGrantedPrivacyLevels();
    
    
    /**
     * @param privacySetting
     *            the privacy level in question
     * @return the granted minimum value of the {@link IPrivacySetting} for this service level or null if none set
     */
    public String getGrantedPrivacyLevelValue(IPrivacySetting privacySetting);
    
    
    /**
     * @return Returns the assigned {@link IApp}s to this {@link IPreset}.
     */
    public IApp[] getAssignedApps();
    
    
    /**
     * @return Returns true if the {@link IApp} is assigned, false otherwise.
     */
    public boolean isAppAssigned(IApp app);
    
    
    /**
     * <p>
     * Adds an {@link IApp} to the {@link IPreset}.
     * </p>
     * 
     * <p>
     * <b>This method will cause a new verification of the {@link IApp}s {@link IServiceFeature}s.</b> So, if you have
     * multiple changes on the {@link IPreset} be sure to use {@link IPreset#startUpdate()} and
     * {@link IPreset#endUpdate()}.
     * </p>
     * 
     * @param app
     *            The {@link IApp} which should be added.
     */
    public void assignApp(IApp app);
    
    
    /**
     * Removes an {@link IApp} from the {@link IPreset}.
     * 
     * <p>
     * <b>This method will cause a new verification of the {@link IApp}s {@link IServiceFeature}s.</b> So, if you have
     * multiple changes on the {@link IPreset} be sure to use {@link IPreset#startUpdate()} and
     * {@link IPreset#endUpdate()}.
     * </p>
     * 
     * @param app
     *            The {@link IApp} which should be removed.
     */
    public void removeApp(IApp app);
    
    
    /**
     * Sets an {@link IPrivacySetting} to the {@link IPreset}.
     * 
     * <p>
     * <b>This method will cause a new verification of the {@link IServiceFeature}s of the {@link IApp}s assigned.</b>
     * So, if you have multiple changes on the {@link IPreset} be sure to use {@link IPreset#startUpdate()} and
     * {@link IPreset#endUpdate()}.
     * </p>
     * 
     * @param privacySetting
     *            The {@link IPrivacySetting} which should be set.
     * @param the
     *            value of the privacy level
     */
    public void assignPrivacyLevel(IPrivacySetting privacySetting, String value);
    
    
    /**
     * Removes an {@link IPrivacySetting} from the {@link IPreset}.
     * 
     * <p>
     * <b>This method will cause a new verification of the {@link IServiceFeature}s of the {@link IApp}s assigned.</b>
     * So, if you have multiple changes on the {@link IPreset} be sure to use {@link IPreset#startUpdate()} and
     * {@link IPreset#endUpdate()}.
     * </p>
     * 
     * @param privacySetting
     *            The {@link IPrivacySetting} which should be removed.
     */
    public void removePrivacyLevel(IPrivacySetting privacySetting);
    
    
    /**
     * Starts a cumulative update session. This means, the IPC provider of the {@link IPreset} will start buffering IPC
     * messages instead of directly delivering them directly. Be sure to always call {@link IPreset#endUpdate()}
     * afterwards.
     */
    public void startUpdate();
    
    
    /**
     * Ends a cumulative update session started by {@link IPreset#startUpdate()}.
     */
    public void endUpdate();
    
    
    /**
     * Stores the deleted property for this preset.
     * 
     * @param deleted
     *            whether this preset is currently deleted
     */
    public void setDeleted(boolean deleted);
    
    
    /**
     * 
     * @return true, if and only if this preset is marked as deleted
     */
    public boolean isDeleted();
    
    
    /**
     * @return true, if all {@link IResourceGroup}s and {@link IApp}s which this preset requires are available in PMP
     */
    public boolean isAvailable();
}
