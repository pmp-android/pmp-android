package de.unistuttgart.ipvs.pmp.model.element.resourcegroup;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import android.graphics.drawable.Drawable;
import android.os.IBinder;
import de.unistuttgart.ipvs.pmp.model.assertion.Assert;
import de.unistuttgart.ipvs.pmp.model.assertion.ModelMisuseError;
import de.unistuttgart.ipvs.pmp.model.element.ModelElement;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.PrivacySetting;
import de.unistuttgart.ipvs.pmp.resource.Resource;
import de.unistuttgart.ipvs.pmp.util.xml.rg.RgInformationSet;

/**
 * @see IResourceGroup
 * @author Tobias Kuhn
 * 
 */
public class ResourceGroup extends ModelElement implements IResourceGroup {
    
    /**
     * localized values
     */
    protected RgInformationSet rgis;
    
    /**
     * internal data & links
     */
    protected Map<String, PrivacySetting> privacySettings;
    protected Drawable icon;
    protected de.unistuttgart.ipvs.pmp.resource.ResourceGroup link;
    
    
    /* organizational */
    
    public ResourceGroup(String rgPackage) {
        super(rgPackage);
    }
    
    
    @Override
    public String toString() {
        return super.toString()
                + String.format(" [rgis = %s, ps = %s, link = %s]", this.rgis,
                        ModelElement.collapseMapToString(this.privacySettings), this.link);
    }
    
    
    /* interface */
    
    @Override
    public String getName() {
        checkCached();
        String name = this.rgis.getNames().get(Locale.getDefault());
        if (name == null) {
            name = this.rgis.getNames().get(Locale.ENGLISH);
        }
        return name;
    }
    
    
    @Override
    public String getDescription() {
        checkCached();
        String description = this.rgis.getDescriptions().get(Locale.getDefault());
        if (description == null) {
            description = this.rgis.getDescriptions().get(Locale.ENGLISH);
        }
        return description;
    }
    
    
    @Override
    public Drawable getIcon() {
        checkCached();
        return this.icon;
    }
    
    
    @Override
    public int getRevision() {
        checkCached();
        return new Integer(this.rgis.getRevision());
    }
    
    
    @Override
    public IPrivacySetting[] getPrivacySettings() {
        checkCached();
        Collection<PrivacySetting> result = this.privacySettings.values();
        return result.toArray(new IPrivacySetting[result.size()]);
    }
    
    
    @Override
    public IPrivacySetting getPrivacySetting(String privacySettingIdentifier) {
        checkCached();
        Assert.nonNull(privacySettingIdentifier, new ModelMisuseError(Assert.ILLEGAL_NULL, "privacySettingIdentifier",
                privacySettingIdentifier));
        return this.privacySettings.get(privacySettingIdentifier);
    }
    
    
    @Override
    public IBinder getResource(String appPackage, String resource) {
        checkCached();
        Assert.nonNull(appPackage, new ModelMisuseError(Assert.ILLEGAL_NULL, "appPackage", appPackage));
        Assert.nonNull(resource, new ModelMisuseError(Assert.ILLEGAL_NULL, "resource", resource));
        
        Resource res = this.link.getResource(resource);
        if (res != null) {
            return res.getAndroidInterface(appPackage);
        } else {
            return null;
        }
    }
    
    
    /* inter-model communication */
    public RgInformationSet getRgis() {
        checkCached();
        return this.rgis;
    }
    
}
