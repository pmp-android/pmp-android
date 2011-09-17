package de.unistuttgart.ipvs.pmp.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.os.RemoteException;
import de.unistuttgart.ipvs.pmp.Constants;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.PMPComponentType;
import de.unistuttgart.ipvs.pmp.resource.privacylevel.PrivacyLevel;
import de.unistuttgart.ipvs.pmp.resource.privacylevel.PrivacyLevelValueException;
import de.unistuttgart.ipvs.pmp.service.pmp.IPMPServiceRegistration;
import de.unistuttgart.ipvs.pmp.service.resource.ResourceGroupService;
import de.unistuttgart.ipvs.pmp.service.utils.IConnectorCallback;
import de.unistuttgart.ipvs.pmp.service.utils.PMPServiceConnector;
import de.unistuttgart.ipvs.pmp.service.utils.PMPSignee;

/**
 * <p>
 * A resource group that bundles {@link Resource}s and {@link PrivacyLevel}s.
 * You can register them by using the methods
 * {@link ResourceGroup#registerResource(String, Resource)} and
 * {@link ResourceGroup#registerPrivacyLevel(String, PrivacyLevel)}.
 * </p>
 * 
 * <p>
 * In order to work, a ResourceGroup needs a service defined in the manifest
 * file which simply is {@link ResourceGroupService}, and the app containing the
 * ResourceGroup and its service must extend {@link ResourceGroupApp}.
 * </p>
 * 
 * @author Tobias Kuhn
 * 
 */
public abstract class ResourceGroup {

    /**
     * Stores the associated signee.
     */
    private PMPSignee signee;

    /**
     * The resources present in that resource group.
     */
    private final Map<String, Resource> resources;

    /**
     * The privacy levels present in that resource group.
     */
    private final Map<String, PrivacyLevel<?>> privacyLevels;

    /**
     * Creates a new {@link ResourceGroup}.
     * 
     * @param serviceContext
     *            context of the service for this resource group
     */
    public ResourceGroup(Context serviceContext) {
	signee = new PMPSignee(PMPComponentType.RESOURCE_GROUP,
		getServiceAndroidName(), serviceContext);

	resources = new HashMap<String, Resource>();
	privacyLevels = new HashMap<String, PrivacyLevel<?>>();
    }

    /**
     * 
     * @param locale
     *            the ISO-639 locale string available from
     *            {@link Locale#getLanguage()}
     * @return the name of this resource group for the given locale
     */
    public abstract String getName(String locale);

    /**
     * 
     * @param locale
     *            the ISO-639 locale string available from
     *            {@link Locale#getLanguage()}
     * @return the description of this resource group for the given locale
     */
    public abstract String getDescription(String locale);

    /**
     * Overwrite this method to return the <b>exact same</b> identifier you have
     * put in the manifest file for the service for this Resource Group:
     * &lt;service>...&lt;intent-filter>...&lt;action
     * android:name="<b>HERE</b>">. If the identifier differ, the service will
     * not work.
     * 
     * @return the specified identifier
     */
    protected abstract String getServiceAndroidName();

    /**
     * 
     * @return the signee
     */
    public PMPSignee getSignee() {
	return this.signee;
    }

    /**
     * Registers resource as resource "identifier" in this resource group.
     * 
     * @param identifier
     * @param resource
     */
    public void registerResource(String identifier, Resource resource) {
	resource.assignResourceGroup(this);
	resources.put(identifier, resource);
    }

    /**
     * 
     * @param identifier
     * @return the resource identified by "identifier", if present, null
     *         otherwise
     */
    public Resource getResource(String identifier) {
	return resources.get(identifier);
    }

    /**
     * 
     * @return a list of all the valid resource identifiers.
     */
    public List<String> getResources() {
	return new ArrayList<String>(resources.keySet());
    }

    /**
     * Registers privacyLevel as privacy level "identifier" in this resource
     * group.
     * 
     * @param identifier
     * @param privacyLevel
     */
    public void registerPrivacyLevel(String identifier,
	    PrivacyLevel<?> privacyLevel) {
	privacyLevels.put(identifier, privacyLevel);
    }

    /**
     * 
     * @param identifier
     * @return the privacy level identified by "identifier", if present, null
     *         otherwise
     */
    public PrivacyLevel<?> getPrivacyLevel(String identifier) {
	return privacyLevels.get(identifier);
    }

    /**
     * 
     * @return a list of all the valid resource identifiers.
     */
    public List<String> getPrivacyLevels() {
	return new ArrayList<String>(privacyLevels.keySet());
    }

    /**
     * Used for getting changed access rules from the
     * {@link ResourceGroupService}. <b>Do not call this method yourself or
     * allow a different context to call it.</b>
     * 
     * @param privacyLevelIdentifier
     *            the identifier of the privacy level to update
     * @param privacyLevelValues
     *            a map from appIdentifier to privacy level value.
     * @throws PrivacyLevelValueException
     *             if any of the supplied values did not match the privacy
     *             levels criteria
     */
    public final void updateAccess(String privacyLevelIdentifier,
	    Map<String, String> privacyLevelValues)
	    throws PrivacyLevelValueException {
	PrivacyLevel<?> pl = getPrivacyLevel(privacyLevelIdentifier);
	if (pl == null) {
	    Log.e("Should update access for a privacy level which does not exist.");
	} else {
	    pl.setValues(privacyLevelValues);
	}
    }

    /**
     * Effectively starts this resource group and registers it with PMP. Note
     * that it needs to be "connected" to a {@link ResourceGroupService} via a
     * {@link ResourceGroupApp}. You can implement reacting to the result of
     * this operation by implementing onRegistrationSuccess() or
     * onRegistrationFailed()
     * 
     * @param context
     *            {@link Context} to use for the connection
     * 
     */
    protected void start(Context context) {

	// connect to PMP
	final PMPServiceConnector pmpsc = new PMPServiceConnector(context,
		signee);

	pmpsc.addCallbackHandler(new IConnectorCallback() {

	    @Override
	    public void disconnected() {
	    }

	    @Override
	    public void connected() {
		if (!pmpsc.isRegistered()) {
		    // register with PMP
		    IPMPServiceRegistration ipmpsr = pmpsc
			    .getRegistrationService();
		    try {
			byte[] pmpPublicKey = ipmpsr
				.registerResourceGroup(signee
					.getLocalPublicKey());

			// save the returned public key to be PMP's
			signee.setRemotePublicKey(PMPComponentType.PMP,
				Constants.PMP_IDENTIFIER, pmpPublicKey);

		    } catch (RemoteException e) {
			Log.e("RemoteException during registering resource group",
				e);
		    }
		}
		pmpsc.unbind();
	    }

	    @Override
	    public void bindingFailed() {
		Log.e("Binding failed during registering resource group.");
	    }
	});
	pmpsc.bind();
    }

    /**
     * Callback called when the preceding call to start() registered this
     * resource group successfully with PMP.
     */
    public abstract void onRegistrationSuccess();

    /**
     * Callback called when the preceding call to start() could not register
     * this resource group with PMP due to errors.
     * 
     * @param message
     *            returned message from the PMP service
     */
    public abstract void onRegistrationFailed(String message);
}
