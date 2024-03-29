/*
 * Copyright 2012 pmp-android development team
 * Project: InfoApp-CommunicationLib
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
package de.unistuttgart.ipvs.pmp.infoapp.webservice.properties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import de.unistuttgart.ipvs.pmp.infoapp.webservice.Service;
import de.unistuttgart.ipvs.pmp.infoapp.webservice.exceptions.InternalDatabaseException;
import de.unistuttgart.ipvs.pmp.infoapp.webservice.exceptions.InvalidParameterException;

/**
 * Stores information about the device's short range connection and allows to update or insert a new device information
 * set
 * 
 * @author Patrick Strobel
 */
public class ConnectionProperties extends Properties {
    
    private short wifiCount;
    private short bluetoothCount;
    
    
    /**
     * 
     * @param service
     *            Helper class used for communication with the webservice
     * @param wifiCount
     *            Number of wifi-networks the device has been connected to
     * @param bluetoothCount
     *            Number of bluetooth devices this device has been paired with
     */
    public ConnectionProperties(Service service, short wifiCount, short bluetoothCount) {
        super(service);
        this.wifiCount = wifiCount;
        this.bluetoothCount = bluetoothCount;
    }
    
    
    /**
     * Gets the number of wifi-networks the device has been connected to
     * 
     * @return Number of wifis
     */
    public short getWifiCount() {
        return this.wifiCount;
    }
    
    
    /**
     * Gets the number of bluetooth device the device has paired with
     * 
     * @return Number of bluetooth devices
     */
    public short getBluetoothCount() {
        return this.bluetoothCount;
    }
    
    
    @Override
    public void commit() throws InternalDatabaseException, InvalidParameterException, IOException {
        try {
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("wifi", Short.toString(this.wifiCount)));
            params.add(new BasicNameValuePair("bluetooth", Short.toString(this.bluetoothCount)));
            super.service.requestPostService("update_connection.php", params);
        } catch (JSONException e) {
            throw new IOException("Server returned no valid JSON object: " + e);
        }
    }
    
}
