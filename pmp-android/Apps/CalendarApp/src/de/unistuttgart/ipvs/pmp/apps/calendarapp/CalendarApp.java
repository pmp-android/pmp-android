/*
 * Copyright 2011 pmp-android development team
 * Project: CalendarApp
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
package de.unistuttgart.ipvs.pmp.apps.calendarapp;

import android.app.Application;
import android.app.Dialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import de.unistuttgart.ipvs.pmp.api.PMP;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.gui.dialogs.ChangeAppointmentDialog;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.gui.util.UiManager;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.model.Appointment;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.model.Model;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.sqlConnector.SqlConnector;

public class CalendarApp extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        PMP.get(this);
    }
    
    
    /**
     * Changes the functionality of the app according to its set ServiceFeature
     */
    public void changeFunctionalityAccordingToServiceFeature(Boolean registered) {
        
        final Boolean read = PMP.get().isServiceFeatureEnabled("read");
        final Boolean write = PMP.get().isServiceFeatureEnabled("write");
        
        // Clear the local list because you don't know if you can display the appointments
        Model.getInstance().clearLocalList();
        
        if (read) {
            // Read files
            new SqlConnector().loadAppointments();
        }
        
        /*
         * Listener for clicking one item. Opens a new dialog where the user can
         * change the date.
         */
        Model.getInstance().getContext().getListView().setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object clicked = Model.getInstance().getArrayAdapter().getItem(position);
                if (write) {
                    if (clicked instanceof Appointment) {
                        Dialog changeDateDialog = new ChangeAppointmentDialog(Model.getInstance().getContext(),
                                (Appointment) clicked);
                        changeDateDialog.show();
                    }
                } else {
                    String[] requested = new String[1];
                    requested[0] = "write";
                    UiManager.getInstance().showServiceFeatureInsufficientDialog(requested);
                }
            }
        });
        
        // Update the "No appointments available" textview
        Model.getInstance().getContext().updateNoAvaiableAppointmentsTextView();
    }
    
}
