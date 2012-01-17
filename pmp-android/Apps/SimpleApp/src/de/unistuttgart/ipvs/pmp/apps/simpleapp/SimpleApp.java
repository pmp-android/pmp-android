/*
 * Copyright 2011 pmp-android development team
 * Project: SimpleApp
 * Project-Site: http://code.google.com/p/pmp-android/
 * 
 * ---------------------------------------------------------------------
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.unistuttgart.ipvs.pmp.apps.simpleapp;

import android.app.Application;
import android.os.RemoteException;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.api.PMP;
import de.unistuttgart.ipvs.pmp.app.App;
import de.unistuttgart.ipvs.pmp.apps.simpleapp.provider.Model;
import de.unistuttgart.ipvs.pmp.service.pmp.IPMPService;
import de.unistuttgart.ipvs.pmp.service.utils.PMPServiceConnector;

public class SimpleApp extends Application {

	static {
		Log.setTagSufix("SimpleApp");
	}

	@Override
	public void onCreate() {
		super.onCreate();

		PMP.get(this);
		Model.getInstance().setApp(this);
	}

	/* API 1 legacy code
	 * 
	 * 
	 * 
	 * 
	 
	@Override
	public void onRegistrationSuccess() {
		Log.v("Registration succeed");

		if (Model.getInstance().getActivity() != null) {
			Model.getInstance().getActivity().registrationEnded(true, null);
		}
		
		requestServiceFeatures();
	}

	@Override
	public void onRegistrationFailed(String message) {
		Log.e("Registration failed: " + message);
		
		if (Model.getInstance().getActivity() != null) {
			Model.getInstance().getActivity().registrationEnded(false, message);
		}
	}
	


	public boolean isRegistered() {
		final PMPServiceConnector pmpsc = new PMPServiceConnector(
				getApplicationContext());
		final String name = getApplicationContext().getPackageName();

		pmpsc.bind(true);
		IPMPService pmpservice = pmpsc.getAppService();
		try {
			return pmpservice.isRegistered(name);
		} catch (RemoteException e) {
			Log.e("Could not check registration state", e);
		}

		return false;
	}
	*/
}
