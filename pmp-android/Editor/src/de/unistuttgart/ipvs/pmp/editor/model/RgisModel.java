/*
 * Copyright 2012 pmp-android development team
 * Project: Editor
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
package de.unistuttgart.ipvs.pmp.editor.model;

import de.unistuttgart.ipvs.pmp.editor.ui.editors.RgisEditor;
import de.unistuttgart.ipvs.pmp.xmlutil.rgis.IRGIS;
import de.unistuttgart.ipvs.pmp.xmlutil.rgis.RGIS;

/**
 * Extends the {@link AbstractModel} to realize the model for the {@link RgisEditor}
 * 
 * @author Thorsten Berberich
 * 
 */
public class RgisModel extends AbstractModel {
    
    /**
     * The stored {@link RGIS}
     */
    private IRGIS rgis;
    
    
    /**
     * Sets the {@link IRGIS} to store
     * 
     * @param rgis
     *            {@link IRGIS} to store
     */
    public void setRgis(IRGIS rgis) {
        this.rgis = rgis;
    }
    
    
    /**
     * Get the stored {@link RGIS}
     * 
     * @return the {@link RGIS}
     */
    public IRGIS getRgis() {
        return this.rgis;
    }
    
}
