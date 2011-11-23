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
package de.unistuttgart.ipvs.pmp.model.element.app;

import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.IServiceFeature;

/**
 * <p>This represents an {@link IApp} registered at PMP.</p>
 * 
 * <p>You can identify an {@link IApp} by its identifier, use {@link IApp#getIdentifier()}. With only an identifier you can
 * get the {@link IApp} object from {@link IModel#getApp(String)}.</p>
 * 
 * @author Jakob Jarosch
 */
public interface IApp {
    
    /**
     * @return Returns the <b>unique</b> identifier of the {@link IApp}.
     */
    public String getIdentifier();
    
    
    /**
     * @return Returns the localized name of the {@link IApp}.
     */
    public String getName();
    
    
    /**
     * @return Returns the localized description of the {@link IApp}.
     */
    public String getDescription();
    
    
    /**
     * @return Returns the service features provided by the {@link IApp}.
     */
    public IServiceFeature[] getServiceFeatures();
    
    
    /**
     * Returns a service feature with exactly this identifier.
     * 
     * @param serviceFeatureIdentifier
     *            identifier of the {@link IServiceFeature}
     * @return the {@link IServiceFeature} with the specified identifier for this app
     */
    public IServiceFeature getServiceFeature(String serviceFeatureIdentifier);
    
    
    /**
     * @return Returns the currently active {@link IServiceFeature}s for this {@link IApp}.
     */
    public IServiceFeature[] getActiveServiceFeatures();
    
    
    /**
     * <p>
     * Queues the verification of all service features of this app which will publish the results to the app when
     * finished.
     * </p>
     * 
     * <p>
     * The verification itself is concurrent, the method will return without any information of the success of the
     * verification or the publishing.
     * </p>
     */
    public void verifyServiceFeatures();
    
    
    /**
     * @return Returns all {@link IPreset}s which were assigned to the {@link IApp}.
     */
    public IPreset[] getAssignedPresets();
    
}