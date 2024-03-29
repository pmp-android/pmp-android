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
package de.unistuttgart.ipvs.pmp.apps.vhike.gui.dialog;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.maps.MapView;

import de.unistuttgart.ipvs.pmp.apps.vhike.R;
import de.unistuttgart.ipvs.pmp.apps.vhike.ctrl.Controller;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.adapter.SpinnerDialog;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.Profile;
import de.unistuttgart.ipvs.pmp.resourcegroups.contact.aidl.IContact;
import de.unistuttgart.ipvs.pmp.resourcegroups.vHikeWS.aidl.IvHikeWebservice;

/**
 * This class provides access to all dialogs in vHike
 * 
 * @author Andre Nguyen, Dang Huynh
 * 
 */
public class vhikeDialogs extends Activity {
    
    private static vhikeDialogs instance;
    
    private ProgressDialog dLogin;
    private ProgressDialog dAnnounce;
    private ProgressDialog dSearch;
    
    private ChangeServiceFeature csf;
    
    
    public static vhikeDialogs getInstance() {
        if (instance == null) {
            instance = new vhikeDialogs();
        }
        return instance;
    }
    
    
    /**
     * ProgressDialog when logging in
     * 
     * @param context
     * @return login progress dialog
     */
    public ProgressDialog getLoginPD(Context context) {
        if (this.dLogin == null) {
            this.dLogin = new ProgressDialog(context);
        }
        this.dLogin.setTitle("Login");
        this.dLogin.setMessage("Logging in...");
        this.dLogin.setIndeterminate(true);
        this.dLogin.setCancelable(false);
        return this.dLogin;
    }
    
    
    public void clearLoginPD() {
        this.dLogin = null;
    }
    
    
    public ChangeServiceFeature getChangeSF(Context context) {
        this.csf = new ChangeServiceFeature(context);
        return this.csf;
    }
    
    
    /**
     * ProgressDialog for driver when announcing a trip and calculating current position
     * 
     * @param context
     * @return announce progress dialog
     */
    public ProgressDialog getAnnouncePD(Context context) {
        if (this.dAnnounce == null) {
            this.dAnnounce = new ProgressDialog(context);
        }
        this.dAnnounce.setTitle("Announcing trip");
        this.dAnnounce.setMessage("Getting current location...");
        this.dAnnounce.setIndeterminate(true);
        this.dAnnounce.setCancelable(false);
        
        return this.dAnnounce;
    }
    
    
    public void clearAnnouncPD() {
        this.dAnnounce = null;
    }
    
    
    /**
     * ProgressDialog when searching for drivers and calculating current position
     * 
     * @param context
     * @return search progress dialog
     */
    public ProgressDialog getSearchPD(Context context) {
        if (this.dSearch == null) {
            this.dSearch = new ProgressDialog(context);
        }
        this.dSearch.setTitle("Thumbs up");
        this.dSearch.setMessage("Getting current location...\nHolding thumb up...");
        this.dSearch.setIndeterminate(true);
        this.dSearch.setCancelable(false);
        
        return this.dSearch;
    }
    
    
    public void clearSearchPD() {
        this.dSearch = null;
    }
    
    
    public Dialog getUpdateDataDialog(IvHikeWebservice ws, Context mContext, int tripOrQuery) {
        return new UpdateData(mContext, ws, tripOrQuery);
    }
    
    
    public Wait4PickUp getW4PU(Context context) {
        return new Wait4PickUp(context);
    }
    
    
    public RateProfileConfirm getRateProfileConfirmation(IvHikeWebservice ws, Context context, int profileID,
            int rating, int tripID) {
        return new RateProfileConfirm(ws, context, profileID, rating, tripID);
    }
    
    
    public SpinnerDialog spDialog(Context context) {
        return new SpinnerDialog(context);
    }
    
    
    public ProfileDialog getProfileDialog(IvHikeWebservice ws, Context context, int profileID,
            MapView mapView,
            IContact iContact, Profile foundUser, int driverOrpassenger) {
        return new ProfileDialog(ws, context, profileID, mapView, iContact, foundUser, driverOrpassenger);
    }
    
    
    /**
     * Returns a ready to use DateTimePicker-Dialog
     * 
     * @param inActivity
     *            The activity which invoke this Dialog. This Activity must implement the interface
     *            {@link IDialogFinishedCallBack}
     * @param ID
     *            The ID Dialog
     * @param c
     *            Initial date and time for the pickers
     * @return The Dialog, which must be show by calling the method show(). Title and Buttons can be
     *         set beforehand
     */
    public AlertDialog getDateTimePicker(final Activity inActivity, final int ID, Calendar cal) {
        final Context mContext = inActivity;
        
        // TODO If dialog already exists
        
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_ride_datetime, null); // , (ViewGroup)
                                                                             // findViewById(R.id.layout_root)
        AlertDialog.Builder builder = new Builder(mContext);
        
        // Set up the dialog
        if (cal == null) {
            cal = Calendar.getInstance();
        }
        DatePicker dPicker = (DatePicker) layout.findViewById(R.id.dpicker);
        dPicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        TimePicker tPicker = (TimePicker) layout.findViewById(R.id.tpicker);
        tPicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
        tPicker.setCurrentMinute(cal.get(Calendar.MINUTE));
        tPicker.setIs24HourView(DateFormat.is24HourFormat(mContext));
        
        builder.setView(layout)
                .setTitle(R.string.dialog_pick_date_and_time)
                .setPositiveButton(mContext.getString(R.string.default_OK),
                        new DialogInterface.OnClickListener() {
                            
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    IDialogFinishedCallBack d = (IDialogFinishedCallBack) inActivity;
                                    d.dialogFinished(ID, IDialogFinishedCallBack.POSITIVE_BUTTON);
                                } catch (Exception e) {
                                    Toast.makeText(mContext, "ERROR", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        });
        
        return builder.create();
    }
    
    private static AlertDialog confirm;
    
    
    /**
     * Returns a confirmation dialog
     * 
     * @param inActivity
     * @param titleId
     * @param messageId
     * @param positiveTextId
     * @param negativeTextId
     * @param callBackFunctionID
     * @return The confirmation dialog
     */
    public static AlertDialog getConfirmationDialog(final Activity inActivity, int titleId, int messageId,
            int positiveTextId, int negativeTextId, final int callBackFunctionID) {
        return getConfirmationDialog(inActivity, inActivity.getText(titleId), inActivity.getText(messageId),
                inActivity.getText(positiveTextId), inActivity.getText(negativeTextId), callBackFunctionID);
    }
    
    
    public static AlertDialog getConfirmationDialog(final Activity inActivity, CharSequence title,
            CharSequence message, CharSequence positiveText, CharSequence negativeText,
            final int callBackFunctionID) {
        final Context mContext = inActivity;
        
        if (confirm == null) {
            Builder builder = new Builder(mContext);
            confirm = builder.create();
        }
        confirm.setTitle(title);
        confirm.setMessage(message);
        confirm.setButton(DialogInterface.BUTTON_POSITIVE, positiveText,
                new DialogInterface.OnClickListener() {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OnConfirmationDialogFinished callback = (OnConfirmationDialogFinished) inActivity;
                        callback.confirmDialogPositive(callBackFunctionID);
                    }
                });
        confirm.setButton(DialogInterface.BUTTON_NEGATIVE, negativeText,
                new DialogInterface.OnClickListener() {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OnConfirmationDialogFinished callback = (OnConfirmationDialogFinished) inActivity;
                        callback.confirmDialogNegative(callBackFunctionID);
                    }
                });
        
        return confirm;
    }
    
    
    public SMSDialog getSMSDialog(Context context, String tel, IContact contactRG, Controller ctrl,
            Profile profile) {
        return new SMSDialog(context, tel, contactRG, ctrl, profile);
    }
    
}
