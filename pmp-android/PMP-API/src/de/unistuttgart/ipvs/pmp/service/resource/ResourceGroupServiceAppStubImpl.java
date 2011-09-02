package de.unistuttgart.ipvs.pmp.service.resource;

import java.util.List;

import android.os.IBinder;
import android.os.RemoteException;
import de.unistuttgart.ipvs.pmp.resource.Resource;
import de.unistuttgart.ipvs.pmp.resource.ResourceGroup;
import de.unistuttgart.ipvs.pmp.service.resource.IResourceGroupServiceApp;

/**
 * Implementation of the {@link IResourceGroupServiceApp.Stub} stub.
 * 
 * @author Tobias Kuhn
 */
public class ResourceGroupServiceAppStubImpl extends IResourceGroupServiceApp.Stub {
    
    /**
     * {@link ResourceGroup} referenced.
     */
    private ResourceGroup rg;
    
    public void setResourceGroup(ResourceGroup rg) {
	this.rg = rg;	
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getResources() throws RemoteException {
	return rg.getResources();
    }

    @Override
    public IBinder getResource(String resourceIdentifier)
	    throws RemoteException {
	Resource resource = rg.getResource(resourceIdentifier);
	if (resource == null) {
	    return null;
	} else {
	    return resource.getAndroidInterface();
	}
    }    

}
