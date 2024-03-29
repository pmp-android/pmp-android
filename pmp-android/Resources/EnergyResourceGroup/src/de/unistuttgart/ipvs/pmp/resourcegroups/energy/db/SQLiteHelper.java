/*
 * Copyright 2012 pmp-android development team
 * Project: EnergyResourceGroup
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
package de.unistuttgart.ipvs.pmp.resourcegroups.energy.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.unistuttgart.ipvs.pmp.resourcegroups.energy.EnergyConstants;

/**
 * This is the sqlite helper class for creating tables for battery events and device data
 * 
 * @author Marcus Vetter
 * 
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    
    /**
     * Statement to create the battery table
     */
    private static final String CREATE_TABLE_BATTERY = "CREATE TABLE IF NOT EXISTS " + DBConstants.TABLE_BATTERY
            + " ( " + DBConstants.TABLE_BATTERY_COL_ID + " integer primary key autoincrement, "
            + DBConstants.TABLE_BATTERY_COL_TIMESTAMP + " integer, " + DBConstants.TABLE_BATTERY_COL_LEVEL
            + " integer, " + DBConstants.TABLE_BATTERY_COL_HEALTH + " text, " + DBConstants.TABLE_BATTERY_COL_STATUS
            + " text, " + DBConstants.TABLE_BATTERY_COL_PLUGGED + " text, " + DBConstants.TABLE_BATTERY_COL_PRESENT
            + " integer, " + DBConstants.TABLE_BATTERY_COL_TECHNOLOGY + " text, "
            + DBConstants.TABLE_BATTERY_COL_TEMPERATURE + " real, " + DBConstants.TABLE_BATTERY_COL_VOLTAGE
            + " integer );";
    
    /**
     * Statement to create the device data table
     */
    private static final String CREATE_TABLE_DEVICE_DATA = "CREATE TABLE IF NOT EXISTS "
            + DBConstants.TABLE_DEVICE_DATA + " ( " + DBConstants.TABLE_DEVICE_DATA_COL_KEY + " text primary key, "
            + DBConstants.TABLE_DEVICE_DATA_COL_VALUE + " integer );";
    
    
    public SQLiteHelper(Context context) {
        super(context, DBConstants.DATABASE_NAME, null, DBConstants.DATABASE_VERSION);
    }
    
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BATTERY);
        db.execSQL(CREATE_TABLE_DEVICE_DATA);
        insertKeysOfDeviceData(db);
    }
    
    
    private void insertKeysOfDeviceData(SQLiteDatabase db) {
        /*
         * Create the content values
         */
        ContentValues cvs1 = new ContentValues();
        cvs1.put(DBConstants.TABLE_DEVICE_DATA_COL_KEY, DBConstants.TABLE_DEVICE_DATA_KEY_DEVICE_ON_FLAG);
        cvs1.put(DBConstants.TABLE_DEVICE_DATA_COL_VALUE, 1);
        
        ContentValues cvs2 = new ContentValues();
        cvs2.put(DBConstants.TABLE_DEVICE_DATA_COL_KEY, DBConstants.TABLE_DEVICE_DATA_KEY_LAST_BOOT_DATE);
        cvs2.put(DBConstants.TABLE_DEVICE_DATA_COL_VALUE, System.currentTimeMillis());
        
        ContentValues cvs3 = new ContentValues();
        cvs3.put(DBConstants.TABLE_DEVICE_DATA_COL_KEY, DBConstants.TABLE_DEVICE_DATA_KEY_FIRST_MEASUREMENT_DATE);
        cvs3.put(DBConstants.TABLE_DEVICE_DATA_COL_VALUE, System.currentTimeMillis());
        
        ContentValues cvs4 = new ContentValues();
        cvs4.put(DBConstants.TABLE_DEVICE_DATA_COL_KEY, DBConstants.TABLE_DEVICE_DATA_KEY_TOTAL_UPTIME);
        cvs4.put(DBConstants.TABLE_DEVICE_DATA_COL_VALUE, 0);
        
        ContentValues cvs5 = new ContentValues();
        cvs5.put(DBConstants.TABLE_DEVICE_DATA_COL_KEY, DBConstants.TABLE_DEVICE_DATA_KEY_LAST_SCREEN_ON_DATE);
        cvs5.put(DBConstants.TABLE_DEVICE_DATA_COL_VALUE, System.currentTimeMillis());
        
        ContentValues cvs6 = new ContentValues();
        cvs6.put(DBConstants.TABLE_DEVICE_DATA_COL_KEY, DBConstants.TABLE_DEVICE_DATA_KEY_SCREEN_ON_TIME);
        cvs6.put(DBConstants.TABLE_DEVICE_DATA_COL_VALUE, 0);
        
        ContentValues cvs7 = new ContentValues();
        cvs7.put(DBConstants.TABLE_DEVICE_DATA_COL_KEY, DBConstants.TABLE_DEVICE_DATA_KEY_LAST_BOOT_SCREEN_ON_TIME);
        cvs7.put(DBConstants.TABLE_DEVICE_DATA_COL_VALUE, 0);
        
        List<ContentValues> cvsList = new ArrayList<ContentValues>();
        cvsList.add(cvs1);
        cvsList.add(cvs2);
        cvsList.add(cvs3);
        cvsList.add(cvs4);
        cvsList.add(cvs5);
        cvsList.add(cvs6);
        cvsList.add(cvs7);
        
        for (ContentValues cvs : cvsList) {
            db.insert(DBConstants.TABLE_DEVICE_DATA, null, cvs);
        }
        
    }
    
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(EnergyConstants.LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.TABLE_BATTERY);
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.TABLE_DEVICE_DATA);
        onCreate(db);
    }
    
}
