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
package de.unistuttgart.ipvs.pmp.infoapp.webservice.events;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A battery event stores information about the state of the battery
 * at a given timestamp
 * 
 * @author Patrick Strobel
 */
public class BatteryEvent extends Event {
    
    public enum Adapter {
        AC,
        USB,
        NOT_PLUGGED
    };
    
    public enum Status {
        CHARGING,
        DISCHARGING,
        FULL,
        NOT_CHARGING,
        UNKNOWN
    }
    
    private int level;
    private int voltage;
    private Adapter plugged;
    private boolean present;
    private Status status;
    private float temperature;
    
    
    /**
     * Creates a new battery event
     * 
     * @param timestamp
     *            Point in time when this event occurred
     * @param level
     *            The battery's level in percent
     * @param voltage
     *            The battery's voltage in mV
     * @param plugged
     *            Power status
     * @param present
     *            True, if the battery is installed in the device
     * @param status
     *            Charging status
     * @param temperature
     *            The battery's temperature
     */
    public BatteryEvent(long timestamp, int level, int voltage, Adapter plugged, boolean present, Status status,
            float temperature) {
        super(timestamp);
        
        this.level = level;
        this.voltage = voltage;
        this.plugged = plugged;
        this.present = present;
        this.status = status;
        this.temperature = temperature;
    }
    
    
    /**
     * Creates a new battery event
     * 
     * @param id
     *            The event's ID
     * @param timestamp
     *            Point in time when this event occurred
     * @param level
     *            The battery's level
     * @param voltage
     *            The battery's voltage in mV
     * @param plugged
     *            Power status
     * @param present
     *            True, if the battery is installed in the device
     * @param status
     *            Charging status
     * @param temperature
     *            The battery's temperature
     */
    public BatteryEvent(int id, long timestamp, int level, int voltage, Adapter plugged, boolean present,
            Status status, float temperature) {
        super(id, timestamp);
        
        this.level = level;
        this.voltage = voltage;
        this.plugged = plugged;
        this.present = present;
        this.status = status;
        this.temperature = temperature;
    }
    
    
    /**
     * Gets the battery's level
     * 
     * @return Level in percent
     */
    public int getLevel() {
        return this.level;
    }
    
    
    /**
     * Gets the battery's voltage
     * 
     * @return Voltage in mV
     */
    public int getVoltage() {
        return this.voltage;
    }
    
    
    /**
     * Gets the ac-adapter status
     * 
     * @return Adapter status
     */
    public Adapter getPlugged() {
        return this.plugged;
    }
    
    
    /**
     * Gets the installation status of the battery
     * 
     * @return True, if the battery is installed in the device
     */
    public boolean isPresent() {
        return this.present;
    }
    
    
    /**
     * Gets the charging status
     * 
     * @return Charging status
     */
    public Status getStatus() {
        return this.status;
    }
    
    
    /**
     * Gets the battery's temperature
     * 
     * @return Temperature
     */
    public float getTemperature() {
        return this.temperature;
    }
    
    
    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject json = super.toJSONObject();
        json.put("level", this.level);
        json.put("voltage", this.voltage);
        
        switch (this.plugged) {
            case AC:
                json.put("plugged", "a");
                break;
            case NOT_PLUGGED:
                json.put("plugged", "n");
                break;
            case USB:
                json.put("plugged", "u");
                break;
        }
        
        json.put("present", this.present);
        
        switch (this.status) {
            case CHARGING:
                json.put("status", "c");
                break;
            case DISCHARGING:
                json.put("status", "d");
                break;
            case FULL:
                json.put("status", "f");
                break;
            case NOT_CHARGING:
                json.put("status", "n");
                break;
            case UNKNOWN:
                json.put("status", "u");
                break;
        }
        
        json.put("temperature", this.temperature);
        return json;
    }
    
}
