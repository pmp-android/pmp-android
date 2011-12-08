package de.unistuttgart.ipvs.pmp.gui.placeholder;

import android.view.View;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.IResourceGroup;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.PrivacySettingValueException;

public class PrivacySetting implements IPrivacySetting {
    
    private String identifier;
    private IResourceGroup resourceGroup;
    private String name;
    private String description;
    
    
    public PrivacySetting(String identifier, IResourceGroup rg, String name, String description) {
        this.identifier = identifier;
        this.resourceGroup = rg;
        this.name = name;
        this.description = description;
    }
    
    
    @Override
    public String getIdentifier() {
        return this.identifier;
    }
    
    
    @Override
    public String getLocalIdentifier() {
        return this.identifier;
    }
    
    
    @Override
    public IResourceGroup getResourceGroup() {
        return this.resourceGroup;
    }
    
    
    @Override
    public String getName() {
        return this.name;
    }
    
    
    @Override
    public String getDescription() {
        return this.description;
    }
    
    
    @Override
    public boolean isValueValid(String value) {
        return false;
    }
    
    
    @Override
    public String getHumanReadableValue(String value) throws PrivacySettingValueException {
        return value;
    }
    
    
    @Override
    public boolean permits(String reference, String value) throws PrivacySettingValueException {
        return false;
    }
    
    
    @Override
    public View getView() {
        return null;
    }
    
    
    @Override
    public String getViewValue(View view) {
        return null;
    }
    
    
    @Override
    public void setViewValue(View view, String value) throws PrivacySettingValueException {
        return;
    }
    
}