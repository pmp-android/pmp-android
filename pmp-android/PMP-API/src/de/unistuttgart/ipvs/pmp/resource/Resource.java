package de.unistuttgart.ipvs.pmp.resource;

import android.os.IBinder;
import de.unistuttgart.ipvs.pmp.resource.privacylevel.PrivacyLevel;
import de.unistuttgart.ipvs.pmp.service.resource.ResourceGroupService;

/**
 * An individual Resource of a {@link ResourceGroup}.
 * 
 * @author Tobias Kuhn
 * 
 */
public abstract class Resource {

    /**
     * The resource group that this resource is assigned to.
     */
    private ResourceGroup resourceGroup;

    /**
     * Assigns the resource group during registration.
     * 
     * <b>Do not call this method.</b>
     * 
     * @param resourceGroup
     */
    protected final void assignResourceGroup(ResourceGroup resourceGroup) {
	this.resourceGroup = resourceGroup;
    }

    /**
     * 
     * @return the associated {@link ResourceGroup}.
     */
    protected final ResourceGroup getResourceGroup() {
	return this.resourceGroup;
    }

    /**
     * Retrieves an actual privacy level class.
     * 
     * @param privacyLevelIdentifier
     *            the identifier of the privacy level
     * @return the privacy level with the the identifier in the resource group.
     */
    public final PrivacyLevel<?> getPrivacyLevel(String privacyLevelIdentifier) {
	return resourceGroup.getPrivacyLevel(privacyLevelIdentifier);
    }

    /**
     * Sets the {@link IBinder} defined in AIDL for communicating over a
     * Service.
     * 
     * @see http://developer.android.com/guide/developing/tools/aidl.html
     * 
     * @param appIdentifier
     *            the identifier for the app accessing the interface.
     * 
     * @return The IBinder that shall be returned when an App binds against the
     *         {@link ResourceGroupService} requesting this resource.
     */
    public abstract IBinder getAndroidInterface(String appIdentifier);

}
