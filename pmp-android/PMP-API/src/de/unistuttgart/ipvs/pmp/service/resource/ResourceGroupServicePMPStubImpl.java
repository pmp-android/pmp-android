package de.unistuttgart.ipvs.pmp.service.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.os.RemoteException;
import de.unistuttgart.ipvs.pmp.PMPComponentType;
import de.unistuttgart.ipvs.pmp.resource.ResourceGroup;
import de.unistuttgart.ipvs.pmp.resource.ResourceGroupAccess;
import de.unistuttgart.ipvs.pmp.resource.privacylevel.PrivacyLevel;
import de.unistuttgart.ipvs.pmp.resource.privacylevel.PrivacyLevelValueException;
import de.unistuttgart.ipvs.pmp.service.RegistrationState;
import de.unistuttgart.ipvs.pmp.service.SerializableContainer;

/**
 * Implementation of the {@link IResourceGroupServicePMP.Stub} stub.
 * 
 * @author Tobias Kuhn
 */
public class ResourceGroupServicePMPStubImpl extends
	IResourceGroupServicePMP.Stub {

    /**
     * referenced resource group
     */
    private ResourceGroup rg;

    public void setResourceGroup(ResourceGroup rg) {
	this.rg = rg;
    }

    @Override
    public String getName(String locale) throws RemoteException {
	return rg.getName(locale);
    }

    @Override
    public String getDescription(String locale) throws RemoteException {
	return rg.getDescription(locale);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public List getPrivacyLevelIdentifiers() throws RemoteException {
	return rg.getPrivacyLevels();
    }

    @Override
    public String getPrivacyLevelName(String locale, String identifier)
	    throws RemoteException {
	PrivacyLevel<?> pl = rg.getPrivacyLevel(identifier);
	if (pl == null) {
	    return null;
	} else {
	    return pl.getName(locale);
	}
    }

    @Override
    public String getPrivacyLevelDescription(String locale, String identifier)
	    throws RemoteException {
	PrivacyLevel<?> pl = rg.getPrivacyLevel(identifier);
	if (pl == null) {
	    return null;
	} else {
	    return pl.getDescription(locale);
	}
    }

    @Override
    public String getHumanReadablePrivacyLevelValue(String locale,
	    String identifier, String value) throws RemoteException {
	PrivacyLevel<?> pl = rg.getPrivacyLevel(identifier);
	if (pl == null) {
	    return null;
	} else {
	    
	    try {
		return pl.getHumanReadableValue(locale, value);
	    } catch (PrivacyLevelValueException e) {
		throw generateCausedRemoteException(e);
	    }
	}
    }

    @Override
    public void setAccesses(SerializableContainer remoteAccesses)
	    throws RemoteException {
	/*
	 * local resorting variables are marked local:
	 * Application => (PrivacyLevel => Value)
	 * remote variables are marked remote:
	 * PrivacyLevel => (Application => Value)
	 */
	Map<String, Map<String, String>> localPLsAccess = new HashMap<String, Map<String,String>>();
	
	ResourceGroupAccess[] rgas = (ResourceGroupAccess[]) remoteAccesses.getSerializable();
	for (ResourceGroupAccess rga : rgas) {
	    // update the public keys
	    rg.getSignee().setRemotePublicKey(PMPComponentType.APP, rga.getHeader()
		    .getIdentifier(), rga.getHeader().getPublicKey());	    
	    
	    // create our new internal representation,
	    // the resorting is quite confusing...
	    for (Entry<String, String> remotePLValue : rga.getPrivacyLevelValues().entrySet()) {
		
		Map<String, String> localAppValues = localPLsAccess.get(remotePLValue.getKey());
		if (localAppValues == null) {
		    localAppValues = new HashMap<String, String>();
		    localPLsAccess.put(remotePLValue.getKey(), localAppValues);
		}
		
		localAppValues.put(rga.getHeader().getIdentifier(), remotePLValue.getValue());
		
	    }	   
	}
	for (Entry<String, Map<String, String>> e : localPLsAccess.entrySet()) {
	    try {
		rg.updateAccess(e.getKey(), e.getValue());
	    } catch (PrivacyLevelValueException e1) {
		throw generateCausedRemoteException(e1);
	    }
	}
    }

    @Override
    public boolean satisfiesPrivacyLevel(String privacyLevel, String reference,
	    String value) throws RemoteException {
	PrivacyLevel<?> pl = rg.getPrivacyLevel(privacyLevel);
	if (pl == null) {
	    return false;
	} else {
	    
	    try {
		return pl.permits(reference, value);
	    } catch (PrivacyLevelValueException e) {
		throw generateCausedRemoteException(e);
	    }
	}
    }

    @Override
    public void changePrivacyLevels(String appIdentifier)
	    throws RemoteException {
	/*
	 * TODO: some magic happening here I assume we have to call
	 * ResourceGroup.createActivity() that presents a standard
	 * representation of all privacylevels or something.
	 * 
	 * NOTICE: THIS WOULD BE GUI!
	 */

    }

    @Override
    public void setRegistrationState(RegistrationState state)
	    throws RemoteException {
	if (state.getState()) {
	    rg.onRegistrationSuccess();
	} else {
	    rg.onRegistrationFailed(state.getMessage());
	}
    }
    
    /**
     * Generates a {@link RemoteException} caused by cause.
     * @param cause
     * @return
     */
    private static RemoteException generateCausedRemoteException(Throwable cause) {
	RemoteException re = new RemoteException();
	re.initCause(cause);
	return re;
    }

}
