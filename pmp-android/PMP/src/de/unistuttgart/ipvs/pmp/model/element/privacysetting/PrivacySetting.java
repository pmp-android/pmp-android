package de.unistuttgart.ipvs.pmp.model.element.privacysetting;

import java.util.Locale;

import android.content.Context;
import android.view.View;
import de.unistuttgart.ipvs.pmp.model.PersistenceConstants;
import de.unistuttgart.ipvs.pmp.model.assertion.Assert;
import de.unistuttgart.ipvs.pmp.model.assertion.ModelMisuseError;
import de.unistuttgart.ipvs.pmp.model.element.ModelElement;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.IResourceGroup;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.ResourceGroup;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.AbstractPrivacySetting;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.PrivacySettingValueException;

/**
 * @see IPrivacySetting
 * @author Tobias Kuhn
 * 
 */
public class PrivacySetting extends ModelElement implements IPrivacySetting {
    
    /**
     * identifying attributes
     */
    protected ResourceGroup resourceGroup;
    protected String localIdentifier;
    
    /**
     * internal data & links
     */
    protected AbstractPrivacySetting<?> link;
    
    
    /* organizational */
    
    public PrivacySetting(ResourceGroup resourceGroup, String identifier) {
        super(resourceGroup.getIdentifier() + PersistenceConstants.PACKAGE_SEPARATOR + identifier);
        this.resourceGroup = resourceGroup;
        this.localIdentifier = identifier;
    }
    
    
    @Override
    public String toString() {
        return super.toString() + String.format(" [link = %s]", this.link);
    }
    
    
    /* interface */
    
    @Override
    public IResourceGroup getResourceGroup() {
        return this.resourceGroup;
    }
    
    
    @Override
    public String getName() {
        String name = this.resourceGroup.getRgis().getPrivacySettingForIdentifier(getLocalIdentifier())
                .getNameForLocale(Locale.getDefault());
        if (name == null) {
            name = this.resourceGroup.getRgis().getPrivacySettingForIdentifier(getLocalIdentifier())
                    .getNameForLocale(Locale.ENGLISH);
        }
        return name;
    }
    
    
    @Override
    public String getDescription() {
        String description = this.resourceGroup.getRgis().getPrivacySettingForIdentifier(getLocalIdentifier())
                .getDescriptionForLocale(Locale.getDefault());
        if (description == null) {
            description = this.resourceGroup.getRgis().getPrivacySettingForIdentifier(getLocalIdentifier())
                    .getDescriptionForLocale(Locale.ENGLISH);
        }
        return description;
    }
    
    
    @Override
    public String getLocalIdentifier() {
        return this.localIdentifier;
    }
    
    
    @Override
    public boolean isValueValid(String value) {
        checkCached();
        try {
            this.link.parseValue(value);
            return true;
        } catch (PrivacySettingValueException plve) {
            return false;
        }
    }
    
    
    @Override
    public String getHumanReadableValue(String value) throws PrivacySettingValueException {
        checkCached();
        return this.link.getHumanReadableValue(value);
    }
    
    
    @Override
    public boolean permits(String reference, String value) throws PrivacySettingValueException {
        checkCached();
        return this.link.permits(value, reference);
    }
    
    
    @Override
    public View getView(Context context) {
        checkCached();
        return this.link.getView(context);
    }
    
    
    @Override
    public String getViewValue(View view) {
        checkCached();
        Assert.nonNull(view, new ModelMisuseError(Assert.ILLEGAL_NULL, "view", view));
        return this.link.getViewValue(view);
    }
    
    
    @Override
    public void setViewValue(View view, String value) throws PrivacySettingValueException {
        checkCached();
        Assert.nonNull(view, new ModelMisuseError(Assert.ILLEGAL_NULL, "view", view));
        this.link.setViewValue(view, value);
    }
    
}
