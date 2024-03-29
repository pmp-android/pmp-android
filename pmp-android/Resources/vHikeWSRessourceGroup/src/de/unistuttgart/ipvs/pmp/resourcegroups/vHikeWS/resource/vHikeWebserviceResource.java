/*
 * Copyright 2012 pmp-android development team
 * Project: vHikeWSRessourceGroup
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
package de.unistuttgart.ipvs.pmp.resourcegroups.vHikeWS.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.IBinder;
import android.os.RemoteException;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.resource.Resource;
import de.unistuttgart.ipvs.pmp.resourcegroups.vHikeWS.vHikeWSResourceGroup;

public class vHikeWebserviceResource extends Resource {
    
    static List<ParamObject> listToParse = new ArrayList<ParamObject>();
    
    vHikeWSResourceGroup vHikeRG;
    
    
    public vHikeWebserviceResource(vHikeWSResourceGroup vHikeRG) {
        this.vHikeRG = vHikeRG;
    }
    
    
    @Override
    public IBinder getAndroidInterface(String appIdentifier) {
        return new vHikeWebserviceImpl(this.vHikeRG, this, appIdentifier);
    }
    
    
    @Override
    public IBinder getMockedAndroidInterface(String appIdentifier) {
        return new vHikeWebserviceMockImpl(this.vHikeRG, this, appIdentifier);
    }
    
    
    @Override
    public IBinder getCloakedAndroidInterface(String appIdentifier) {
        return new vHikeWebserviceCloakImpl(this.vHikeRG, this, appIdentifier);
    }
    
    
    public String register(String username, String password, String email, String firstname, String lastname,
            String tel, String description, boolean email_public, boolean firstname_public, boolean lastname_public,
            boolean tel_public) throws RemoteException {
        
        listToParse.clear();
        listToParse.add(new ParamObject("username", username, true));
        listToParse.add(new ParamObject("password", password, true));
        listToParse.add(new ParamObject("email", email, true));
        listToParse.add(new ParamObject("firstname", firstname, true));
        listToParse.add(new ParamObject("lastname", lastname, true));
        listToParse.add(new ParamObject("tel", tel, true));
        listToParse.add(new ParamObject("email_public", String.valueOf(email_public), true));
        listToParse.add(new ParamObject("firstname_public", String.valueOf(firstname_public), true));
        listToParse.add(new ParamObject("lastname_public", String.valueOf(lastname_public), true));
        listToParse.add(new ParamObject("tel_public", String.valueOf(tel_public), true));
        
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "register.php").toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String login(String username, String pw) throws RemoteException {
        listToParse.clear();
        listToParse.add(new ParamObject("username", username, true));
        listToParse.add(new ParamObject("password", pw, true));
        
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "login.php").toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String logout(String session_id) throws RemoteException {
        listToParse.clear();
        listToParse.add(new ParamObject("sid", session_id, false));
        
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "logout.php").toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String getOwnProfile(String sid) throws RemoteException {
        listToParse.clear();
        listToParse.add(new ParamObject("sid", sid, false));
        
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "own_profile.php").toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String getProfile(String session_id, int id) throws RemoteException {
        listToParse.clear();
        listToParse.add(new ParamObject("id", String.valueOf(id), false));
        listToParse.add(new ParamObject("sid", session_id, false));
        
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "get_profile.php").toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String editProfile(String sid, String lastname, String firstname, String tel, String description) {
        listToParse.clear();
        listToParse.add(new ParamObject("sid", sid, false));
        listToParse.add(new ParamObject("lastname", lastname, true));
        listToParse.add(new ParamObject("firstname", firstname, true));
        listToParse.add(new ParamObject("tel", tel, true));
        listToParse.add(new ParamObject("description", "", true));
        
        String ret = "";
        try {
            Log.i(this, "Im editing the profile");
            ret = JSonRequestProvider.doRequest(listToParse, "profile_edit.php").toString();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String setProfileVisibility(String sid, boolean lastname_public, boolean firstname_public,
            boolean email_public, boolean tel_public) {
        listToParse.clear();
        listToParse.add(new ParamObject("sid", sid, false));
        listToParse.add(new ParamObject("email_public", String.valueOf(email_public), true));
        listToParse.add(new ParamObject("firstname_public", String.valueOf(firstname_public), true));
        listToParse.add(new ParamObject("lastname_public", String.valueOf(lastname_public), true));
        listToParse.add(new ParamObject("tel_public", String.valueOf(tel_public), true));
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "profile_edit_visibility.php").toString();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String isProfileAnonymous(String sid, int uid) {
        listToParse.clear();
        listToParse.add(new ParamObject("user_id", String.valueOf(uid), false));
        listToParse.add(new ParamObject("sid", sid, false));
        
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "is_profile_anonymous.php").toString();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String enableAnonymity(String sid) {
        listToParse.clear();
        listToParse.add(new ParamObject("sid", sid, false));
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "enable_anonymity.php").toString();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String disableAnonymity(String sid) {
        listToParse.clear();
        listToParse.add(new ParamObject("sid", sid, false));
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "disable_anonymity.php").toString();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String tripUpdatePos(String sid, int trip_id, float current_lat, float current_lon) throws RemoteException {
        listToParse.clear();
        listToParse.add(new ParamObject("sid", sid, false));
        
        listToParse.add(new ParamObject("id", String.valueOf(trip_id), true));
        listToParse.add(new ParamObject("current_lat", String.valueOf(current_lat), true));
        listToParse.add(new ParamObject("current_lon", String.valueOf(current_lon), true));
        
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "trip_update_pos.php").toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String userUpdatePos(String sid, float lat, float lon) throws RemoteException {
        listToParse.clear();
        listToParse.add(new ParamObject("sid", sid, false));
        
        listToParse.add(new ParamObject("lat", String.valueOf(lat), true));
        listToParse.add(new ParamObject("lon", String.valueOf(lon), true));
        
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "user_update_pos.php").toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String getUserPosition(String sid, int user_id) throws RemoteException {
        listToParse.clear();
        listToParse.add(new ParamObject("sid", sid, false));
        
        listToParse.add(new ParamObject("user_id", String.valueOf(user_id), true));
        
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "get_position.php").toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String tripUpdateData(String sid, int trip_id, int avail_seats) throws RemoteException {
        listToParse.clear();
        listToParse.add(new ParamObject("sid", sid, false));
        
        listToParse.add(new ParamObject("id", String.valueOf(trip_id), true));
        listToParse.add(new ParamObject("avail_seats", String.valueOf(avail_seats), true));
        
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "trip_update_data.php").toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String queryUpdateData(String sid, int query_id, int wanted_seats) throws RemoteException {
        listToParse.clear();
        listToParse.add(new ParamObject("sid", sid, false));
        
        listToParse.add(new ParamObject("id", String.valueOf(query_id), true));
        listToParse.add(new ParamObject("wanted_seats", String.valueOf(wanted_seats), true));
        
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "query_update_data.php").toString();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String getTripPassengers() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }
    
    
    public String endTrip(String sid, int trip_id) throws RemoteException {
        // listToParse.clear();
        listToParse.add(new ParamObject("sid", sid, false));
        if (trip_id > -1) {
            listToParse.add(new ParamObject("id", String.valueOf(trip_id), true));
        }
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "trip_end.php").toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String startQuery(String sid, String destination, float current_lat, float current_lon, int seats)
            throws RemoteException {
        listToParse.clear();
        listToParse.add(new ParamObject("sid", sid, false));
        
        listToParse.add(new ParamObject("destination", destination, true));
        listToParse.add(new ParamObject("current_lat", String.valueOf(current_lat), true));
        listToParse.add(new ParamObject("current_lon", String.valueOf(current_lon), true));
        listToParse.add(new ParamObject("seats", String.valueOf(seats), true));
        
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "query_start.php").toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String stopQuery(String sid, int id) throws RemoteException {
        listToParse.clear();
        listToParse.add(new ParamObject("sid", sid, false));
        
        listToParse.add(new ParamObject("query_id", String.valueOf(id), true));
        
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "query_delete.php").toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String searchQuery(String sid, float lat, float lon, int perimeter) throws RemoteException {
        // listToParse.clear();
        listToParse.add(new ParamObject("sid", sid, false));
        listToParse.add(new ParamObject("lat", String.valueOf(lat), true));
        listToParse.add(new ParamObject("lon", String.valueOf(lon), true));
        listToParse.add(new ParamObject("distance", String.valueOf(perimeter), true));
        
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "query_search.php").toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String searchRides(String sid, float lat, float lon, int perimeter) throws RemoteException {
        listToParse.clear();
        listToParse.add(new ParamObject("sid", sid, false));
        listToParse.add(new ParamObject("lat", String.valueOf(lat), true));
        listToParse.add(new ParamObject("lon", String.valueOf(lon), true));
        listToParse.add(new ParamObject("distance", String.valueOf(perimeter), true));
        
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "ride_search.php").toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String sendOffer(String sid, int trip_id, int query_id, String message) throws RemoteException {
        listToParse.clear();
        listToParse.add(new ParamObject("sid", sid, false));
        listToParse.add(new ParamObject("trip", String.valueOf(trip_id), true));
        listToParse.add(new ParamObject("query", String.valueOf(query_id), true));
        listToParse.add(new ParamObject("message", String.valueOf(message), true));
        
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "offer.php").toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String viewOffer(String sid) throws RemoteException {
        listToParse.clear();
        listToParse.add(new ParamObject("sid", sid, false));
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "offer_view.php").toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String handleOffer(String sid, int offer_id, boolean accept) throws RemoteException {
        listToParse.clear();
        listToParse.add(new ParamObject("sid", sid, false));
        
        listToParse.add(new ParamObject("offer", String.valueOf(offer_id), true));
        listToParse.add(new ParamObject("accept", String.valueOf(accept), true));
        
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "offer_handle.php").toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String pick_up(String sid, int user_id) throws RemoteException {
        listToParse.clear();
        listToParse.add(new ParamObject("sid", sid, false));
        
        listToParse.add(new ParamObject("user_id", String.valueOf(user_id), true));
        
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "pick_up.php").toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String isPicked(String sid) throws RemoteException {
        listToParse.clear();
        listToParse.add(new ParamObject("sid", sid, false));
        
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "isPicked.php").toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String offer_accepted(String sid, int offer_id) throws RemoteException {
        listToParse.clear();
        listToParse.add(new ParamObject("sid", sid, false));
        
        listToParse.add(new ParamObject("offer_id", String.valueOf(offer_id), true));
        
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "offer_status.php").toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String getHistory(String sid, String role) throws RemoteException {
        listToParse.clear();
        listToParse.add(new ParamObject("sid", sid, false));
        listToParse.add(new ParamObject("role", role, true));
        
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "ride_history.php").toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String rateUser(String sid, int userid, int tripid, int rating) throws RemoteException {
        listToParse.clear();
        listToParse.add(new ParamObject("sid", sid, false));
        listToParse.add(new ParamObject("recipient", String.valueOf(userid), true));
        listToParse.add(new ParamObject("trip", String.valueOf(tripid), true));
        listToParse.add(new ParamObject("rating", String.valueOf(rating), true));
        
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "ride_rate.php").toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String announceTrip(String session_id, String destination, float current_lat, float current_lon,
            int avail_seats, long date) throws RemoteException {
        listToParse.clear();
        listToParse.add(new ParamObject("sid", session_id, false));
        if (date != 0) {
            listToParse.add(new ParamObject("date", String.valueOf(date), true));
        }
        listToParse.add(new ParamObject("destination", destination, true));
        listToParse.add(new ParamObject("avail_seats", String.valueOf(avail_seats), true));
        
        if (current_lat < Constants.COORDINATE_INVALID) {
            listToParse.add(new ParamObject("current_lat", String.valueOf(current_lat), true));
        }
        if (current_lon < Constants.COORDINATE_INVALID) {
            listToParse.add(new ParamObject("current_lon", String.valueOf(current_lon), true));
        }
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "trip_announce.php").toString();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    
    public String getOpenTrip(String sessionID) {
        
        listToParse.clear();
        listToParse.add(new ParamObject("sid", sessionID, false));
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "trip_get_open.php").toString();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
        }
        
        return ret;
    }
    
    
    public String disableObservation(String sessionID, String user_id) {
        listToParse.clear();
        listToParse.add(new ParamObject("sid", sessionID, false));
        listToParse.add(new ParamObject("user_id", user_id, true));
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "deactivate_observation.php").toString();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
        }
        
        return ret;
    }
    
    
    public String enableObservation(String sessionID, String user_id) {
        
        listToParse.clear();
        listToParse.add(new ParamObject("sid", sessionID, false));
        listToParse.add(new ParamObject("user_id", user_id, true));
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "activate_observation.php").toString();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
        }
        return ret;
    }
    
    
    public String isObservationEnabled(int uid) {
        listToParse.clear();
        listToParse.add(new ParamObject("uid", String.valueOf(uid), false));
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "isObserved.php").toString();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    
    /**
     * List all my trips
     * 
     * @param format
     *            format of the result
     * 
     * @return depend on the 'format'
     *         0 - returns all trips
     *         1 - return current and started trips with count of new offers, messages and passengers
     *         other values - returns only current and not started trips
     */
    public String getMyTrips(int format) {
        listToParse.clear();
        
        switch (format) {
            case 0:
                listToParse.add(new ParamObject("all", "", false));
                break;
            case 1:
                listToParse.add(new ParamObject("compact", "", false));
                break;
        }
        String ret = "";
        try {
            ret = JSonRequestProvider.doRequest(listToParse, "trip_get_all.php").toString();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    
    /**
     * Return an overview about a trip
     * 
     * @param sid
     *            Session ID
     * @param tripId
     *            The trip's ID
     * @return JSON result string
     */
    public String getTripOverview(String sid, int tripId) {
        listToParse.clear();
        listToParse.add(new ParamObject("tripId", String.valueOf(tripId), true));
        
        String result = "{}";
        Log.d(this, listToParse.toArray().toString());
        try {
            result = JSonRequestProvider.doRequest(listToParse, "trip_overview.php").toString();
            //        } catch {ClientProtocolException cpe) {
            //            
            //        } catch (IOException ioe) {
        } catch (Exception e) {
            JSONObject o = new JSONObject();
            try {
                o.put("successful", false);
                o.put("error", "Exception");
                o.put("message", e.getMessage());
                e.printStackTrace();
                return o.toString();
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return result;
    }
}
