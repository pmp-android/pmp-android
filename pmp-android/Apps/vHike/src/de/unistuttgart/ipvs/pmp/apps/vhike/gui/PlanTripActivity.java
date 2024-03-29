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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IInterface;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.apps.vhike.Constants;
import de.unistuttgart.ipvs.pmp.apps.vhike.R;
import de.unistuttgart.ipvs.pmp.apps.vhike.ctrl.Controller;
import de.unistuttgart.ipvs.pmp.apps.vhike.ctrl.vHikeService;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.adapter.AddStopOverListener;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.dialog.IDialogFinishedCallBack;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.dialog.OnConfirmationDialogFinished;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.dialog.vhikeDialogs;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.maps.ViewModel;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.utils.ResourceGroupReadyActivity;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.Model;

/**
 * This Activity gives user freedom to act as a Driver or Passenger, later user will get the chance
 * to start planned rides, set needed or available seats depending on acting as driver or passenger,
 * set destination over multiple stop overs
 * 
 * @author Andre Nguyen, Dang Huynh
 * 
 */
public class PlanTripActivity extends ResourceGroupReadyActivity implements IDialogFinishedCallBack,
        OnConfirmationDialogFinished {
    
    // Function call back ID(s)
    private static final byte CONFIRM_END_TRIP = 0;
    
    private final int DIALOG_DATE_TIME_PICKER = 1;
    private SparseArray<Dialog> dialogs;
    
    private final String sid = Model.getInstance().getSid();
    
    private RadioButton pickDate;
    private RadioButton now;
    private Spinner spinner;
    private Spinner spinnerSeats;
    private Button addButton;
    private Button btnDrive;
    
    private Calendar plannedDate;
    
    private Controller ctrl;
    private Handler handler;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // TODO if not logged in
        
        setContentView(R.layout.activity_trip);
        this.handler = new Handler();
        registerListener();
        
        if (getvHikeRG(this) != null) {
            this.ctrl = new Controller(rgvHike);
        }
    }
    
    
    @Override
    public void onResume() {
        super.onResume();
        
        vHikeService.getInstance().updateServiceFeatures();
        
        this.ctrl = new Controller(rgvHike);
        
        if (vHikeService.isServiceFeatureEnabled(Constants.SF_HIDE_CONTACT_INFO)) {
            this.ctrl.enableAnonymity(Model.getInstance().getSid());
        } else {
            this.ctrl.disableAnonymity(Model.getInstance().getSid());
        }
        
        // TODO Check date!
        Log.i(this, "OnResume Plantrip");
    }
    
    
    @Override
    public void onResourceGroupReady(IInterface resourceGroup, int resourceGroupId) throws SecurityException {
        super.onResourceGroupReady(resourceGroup, resourceGroupId);
        
        Log.i(this, "RG ready: " + resourceGroup);
        if (rgvHike != null) {
            this.handler.post(new Runnable() {
                
                @Override
                public void run() {
                    PlanTripActivity.this.ctrl = new Controller(rgvHike);
                    Log.i(this, "OnRGReady: Controller");
                }
            });
        } else {
            Log.i(this, "OnRGReady: NULL");
        }
        
    }
    
    
    private void registerListener() {
        
        this.btnDrive = (Button) findViewById(R.id.Button_Drive);
        
        // Pick a date
        this.pickDate = (RadioButton) findViewById(R.id.radio_later);
        this.pickDate.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                getDialog(PlanTripActivity.this.DIALOG_DATE_TIME_PICKER).show();
                PlanTripActivity.this.btnDrive.setText("Create trip");
            }
        });
        
        // Destination Spinner
        this.spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.array_cities,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner.setAdapter(adapter);
        
        this.spinner.setOnLongClickListener(new OnLongClickListener() {
            
            @Override
            public boolean onLongClick(View v) {
                for (Spinner s : ViewModel.getInstance().getDestinationSpinners()) {
                    if (s == (Spinner) v) {
                        ViewModel.getInstance().setClickedSpinner(s);
                    }
                }
                vhikeDialogs.getInstance().spDialog(v.getContext()).show();
                return false;
            }
            
        });
        ViewModel.getInstance().getDestinationSpinners().clear();
        ViewModel.getInstance().getDestinationSpinners().add(this.spinner);
        Log.i(this, "Added spinner, Size" + ViewModel.getInstance().getDestinationSpinners().size()
                + ", Clicked ");
        
        // Insert the add-Button and set the OnClickListener
        this.addButton = (Button) findViewById(R.id.ib_add);
        this.addButton.setOnClickListener(new AddStopOverListener());
        
        // Number of Seats spinner
        this.spinnerSeats = (Spinner) findViewById(R.id.spinner_numSeats);
        adapter = ArrayAdapter.createFromResource(this, R.array.array_numSeats,
                android.R.layout.simple_spinner_item);
        this.spinnerSeats.setAdapter(adapter);
        
        this.now = (RadioButton) findViewById(R.id.radio_now);
        if (this.now.isChecked()) {
            this.btnDrive.setText("Drive");
        }
        this.now.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                PlanTripActivity.this.btnDrive.setText("Drive");
            }
        });
        
        this.btnDrive.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(final View v) {
                
                if (vHikeService.isServiceFeatureEnabled(Constants.SF_USE_ABSOLUTE_LOCATION)) {
                    Log.v(this, "Enable");
                    if (PlanTripActivity.this.now.isChecked()) {
                        // See if an open trip is open
                        switch (PlanTripActivity.this.ctrl.getOpenTrip(PlanTripActivity.this.sid)) {
                            case Constants.STATUS_ERROR:
                                // TODO ERROR
                                Toast.makeText(PlanTripActivity.this, "Cannot check for open trip",
                                        Toast.LENGTH_LONG)
                                        .show();
                                return;
                            case Constants.TRUE:
                                // Confirm end trip
                                vhikeDialogs.getConfirmationDialog(PlanTripActivity.this,
                                        R.string.confirm_end_trip_title, R.string.confirm_end_trip,
                                        R.string.default_yes, R.string.default_no,
                                        PlanTripActivity.CONFIRM_END_TRIP);
                                
                            case Constants.FALSE:
                                PlanTripActivity.this.announceTrip();
                            default:
                                Log.d(this, getString(R.string.error_unknown) + ": getOpenTrip");
                        }
                        //                    vhikeDialogs.getInstance().getChangeSF(PlanTripActivity.this).show();
                    } else {
                        PlanTripActivity.this.announceTrip();
                        PlanTripActivity.this.finish();
                    }
                } else {
                    Log.v(this, "disable");
                    vHikeService.requestServiceFeature(PlanTripActivity.this,
                            Constants.SF_USE_ABSOLUTE_LOCATION);
                }
                
                // TODO IF NOTNOW
                // announce trip
            } // end onClick btnDrive
        });
        
        // Button Search
        Button btnSearch = (Button) findViewById(R.id.Button_Search);
        btnSearch.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                if (vHikeService.isServiceFeatureEnabled(Constants.SF_USE_ABSOLUTE_LOCATION)) {
                    
                    if (ViewModel.getInstance().getDestinationSpinners().size() > 1) {
                        Toast.makeText(PlanTripActivity.this, "Only one destination allowed for passenger",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        ViewModel.getInstance().setDestination4Passenger(PlanTripActivity.this.spinner);
                        ViewModel.getInstance().setNumSeats(PlanTripActivity.this.spinnerSeats);
                        
                        //                        vhikeDialogs.getInstance().getSearchPD(PlanTripActivity.this).show();
                        Intent intent = new Intent(PlanTripActivity.this, PassengerViewActivity.class);
                        PlanTripActivity.this.startActivity(intent);
                    }
                } else {
                    Log.v(this, "disable");
                    vHikeService.requestServiceFeature(PlanTripActivity.this,
                            Constants.SF_USE_ABSOLUTE_LOCATION);
                }
                
            }
        });
    }
    
    
    protected Dialog getDialog(int dialogID) {
        this.dialogs = (this.dialogs == null) ? new SparseArray<Dialog>(1) : this.dialogs;
        
        switch (dialogID) {
            case DIALOG_DATE_TIME_PICKER:
                if (this.dialogs.get(dialogID) == null) {
                    this.dialogs.append(
                            dialogID,
                            vhikeDialogs.getInstance().getDateTimePicker(PlanTripActivity.this, dialogID,
                                    this.plannedDate));
                }
                return this.dialogs.get(dialogID);
            default:
                return null;
        }
    }
    
    
    @Override
    public void dialogFinished(int dialogId, int buttonId) {
        
        switch (dialogId) {
            case DIALOG_DATE_TIME_PICKER:
                try {
                    DatePicker d = (DatePicker) getDialog(dialogId).findViewById(R.id.dpicker);
                    TimePicker t = (TimePicker) getDialog(dialogId).findViewById(R.id.tpicker);
                    if (this.plannedDate == null) {
                        this.plannedDate = Calendar.getInstance();
                    }
                    this.plannedDate.set(d.getYear(), d.getMonth(), d.getDayOfMonth(), t.getCurrentHour(),
                            t.getCurrentMinute(), 0);
                    if (this.plannedDate.before(Calendar.getInstance())) {
                        RadioButton r = (RadioButton) findViewById(R.id.radio_now);
                        r.toggle();
                        r = (RadioButton) findViewById(R.id.radio_later);
                        r.setText(R.string.dialog_choose_date);
                        Toast.makeText(this, R.string.date_in_past, Toast.LENGTH_LONG).show();
                    } else {
                        RadioButton r = (RadioButton) findViewById(R.id.radio_later);
                        r.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(
                                this.plannedDate.getTime()));
                    }
                } catch (Exception e) {
                    RadioButton r = (RadioButton) findViewById(R.id.radio_now);
                    r.toggle();
                    r = (RadioButton) findViewById(R.id.radio_later);
                    r.setText(R.string.dialog_choose_date);
                    Toast.makeText(this, R.string.date_get_error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        
    }
    
    
    private boolean endOpenTrip() throws Exception {
        switch (this.ctrl.endTrip(this.sid, -1)) {
            case Constants.STATUS_SUCCESS:
                return true;
            case Constants.STATUS_ERROR:
                // TODO get and show error message
                throw new Exception();
            default:
                return false;
        }
    }
    
    
    private void announceTrip() {
        ViewModel.getInstance().setDestination(this.spinner);
        ViewModel.getInstance().setNumSeats(this.spinnerSeats);
        
        Log.d(this, "Destination and StopOvers: " + ViewModel.getInstance().getDestination());
        
        Date date = null;
        if (!this.now.isChecked()) {
            date = this.plannedDate.getTime();
        }
        
        switch (this.ctrl.announceTrip(
                this.sid,
                ViewModel.getInstance().getDestination(),
                Constants.COORDINATE_INVALID,
                Constants.COORDINATE_INVALID,
                ViewModel.getInstance().getNumSeats(),
                date)) {
        
            case Constants.STATUS_SUCCESS:
                // TODO inform
                Log.d(this, "Trip announced succesfully");
                
                if (this.now.isChecked()) {
                    // TODO Check RG Location
                    // Show progress dialog for getting position
                    //                    vhikeDialogs.getInstance().getAnnouncePD(PlanTripActivity.this).show();
                    final Intent intent = new Intent(PlanTripActivity.this, DriverViewActivity.class);
                    PlanTripActivity.this.startActivity(intent);
                } else {
                    
                }
                break;
            
            case Constants.TRIP_STATUS_OPEN_TRIP:
                vhikeDialogs.getConfirmationDialog(PlanTripActivity.this, R.string.confirm_end_trip_title,
                        R.string.confirm_end_trip, R.string.default_yes, R.string.default_no,
                        PlanTripActivity.CONFIRM_END_TRIP).show();
                break;
            
            case Constants.STATUS_ERROR:
                // TODO get error message
                Toast.makeText(PlanTripActivity.this, R.string.error_announcing_trip, Toast.LENGTH_SHORT)
                        .show();
                break;
        } // end switch
    } // end announce trip
    
    
    @Override
    public void confirmDialogPositive(int callbackFunctionID) {
        switch (callbackFunctionID) {
            case CONFIRM_END_TRIP:
                try {
                    if (endOpenTrip()) {
                        announceTrip();
                    }
                } catch (Exception e) {
                    // TODO Handle Exception
                }
                return;
            default:
                return;
        }
    }
    
    
    @Override
    public void confirmDialogNegative(int callbackFunctionID) {
        // Do nothing for now
    }
}
