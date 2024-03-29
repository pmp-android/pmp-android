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
package de.unistuttgart.ipvs.pmp.gui.util.model.mockup;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.drawable.Drawable;
import de.unistuttgart.ipvs.pmp.model.element.ElementPersistenceProvider;
import de.unistuttgart.ipvs.pmp.model.element.ModelElement;
import de.unistuttgart.ipvs.pmp.model.element.app.App;
import de.unistuttgart.ipvs.pmp.model.element.preset.Preset;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.ServiceFeature;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.IAIS;

/**
 * 
 * @author Tobias Kuhn
 * 
 */
public class MockupApp extends App {
    
    private Drawable icon;
    
    
    public MockupApp(String appPackage, Drawable icon, IAIS ais) {
        super(appPackage);
        
        this.icon = icon;
        this.ais = ais;
        this.serviceFeatures = new HashMap<String, ServiceFeature>();
        this.assignedPresets = new ArrayList<Preset>();
    }
    
    
    @Override
    public void setPersistenceProvider(ElementPersistenceProvider<? extends ModelElement> persistenceProvider) {
        super.setPersistenceProvider(null);
    }
    
    
    @Override
    public Drawable getIcon() {
        return this.icon;
    }
    
    
    public void addSF(String name, MockupServiceFeature sf) {
        this.serviceFeatures.put(name, sf);
    }
    
    
    @Override
    public void verifyServiceFeatures() {
    }
    
}
