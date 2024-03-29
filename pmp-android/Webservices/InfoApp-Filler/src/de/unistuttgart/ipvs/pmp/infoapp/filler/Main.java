/*
 * Copyright 2012 pmp-android development team
 * Project: InfoApp-Filler
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
package de.unistuttgart.ipvs.pmp.infoapp.filler;

import java.util.Calendar;

import de.unistuttgart.ipvs.pmp.infoapp.webservice.Service;

/**
 * The InfoApp-Filler project is used to fill the webservice' database with randomly generated data.
 * 
 * This had to be done to be able to create the PHP-scripts that visualizes the data, since no data
 * had been available when the scripts where implemented.
 * 
 * This project serves as an example of the usage of the InfoApp-CommunicationLib (for the team-members responsible for
 * the implementation of InfoApp's resource groups)
 * 
 * @author Patrick Strobel
 */
public class Main {
    
    /**
     * Generates and sends data for/to the different upload-webservices.
     * 
     * @param s
     *            Service, used to commit the data
     */
    public static void doFill(Service s) {
        // Create range
        Calendar from = Calendar.getInstance();
        from.set(2011, 12, 1, 00, 00, 00);
        
        Calendar to = Calendar.getInstance();
        to.set(2013, 1, 31, 23, 59, 59);
        
        // Create fillers
        Awake aw = new Awake(s, from.getTimeInMillis(), to.getTimeInMillis());
        Battery bat = new Battery(s, from.getTimeInMillis(), to.getTimeInMillis());
        Connection con = new Connection(s, from.getTimeInMillis(), to.getTimeInMillis());
        CellularConnection cellCon = new CellularConnection(s, from.getTimeInMillis(), to.getTimeInMillis());
        Screen scr = new Screen(s, from.getTimeInMillis(), to.getTimeInMillis());
        Profile pro = new Profile(s, from.getTimeInMillis(), to.getTimeInMillis());
        
        // Fill tables
        System.out.println("Fill DB with randomly generated events");
        System.out.println("--------------------------------------");
        System.out.println("Awake events:");
        aw.fill();
        System.out.println("Battery events:");
        bat.fill();
        System.out.println("Connection events:");
        con.fill();
        System.out.println("Cellular connection events:");
        cellCon.fill();
        System.out.println("Screen events:");
        scr.fill();
        System.out.println("Profile events:");
        pro.fill();
    }
    
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        // Create service
        Service s = new Service(Service.DEFAULT_URL, "f2305a2fbef51bd82008c7cf3788250f");
        
        doFill(s);
        
        /**
         * The code bellow is used for testing some aspects of the webservice and as an example of the ComLib usage.
         * Thus, it's commended-out
         */
        /*
        // Setup service url and device ID
        //Service s = new Service("http://localhost/infoapp/src/json", "b7c2e4787e7f950c89909795907208da");
        //Service s = new Service(Service.DEFAULT_URL, "b7c2e4787e7f950c89909795907208d3");
        CellularConnectionProperties ccp = new CellularConnectionProperties(s, "O2", false, NetworkTypes.HSPAP);
        try {
            ccp.commit();
            ConnectionEventManager cem = new ConnectionEventManager(s);
            EventProperty ep = cem.getLastEventProperty();
            System.out.println("Last ID: " + ep.getId());
            System.out.println("Last Timestamp: " + ep.getTimestamp());
        } catch (InternalDatabaseException e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
        } catch (InvalidParameterException e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
        } catch (IOException e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
        }
        
        // Create some events and...
        ConnectionEvent e1 = new ConnectionEvent(2123, ConnectionEvent.Mediums.BLUETOOTH, true, true, null);
        ConnectionEvent e2 = new ConnectionEvent(1337, ConnectionEvent.Mediums.WIFI, false, false, "Stuttgart");
        
        // ...bind them to a list
        List<ConnectionEvent> events = new ArrayList<ConnectionEvent>();
        events.add(e1);
        events.add(e2);
        
        // Contact the service and upload the events
        ConnectionEventManager cem = new ConnectionEventManager(s);
        try {
            EventProperty ep = cem.getLastEventProperty();
            System.out.println("Last ID: " + ep.getId());
            System.out.println("Last Timestamp: " + ep.getTimestamp());
            cem.commitEvents(events);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // Set device properties
        DeviceOem deviceOem = new DeviceOem("Motorola", "Defy", "Motoblur");
        Display display = new Display((short) 800, (short) 600);
        Memory memoryInt = new Memory((short) 2048, (short) 123);
        Memory memoryExt = new Memory((short) 8192, (short) 1024);
        String[] sensors = { "compass", "gps", "tilt" };
        DeviceProperties dp = new DeviceProperties(s, deviceOem, (byte) 7, "2.6", display, (short) 800, memoryInt,
                memoryExt, 3.8f, sensors, 12345.76f);
        try {
            dp.commit();
        } catch (InternalDatabaseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        */
    }
}
