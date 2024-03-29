/*
 * Copyright 2012 pmp-android development team
 * Project: vHikeApp
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
package de.unistuttgart.ipvs.pmp.apps.vhike.gui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.IInterface;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.apps.vhike.Constants;
import de.unistuttgart.ipvs.pmp.apps.vhike.ctrl.Controller;
import de.unistuttgart.ipvs.pmp.apps.vhike.ctrl.vHikeService;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.adapter.HistoryRideAdapter;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.utils.ResourceGroupReadyListActivity;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.Model;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.HistoryPersonObject;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.HistoryRideObject;

public class HistoryRideActivity extends ResourceGroupReadyListActivity {
    
    HistoryRideAdapter adapter;
    List<HistoryPersonObject> hPersonObjects;
    Controller ctrl;
    int tripid;
    Handler handler;
    
    
    @Override
    public void onResourceGroupReady(IInterface resourceGroup, int resourceGroupId) {
        super.onResourceGroupReady(resourceGroup, resourceGroupId);
        Log.i(this, "RG ready: " + resourceGroup);
        if (rgvHike != null) {
            this.handler.post(new Runnable() {
                
                @Override
                public void run() {
                    goForward();
                }
            });
        }
    }
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(de.unistuttgart.ipvs.pmp.apps.vhike.R.layout.activity_history_profile_list);
        if (getvHikeRG(this) != null) {
            goForward();
        }
    }
    
    
    @Override
    public void onResume() {
        super.onResume();
        
        vHikeService.getInstance().updateServiceFeatures();
        
        if (vHikeService.isServiceFeatureEnabled(Constants.SF_HIDE_CONTACT_INFO)) {
            this.ctrl.enableAnonymity(Model.getInstance().getSid());
        } else {
            this.ctrl.disableAnonymity(Model.getInstance().getSid());
        }
        Log.i(this, "");
    }
    
    
    public void goForward() {
        this.ctrl = new Controller(rgvHike);
        this.handler = new Handler();
        int id = getIntent().getExtras().getInt("ID");
        String role = getIntent().getExtras().getString("ROLE");
        
        List<HistoryRideObject> historyRides = this.ctrl.getHistory(Model.getInstance().getSid(), role);
        if (historyRides.size() == 0) {
            this.hPersonObjects = new ArrayList<HistoryPersonObject>();
            this.hPersonObjects.add(new HistoryPersonObject(0, "no user found", 0, 0, false));
        } else {
            this.hPersonObjects = historyRides.get(id).getPersons();
            this.tripid = historyRides.get(id).getTripid();
        }
        
        Log.i(this, "Größe von Persons: " + this.hPersonObjects.size() + ", ID: " + id);
        this.adapter = new HistoryRideAdapter(HistoryRideActivity.this, this.hPersonObjects, this.tripid);
        setListAdapter(this.adapter);
    }
}
