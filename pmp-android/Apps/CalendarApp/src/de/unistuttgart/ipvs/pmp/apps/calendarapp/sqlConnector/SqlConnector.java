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
package de.unistuttgart.ipvs.pmp.apps.calendarapp.sqlConnector;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.app.App;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.R;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.gui.activities.CalendarAppActivity;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.gui.activities.ImportActivity;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.gui.util.UiManager;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.model.Appointment;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.model.Model;
import de.unistuttgart.ipvs.pmp.apps.calendarapp.model.Severity;
import de.unistuttgart.ipvs.pmp.resourcegroups.database.IDatabaseConnection;

public class SqlConnector {
    
    /**
     * Identifier of the needed resource group
     */
    private final String resGroupIdentifier = "de.unistuttgart.ipvs.pmp.resourcegroups.database";
    
    /**
     * Resource identifier
     */
    private String resIdentifier = "DatabaseRG";
    
    /**
     * {@link Context} of the {@link CalendarAppActivity}
     */
    private CalendarAppActivity appContext = Model.getInstance().getContext();
    
    /**
     * The {@link App} of the calendar app
     */
    private App app = ((App) appContext.getApplication());
    
    /*
     * Constants for the database table
     */
    private final String DB_TABLE_NAME = "Appointments";
    private final String ID = "ID";
    private final String NAME = "Name";
    private final String DESC = "Description";
    private final String DATE = "Date";
    private final String SEVERITY = "Severity";
    
    
    /**
     * Loads the dates stored appointments in the SQL database. This method calls
     * {@link Model#loadAppointments(ArrayList)} to store the dates in the model.
     * 
     */
    public void loadAppointments() {
        new Thread() {
            
            @Override
            public void run() {
                IBinder binder = app.getResourceBlocking(resGroupIdentifier, resIdentifier);
                if (binder != null) {
                    IDatabaseConnection idc = IDatabaseConnection.Stub.asInterface(binder);
                    
                    if (createTable(idc)) {
                        // Getting the number of the rows
                        long rowCount;
                        try {
                            rowCount = idc.query(SqlConnector.this.DB_TABLE_NAME, null, null, null, null, null,
                                    SqlConnector.this.DATE);
                            // Getting the rows 
                            for (int itr = 0; itr < rowCount; itr++) {
                                String[] columns = {};
                                columns = idc.getRowAt(itr);
                                
                                // Storing everything from this appointment
                                int id = Integer.valueOf(columns[0]);
                                String name = columns[1];
                                String desc = columns[2];
                                Severity severity = Severity.valueOf(columns[3]);
                                Date date = new Date(Long.valueOf(columns[4]));
                                
                                // Storing in the model
                                Model.getInstance().addAppointment(new Appointment(id, name, desc, date, severity));
                                Log.v("Loading appointment: ID: " + String.valueOf(id) + " date: " + columns[2]
                                        + " name: " + name + " description: " + columns[1] + " severity "
                                        + severity.toString());
                                
                                if (id > Model.getInstance().getHighestId()) {
                                    Model.getInstance().setHighestId(id);
                                }
                            }
                        } catch (RemoteException e) {
                            showToast(appContext.getString(R.string.err_load));
                            Log.e("Remote Exception", e);
                        } finally {
                            try {
                                idc.close();
                            } catch (RemoteException e) {
                                Log.e("RemoteException", e);
                            }
                        }
                    }
                }
            }
        }.start();
    }
    
    
    /**
     * Stores the appointment in the database AND in the model
     * 
     * @param date
     *            date of the appointment
     * @param name
     *            name of the appointment
     * @param description
     *            description of the appointment
     * @param severity
     *            {@link Severity} of the appointment
     */
    public void storeNewAppointment(final Date date, final String name, final String description,
            final Severity severity) {
        
        if (description.equals("") && name.equals("")) {
            Toast.makeText(this.appContext, R.string.appointment_not_added, Toast.LENGTH_SHORT).show();
            return;
        }
        
        new Thread() {
            
            @Override
            public void run() {
                IBinder binder = app.getResourceBlocking(resGroupIdentifier, resIdentifier);
                if (binder != null) {
                    IDatabaseConnection idc = IDatabaseConnection.Stub.asInterface(binder);
                    
                    if (createTable(idc)) {
                        try {
                            
                            // The values to add
                            Map<String, String> values = new HashMap<String, String>();
                            int id = Model.getInstance().getNewHighestId();
                            
                            values.put(ID, String.valueOf(id));
                            values.put(SqlConnector.this.NAME, name);
                            values.put(SqlConnector.this.DESC, description);
                            values.put(SqlConnector.this.DATE, String.valueOf(date.getTime()));
                            values.put(SqlConnector.this.SEVERITY, severity.toString());
                            
                            long result = idc.insert(SqlConnector.this.DB_TABLE_NAME, null, values);
                            Log.v("Return value of insert: " + result);
                            if (result != -1) {
                                idc.query(SqlConnector.this.DB_TABLE_NAME, null, null, null, null, null,
                                        SqlConnector.this.DATE);
                                
                                Log.v("Storing new appointment: id: " + String.valueOf(id) + " date: " + date
                                        + " description: " + description);
                                Model.getInstance().addAppointment(
                                        new Appointment(id, name, description, date, severity));
                            } else {
                                showToast(appContext.getString(R.string.err_store));
                                Log.e("Appointment not stored");
                            }
                        } catch (RemoteException e) {
                            showToast(appContext.getString(R.string.err_store));
                            Log.e("Remote Exception", e);
                        } finally {
                            try {
                                idc.close();
                            } catch (RemoteException e) {
                                Log.e("RemoteException", e);
                            }
                        }
                    }
                }
            }
        }.start();
    }
    
    
    /**
     * Stores the appointment ONLY in the database and NOT in the {@link Model}
     * 
     * @param date
     *            date of the appointment
     * @param name
     *            name of the appointment
     * @param description
     *            description of the appointment
     * @param severity
     *            {@link Severity} of the appointment
     */
    public void storeNewAppointmentWithoutModel(final Date date, final String name, final String description,
            final Severity severity) {
        
        if (description.equals("") && name.equals("")) {
            Toast.makeText(this.appContext, R.string.appointment_not_added, Toast.LENGTH_SHORT).show();
            return;
        }
        
        new Thread() {
            
            @Override
            public void run() {
                IBinder binder = app.getResourceBlocking(resGroupIdentifier, resIdentifier);
                if (binder != null) {
                    IDatabaseConnection idc = IDatabaseConnection.Stub.asInterface(binder);
                    
                    if (createTable(idc)) {
                        try {
                            // The values to add
                            Map<String, String> values = new HashMap<String, String>();
                            
                            int id = Model.getInstance().getNewHighestId();
                            
                            values.put(ID, String.valueOf(id));
                            values.put(SqlConnector.this.NAME, name);
                            values.put(SqlConnector.this.DESC, description);
                            values.put(SqlConnector.this.DATE, String.valueOf(date.getTime()));
                            values.put(SqlConnector.this.SEVERITY, severity.toString());
                            
                            long result = idc.insert(SqlConnector.this.DB_TABLE_NAME, null, values);
                            Log.v("Return value of insert: " + result);
                            if (result != -1) {
                                Log.v("Storing new appointment: id: " + String.valueOf(id) + " date: " + date
                                        + " description: " + description);
                            } else {
                                showToast(appContext.getString(R.string.err_store));
                                Log.e("Appointment not stored");
                            }
                        } catch (RemoteException e) {
                            showToast(appContext.getString(R.string.err_store));
                            Log.e("Remote Exception", e);
                        } finally {
                            try {
                                idc.close();
                            } catch (RemoteException e) {
                                Log.e("RemoteException", e);
                            }
                        }
                    }
                }
            }
        }.start();
    }
    
    
    /**
     * Delete the appointment out of the SQL database with the given id and then calls {@link Model#deleteDateByID(int)}
     * 
     * @param id
     *            id of the appointment to delete
     */
    public void deleteAppointment(final Appointment appointment) {
        new Thread() {
            
            @Override
            public void run() {
                IBinder binder = app.getResourceBlocking(resGroupIdentifier, resIdentifier);
                if (binder != null) {
                    IDatabaseConnection idc = IDatabaseConnection.Stub.asInterface(binder);
                    
                    if (createTable(idc)) {
                        try {
                            Log.v("Trying to delete appointment with id: " + appointment.getId() + " name: "
                                    + appointment.getName() + " Description: " + appointment.getDescrpition());
                            
                            String[] args = new String[1];
                            args[0] = String.valueOf(appointment.getId());
                            
                            /*
                             * Delete the date out of the database
                             */
                            if (idc.delete(SqlConnector.this.DB_TABLE_NAME, SqlConnector.this.ID + " = ?", args) == 1) {
                                Log.v("Deleting date: id: " + String.valueOf(appointment.getId()));
                                Model.getInstance().deleteAppointment(appointment);
                            } else {
                                showToast(appContext.getString(R.string.err_del));
                            }
                        } catch (RemoteException e) {
                            showToast(appContext.getString(R.string.err_del));
                            Log.e("Remote Exception", e);
                        } finally {
                            try {
                                idc.close();
                            } catch (RemoteException e) {
                                Log.e("RemoteException", e);
                            }
                        }
                    }
                }
            }
        }.start();
    }
    
    
    public void deleteAllApointments() {
        new Thread() {
            
            @Override
            public void run() {
                IBinder binder = app.getResourceBlocking(resGroupIdentifier, resIdentifier);
                if (binder != null) {
                    IDatabaseConnection idc = IDatabaseConnection.Stub.asInterface(binder);
                    
                    try {
                        if (idc.isTableExisted(DB_TABLE_NAME)) {
                            if (idc.deleteTable(DB_TABLE_NAME)) {
                                Log.d("Table deleted");
                            } else {
                                Log.e("Could not delete table");
                            }
                        }
                    } catch (RemoteException e) {
                        showToast(appContext.getString(R.string.err_del));
                        Log.e("RemoteException", e);
                    } finally {
                        try {
                            idc.close();
                        } catch (RemoteException e) {
                            Log.e("RemoteException", e);
                        }
                    }
                }
            }
        }.start();
        
    }
    
    
    /**
     * Changes the appointment at the SQL database and then calls {@link Model#changeAppointment(int, String, String)}
     * 
     * @param id
     *            the id of the appointment to change
     * @param date
     *            the date that has changed
     * @param description
     *            the description that has changed
     */
    public void changeAppointment(final Integer id, final Date date, final Date oldDate, final String name,
            final String description, final Severity severity) {
        if (description.equals("") && name.equals("")) {
            Toast.makeText(this.appContext, R.string.appointment_not_changed, Toast.LENGTH_SHORT).show();
            return;
        }
        
        new Thread() {
            
            @Override
            public void run() {
                IBinder binder = app.getResourceBlocking(resGroupIdentifier, resIdentifier);
                if (binder != null) {
                    IDatabaseConnection idc = IDatabaseConnection.Stub.asInterface(binder);
                    
                    if (createTable(idc)) {
                        try {
                            Map<String, String> values = new HashMap<String, String>();
                            
                            values.put(SqlConnector.this.NAME, name);
                            values.put(SqlConnector.this.DESC, description);
                            values.put(SqlConnector.this.DATE, String.valueOf(date.getTime()));
                            values.put(SqlConnector.this.SEVERITY, severity.toString());
                            
                            /*
                             * Change the date in the database and only if one row
                             * was changed change, then change it in the model
                             */
                            if (idc.update(SqlConnector.this.DB_TABLE_NAME, values, SqlConnector.this.ID + " = "
                                    + String.valueOf(id), null) == 1) {
                                Model.getInstance().changeAppointment(id, date, oldDate, name, description, severity);
                                Log.v("Changing date with id " + String.valueOf(id) + " to: date: " + date
                                        + " description: " + description);
                            } else {
                                showToast(appContext.getString(R.string.err_change));
                            }
                        } catch (RemoteException e) {
                            showToast(appContext.getString(R.string.err_change));
                            Log.e("Remote Exception", e);
                        } finally {
                            try {
                                idc.close();
                            } catch (RemoteException e) {
                                Log.e("RemoteException", e);
                            }
                        }
                    }
                }
            }
        }.start();
    }
    
    
    /**
     * Deletes the table, creates a new table, stores all appointments in the given list and closes the
     * {@link ImportActivity}
     * 
     * @param appList
     *            {@link ArrayList} with {@link Appointment}s to store
     */
    public void storeAppointmentListInEmptyList(final ArrayList<Appointment> appList) {
        new Thread() {
            
            @Override
            public void run() {
                IBinder binder = app.getResourceBlocking(resGroupIdentifier, resIdentifier);
                
                if (binder != null) {
                    IDatabaseConnection idc = IDatabaseConnection.Stub.asInterface(binder);
                    try {
                        
                        // Create a new table
                        if (createTable(idc)) {
                            
                            // Store all appointments
                            for (Appointment app : appList) {
                                // The values to add
                                Map<String, String> values = new HashMap<String, String>();
                                
                                int id = Model.getInstance().getNewHighestId();
                                
                                values.put(ID, String.valueOf(id));
                                values.put(SqlConnector.this.NAME, app.getName());
                                values.put(SqlConnector.this.DESC, app.getDescrpition());
                                values.put(SqlConnector.this.DATE, String.valueOf(app.getDate().getTime()));
                                values.put(SqlConnector.this.SEVERITY, app.getSeverity().toString());
                                
                                long result = idc.insert(SqlConnector.this.DB_TABLE_NAME, null, values);
                                Log.v("Return value of insert: " + result);
                                if (result != -1) {
                                    idc.query(SqlConnector.this.DB_TABLE_NAME, null, null, null, null, null,
                                            SqlConnector.this.DATE);
                                    
                                    Log.v("Storing new appointment: id: " + String.valueOf(id) + " date: "
                                            + app.getDate() + " description: " + app.getDescrpition());
                                    
                                } else {
                                    showToast(appContext.getString(R.string.err_store));
                                    Log.e("Appointment not stored");
                                }
                            }
                        }
                    } catch (RemoteException e) {
                        showToast(appContext.getString(R.string.err_del));
                        Log.e("Remote Exception", e);
                    } finally {
                        UiManager.getInstance().getImportActivity().finish();
                        try {
                            idc.close();
                        } catch (RemoteException e) {
                            Log.e("RemoteException", e);
                        }
                    }
                }
            }
            
        }.start();
    }
    
    
    /**
     * Checks if the table exists, if not then the table will be created
     * 
     * @param idc
     *            {@link IDatabaseConnection} to create the table
     */
    private Boolean createTable(IDatabaseConnection idc) {
        try {
            if (!idc.isTableExisted(SqlConnector.this.DB_TABLE_NAME)) {
                
                // Columns of the table
                Map<String, String> columns = new HashMap<String, String>();
                columns.put(SqlConnector.this.ID, "INTEGER");
                columns.put(SqlConnector.this.NAME, "TEXT");
                columns.put(SqlConnector.this.DESC, "TEXT");
                columns.put(SqlConnector.this.DATE, "TEXT");
                columns.put(SqlConnector.this.SEVERITY, "TEXT");
                
                // Creates the table
                Log.v("Creating table");
                
                // Create the table
                if (idc.createTable(SqlConnector.this.DB_TABLE_NAME, columns, null)) {
                    Log.v("Table created. Name: " + SqlConnector.this.DB_TABLE_NAME);
                    return true;
                } else {
                    Log.e("Couldn't create table");
                    showToast(appContext.getString(R.string.err_create));
                    return false;
                }
            } else {
                Log.v("Table already exists");
                return true;
            }
        } catch (RemoteException e) {
            Log.e("RemoteException", e);
        }
        return false;
    }
    
    
    /**
     * Shows a toast. Called from inside a {@link Thread}
     * 
     * @param message
     *            to show
     */
    private void showToast(String message) {
        Looper.prepare();
        Toast.makeText(Model.getInstance().getContext(), message, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
}
