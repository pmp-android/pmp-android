package de.unistuttgart.ipvs.pmp.gui.mockup;

import java.util.HashMap;
import java.util.Map;

import de.unistuttgart.ipvs.pmp.model.IModel;
import de.unistuttgart.ipvs.pmp.model.ModelCache;
import de.unistuttgart.ipvs.pmp.model.element.IModelElement;
import de.unistuttgart.ipvs.pmp.model.element.ModelElement;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.element.preset.Preset;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.IResourceGroup;
import de.unistuttgart.ipvs.pmp.model.ipc.IPCProvider;

/**
 * Mockup model guaranteed to not persist anything or communicate with anyone while trying to maintain the main
 * infrastructure.
 * 
 * @author Tobias Kuhn
 * 
 */
public class MockupModel implements IModel {
    
    public static MockupModel instance = new MockupModel();
    
    private ModelCache mc;
    
    
    private MockupModel() {
        this.mc = new ModelCache();
        IPCProvider.getInstance().startUpdate();
    }
    
    
    @Override
    public IApp[] getApps() {
        return this.mc.getApps().values().toArray(new IApp[0]);
    }
    
    
    @Override
    public IApp getApp(String identifier) {
        return this.mc.getApps().get(identifier);
    }
    
    
    @Override
    @Deprecated
    public void registerApp(String identifier) {
    }
    
    
    public void registerApp(String identifier, MockupApp app) {
        this.mc.getApps().put(identifier, app);
    }
    
    
    @Override
    public boolean unregisterApp(String identifier) {
        return this.mc.getApps().remove(identifier) != null;
    }
    
    
    @Override
    public IResourceGroup[] getResourceGroups() {
        return this.mc.getResourceGroups().values().toArray(new IResourceGroup[0]);
    }
    
    
    @Override
    public IResourceGroup getResourceGroup(String identifier) {
        return this.mc.getResourceGroups().get(identifier);
    }
    
    
    @Override
    public String[] findResourceGroup(String searchString) {
        return new String[] { "test" };
    }
    
    
    @Override
    @Deprecated
    public boolean installResourceGroup(String identifier) {
        return false;
    }
    
    
    public boolean installResourceGroup(String identifier, MockupRG rg) {
        this.mc.getResourceGroups().put(identifier, rg);
        return true;
    }
    
    
    @Override
    public boolean uninstallResourceGroup(String identifier) {
        return this.mc.getResourceGroups().remove(identifier) != null;
    }
    
    
    @Override
    public IPreset[] getPresets() {
        return this.mc.getAllPresets().toArray(new IPreset[0]);
    }
    
    
    @Override
    public IPreset[] getPresets(ModelElement creator) {
        Map<String, Preset> creatorPresets = this.mc.getPresets().get(creator);
        if (creatorPresets == null) {
            return null;
        } else {
            return creatorPresets.values().toArray(new IPreset[0]);
        }
    }
    
    
    @Override
    public IPreset getPreset(IModelElement creator, String identifier) {
        Map<String, Preset> creatorPresets = this.mc.getPresets().get(creator);
        if (creatorPresets == null) {
            return null;
        } else {
            return creatorPresets.get(identifier);
        }
    }
    
    
    @Override
    public IPreset addPreset(IModelElement creator, String identifier, String name, String description) {
        Preset newPreset = new MockupPreset(creator, identifier, name, description);
        Map<String, Preset> creatorMap = this.mc.getPresets().get(creator);
        if (creatorMap == null) {
            creatorMap = new HashMap<String, Preset>();
            this.mc.getPresets().put(creator, creatorMap);
        }
        creatorMap.put(identifier, newPreset);
        return newPreset;
    }
    
    
    @Override
    public boolean removePreset(IModelElement creator, String identifier) {
        // does the creator map exist?
        Map<String, Preset> creatorMap = this.mc.getPresets().get(creator);
        if (creatorMap == null) {
            return false;
        } else {
            return creatorMap.remove(identifier) != null;
        }
    }
    
    
    @Override
    public void clearAll() {
        this.mc.getApps().clear();
        this.mc.getServiceFeatures().clear();
        this.mc.getResourceGroups().clear();
        this.mc.getPrivacySettings().clear();
        this.mc.getPresets().clear();
    }
    
}