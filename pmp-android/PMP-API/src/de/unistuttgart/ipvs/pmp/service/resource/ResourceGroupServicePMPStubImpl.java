package de.unistuttgart.ipvs.pmp.service.resource;

import java.util.List;

import de.unistuttgart.ipvs.pmp.resource.PrivacyLevel;
import de.unistuttgart.ipvs.pmp.resource.ResourceGroup;
import de.unistuttgart.ipvs.pmp.resource.ResourceGroupAccess;
import de.unistuttgart.ipvs.pmp.service.resource.IResourceGroupServicePMP;

import android.os.RemoteException;

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
	PrivacyLevel pl = rg.getPrivacyLevel(identifier);
	if (pl == null) {
	    return null;
	} else {
	    return pl.getName(locale);
	}
    }

    @Override
    public String getPrivacyLevelDescription(String locale, String identifier)
	    throws RemoteException {
	PrivacyLevel pl = rg.getPrivacyLevel(identifier);
	if (pl == null) {
	    return null;
	} else {
	    return pl.getDescription(locale);
	}
    }

    @Override
    public String getHumanReadablePrivacyLevelValue(String locale,
	    String identifier, String value) throws RemoteException {
	PrivacyLevel pl = rg.getPrivacyLevel(identifier);
	if (pl == null) {
	    return null;
	} else {
	    return pl.getHumanReadablePrivacyLevelValue(locale, value);
	}
    }

    @Override
    public void setAccesses(@SuppressWarnings("rawtypes") List accesses)
	    throws RemoteException {
	/**
	 * Note: The given Map is described by the JavaDoc of
	 * {@link IResourceGroupService.Stub#setAccesses}
	 */
	@SuppressWarnings("unchecked")
	List<ResourceGroupAccess> castedAccesses = (List<ResourceGroupAccess>) accesses;

	// TODO Auto-generated method stub
	throw new UnsupportedOperationException();
    }

    @Override
    public boolean satisfiesPrivacyLevel(String privacyLevel, String reference,
	    String value) throws RemoteException {
	PrivacyLevel pl = rg.getPrivacyLevel(privacyLevel);
	if (pl == null) {
	    return false;
	} else {
	    return pl.satisfies(reference, value);
	}
    }

    @Override
    public void changePrivacyLevels(String appIdentifier)
	    throws RemoteException {
	/* TODO: some magic happening here
	 * I assume we have to call ResourceGroup.createActivity() that presents a
	 * standard representation of all privacylevels or something.
	 * 
	 * NOTICE: THIS WOULD BE GUI!
	 */

    }


}