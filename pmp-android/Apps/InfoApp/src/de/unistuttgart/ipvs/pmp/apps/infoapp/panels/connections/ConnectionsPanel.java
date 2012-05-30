/*
 * Copyright 2012 pmp-android development team
 * Project: InfoApp
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
package de.unistuttgart.ipvs.pmp.apps.infoapp.panels.connections;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import de.unistuttgart.ipvs.pmp.api.PMP;
import de.unistuttgart.ipvs.pmp.api.PMPResourceIdentifier;
import de.unistuttgart.ipvs.pmp.api.handler.PMPRequestResourceHandler;
import de.unistuttgart.ipvs.pmp.apps.infoapp.Constants;
import de.unistuttgart.ipvs.pmp.apps.infoapp.InfoAppActivity;
import de.unistuttgart.ipvs.pmp.apps.infoapp.R;
import de.unistuttgart.ipvs.pmp.apps.infoapp.common.ConnectionUploadResourceHandler;
import de.unistuttgart.ipvs.pmp.apps.infoapp.panels.IPanel;
import de.unistuttgart.ipvs.pmp.resourcegroups.connection.IConnection;

/**
 * Displays the connection panel
 * 
 * @author Thorsten Berberich
 * 
 */
public class ConnectionsPanel implements IPanel, OnChildClickListener {
    
    /**
     * The view
     */
    private LinearLayout view;
    
    /**
     * The adapter of the {@link ExpandableListView}
     * 
     */
    private ListViewAdapater adapter;
    
    /**
     * {@link Context}
     */
    private Context context;
    
    /**
     * The info app activity
     */
    private InfoAppActivity activity;
    
    /**
     * PMP resource identifier for the connection rg
     */
    private final PMPResourceIdentifier RG_IDENTIFIER = PMPResourceIdentifier.make(Constants.CONNECTION_RG_IDENTIFIER,
            Constants.CONNECTION_RG_RESOURCE);
    
    /**
     * Handler for updating the gui
     */
    private Handler handler;
    
    
    /**
     * Constructor for the panel
     * 
     * @param context
     *            {@link Context}
     */
    public ConnectionsPanel(Context context, InfoAppActivity activity) {
        this.context = context;
        this.activity = activity;
        handler = new Handler();
        
        // load the layout from the xml file
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.view = (LinearLayout) inflater.inflate(R.layout.connection_panel, null);
        
        // Create the list and the adapter
        ExpandableListView listView = (ExpandableListView) this.view.findViewById(R.id.expandable_list_view_connection);
        adapter = new ListViewAdapater(context, new ArrayList<String>(), new ArrayList<String>(),
                new ArrayList<String>(), new ArrayList<String>());
        listView.setAdapter(adapter);
        listView.setOnChildClickListener(this);
        
        updateLists();
    }
    
    
    /**
     * Get the view of this panel
     */
    public View getView() {
        return this.view;
    }
    
    
    /**
     * Get the title of this panel
     */
    public String getTitle() {
        return "Connections";
    }
    
    
    private void updateLists() {
        PMP.get(activity.getApplication()).getResource(RG_IDENTIFIER, new PMPRequestResourceHandler() {
            
            @Override
            public void onReceiveResource(PMPResourceIdentifier resource, IBinder binder, boolean isMocked) {
                IConnection connectionStub = IConnection.Stub.asInterface(binder);
                fillLists(connectionStub);
            }
            
        });
    }
    
    
    /**
     * Fills the list with the information of the rg
     * 
     * @param connectionStub
     *            Stub of the connected connection rg
     */
    private void fillLists(final IConnection connectionStub) {
        
        new Thread() {
            
            @Override
            public void run() {
                handler.post(new Runnable() {
                    
                    public void run() {
                        // New lists
                        ArrayList<String> wifiList = new ArrayList<String>();
                        ArrayList<String> btList = new ArrayList<String>();
                        ArrayList<String> dataList = new ArrayList<String>();
                        ArrayList<String> cellPhoneList = new ArrayList<String>();
                        
                        // Fill the wifi list
                        if (PMP.get(activity.getApplication()).isServiceFeatureEnabled(Constants.CONNECTION_WIFI_INFO)) {
                            try {
                                //State
                                wifiList.add(context.getString(R.string.connection_panel_state) + " "
                                        + booleanToString(connectionStub.getWifiConnectionStatus()));
                                
                                // Configured networks
                                wifiList.add(context.getString(R.string.connection_panel_wifi_configured_networks)
                                        + " " + connectionStub.getConfigureddWifiNetworks().size());
                                
                                // Connected time
                                wifiList.add(context.getString(R.string.connection_panel_connected_twentyfour)
                                        + " "
                                        + convertMillsecondsToString(connectionStub
                                                .getWifiConnectionLastTwentyFourHours()));
                                wifiList.add(context.getString(R.string.connection_panel_connected_thirty_days) + " "
                                        + convertMillsecondsToString(connectionStub.getWifiConnectionLastMonth()));
                                
                                // Connected cities
                                wifiList.add(context.getString(R.string.connection_panel_connected_cities));
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        } else {
                            wifiList.add(context.getString(R.string.sf_insufficient));
                        }
                        
                        // Fill the bluetooth list
                        if (PMP.get(activity.getApplication()).isServiceFeatureEnabled(Constants.CONNECTION_BT_INFO)) {
                            try {
                                //State
                                btList.add(context.getString(R.string.connection_panel_state) + " "
                                        + booleanToString(connectionStub.getBluetoothStatus()));
                                
                                //Paired devices
                                btList.add(context.getString(R.string.connection_panel_paired_devices) + " "
                                        + connectionStub.getPairedBluetoothDevices().size());
                                
                                //Active time
                                btList.add(context.getString(R.string.connection_panel_active_twentyfour)
                                        + " "
                                        + convertMillsecondsToString(connectionStub
                                                .getBTConnectionLastTwentyFourHours()));
                                btList.add(context.getString(R.string.connection_panel_active_thirty_days) + " "
                                        + convertMillsecondsToString(connectionStub.getBTConnectionLastMonth()));
                                
                                // Cities
                                btList.add(" " + context.getString(R.string.connection_panel_connected_cities));
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        } else {
                            btList.add(context.getString(R.string.sf_insufficient));
                        }
                        
                        // Fill the cell phone list
                        if (PMP.get(activity.getApplication()).isServiceFeatureEnabled(Constants.CONNECTION_CELL_INFO)) {
                            try {
                                // Provider
                                cellPhoneList.add(context.getString(R.string.connection_panel_provider) + " "
                                        + connectionStub.getProvider());
                                
                                // Network type
                                String type = connectionStub.getNetworkType();
                                if (type.equals("unknown")) {
                                    cellPhoneList.add(context.getString(R.string.connection_panel_network_type) + " "
                                            + context.getString(R.string.connection_panel_network_type_unknown));
                                } else {
                                    cellPhoneList.add(context.getString(R.string.connection_panel_network_type) + " "
                                            + type);
                                }
                                
                                // Roaming status
                                cellPhoneList.add(context.getString(R.string.connection_panel_roaming) + " "
                                        + booleanToString(connectionStub.getRoamingStatus()));
                                
                                // Connection time
                                cellPhoneList.add(context
                                        .getString(R.string.connection_panel_active_twentyfour_flight_mode)
                                        + " "
                                        + convertMillsecondsToString(connectionStub
                                                .getAirplaneModeLastTwentyFourHours()));
                                cellPhoneList.add(context
                                        .getString(R.string.connection_panel_active_thirty_days_flight_mode)
                                        + " "
                                        + convertMillsecondsToString(connectionStub.getAirplaneModeLastMonth()));
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        } else {
                            cellPhoneList.add(context.getString(R.string.sf_insufficient));
                        }
                        
                        // Fill the data connection info
                        if (PMP.get(activity.getApplication()).isServiceFeatureEnabled(Constants.CONNECTION_DATA_INFO)) {
                            try {
                                // Status
                                dataList.add(context.getString(R.string.connection_panel_state) + " "
                                        + booleanToString(connectionStub.getDataConnectionStatus()));
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        } else {
                            dataList.add(" " + context.getString(R.string.sf_insufficient));
                        }
                        // Update the view
                        adapter.updateLists(wifiList, btList, dataList, cellPhoneList);
                    }
                });
            }
        }.start();
    }
    
    
    /**
     * Converts a boolean to active or not active
     * 
     * @param convert
     *            to convert
     * @return active iff convert =true else not active
     */
    private String booleanToString(boolean convert) {
        if (convert) {
            return context.getString(R.string.connection_panel_active);
        } else {
            return context.getString(R.string.connection_panel_not_active);
        }
    }
    
    
    /**
     * Converts a timespan in milliseconds into a String representation
     * 
     * @param ms
     *            timespan im milliseconds
     * @return String representation
     */
    private String convertMillsecondsToString(long ms) {
        String result = "";
        final long oneDay = 86400000L;
        final long oneHour = 3600000L;
        final long oneMinute = 60000L;
        
        // Calculate everything
        long days = ms / oneDay;
        long hours = ms / oneHour;
        long minutes = ms / oneMinute;
        
        // Add the days to the string
        if (days != 0) {
            result += days + " " + this.context.getResources().getQuantityString(R.plurals.days, (int) days);
        }
        
        // Add the hours
        if (hours != 0 || days != 0) {
            if (!result.equals("")) {
                result += " ";
            }
            result += hours % 24 + " "
                    + this.context.getResources().getQuantityString(R.plurals.hours, (int) hours % 24);
        }
        
        // Add the minutes
        if (minutes != 0 || days != 0 || hours != 0) {
            if (!result.equals("")) {
                result += " ";
            }
            result += minutes % 60 + " "
                    + this.context.getResources().getQuantityString(R.plurals.minutes, (int) minutes % 60);
        }
        
        // If the string is still empty, then it wasn't used yet
        if (result.equals("")) {
            result = this.context.getString(R.string.connection_panel_not_used);
        }
        return result;
    }
    
    
    /* (non-Javadoc)
     * @see android.widget.ExpandableListView.OnChildClickListener#onChildClick(android.widget.ExpandableListView, android.view.View, int, int, long)
     */
    public boolean onChildClick(ExpandableListView parent, View v, final int groupPosition, int childPosition, long id) {
        String clicked = "";
        
        // Try to get clicked object, should be a string
        try {
            clicked = (String) adapter.getChild(groupPosition, childPosition);
        } catch (ClassCastException e) {
            System.out.println("Something went wrong :(:" + e.getMessage());
        }
        
        // A city child was clicked
        if (clicked.contains(context.getString(R.string.connection_panel_connected_cities))) {
            PMP.get(activity.getApplication()).getResource(RG_IDENTIFIER, new PMPRequestResourceHandler() {
                
                @Override
                public void onReceiveResource(PMPResourceIdentifier resource, IBinder binder, boolean isMocked) {
                    IConnection connectionStub = IConnection.Stub.asInterface(binder);
                    List<String> cities = new ArrayList<String>();
                    switch (groupPosition) {
                        case 0:
                            try {
                                cities = connectionStub.getConnectedWifiCities();
                            } catch (RemoteException e) {
                                cities.add(context.getString(R.string.connection_panel_connected_cities_error));
                            }
                            break;
                        case 1:
                            try {
                                cities = connectionStub.getConnectedBTCities();
                            } catch (RemoteException e) {
                                cities.add(context.getString(R.string.connection_panel_connected_cities_error));
                            }
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                    }
                    
                    // No cities found
                    if (cities.size() == 0) {
                        cities.add(context.getString(R.string.connection_panel_connected_cities_not_found));
                    }
                    CharSequence[] items = cities.toArray(new CharSequence[cities.size()]);
                    
                    Looper.prepare();
                    // Create the dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(context.getString(R.string.connection_panel_connected_cities_dialog));
                    builder.setItems(items, null);
                    AlertDialog alert = builder.create();
                    
                    alert.show();
                    Looper.loop();
                }
            });
        }
        
        // Insufficient service features was clicked
        if (clicked.contains(context.getString(R.string.sf_insufficient))) {
            List<String> sfs = new ArrayList<String>();
            switch (groupPosition) {
                case 0:
                    sfs.add(Constants.CONNECTION_WIFI_INFO);
                    break;
                case 1:
                    sfs.add(Constants.CONNECTION_BT_INFO);
                    break;
                case 2:
                    sfs.add(Constants.CONNECTION_DATA_INFO);
                    break;
                case 3:
                    sfs.add(Constants.CONNECTION_CELL_INFO);
                    break;
            }
            PMP.get(activity.getApplication()).requestServiceFeatures(activity, sfs);
        }
        return true;
    }
    
    
    /**
     * Update the view
     */
    public void update() {
        updateLists();
    }
    
    
    /**
     * Upload the data to the evaluation server
     */
    public void upload(ProgressDialog dialog) {
        if (PMP.get(activity.getApplication()).isServiceFeatureEnabled(Constants.CONNECTION_STATISTICS)) {
            ConnectionUploadResourceHandler handler = new ConnectionUploadResourceHandler(dialog, this.activity);
            PMP.get(activity.getApplication()).getResource(RG_IDENTIFIER, handler);
        } else {
            dialog.dismiss();
            List<String> sfs = new ArrayList<String>();
            sfs.add("connections-statistics");
            PMP.get(this.activity.getApplication()).requestServiceFeatures(this.activity, sfs);
        }
    }
}
