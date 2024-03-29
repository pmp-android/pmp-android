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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.apps.vhike.Constants;
import de.unistuttgart.ipvs.pmp.apps.vhike.ctrl.Controller;
import de.unistuttgart.ipvs.pmp.apps.vhike.ctrl.vHikeService;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.maps.ViewModel;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.Model;

public class HistoryMenuActivity extends Activity {
    
    Button btn_driver;
    Button btn_passenger;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.setContentView(R.layout.activity_history_menu);
        
        this.btn_driver = (Button) findViewById(R.id.Button_Driver_History);
        this.btn_passenger = (Button) findViewById(R.id.Button_Passenger_History);
        
        createTouchListener(getBaseContext());
    }
    
    
    @Override
    public void onResume() {
        super.onResume();
        
        vHikeService.getInstance().updateServiceFeatures();
        
        Controller ctrl = new Controller(ViewModel.getInstance().getvHikeRG());
        
        if (vHikeService.isServiceFeatureEnabled(Constants.SF_HIDE_CONTACT_INFO)) {
            ctrl.enableAnonymity(Model.getInstance().getSid());
        } else {
            ctrl.disableAnonymity(Model.getInstance().getSid());
        }
        Log.i(this, "");
    }
    
    
    private void createTouchListener(final Context context) {
        this.btn_driver.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                
                Intent intent = new Intent(HistoryMenuActivity.this, HistoryActivity.class);
                intent.putExtra("IS_DRIVER", true);
                HistoryMenuActivity.this.startActivity(intent);
            }
        });
        this.btn_passenger.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                
                Intent intent = new Intent(HistoryMenuActivity.this, HistoryActivity.class);
                intent.putExtra("IS_DRIVER", false);
                HistoryMenuActivity.this.startActivity(intent);
            }
        });
    }
    
}
