package de.unistuttgart.ipvs.pmp.apps.simpleapp.provider;

import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.apps.simpleapp.SimpleApp;
import de.unistuttgart.ipvs.pmp.apps.simpleapp.gui.SimpleAppActivity;
import de.unistuttgart.ipvs.pmp.resourcegroups.switches.IWifiSwitch;

public class Model {

	private static final String RG_SWITCHES = "de.unistuttgart.ipvs.pmp.resourcegroups.switches";
	private static final String R_WIFI = "WifiSwitch";

	public static final String SF_WIFI_STATE = "wifiState";
	public static final String SF_WIFI_CHANGE = "wifiChange";

	private static Model instance;

	private SimpleApp app;

	private SimpleAppActivity activity;

	private Model() {

	}

	public static Model getInstance() {
		if (Model.instance == null) {
			Model.instance = new Model();
		}

		return Model.instance;
	}

	public void setWifi(final boolean state) {
		new Thread() {
			@Override
			public void run() {
				IBinder binder = app.getResourceBlocking(RG_SWITCHES, R_WIFI);
				IWifiSwitch remote = IWifiSwitch.Stub.asInterface(binder);
				getActivity().refreshWifi();

				try {
					remote.setState(state);
				} catch (Exception e) {
					Log.e("Could not set Wifi State", e);
					makeToast("Wifi state could not be changed");
				}
			}
		}.start();
		
	}

	public boolean getWifi() {
		IBinder binder = app.getResourceBlocking(RG_SWITCHES, R_WIFI);
		IWifiSwitch remote = IWifiSwitch.Stub.asInterface(binder);

		try {
			return remote.getState();
		} catch (Exception e) {
			Log.e("Could not get Wifi State", e);
			makeToast("Wifi state couldn't be fetched");
		}
		return false;
	}

	public void setApp(SimpleApp app) {
		this.app = app;
	}

	public SimpleApp getApp() {
		return app;
	}

	public void setActivity(SimpleAppActivity activity) {
		this.activity = activity;
	}

	public SimpleAppActivity getActivity() {
		return activity;
	}

	private void makeToast(final String message) {
		getActivity().handler.post(new Runnable() {

			public void run() {

				Toast.makeText(app.getApplicationContext(), message,
						Toast.LENGTH_SHORT).show();
			}
		});
	}
}