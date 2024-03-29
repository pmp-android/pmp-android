/*
 * Copyright 2012 pmp-android development team
 * Project: ConnectionResourceGroup
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
package de.unistuttgart.ipvs.pmp.resourcegroups.connection.resource;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import de.unistuttgart.ipvs.pmp.infoapp.graphs.UrlBuilder;
import de.unistuttgart.ipvs.pmp.infoapp.graphs.UrlBuilder.Views;
import de.unistuttgart.ipvs.pmp.infoapp.webservice.Service;
import de.unistuttgart.ipvs.pmp.infoapp.webservice.eventmanager.CellularConnectionEventManager;
import de.unistuttgart.ipvs.pmp.infoapp.webservice.eventmanager.ConnectionEventManager;
import de.unistuttgart.ipvs.pmp.infoapp.webservice.events.CellularConnectionEvent;
import de.unistuttgart.ipvs.pmp.infoapp.webservice.events.ConnectionEvent;
import de.unistuttgart.ipvs.pmp.infoapp.webservice.exceptions.InternalDatabaseException;
import de.unistuttgart.ipvs.pmp.infoapp.webservice.exceptions.InvalidEventOrderException;
import de.unistuttgart.ipvs.pmp.infoapp.webservice.exceptions.InvalidParameterException;
import de.unistuttgart.ipvs.pmp.infoapp.webservice.properties.CellularConnectionProperties;
import de.unistuttgart.ipvs.pmp.infoapp.webservice.properties.CellularConnectionProperties.NetworkTypes;
import de.unistuttgart.ipvs.pmp.infoapp.webservice.properties.ConnectionProperties;
import de.unistuttgart.ipvs.pmp.resource.ResourceGroup;
import de.unistuttgart.ipvs.pmp.resourcegroups.connection.ConnectionConstants;
import de.unistuttgart.ipvs.pmp.resourcegroups.connection.IConnection;
import de.unistuttgart.ipvs.pmp.resourcegroups.connection.database.DBConnector;
import de.unistuttgart.ipvs.pmp.resourcegroups.connection.database.DBConstants;

/**
 * Implements the IConnection aidl file
 * 
 * @author Thorsten Berberich
 * 
 */
public class ConnectionImpl extends IConnection.Stub {
    
    /**
     * Context of the RG
     */
    private Context context;
    
    /**
     * {@link PermissionValidator}
     */
    private PermissionValidator validator;
    
    /**
     * Stores if {@link Looper#prepare()} was called or not
     */
    Boolean looped = false;
    
    
    /**
     * Constructor to get a context
     * 
     * @param context
     *            {@link Context} of the rg
     * @param rg
     *            {@link ResourceGroup}
     * @param appIdentifier
     *            identifier of the app that wants to do sth.
     * 
     */
    public ConnectionImpl(Context context, ResourceGroup rg, String appIdentifier) {
        this.context = context;
        this.validator = new PermissionValidator(rg, appIdentifier);
    }
    
    
    /* (non-Javadoc)
     * @see de.unistuttgart.ipvs.pmp.resourcegroups.connection.IConnection#getWifiConnectionStatus()
     */
    @Override
    public boolean getWifiConnectionStatus() throws RemoteException {
        // Check the privacy setting
        this.validator.validate(ConnectionConstants.PS_WIFI_STATUS, "true");
        
        boolean result = false;
        
        ConnectivityManager connManager = (ConnectivityManager) this.context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (connManager != null) {
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWifi == null) {
                return false;
            } else {
                result = mWifi.isConnected();
            }
        }
        return result;
    }
    
    
    /* (non-Javadoc)
     * @see de.unistuttgart.ipvs.pmp.resourcegroups.connection.IConnection#getWifiConnectionLastTwentyFourHours()
     */
    @Override
    public long getWifiConnectionLastTwentyFourHours() throws RemoteException {
        // Check the privacy setting
        this.validator.validate(ConnectionConstants.PS_WIFI_STATUS, "true");
        
        long result = DBConnector.getInstance(this.context).getTimeDuration(DBConstants.TABLE_WIFI,
                ConnectionConstants.ONE_DAY);
        return result;
    }
    
    
    /* (non-Javadoc)
     * @see de.unistuttgart.ipvs.pmp.resourcegroups.connection.IConnection#getWifiConnectionLastMonth()
     */
    @Override
    public long getWifiConnectionLastMonth() throws RemoteException {
        // Check the privacy setting
        this.validator.validate(ConnectionConstants.PS_WIFI_STATUS, "true");
        
        long result = DBConnector.getInstance(this.context).getTimeDuration(DBConstants.TABLE_WIFI,
                ConnectionConstants.ONE_MONTH);
        return result;
    }
    
    
    /* (non-Javadoc)
     * @see de.unistuttgart.ipvs.pmp.resourcegroups.connection.IConnection#getConfigureddWifiNetworks()
     */
    @Override
    public List<String> getConfigureddWifiNetworks() throws RemoteException {
        // Check the privacy setting
        this.validator.validate(ConnectionConstants.PS_CONFIGURED_NETWORKS, "true");
        
        List<String> result = new ArrayList<String>();
        
        // Get the wifi manager
        WifiManager wifi = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        
        if (wifi != null) {
            List<WifiConfiguration> configs = wifi.getConfiguredNetworks();
            
            // Iterate over all wifi configurations to get the SSIDs
            for (WifiConfiguration config : configs) {
                result.add(config.SSID);
            }
        }
        return result;
    }
    
    
    /* (non-Javadoc)
     * @see de.unistuttgart.ipvs.pmp.resourcegroups.connection.IConnection#getConnectedWifiCities()
     */
    @Override
    public List<String> getConnectedWifiCities() throws RemoteException {
        // Check the privacy setting
        this.validator.validate(ConnectionConstants.PS_WIFI_CONNECTED_CITIES, "true");
        
        List<String> result = DBConnector.getInstance(this.context).getConnectedCities(DBConstants.TABLE_WIFI);
        return result;
    }
    
    
    /* (non-Javadoc)
     * @see de.unistuttgart.ipvs.pmp.resourcegroups.connection.IConnection#getBluetoothStatus()
     */
    @Override
    public boolean getBluetoothStatus() throws RemoteException {
        // Check the privacy setting
        this.validator.validate(ConnectionConstants.PS_BLUETOOTH_STATUS, "true");
        
        Boolean result = false;
        
        try {
            if (!this.looped) {
                Looper.prepare();
                this.looped = true;
            }
        } catch (RuntimeException e) {
        }
        
        // Check if the BluetoothAdapter is supported
        if (BluetoothAdapter.getDefaultAdapter() != null) {
            result = BluetoothAdapter.getDefaultAdapter().isEnabled();
        }
        return result;
    }
    
    
    /* (non-Javadoc)
     * @see de.unistuttgart.ipvs.pmp.resourcegroups.connection.IConnection#getPairedBluetoothDevices()
     */
    @Override
    public List<String> getPairedBluetoothDevices() throws RemoteException {
        // Check the privacy setting
        this.validator.validate(ConnectionConstants.PS_BLUETOOTH_DEVICES, "true");
        
        List<String> result = new ArrayList<String>();
        
        try {
            if (!this.looped) {
                Looper.prepare();
                this.looped = true;
            }
        } catch (RuntimeException e) {
        }
        
        // Check if the BluetoothAdapter is supported
        if (BluetoothAdapter.getDefaultAdapter() != null) {
            Set<BluetoothDevice> devices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
            if (devices != null) {
                for (BluetoothDevice device : devices) {
                    result.add(device.getName());
                }
            }
        }
        return result;
    }
    
    
    /* (non-Javadoc)
     * @see de.unistuttgart.ipvs.pmp.resourcegroups.connection.IConnection#getBTConnectionLastTwentyFourHours()
     */
    @Override
    public long getBTConnectionLastTwentyFourHours() throws RemoteException {
        // Check the privacy setting
        this.validator.validate(ConnectionConstants.PS_BLUETOOTH_STATUS, "true");
        
        long result = DBConnector.getInstance(this.context).getTimeDuration(DBConstants.TABLE_BT,
                ConnectionConstants.ONE_DAY);
        return result;
    }
    
    
    /* (non-Javadoc)
     * @see de.unistuttgart.ipvs.pmp.resourcegroups.connection.IConnection#getBTConnectionLastMonth()
     */
    @Override
    public long getBTConnectionLastMonth() throws RemoteException {
        // Check the privacy setting
        this.validator.validate(ConnectionConstants.PS_BLUETOOTH_STATUS, "true");
        
        long result = DBConnector.getInstance(this.context).getTimeDuration(DBConstants.TABLE_BT,
                ConnectionConstants.ONE_MONTH);
        return result;
    }
    
    
    /* (non-Javadoc)
     * @see de.unistuttgart.ipvs.pmp.resourcegroups.connection.IConnection#getConnectedBTCities()
     */
    @Override
    public List<String> getConnectedBTCities() throws RemoteException {
        // Check the privacy setting
        this.validator.validate(ConnectionConstants.PS_BT_CONNECTED_CITIES, "true");
        
        List<String> result = DBConnector.getInstance(this.context).getConnectedCities(DBConstants.TABLE_BT);
        return result;
    }
    
    
    /* (non-Javadoc)
     * @see de.unistuttgart.ipvs.pmp.resourcegroups.connection.IConnection#getDataConnectionStatus()
     */
    @Override
    public boolean getDataConnectionStatus() throws RemoteException {
        // Check the privacy setting
        this.validator.validate(ConnectionConstants.PS_DATA_STATUS, "true");
        
        // Get the telephony manager
        TelephonyManager manager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
        if (manager != null) {
            //Get the data state
            int state = manager.getDataState();
            switch (state) {
                case TelephonyManager.DATA_DISCONNECTED:
                    return false;
                case TelephonyManager.DATA_CONNECTED:
                    return true;
                case TelephonyManager.DATA_CONNECTING:
                    return true;
            }
        }
        return false;
    }
    
    
    /* (non-Javadoc)
     * @see de.unistuttgart.ipvs.pmp.resourcegroups.connection.IConnection#getProvider()
     */
    @Override
    public String getProvider() throws RemoteException {
        // Check the privacy setting
        this.validator.validate(ConnectionConstants.PS_CELL_STATUS, "true");
        
        // Get the telephony manager
        TelephonyManager manager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
        if (manager != null) {
            if (manager.getNetworkOperatorName().equals("")) {
                return "-";
            }
            return manager.getNetworkOperatorName();
        }
        return "-";
    }
    
    
    /* (non-Javadoc)
     * @see de.unistuttgart.ipvs.pmp.resourcegroups.connection.IConnection#getRoamingStatus()
     */
    @Override
    public boolean getRoamingStatus() throws RemoteException {
        // Check the privacy setting
        this.validator.validate(ConnectionConstants.PS_CELL_STATUS, "true");
        
        boolean result = false;
        
        // Get the telephony manager
        TelephonyManager manager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
        if (manager != null) {
            result = manager.isNetworkRoaming();
        }
        return result;
    }
    
    
    /* (non-Javadoc)
     * @see de.unistuttgart.ipvs.pmp.resourcegroups.connection.IConnection#getNetworkType()
     */
    @Override
    public String getNetworkType() throws RemoteException {
        TelephonyManager tManager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
        int type = tManager.getNetworkType();
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "unknown";
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "EVDO 0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "EVDO A";
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "1xRTT";
        }
        return "unknown";
    }
    
    
    /* (non-Javadoc)
     * @see de.unistuttgart.ipvs.pmp.resourcegroups.connection.IConnection#getAirplaneModeLastTwentyFourHours()
     */
    @Override
    public long getAirplaneModeLastTwentyFourHours() throws RemoteException {
        // Check the privacy setting
        this.validator.validate(ConnectionConstants.PS_CELL_STATUS, "true");
        
        long result = DBConnector.getInstance(this.context).getTimeDuration(DBConstants.TABLE_CELL,
                ConnectionConstants.ONE_DAY);
        return result;
    }
    
    
    /* (non-Javadoc)
     * @see de.unistuttgart.ipvs.pmp.resourcegroups.connection.IConnection#getAirplaneModeLastMonth()
     */
    @Override
    public long getAirplaneModeLastMonth() throws RemoteException {
        // Check the privacy setting
        this.validator.validate(ConnectionConstants.PS_CELL_STATUS, "true");
        
        long result = DBConnector.getInstance(this.context).getTimeDuration(DBConstants.TABLE_CELL,
                ConnectionConstants.ONE_MONTH);
        return result;
    }
    
    
    /* (non-Javadoc)
     * @see de.unistuttgart.ipvs.pmp.resourcegroups.connection.IConnection#uploadData()
     */
    @Override
    public String uploadData() throws RemoteException {
        // Check the privacy setting 
        this.validator.validate(ConnectionConstants.PS_UPLOAD_DATA, "true");
        
        // Get the device id
        TelephonyManager tManager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
        
        String deviceId = tManager.getDeviceId();
        MessageDigest digest;
        String hashedID = "";
        
        // Hash the id with MD5
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(deviceId.getBytes(), 0, deviceId.length());
            hashedID = new BigInteger(1, digest.digest()).toString(16);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        
        // Create service
        Service service = new Service(Service.DEFAULT_URL, hashedID);
        try {
            // Try to upload the wifi and bluetooth events
            List<ConnectionEvent> events = getConnectionEvents();
            
            if (events.size() > 0) {
                new ConnectionEventManager(service).commitEvents(events);
                DBConnector.getInstance(this.context).clearWifiAndBTLists();
            }
        } catch (InternalDatabaseException e1) {
            e1.printStackTrace();
            return null;
        } catch (InvalidParameterException e1) {
            e1.printStackTrace();
            return null;
        } catch (InvalidEventOrderException e1) {
            e1.printStackTrace();
            return null;
        } catch (IOException e1) {
            e1.printStackTrace();
            return null;
        }
        
        try {
            // Upload the cellular connection events
            List<CellularConnectionEvent> events = DBConnector.getInstance(this.context).getCellEvents();
            
            if (events.size() > 0) {
                new CellularConnectionEventManager(service).commitEvents(events);
                DBConnector.getInstance(this.context).clearCellList();
            }
        } catch (InternalDatabaseException e1) {
            e1.printStackTrace();
            return null;
        } catch (InvalidParameterException e1) {
            e1.printStackTrace();
            return null;
        } catch (InvalidEventOrderException e1) {
            e1.printStackTrace();
            return null;
        } catch (IOException e1) {
            e1.printStackTrace();
            return null;
        }
        
        try {
            // The cellular and connection properties
            String provider = getProvider();
            if (provider.equals("-")) {
                provider = "unknown";
            }
            new CellularConnectionProperties(service, provider, getRoamingStatus(), getNetworkTypeEnum()).commit();
            
            Integer configNetworks = getConfigureddWifiNetworks().size();
            Integer pairedDevices = getPairedBluetoothDevices().size();
            new ConnectionProperties(service, configNetworks.shortValue(), pairedDevices.shortValue()).commit();
        } catch (InternalDatabaseException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidParameterException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidEventOrderException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        
        // Get the api level to check if svg is support by the browser
        int apiLevel = Integer.valueOf(android.os.Build.VERSION.SDK);
        if (apiLevel >= 11) {
            UrlBuilder urlB = new UrlBuilder(UrlBuilder.DEFAULT_URL, hashedID);
            urlB.setView(Views.DYNAMIC);
            return urlB.getConnectionGraphUrl();
        } else {
            UrlBuilder urlB = new UrlBuilder(UrlBuilder.DEFAULT_URL, hashedID);
            urlB.setView(Views.STATIC);
            return urlB.getConnectionGraphUrl();
        }
    }
    
    
    /**
     * Get the network typ as a {@link NetworkTypes} enumeration
     * 
     * @return {@link NetworkTypes}
     */
    public NetworkTypes getNetworkTypeEnum() {
        TelephonyManager tManager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
        int type = tManager.getNetworkType();
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return NetworkTypes.UNKNOWN;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return NetworkTypes.GPRS;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return NetworkTypes.EDGE;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return NetworkTypes.UMTS;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return NetworkTypes.HSDPA;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return NetworkTypes.HSUPA;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return NetworkTypes.HSPA;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return NetworkTypes.CDMA;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return NetworkTypes.EVDO_0;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return NetworkTypes.EVDO_A;
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return NetworkTypes.RTT;
        }
        return NetworkTypes.UNKNOWN;
    }
    
    
    /**
     * Get the connection events of wifi and bluetooth in one list, sorted ascending to the timestamp
     * 
     * @return List of {@link ConnectionEvent}s
     */
    private List<ConnectionEvent> getConnectionEvents() {
        // Get all events
        List<ConnectionEvent> wlan = DBConnector.getInstance(this.context).getWifiEvents();
        List<ConnectionEvent> bt = DBConnector.getInstance(this.context).getBluetoothEvents();
        
        // Join the 2 lists
        List<ConnectionEvent> join = new ArrayList<ConnectionEvent>();
        join.addAll(wlan);
        join.addAll(bt);
        
        // Sort the lists
        Collections.sort(join, new ConnectionEventComparator());
        return join;
    }
    
    
    /**
     * Only for debugging
     * 
     * @param events
     */
    @SuppressWarnings("unused")
    private void printCellularConnectionEvents(List<CellularConnectionEvent> events) {
        long lastTimestamp = 0L;
        for (CellularConnectionEvent event : events) {
            System.out.println("ID: \t" + event.getId());
            System.out.println("Time: \t" + event.getTimestamp());
            System.out.println("Airplane: \t" + event.isAirplane());
            if (event.getTimestamp() - lastTimestamp >= 0) {
                System.out.println("Greater than last: \t" + "true (" + (event.getTimestamp() - lastTimestamp) + ")");
            } else {
                System.out.println("Greater than last: \t" + "false (" + (event.getTimestamp() - lastTimestamp) + ")");
            }
            lastTimestamp = event.getTimestamp();
            System.out.println("-----------------------------------------");
        }
    }
    
    
    /**
     * Only for debugging
     * 
     * @param events
     */
    @SuppressWarnings("unused")
    private void printConectionEvents(List<ConnectionEvent> events) {
        long lastTimestamp = 0;
        for (ConnectionEvent event : events) {
            System.out.println("ID: \t" + event.getId());
            System.out.println("Time: \t" + event.getTimestamp());
            System.out.println("Medium: \t" + event.getMedium());
            System.out.println("Connected: \t" + event.isConnected());
            System.out.println("Enabled: \t" + event.isEnabled());
            if (event.getCity() != null) {
                System.out.println("City: \t" + event.getCity());
            } else {
                System.out.println("City: \t" + "null");
            }
            if (event.getTimestamp() - lastTimestamp >= 0) {
                System.out.println("Greater than last: \t" + "true (" + (event.getTimestamp() - lastTimestamp) + ")");
            } else {
                System.out.println("Greater than last: \t" + "false (" + (event.getTimestamp() - lastTimestamp) + ")");
            }
            lastTimestamp = event.getTimestamp();
            System.out.println("-----------------------------------------");
        }
    }
}
