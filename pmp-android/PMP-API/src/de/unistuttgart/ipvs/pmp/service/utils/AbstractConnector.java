package de.unistuttgart.ipvs.pmp.service.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * {@link AbstractConnector} implements methods used by {@link PMPServiceConnector},
 * {@link ResourceGroupServiceConnector} and {@link AppServiceConnector}.
 * 
 * @author Jakob Jarosch
 */
public abstract class AbstractConnector {

    /**
     * Reference to the connected service {@link IBinder}.
     */
    private IBinder connectedService = null;

    /**
     * Binding state, true if a binding is on the way or the service is bound,
     * otherwise false.
     */
    private boolean binding = false;

    /**
     * Set to true if a {@link Service} has connected.
     */
    private boolean connected = false;

    /**
     * List of all {@link IConnectorCallback} handler which will receive a connection
     * message.
     */
    private List<IConnectorCallback> callbackHandler = new ArrayList<IConnectorCallback>();

    /**
     * The context used to initiate the binding.
     */
    private Context context;

    /**
     * The {@link ServiceConnection} is used to handle the bound Service
     * {@link IBinder}.
     */
    protected ServiceConnection serviceConnection = new ServiceConnection() {

	@Override
	public void onServiceDisconnected(ComponentName name) {
	    connectedService = null;
	    connected = false;
	    informCallback();
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
	    connectedService = service;
	    connected = true;
	    serviceConnected();
	    informCallback();
	}
    };

    public AbstractConnector(Context context) {
	this.context = context;
    }

    /**
     * Bind the {@link Service}.
     * 
     * @return Returns true if a binding is on the way, otherwise false.
     */
    public boolean bind() {
	binding = true;

	if (!isBound()) {
	    Intent intent = createIntent();
	    return context.bindService(intent, serviceConnection,
		    Context.BIND_AUTO_CREATE);
	} else {
	    return true;
	}
    }

    /**
     * Unbind the {@link Service}.
     */
    public void unbind() {
	binding = false;

	context.unbindService(serviceConnection);
    }

    /**
     * @return true when the service is bound, false if not.
     */
    public boolean isBound() {
	return connected;
    }

    /**
     * Adds a {@link IConnectorCallback} instance to the list.
     * 
     * @param connectorCallback
     *            {@link IConnectorCallback} object which should be added
     */
    public void addCallbackHandler(IConnectorCallback connectorCallback) {
	callbackHandler.add(connectorCallback);
    }

    /**
     * Removes an {@link IConnectorCallback} instance from the list.
     * 
     * @param connectorCallback
     *            {@link IConnectorCallback} object which should be removed
     */
    public void removeCallbackHandler(IConnectorCallback connectorCallback) {
	callbackHandler.remove(connectorCallback);
    }

    /**
     * @return Returns the Service as {@link IBinder}, should be casted to the
     *         correct interface. Returns NULL if the Service returned a NULL-
     *         {@link IBinder}.
     */
    protected IBinder getService() {
	return connectedService;
    }

    protected Context getContext() {
	return context;
    }

    /**
     * Create a new {@link Intent} to connect to the {@link Service}.
     * 
     * @return Returns the created {@link Intent}.
     */
    protected abstract Intent createIntent();

    /**
     * Is called when the service connected.
     */
    protected abstract void serviceConnected();

    /**
     * Is called when the service has disconnected.
     */
    protected abstract void serviceDisconnected();

    /**
     * Inform all {@link IConnectorCallback} instances which are registered that
     * something has changed.
     */
    private void informCallback() {
	for (IConnectorCallback handler : callbackHandler) {
	    if (!binding) {

	    } else if (binding && !isBound()) {
		handler.bindingFailed();
	    } else {
		handler.connected();
	    }
	}
    }
}