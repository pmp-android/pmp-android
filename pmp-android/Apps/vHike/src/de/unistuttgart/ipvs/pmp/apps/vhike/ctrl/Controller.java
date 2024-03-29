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
package de.unistuttgart.ipvs.pmp.apps.vhike.ctrl;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.RemoteException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.apps.vhike.Constants;
import de.unistuttgart.ipvs.pmp.apps.vhike.exception.QueryException;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.utils.StringUtils;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.CompactMessage;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.CompactTrip;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.CompactUser;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.FoundProfilePos;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.Model;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.Profile;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.SliderObject;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.Trip;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.TripOverview;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.HistoryPersonObject;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.HistoryRideObject;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.OfferObject;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.PassengerObject;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.PositionObject;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.QueryObject;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.RideObject;
import de.unistuttgart.ipvs.pmp.resourcegroups.vHikeWS.aidl.IvHikeWebservice;

/**
 * Controls the behavior of vHike
 * 
 * @author Alexander Wassiljew, Dang Huynh
 * 
 */
public class Controller {
    
    public static String ERROR = "error";
    private static String error = "";
    private static final String TAG = "ControllerWS";
    
    IvHikeWebservice ws = null; // HikeService.getInstance().getVHikeWebServiceResourceGroup(); 
    JsonParser parser = new JsonParser();
    
    
    /**
     * Constructor
     */
    public Controller(IvHikeWebservice ws) {
        this.ws = ws;
    }
    
    
    /**
     * Announce a trip to the web servprintStackTraceice
     * 
     * @param session_id
     * @param destination
     * @return TRIP_STATUS_ANNOUNCED, TRIP_STATUS_OPEN_TRIP,STATUS_ERROR
     */
    public int announceTrip(final String session_id, final String destination, final float current_lat,
            final float current_lon, final int avail_seats, Date date) {
        Log.i(this, session_id + ", " + destination + ", " + current_lat + ", " + current_lat + ", "
                + avail_seats);
        String ret = "";
        try {
            if (date == null) {
                ret = this.ws.announceTrip(session_id, destination, current_lat, current_lon, avail_seats, 0);
            } else {
                System.out.println("DATE: " + DateFormat.getDateTimeInstance().format(date.getTime()));
                ret = this.ws.announceTrip(session_id, destination, current_lat, current_lon, avail_seats,
                        date.getTime());
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        JsonObject object = this.parser.parse(ret).getAsJsonObject();
        
        int tripId = -1;
        JsonElement status = object.get("status");
        
        if (object != null && object.get(ERROR) == null && status != null) {
            JsonElement id = object.get("id");
            if (status.getAsString().equals("announced") && id != null) {
                tripId = id.getAsInt();
                Model.getInstance().setTripId(tripId);
                Log.d(TAG, String.valueOf(Model.getInstance().getTripId()));
                return Constants.STATUS_SUCCESS;
            }
            if (status.getAsString().equals("open_trip_exists")) {
                return Constants.TRIP_STATUS_OPEN_TRIP;
            }
        } else {
            setError(object);
            return Constants.STATUS_ERROR;
        }
        return Constants.STATUS_ERROR;
        
    }
    
    
    public static String getError() {
        return error;
    }
    
    
    private static void setError(JsonObject e) {
        if (e == null) {
            return;
        }
        try {
            error = e.get(ERROR).getAsString() + ": " + e.get("msg").getAsString();
        } catch (NullPointerException ex) {
        }
    }
    
    
    /**
     * Get the open trip if available
     * 
     * @param sessionID
     * @return STATUS_ERROR, TRUE see {@link Constants}
     */
    public int getOpenTrip(String sessionID) {
        Log.d(this, "getOpentrip " + sessionID);
        String ret = "";
        try {
            ret = this.ws.getOpenTrip(sessionID);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        JsonObject json = this.parser.parse(ret).getAsJsonObject();
        String status = "";
        if (json != null && json.get(ERROR) == null) {
            if (json.get("trip_id") != null) {
                Trip trip = new Trip(json.get("trip_id").getAsInt(), Model.getInstance().getUserId(), json
                        .get(
                                "avail_seats").getAsInt(), json.get("destination").getAsString(), json.get(
                        "creation")
                        .getAsLong(), 0);
                Model.getInstance().setOpenTrip(trip);
                status = "TRUE";
            } else {
                status = "FALSE";
            }
        }
        if (status.equals("FALSE")) {
            return Constants.FALSE;
        } else if (status.equals("TRUE")) {
            return Constants.TRUE;
        }
        
        return Constants.STATUS_ERROR;
    }
    
    
    /**
     * End the active trip
     * 
     * @param sid
     * @param trip_id
     * @return STATUS_UPDATED, STATUS_UPTODATE, STATUS_NOTRIP, STATUS_HASENDED
     *         STATUS_INVALID_USER see {@link Constants} and design.html
     */
    public int endTrip(final String sid, final int trip_id) {
        String ret = null;
        try {
            ret = this.ws.endTrip(sid, trip_id);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        Log.i(TAG, "End trip: " + ret);
        JsonObject object = this.parser.parse(ret).getAsJsonObject();
        
        String status = object.get("status").getAsString();
        int retStatus = Constants.STATUS_ERROR;
        if (status.equals("invalid_id")) {
            retStatus = Constants.STATUS_INVALID_USER;
        } else if (status.equals("nothing_to_update")) {
            retStatus = Constants.STATUS_NO_TRIP;
        } else if (status.equals("trip_ended")) {
            retStatus = Constants.STATUS_SUCCESS;
        }
        return retStatus;
    }
    
    
    /**
     * Returns the History of an user
     * 
     * @param sid
     * @param role
     * @return
     */
    public List<HistoryRideObject> getHistory(final String sid, final String role) {
        String ret = "";
        try {
            ret = this.ws.getHistory(sid, role);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        JsonObject object = this.parser.parse(ret).getAsJsonObject();
        
        List<HistoryRideObject> historyObjects = null;
        List<HistoryPersonObject> historyPersons = null;
        boolean suc = false;
        JsonArray array_rides;
        if (object != null) {
            suc = object.get("successful").getAsBoolean();
            if (suc) {
                array_rides = object.get("rides").getAsJsonArray();
                Log.d(TAG, "Array rides: " + array_rides.toString());
                Log.d(TAG, "Array rides: " + array_rides.size());
                historyObjects = new ArrayList<HistoryRideObject>();
                
                for (int i = 0; i < array_rides.size(); i++) {
                    historyPersons = new ArrayList<HistoryPersonObject>();
                    JsonObject IObject = array_rides.get(i).getAsJsonObject();
                    int tripid = IObject.get("trip").getAsInt();
                    int avail_seats = IObject.get("avail_seats").getAsInt();
                    String destination = IObject.get("destination").getAsString();
                    String creation = IObject.get("creation").getAsString();
                    String ending = IObject.get("ending").getAsString();
                    
                    if (role.equals(Constants.ROLE_DRIVER)) {
                        JsonArray array_passengers = IObject.get("passengers").getAsJsonArray();
                        for (int j = 0; j < array_passengers.size(); j++) {
                            JsonObject passObjects = array_passengers.get(j).getAsJsonObject();
                            
                            int userid = passObjects.get("userid").getAsInt();
                            String username = passObjects.get("username").getAsString();
                            float rating = passObjects.get("rating").getAsFloat();
                            int rating_num = passObjects.get("rating_num").getAsInt();
                            boolean rated = passObjects.get("rated").getAsBoolean();
                            
                            HistoryPersonObject person = new HistoryPersonObject(userid, username, rating,
                                    rating_num,
                                    rated);
                            historyPersons.add(person);
                        }
                    } else {
                        JsonArray array_drivers = IObject.get("passengers").getAsJsonArray();
                        for (int j = 0; j < array_drivers.size(); j++) {
                            JsonObject passObjects = array_drivers.get(j).getAsJsonObject();
                            
                            int userid = passObjects.get("userid").getAsInt();
                            String username = passObjects.get("username").getAsString();
                            float rating = passObjects.get("rating").getAsFloat();
                            int rating_num = passObjects.get("rating_num").getAsInt();
                            boolean rated = passObjects.get("rated").getAsBoolean();
                            
                            HistoryPersonObject person = new HistoryPersonObject(userid, username, rating,
                                    rating_num,
                                    rated);
                            historyPersons.add(person);
                        }
                        JsonObject driver = IObject.get("driver").getAsJsonObject();
                        
                        int userid = driver.get("userid").getAsInt();
                        String username = driver.get("username").getAsString();
                        float rating = driver.get("rating").getAsFloat();
                        int rating_num = driver.get("rating_num").getAsInt();
                        boolean rated = driver.get("rated").getAsBoolean();
                        
                        HistoryPersonObject person = new HistoryPersonObject(userid, username, rating,
                                rating_num,
                                rated);
                        historyPersons.add(person);
                    }
                    Log.d(null, "IN READER HISTORYPERSONS" + historyPersons.size());
                    HistoryRideObject ride = new HistoryRideObject(tripid, avail_seats, creation, ending,
                            destination,
                            historyPersons);
                    historyObjects.add(ride);
                }
            }
            Model.getInstance().setHistoryObjHolder(historyObjects);
        }
        
        return historyObjects;
    }
    
    
    /**
     * Returns the Profile of an user
     * 
     * @param user_id
     * @return {@link Profile}
     */
    public Profile getProfile(final String session_id, final int user_id) {
        String ret = "";
        try {
            ret = this.ws.getProfile(session_id, user_id);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JsonObject object = this.parser.parse(ret).getAsJsonObject();
        
        boolean suc = false;
        String username = null;
        String email = "xxx";
        String firstname = "xxx";
        String lastname = "xxx";
        String tel = "xxx";
        String description = null;
        boolean email_public = false;
        boolean firstname_public = false;
        boolean lastname_public = false;
        boolean tel_public = false;
        int userid = 0;
        double rating_avg = 0;
        int rating_num = 0;
        if (object != null) {
            suc = object.get("successful").getAsBoolean();
            userid = object.get("id").getAsInt();
            email_public = object.get("email_public").getAsBoolean();
            firstname_public = object.get("firstname_public").getAsBoolean();
            lastname_public = object.get("lastname_public").getAsBoolean();
            tel_public = object.get("tel_public").getAsBoolean();
            
            if (email_public) {
                email = object.get("email").getAsString();
            }
            if (firstname_public) {
                firstname = object.get("firstname").getAsString();
            }
            if (lastname_public) {
                lastname = object.get("lastname").getAsString();
            }
            if (tel_public) {
                tel = object.get("tel").getAsString();
            }
            username = object.get("username").getAsString();
            description = object.get("description").getAsString();
            Log.d(TAG, "GETPROFILE_: description:" + description);
            object.get("regdate").getAsString();
            rating_avg = object.get("rating_avg").getAsFloat();
            rating_num = object.get("rating_num").getAsInt();
            
        }
        
        // String userid = object.get("id").getAsString();
        // TODO
        // String regdate = object.get("regdate").getAsString();
        Profile profile;
        
        Date date = new Date();
        if (suc) {
            profile = new Profile(userid, username, email, firstname, lastname, tel, description, date,
                    email_public,
                    firstname_public, lastname_public, tel_public, rating_avg, rating_num);
            return profile;
        }
        return null;
    }
    
    
    public int editProfile(String sid, String lastname, String firstname, String tel) {
        try {
            Log.i(this, "Last: " + lastname + ", First: " + firstname + ", Tel: " + tel);
            this.ws.editProfile(sid, lastname, firstname, tel, "");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    
    /**
     * Sets visibility of lastname, firstname, email and/or phone number
     * 
     * @param sid
     * @param list
     * @return
     */
    public int setProfileVisibility(String sid, Map<String, String> list) {
        try {
            Log.i(this, "Public Lastname: " + Boolean.parseBoolean(list.get("lastname_public")));
            Log.i(this, "Public Firstname: " + Boolean.parseBoolean(list.get("firstname_public")));
            this.ws.setProfileVisibility(sid, Boolean.parseBoolean(list.get("lastname_public")),
                    Boolean.parseBoolean(list.get("firstname_public")),
                    Boolean.parseBoolean(list.get("email_public")),
                    Boolean.parseBoolean(list.get("tel_public")));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        
        return 0;
        
    }
    
    
    public int enableAnonymity(String sid) {
        try {
            this.ws.enableAnonymity(sid);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    
    public int disableAnonymity(String sid) {
        try {
            this.ws.disableAnonymity(sid);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    
    public boolean isProfileAnonymous(String sid, int uid) {
        String ret = "";
        
        try {
            ret = this.ws.isProfileAnonymous(sid, uid);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        JsonObject object = this.parser.parse(ret).getAsJsonObject();
        boolean suc = false;
        boolean isAnonymous = false;
        if (object != null) {
            suc = object.get("successful").getAsBoolean();
            if (suc) {
                isAnonymous = object.get("anonymous").getAsBoolean();
            }
        }
        return isAnonymous;
    }
    
    
    public PositionObject getUserPosition(final String sid, final int user_id) {
        String ret = "";
        try {
            ret = this.ws.getUserPosition(sid, user_id);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JsonObject object = this.parser.parse(ret).getAsJsonObject();
        boolean suc = false;
        PositionObject posObj = null;
        if (object != null) {
            suc = object.get("successful").getAsBoolean();
            if (suc) {
                //                array = object.get("position").getAsJsonArray();
                JsonObject pos = object.get("position").getAsJsonObject();
                //                float lat = array.get(0).getAsFloat();
                //                float lon = array.get(1).getAsFloat();
                float lat = pos.get("latitude").getAsFloat();
                float lon = pos.get("longitude").getAsFloat();
                posObj = new PositionObject(lat, lon);
            }
            
        }
        return posObj;
    }
    
    
    /**
     * Hitchhiker can accept or decline offers
     * 
     * @param sid
     * @param offer_id
     * @param accept
     * @return STATUS_HANDLED, STATUS_INVALID_OFFER, STATUS_INVALID_USER,
     *         STATUS_ERROR
     */
    public int handleOffer(final String sid, final int offer_id, final boolean accept) {
        String ret = "";
        
        try {
            ret = this.ws.handleOffer(sid, offer_id, accept);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        JsonObject object = this.parser.parse(ret).getAsJsonObject();
        
        boolean suc = false;
        String status = "";
        if (object != null) {
            suc = object.get("successful").getAsBoolean();
            if (suc) {
                status = object.get("status").getAsString();
                Log.d(null, "STATUS after handleOFFER: " + status);
            }
        }
        Log.d(null, "STATUS after handleOFFER: " + status);
        
        if (!status.equals("")) {
            if (status.equals("accepted")) {
                return Constants.STATUS_HANDLED;
            } else if (status.equals("invalid_offer")) {
                return Constants.STATUS_INVALID_OFFER;
            } else if (status.equals("invalid_user")) {
                return Constants.STATUS_INVALID_USER;
            } else if (status.equals("denied")) {
                return Constants.STATUS_ERROR;
            } else if (status.equals("cannot_update")) {
                return Constants.STATUS_ERROR;
            }
        }
        return Constants.STATUS_ERROR;
    }
    
    
    /**
     * Checks if the user where picked up
     * 
     * @param sid
     * @return true if picked up, false otherwise
     */
    public boolean isPicked(final String sid) {
        String ret = "";
        
        try {
            ret = this.ws.isPicked(sid);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        JsonObject object = this.parser.parse(ret).getAsJsonObject();
        boolean suc = false;
        boolean picked = false;
        if (object != null) {
            suc = object.get("successful").getAsBoolean();
            if (suc) {
                picked = object.get("picked").getAsBoolean();
            }
        }
        return picked;
    }
    
    
    /**
     * Log on an user and save the session id in the {@link Model}
     * 
     * @param username
     * @param pw
     * @return true if succeed
     */
    public boolean login(final String username, final String pw) {
        
        String ret = "";
        
        try {
            ret = this.ws.login(username, pw);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        JsonObject object = this.parser.parse(ret).getAsJsonObject();
        
        boolean suc = false;
        if (object != null) {
            suc = object.get("successful").getAsBoolean();
        }
        String sid = null;
        String status = null;
        if (suc) {
            status = object.get("status").getAsString();
            Log.d(TAG, "STATUS NACH DEM LOGIN:" + status);
            if (!status.equals("invalid")) {
                sid = object.get("sid").getAsString();
                Model.getInstance().setSid(sid);
                Model.getInstance().setOwnProfile(getOwnProfile(sid));
            }
        }
        
        if (status.equals("logged_in")) {
            return true;
        } else {
            return false;
        }
    }
    
    
    private Profile getOwnProfile(String sid) {
        String ret = "";
        Log.i(this, "SID OWN PROFILEs" + sid, null);
        try {
            ret = this.ws.getOwnProfile(sid);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i(this, "RET OBJECT:" + ret, null);
        JsonObject object = this.parser.parse(ret).getAsJsonObject();
        
        boolean suc = false;
        int id = 0;
        String username = null;
        String email = null;
        String firstname = null;
        String lastname = null;
        String tel = null;
        String description = null;
        boolean email_public = false;
        boolean firstname_public = false;
        boolean lastname_public = false;
        boolean tel_public = false;
        
        double rating_avg = 0;
        int rating_num = 0;
        if (object != null) {
            suc = object.get("successful").getAsBoolean();
            id = object.get("id").getAsInt();
            username = object.get("username").getAsString();
            Log.d(TAG, "USERNAME:" + username);
            email = object.get("email").getAsString();
            firstname = object.get("firstname").getAsString();
            lastname = object.get("lastname").getAsString();
            tel = object.get("tel").getAsString();
            description = object.get("description").getAsString();
            object.get("regdate").getAsString();
            rating_avg = object.get("rating_avg").getAsFloat();
            rating_num = object.get("rating_num").getAsInt();
            email_public = object.get("email_public").getAsBoolean();
            firstname_public = object.get("firstname_public").getAsBoolean();
            lastname_public = object.get("lastname_public").getAsBoolean();
            tel_public = object.get("tel_public").getAsBoolean();
        }
        
        // String userid = object.get("id").getAsString();
        // TODO
        // String regdate = object.get("regdate").getAsString();
        Profile profile;
        
        Date date = new Date();
        if (suc) {
            profile = new Profile(id, username, email, firstname, lastname, tel, description, date,
                    email_public,
                    firstname_public, lastname_public, tel_public, rating_avg, rating_num);
            return profile;
        }
        return null;
    }
    
    
    /**
     * Log out an user
     * 
     * @param sid
     * @return true if succeed
     */
    public boolean logout(String sid) {
        
        String ret = "";
        
        try {
            ret = this.ws.logout(sid);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        JsonObject object = this.parser.parse(ret).getAsJsonObject();
        boolean suc = object.get("successful").getAsBoolean();
        if (suc) {
            Model.getInstance().logout();
            return true;
        } else {
            return false;
        }
    }
    
    
    public List<SliderObject> mergeQOLwithFU(final List<QueryObject> qobjs,
            final List<FoundProfilePos> foundList) {
        final List<SliderObject> sliderList = new ArrayList<SliderObject>();
        for (final FoundProfilePos foundProfile : foundList) {
            sliderList.add(new SliderObject(foundProfile));
        }
        for (final QueryObject objects : qobjs) {
            sliderList.add(new SliderObject(new FoundProfilePos(objects.getUserid(), objects.getCur_lat(),
                    objects
                            .getCur_lon(), objects.getQueryid())));
        }
        return sliderList;
    }
    
    
    /**
     * Get a List with Passengers and their status to pick up.
     * 
     * @param sid
     *            Session id
     * @param trip_id
     *            Trip id
     * @return List of {@link PassengerObject}
     */
    public int offer_accepted(final String sid, final int offer_id) {
        String ret = "";
        
        try {
            ret = this.ws.offer_accepted(sid, offer_id);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        JsonObject object = this.parser.parse(ret).getAsJsonObject();
        
        boolean suc = false;
        String status = "";
        if (object != null) {
            suc = object.get("successful").getAsBoolean();
            if (suc) {
                status = object.get("status").getAsString();
            }
        }
        
        if (status.equals("unread")) {
            return Constants.STATUS_UNREAD;
        } else if (status.equals("accepted")) {
            return Constants.STATUS_ACCEPTED;
        } else if (status.equals("denied")) {
            return Constants.STATUS_DENIED;
        }
        
        return Constants.STATUS_UNREAD;
    }
    
    
    /**
     * Picks up a hitchhiker
     * 
     * @param sid
     * @param user_id
     * @return true if succeeded, false otherwise
     */
    public boolean pick_up(final String sid, final int user_id) {
        String ret = "";
        
        try {
            ret = this.ws.pick_up(sid, user_id);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        JsonObject object = this.parser.parse(ret).getAsJsonObject();
        boolean suc = false;
        if (object != null) {
            suc = object.get("successful").getAsBoolean();
            if (suc) {
                return true;
            }
        }
        return false;
    }
    
    
    public String rateUser(final String sid, final int userid, final int tripid, final int rating) {
        String ret = "";
        
        try {
            ret = this.ws.rateUser(sid, userid, tripid, rating);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        JsonObject object = this.parser.parse(ret).getAsJsonObject();
        
        boolean suc = false;
        String status = "";
        if (object != null) {
            suc = object.get("successful").getAsBoolean();
            if (suc) {
                status = object.get("status").getAsString();
            }
        }
        return status;
    }
    
    
    /**
     * Register an user
     * 
     * @return Code specified in {@link Constants}
     */
    public int register(final Map<String, String> list) {
        
        String ret = "";
        
        try {
            ret = this.ws.register(list.get("username"), list.get("password"), list.get("email"),
                    list.get("firstname"), list.get("lastname"), list.get("tel"), list.get("description"),
                    Boolean.parseBoolean(list.get("email_public")),
                    Boolean.parseBoolean(list.get("firstname_public")),
                    Boolean.parseBoolean(list.get("lastname_public")),
                    Boolean.parseBoolean(list.get("tel_public")));
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        JsonObject object = this.parser.parse(ret).getAsJsonObject();
        
        String status = "";
        if (object != null) {
            boolean suc = object.get("successful").getAsBoolean();
            if (suc) {
                status = object.get("status").getAsString();
            }
            
        }
        
        if (status.equals("registered")) {
            return Constants.STATUS_SUCCESS;
        } else if (status.contains("username_exists")) {
            return Constants.REG_STAT_USED_USERNAME;
        } else if (status.contains("email_exists")) {
            return Constants.REG_STAT_USED_MAIL;
        } else if (status.contains("invalid_username")) {
            return Constants.REG_STAT_INVALID_USERNAME;
        } else if (status.contains("invalid_password")) {
            return Constants.REG_STAT_INVALID_PW;
        } else if (status.contains("invalid_firstname")) {
            return Constants.REG_STAT_INVALID_FIRSTNAME;
        } else if (status.contains("invalid_lastname")) {
            return Constants.REG_STAT_INVALID_LASTNAME;
        } else if (status.contains("invalid_tel")) {
            return Constants.REG_STAT_INVALID_TEL;
        }
        
        return Constants.STATUS_ERROR;
    }
    
    
    /**
     * Driver search for potential hitchhikers
     * 
     * @param sid
     * @param lat
     * @param lon
     * @param perimeter
     * @return List if QueryObjects otherwise, null
     */
    public List<QueryObject> searchQuery(final String sid, final float lat, final float lon,
            final int perimeter) {
        String ret = "";
        List<QueryObject> queryObjects = null;
        try {
            ret = this.ws.searchQuery(sid, lat, lon, perimeter);
            
            JsonObject object = this.parser.parse(ret).getAsJsonObject();
            
            boolean suc = false;
            if (object != null) {
                suc = object.get("successful").getAsBoolean();
                if (suc) {
                    String status = object.get("status").getAsString();
                    if (status.equals("result")) {
                        JsonArray array = object.get("queries").getAsJsonArray();
                        Log.d(TAG, array.toString());
                        queryObjects = new ArrayList<QueryObject>();
                        
                        /*
                         * "queryid": Integer, "userid": Integer, "username": String, "rating": Float,
                         * "lat": Float, "lon": Float, "seats": Integer, distance: Float
                         */
                        
                        for (int i = 0; i < array.size(); i++) {
                            JsonObject Iobject = array.get(i).getAsJsonObject();
                            int queryid = Iobject.get("queryid").getAsInt();
                            
                            int userid = Iobject.get("userid").getAsInt();
                            
                            String username = Iobject.get("username").getAsString();
                            // float rating = Iobject.get("rating").getAsFloat();
                            
                            float cur_lat = Iobject.get("lat").getAsFloat();
                            float cur_lon = Iobject.get("lon").getAsFloat();
                            int seats = Iobject.get("seats").getAsInt();
                            float distance = Iobject.get("distance").getAsFloat();
                            
                            QueryObject qObject = new QueryObject(queryid, userid, username, cur_lat,
                                    cur_lon, seats,
                                    distance);
                            queryObjects.add(qObject);
                        }
                        if (queryObjects != null) {
                            Model.getInstance().setQueryHolder(queryObjects);
                        }
                        return queryObjects;
                    }
                }
            }
            if (queryObjects != null) {
                Model.getInstance().setQueryHolder(queryObjects);
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NullPointerException ne) {
            
        }
        
        return queryObjects;
        
    }
    
    
    /**
     * Hitchhiker search for the drivers in the given perimeter
     * 
     * @param sid
     * @param lat
     * @param lon
     * @param perimeter
     * @return List if QueryObjects otherwise, null
     */
    public List<RideObject> searchRides(final String sid, final float lat, final float lon,
            final int perimeter) {
        String ret = "";
        
        try {
            ret = this.ws.searchRides(sid, lat, lon, perimeter);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        JsonObject object = this.parser.parse(ret).getAsJsonObject();
        boolean suc = false;
        List<RideObject> rideObjects = null;
        if (object != null) {
            suc = object.get("successful").getAsBoolean();
            if (suc) {
                
                JsonArray array = object.get("queries").getAsJsonArray();
                
                rideObjects = new ArrayList<RideObject>();
                
                for (int i = 0; i < array.size(); i++) {
                    
                    JsonObject Iobject = array.get(i).getAsJsonObject();
                    int tripid = Iobject.get("tripid").getAsInt();
                    int seats = Iobject.get("seats").getAsInt();
                    float cur_lat = Iobject.get("lat").getAsFloat();
                    float cur_lon = Iobject.get("lon").getAsFloat();
                    
                    String destination = Iobject.get("destination").getAsString();
                    int driverid = Iobject.get("driverid").getAsInt();
                    
                    String username = Iobject.get("username").getAsString();
                    float rating = Iobject.get("rating").getAsFloat();
                    
                    float distance = Iobject.get("distance").getAsFloat();
                    
                    RideObject qObject = new RideObject(tripid, seats, cur_lat, cur_lon, destination,
                            driverid,
                            username, rating, distance);
                    rideObjects.add(qObject);
                }
                
                return rideObjects;
            }
        }
        return rideObjects;
        
    }
    
    
    /**
     * Sends an offer to the hitchhiker
     * 
     * @param sid
     * @param trip_id
     * @param query_id
     * @param message
     * @return STATUS_SENT, STATUS_INVALID_TRIP, STATUS_INVALID_QUERY,
     *         STATUS_ALREADY_SENT see {@link Constants}
     */
    public int sendOffer(final String sid, final int trip_id, final int query_id, final String message) {
        String ret = "";
        JsonObject object = null;
        
        try {
            ret = this.ws.sendOffer(sid, trip_id, query_id, message);
            object = this.parser.parse(ret).getAsJsonObject();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        
        boolean suc = false;
        String status = "";
        if (object != null) {
            suc = object.get("successful").getAsBoolean();
            if (suc) {
                status = object.get("status").getAsString();
                if (status.equals("sent")) {
                    status = String.valueOf(object.get("offer_id").getAsInt());
                }
            }
        }
        
        if (!status.equals("")) {
            
            if (status.equals("invalid_trip")) {
                return Constants.STATUS_INVALID_TRIP;
            } else if (status.equals("invalid_query")) {
                return Constants.STATUS_INVALID_QUERY;
            } else if (status.equals("already_sent")) {
                return Constants.STATUS_ALREADY_SENT;
            } else {
                return Integer.valueOf(status);
            }
        }
        return Constants.STATUS_ERROR;
    }
    
    
    /**
     * Starts the Query and returns the id, if the creation succeeded
     * 
     * @param sid
     * @param destination
     * @param current_lat
     * @param current_lon
     * @param avail_seats
     * @return QUERY_ID_ERROR || queryId
     */
    public int startQuery(final String sid, final String destination, final float current_lat,
            final float current_lon,
            final int avail_seats) {
        
        String ret = "";
        
        try {
            ret = this.ws.startQuery(sid, destination, current_lat, current_lon, avail_seats);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        JsonObject object = this.parser.parse(ret).getAsJsonObject();
        Log.i(this, ret);
        
        boolean suc = false;
        int id = Constants.QUERY_ID_ERROR;
        if (object != null) {
            suc = object.get("successful").getAsBoolean();
            if (suc) {
                id = object.get("id").getAsInt();
                Log.d(TAG, String.valueOf(id));
            }
        }
        
        if (id != Constants.QUERY_ID_ERROR) {
            Model.getInstance().setQueryId(id);
            return id;
        } else {
            return Constants.QUERY_ID_ERROR;
        }
    }
    
    
    /**
     * Delete the active query.
     * 
     * @param sid
     * @param queryId
     * @return
     *         STATUS_QUERY_DELETED,STATUS_NO_QUERY,STATUS_INVALID_USER,STATUS_ERROR
     */
    public int stopQuery(final String sid, final int queryId) {
        String ret = "";
        
        try {
            ret = this.ws.stopQuery(sid, queryId);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        JsonObject object = this.parser.parse(ret).getAsJsonObject();
        
        boolean suc = false;
        String status = null;
        if (object != null) {
            suc = object.get("successful").getAsBoolean();
            if (suc) {
                // status = object.get("status").getAsString();
                status = "deleted";
                Log.d(TAG, "STATUS AFTER STOPQUERY:" + status);
            }
            Log.d(TAG, "Status after STOPQUERY:" + status + ", suc" + suc);
        }
        
        if (status != null) {
            if (status.equals("deleted")) {
                return Constants.STATUS_QUERY_DELETED;
            } else if (status.equals("no_query")) {
                return Constants.STATUS_NO_QUERY;
            } else if (status.equals("invalid_user")) {
                return Constants.STATUS_INVALID_USER;
            }
        }
        return Constants.STATUS_ERROR;
    }
    
    
    /**
     * Updates the data of the trip
     * 
     * @param sid
     * @param trip_id
     * @param avail_seats
     * @return STATUS_UPDATED, STATUS_UPTODATE, STATUS_NOTRIP, STATUS_HASENDED
     *         STATUS_INVALID_USER see {@link Constants} and design.html
     */
    public int tripUpdateData(final String sid, final int trip_id, final int avail_seats) {
        String ret = "";
        
        try {
            ret = this.ws.tripUpdateData(sid, trip_id, avail_seats);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        JsonObject object = this.parser.parse(ret).getAsJsonObject();
        
        String status = null;
        if (object != null) {
            object.get("successful").getAsBoolean();
            status = object.get("status").getAsString();
        }
        
        if (status.equals("updated")) {
            return Constants.STATUS_UPDATED;
        } else if (status.equals("already_uptodate")) {
            return Constants.STATUS_UPTODATE;
        } else if (status.equals("no_trip")) {
            return Constants.STATUS_NO_TRIP;
        } else if (status.equals("has_ended")) {
            return Constants.STATUS_HASENDED;
        } else if (status.equals("invalid_user")) {
            return Constants.STATUS_INVALID_USER;
        }
        return 0;
    }
    
    
    public int queryUpdateData(final String sid, final int query_id, final int wanted_seats) {
        String ret = "";
        
        try {
            ret = this.ws.queryUpdateData(sid, query_id, wanted_seats);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        JsonObject object = this.parser.parse(ret).getAsJsonObject();
        
        String status = null;
        if (object != null) {
            object.get("successful").getAsBoolean();
            status = object.get("status").getAsString();
        }
        
        if (status.equals("updated")) {
            return Constants.STATUS_UPDATED;
        } else if (status.equals("already_uptodate")) {
            return Constants.STATUS_UPTODATE;
        } else if (status.equals("no_query")) {
            return Constants.STATUS_NO_QUERY;
        } else if (status.equals("invalid_user")) {
            return Constants.STATUS_INVALID_USER;
        }
        return 0;
    }
    
    
    /**
     * Updates the users position
     * 
     * @param sid
     * @param lat
     * @param lon
     * @return STATUS_UPDATED, STATUS_UPTODATE, STATUS_ERROR
     */
    public int userUpdatePos(final String sid, final float lat, final float lon) {
        String ret = "";
        
        try {
            ret = this.ws.userUpdatePos(sid, lat, lon);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        JsonObject object = this.parser.parse(ret).getAsJsonObject();
        
        boolean suc = false;
        String status = "update_fail";
        if (object != null) {
            suc = object.get("successful").getAsBoolean();
            if (suc) {
                status = object.get("status").getAsString();
            }
        }
        
        if (status.equals("updated")) {
            return Constants.STATUS_UPDATED;
        } else if (status.equals("no_update")) {
            return Constants.STATUS_UPTODATE;
        } else if (status.equals("update_fail")) {
            return Constants.STATUS_ERROR;
        }
        return Constants.STATUS_ERROR;
    }
    
    
    /**
     * 
     * @param sid
     * @return List if OfferObjects otherwise, null
     */
    public List<OfferObject> viewOffers(final String sid) {
        String ret = "";
        
        try {
            ret = this.ws.viewOffer(sid);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        
        JsonObject object = this.parser.parse(ret).getAsJsonObject();
        boolean suc = false;
        List<OfferObject> offerObjects = null;
        if (object != null) {
            suc = object.get("successful").getAsBoolean();
            if (suc) {
                JsonArray array;
                try {
                    array = object.get("offers").getAsJsonArray();
                    offerObjects = new ArrayList<OfferObject>();
                    for (int i = 0; i < array.size(); i++) {
                        JsonObject Iobject = array.get(i).getAsJsonObject();
                        int offer_id = Iobject.get("offer").getAsInt();
                        int user_id = Iobject.get("userid").getAsInt();
                        String username = Iobject.get("username").getAsString();
                        float rating = Iobject.get("rating").getAsFloat();
                        float rating_num = Iobject.get("rating_num").getAsFloat();
                        float lat = Iobject.get("lat").getAsFloat();
                        float lon = Iobject.get("lon").getAsFloat();
                        
                        OfferObject oObject = new OfferObject(offer_id, user_id, username, rating,
                                rating_num, lat, lon);
                        offerObjects.add(oObject);
                    }
                    Model.getInstance().setOfferHolder(offerObjects);
                } catch (Exception ex) {
                    offerObjects = new ArrayList<OfferObject>();
                }
                
                return offerObjects;
            }
        }
        Model.getInstance().setOfferHolder(offerObjects);
        return offerObjects;
        
    }
    
    
    public int enableObservation(final String sid, int user_id) {
        String ret = "";
        
        try {
            ret = this.ws.enableObservation(sid, String.valueOf(user_id));
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        JsonObject object = this.parser.parse(ret).getAsJsonObject();
        boolean suc = false;
        int obs_nr = 0;
        if (object != null) {
            suc = object.get("successful").getAsBoolean();
            if (suc) {
                obs_nr = object.get("obs_nr").getAsInt();
            }
        }
        return obs_nr;
    }
    
    
    public boolean disableObservation(final String sid, int user_id) {
        String ret = "";
        
        try {
            ret = this.ws.disableObservation(sid, String.valueOf(user_id));
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        JsonObject object = this.parser.parse(ret).getAsJsonObject();
        boolean suc = false;
        //        int obs_nr = 0;
        if (object != null) {
            suc = object.get("successful").getAsBoolean();
            
        }
        return suc;
    }
    
    
    public boolean isObservationEnabled(int uid) {
        String ret = "";
        try {
            ret = this.ws.isObservationEnabled(uid);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        JsonObject object = this.parser.parse(ret).getAsJsonObject();
        boolean suc = false;
        if (object != null) {
            suc = object.get("status").getAsBoolean();
        }
        return suc;
    }
    
    
    public ArrayList<CompactTrip> getTrips(final String sid) throws QueryException {
        try {
            
            // Get data from server
            JSONObject root = new JSONObject(this.ws.getMyTrips(1));
            
            if (root.has("error")) {
                throw new QueryException(root.getString("error") + (
                        root.has("message") ? ": " + root.getString("message") : ""));
            } else if (root.has("trips") && root.get("trips") != null) {
                
                JSONArray trips = root.getJSONArray("trips");
                ArrayList<CompactTrip> listTrips = new ArrayList<CompactTrip>(trips.length());
                
                // Add trips to list
                for (int i = 0; i < trips.length(); i++) {
                    JSONObject o = trips.getJSONObject(i);
                    listTrips.add(new CompactTrip(
                            o.getInt("id"),
                            o.getString("destination"),
                            o.getLong("creation"),
                            o.getInt("passenger_count"),
                            o.getInt("offer_count"),
                            o.getInt("message_count")));
                }
                
                return listTrips;
            }
            
        } catch (RemoteException re) {
            System.err.println(re);
        } catch (JSONException je) {
            //            throw je;
            je.printStackTrace();
        }
        return null;
    }
    
    
    public TripOverview getTripOverview(final String sid, int tripId) throws RemoteException, JSONException,
            QueryException {
        // Get data from server
        JSONObject root = new JSONObject(ws.getTripOverview(sid, tripId));
        
        if (root.has("error")) {
            throw new QueryException(root.getString("error") + (
                    root.has("message") ? ": " + root.getString("message") : ""));
        } else {
            
            // parse passengers
            ArrayList<CompactUser> passengers = null;
            if (root.has("passengers")) {
                JSONArray jPassengers = root.getJSONArray("passengers");
                passengers = new ArrayList<CompactUser>(jPassengers.length());
                for (int i = 0; i < jPassengers.length(); i++) {
                    JSONObject o = jPassengers.getJSONObject(i);
                    passengers.add(new CompactUser(o.getInt("pid"), o.getString("username"), 0.0f));
                }
            }
            
            // parse offers
            ArrayList<CompactMessage> newMsgs = null;
            JSONArray jMessages = root.has("messages") ? root.getJSONArray("messages") : null;
            if (root.has("offers")) {
                JSONArray jOffers = root.getJSONArray("offers");
                newMsgs = new ArrayList<CompactMessage>(jOffers.length() +
                        (jMessages != null ? jMessages.length() : 0));
                for (int i = 0; i < jOffers.length(); i++) {
                    JSONObject o = jOffers.getJSONObject(i);
                    newMsgs.add(new CompactMessage(
                            o.getInt("id"),
                            new CompactUser(
                                    o.getInt("sender"), o.getString("username"), o.getDouble("rating_avg")),
                            null,
                            true,
                            o.has("message") ? o.getString("message") : ""));
                }
            }
            
            // parse messages
            if (jMessages != null) {
                if (newMsgs == null)
                    newMsgs = new ArrayList<CompactMessage>(jMessages.length());
                for (int i = 0; i < jMessages.length(); i++) {
                    JSONObject o = jMessages.getJSONObject(i);
                    newMsgs.add(new CompactMessage(
                            -1,
                            new CompactUser(o.getInt("sender"), o.getString("username"), o
                                    .getDouble("rating_avg")),
                            null,
                            false,
                            o.has("message") ? o.getString("message") : ""));
                }
            }
            
            String destinationStopovers = StringUtils.cleanUpDestionationAndStopoversString(root
                    .getString("destination"));
            
            TripOverview trip = new TripOverview(
                    tripId,
                    root.has("destination") ? StringUtils.getDestination(destinationStopovers) : "",
                    StringUtils.getStopovers(destinationStopovers),
                    passengers,
                    root.getLong("creation"),
                    root.getInt("avail_seats"),
                    newMsgs);
            Log.v(this, String.valueOf(root.getLong("creation")));
            return trip;
        }
    }
    //    public ArrayList<CompactTrip> getMyTrips(int uid) {
    //        String ret = "";
    //        try {
    //            ret = this.ws.getMyTrips(uid);
    //        } catch (RemoteException e) {
    //            throw e;
    //        }
    //        JsonObject object = this.parser.parse(ret).getAsJsonObject();
    //        boolean suc = false;
    //        ArrayList<CompactTrip> trips = null;
    //        if (object != null) {
    //            suc = object.get("successful").getAsBoolean();
    //            if (suc) {
    //                JsonArray array;
    //                try {
    //                    array = object.get("my_trips").getAsJsonArray();
    //                    trips = new ArrayList<CompactTrip>();
    //                    for (int i = 0; i < array.size(); i++) {
    //                        JsonArray jArray = array.get(i).getAsJsonArray();
    //                        for (int j = 0; j < jArray.size(); j++) {
    //                            JsonObject Iobject = jArray.get(j).getAsJsonObject();
    //                            int tid = Iobject.get("TripID").getAsInt();
    //                            String destination = Iobject.get("Destination").getAsString();
    //                            String date = Iobject.get("Time").getAsString();
    //                            int passengers = Iobject.get("Passengers").getAsInt();
    //                            int invites = Iobject.get("Invites").getAsInt();
    //                            PrePlannedTrip pObject = new PrePlannedTrip(tid, destination, date, passengers, invites);
    //                            trips.add(pObject);
    //                        }
    //                    }
    //                    Model.getInstance().setMyTrips(trips);
    //                } catch (Exception ex) {
    //                    ex.printStackTrace();
    //                    trips = new ArrayList<CompactTrip>();
    //                }
    //                return trips;
    //            }
    //            
    //        } else {
    //            Log.i(this, "Parsing Object null");
    //        }
    //        Model.getInstance().setMyTrips(trips);
    //        return trips;
    //    }
}
