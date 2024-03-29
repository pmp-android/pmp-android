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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.apps.vhike.Constants;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.FoundProfilePos;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.Model;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.Profile;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.SliderObject;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.HistoryRideObject;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.JSonRequestReader;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.OfferObject;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.PassengerObject;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.PositionObject;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.QueryObject;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.RideObject;

/**
 * Controls the behavior of vHike
 * 
 * @author Alexander Wassiljew, Dang Huynh
 */
public class Controllerstable {
    
    /**
     * Constructor
     */
    public Controllerstable() {
    }
    
    
    /**
     * Log on an user and save the session id in the {@link Model}
     * 
     * @param username
     * @param pw
     * @return true if succeed
     */
    public boolean login(String username, String pw) {
        
        Log.v(this, "USERNAME: " + username);
        Log.v(this, "PASSSWORD: " + pw);
        String status = JSonRequestReader.login(username, pw);
        Log.v(this, "Status im CTRL: " + status);
        if (status.equals("logged_in")) {
            return true;
        } else {
            return false;
        }
    }
    
    
    public List<SliderObject> mergeQOLwithFU(List<QueryObject> qobjs, List<FoundProfilePos> foundList) {
        List<SliderObject> sliderList = new ArrayList<SliderObject>();
        for (FoundProfilePos foundProfile : foundList) {
            sliderList.add(new SliderObject(foundProfile));
        }
        for (QueryObject objects : qobjs) {
            sliderList.add(new SliderObject(new FoundProfilePos(objects.getUserid(), objects.getCur_lat(),
                    objects
                            .getCur_lon(), objects.getQueryid())));
        }
        return sliderList;
    }
    
    
    /**
     * Log out an user
     * 
     * @param sid
     * @return true if succeed
     */
    public boolean logout(String sid) {
        if (JSonRequestReader.logout(sid)) {
            Model.getInstance().logout();
            return true;
        } else {
            return false;
        }
    }
    
    
    /**
     * Register a new user
     * 
     * @return code specified in {@link Constants}
     */
    public int register(Map<String, String> list) {
        
        String status = JSonRequestReader.register(list.get("username"), list.get("password"),
                list.get("email"),
                list.get("firstname"), list.get("lastname"), list.get("tel"), list.get("description"),
                Boolean.parseBoolean(list.get("email_public")),
                Boolean.parseBoolean(list.get("firstname_public")),
                Boolean.parseBoolean(list.get("lastname_public")),
                Boolean.parseBoolean(list.get("tel_public")));
        
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
     * Returns the Profile of an user
     * 
     * @param user_id
     * @return {@link Profile}
     */
    public Profile getProfile(String session_id, int user_id) {
        Profile profile = JSonRequestReader.getProfile(session_id, user_id);
        Log.v(this, "Controller->Profile->Description:" + profile.getDescription());
        return profile;
    }
    
    
    /**
     * Announce a trip to the web service
     * 
     * @param session_id
     * @param destination
     * @return STATUS_SUCCESS, TRIP_STATUS_OPEN_TRIP, STATUS_ERROR
     */
    public int announceTrip(String session_id, String destination, float current_lat, float current_lon,
            int avail_seats, Date date) {
        Log.v(this, "announceTrip: " + session_id + ", " + destination + ", " + current_lat + ", "
                + current_lat + ", "
                + avail_seats);
        String status = JSonRequestReader.announceTrip(session_id, destination, current_lat, current_lon,
                avail_seats,
                date);
        
        if (status.equals("announced")) {
            return Constants.STATUS_SUCCESS;
        } else if (status.equals("open_trip_exists")) {
            return Constants.TRIP_STATUS_OPEN_TRIP;
        }
        return Constants.STATUS_ERROR;
    }
    
    
    /**
     * Get the open trip if available
     * 
     * @param sessionID
     * @return STATUS_ERROR, TRUE see {@link Constants}
     */
    public int getOpenTrip(String sessionID) {
        Log.d(this, "getOpentrip " + sessionID);
        String status = JSonRequestReader.getOpenTrip(sessionID);
        
        if (status.equals("FALSE")) {
            return Constants.FALSE;
        } else if (status.equals("TRUE")) {
            return Constants.TRUE;
        }
        
        return Constants.STATUS_ERROR;
    }
    
    
    // /**
    // * Updates the position of the driver <br>
    // * Use {@link userUpdatePos} instead
    // *
    // * @param sid
    // * @param trip_id
    // * @param current_lat
    // * @param current_lon
    // * @return STATUS_UPDATED, STATUS_UPTODATE, STATUS_NOTRIP, STATUS_HASENDED
    // * STATUS_INVALID_USER see {@link Constants} and design.html
    // */
    // @Deprecated
    // public int tripUpdatePos(String sid, int trip_id, float current_lat, float current_lon) {
    // String status = JSonRequestReader.tripUpdatePos(sid, trip_id, current_lat, current_lon);
    // Log.v(this, current_lat + " " + current_lon);
    // if (status.equals("updated")) {
    // return Constants.STATUS_UPDATED;
    // } else if (status.equals("already_uptodate")) {
    // return Constants.STATUS_UPTODATE;
    // } else if (status.equals("no_trip")) {
    // return Constants.STATUS_NOTRIP;
    // } else if (status.equals("has_ended")) {
    // return Constants.STATUS_HASENDED;
    // } else if (status.equals("invalid_user")) {
    // return Constants.STATUS_INVALID_USER;
    // }
    // return 0;
    // }
    
    /**
     * Updates the users position
     * 
     * @param sid
     * @param lat
     * @param lon
     * @return STATUS_UPDATED, STATUS_UPTODATE, STATUS_ERROR
     */
    public int userUpdatePos(String sid, float lat, float lon) {
        String status = JSonRequestReader.userUpdatePos(sid, lat, lon);
        
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
     * Updates the data of the trip
     * 
     * @param sid
     * @param trip_id
     * @param avail_seats
     * @return STATUS_UPDATED, STATUS_UPTODATE, STATUS_NOTRIP, STATUS_HASENDED STATUS_INVALID_USER see {@link Constants}
     *         and design.html
     */
    public int tripUpdateData(String sid, int trip_id, int avail_seats) {
        String status = JSonRequestReader.tripUpdateData(sid, trip_id, avail_seats);
        
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
        return Constants.STATUS_ERROR;
    }
    
    
    public PositionObject getUserPosition(String sid, int user_id) {
        PositionObject object = JSonRequestReader.getUserPosition(sid, user_id);
        
        return object;
    }
    
    
    /**
     * End the active trip
     * 
     * @param sid
     * @param trip_id
     * @return STATUS_SUCCESS, STATUS_NO_TRIP, STATUS_INVALID_USER see {@link Constants} and
     *         design.html
     */
    public int endTrip(String sid, int trip_id) {
        Log.d("End trip", "trip id " + trip_id);
        String status = JSonRequestReader.endTrip(sid, trip_id);
        
        if (status.equals("invalid_id")) {
            return Constants.STATUS_INVALID_USER;
        } else if (status.equals("nothing_to_update")) {
            return Constants.STATUS_NO_TRIP;
        } else if (status.equals("trip_ended")) {
            return Constants.STATUS_SUCCESS;
        } else {
            Log.w(this, "End trip status: " + status);
            return Constants.STATUS_ERROR;
        }
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
    public int startQuery(String sid, String destination, float current_lat, float current_lon,
            int avail_seats) {
        int queryId = JSonRequestReader.startQuery(sid, destination, current_lat, current_lon, avail_seats);
        if (queryId != Constants.QUERY_ID_ERROR) {
            Model.getInstance().setQueryId(queryId);
            return queryId;
        } else {
            return Constants.QUERY_ID_ERROR;
        }
    }
    
    
    /**
     * Delete the active query.
     * 
     * @param sid
     * @param queryId
     * @return STATUS_QUERY_DELETED,STATUS_NO_QUERY,STATUS_INVALID_USER,STATUS_ERROR
     */
    public int stopQuery(String sid, int queryId) {
        String status = JSonRequestReader.stopQuery(sid, queryId);
        
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
     * Driver search for potential hitchhikers
     * 
     * @param sid
     * @param lat
     * @param lon
     * @param perimeter
     * @return List if QueryObjects otherwise, null
     */
    public List<QueryObject> searchQuery(String sid, float lat, float lon, int perimeter) {
        List<QueryObject> queryList = JSonRequestReader.searchQuery(sid, lat, lon, perimeter);
        
        return queryList;
        
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
    public List<RideObject> searchRides(String sid, float lat, float lon, int perimeter) {
        List<RideObject> queryList = JSonRequestReader.searchRides(sid, lat, lon, perimeter);
        
        return queryList;
        
    }
    
    
    /**
     * @param sid
     * @return List if OfferObjects otherwise, null
     */
    public List<OfferObject> viewOffers(String sid) {
        List<OfferObject> offerList = JSonRequestReader.viewOffer(sid);
        return offerList;
        
    }
    
    
    /**
     * Sends an offer to the hitchhiker
     * 
     * @param sid
     * @param trip_id
     * @param query_id
     * @param message
     * @return STATUS_SENT, STATUS_INVALID_TRIP, STATUS_INVALID_QUERY, STATUS_ALREADY_SENT see {@link Constants}
     */
    public int sendOffer(String sid, int trip_id, int query_id, String message) {
        String status = JSonRequestReader.sendOffer(sid, trip_id, query_id, message);
        
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
     * Hitchhiker can accept or decline offers
     * 
     * @param sid
     * @param offer_id
     * @param accept
     * @return STATUS_HANDLED, STATUS_INVALID_OFFER, STATUS_INVALID_USER, STATUS_ERROR
     */
    public int handleOffer(String sid, int offer_id, boolean accept) {
        String status = JSonRequestReader.handleOffer(sid, offer_id, accept);
        
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
     * Picks up a hitchhiker
     * 
     * @param sid
     * @param user_id
     * @return true if succeeded, false otherwise
     */
    public boolean pick_up(String sid, int user_id) {
        boolean bool = JSonRequestReader.pick_up(sid, user_id);
        
        return bool;
    }
    
    
    /**
     * Checks if the user where picked up
     * 
     * @param sid
     * @return true if picked up, false otherwise
     */
    public boolean isPicked(String sid) {
        boolean bool = JSonRequestReader.isPicked(sid);
        return bool;
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
    public int offer_accepted(String sid, int offer_id) {
        String status = JSonRequestReader.offer_accepted(sid, offer_id);
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
     * Returns the History of an user
     * 
     * @param sid
     * @param role
     * @return
     */
    public List<HistoryRideObject> getHistory(String sid, String role) {
        List<HistoryRideObject> list = JSonRequestReader.getHistory(sid, role);
        Log.v(this, "getHistory history size: " + list.size());
        return list;
    }
    
    
    public String rateUser(String sid, int userid, int tripid, int rating) {
        String status = JSonRequestReader.rateUser(sid, userid, tripid, rating);
        Log.v(this, "CONTROLLER STATUS AFTER RATING:" + status);
        return status;
    }
}
