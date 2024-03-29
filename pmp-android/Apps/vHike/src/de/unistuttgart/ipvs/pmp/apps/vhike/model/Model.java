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
package de.unistuttgart.ipvs.pmp.apps.vhike.model;

import java.util.ArrayList;
import java.util.List;

import de.unistuttgart.ipvs.pmp.apps.vhike.tools.HistoryRideObject;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.OfferObject;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.PrePlannedTrip;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.QueryObject;

/**
 * Model for vHike, it holds all necessary informations to work with
 * 
 * @author Alexander Wassiljew, Dang Huynh
 * 
 */
public class Model {
    
    /**
     * Instance of the Object {@link Model}
     */
    static Model theInstance;
    /**
     * Session ID
     */
    private String sid = "";
    
    private int id = -1;
    
    /**
     * This is the trip id, in which the user participate at the moment
     */
    // TODO: replace the tripId with the openTrip object
    private int tripId = -1;
    private Trip openTrip = null;
    
    /**
     * This is the query id of an user
     */
    private int queryId;
    
    private ArrayList<Trip> plannedTrips = null;
    
    /**
     * Holds queries of the Driver, within are hitchhiker
     */
    private List<QueryObject> queryHolder;
    
    private List<PrePlannedTrip> prePlannedTrips;
    
    
    public List<QueryObject> getQueryHolder() {
        return this.queryHolder;
    }
    
    
    public List<PrePlannedTrip> getMyTrips() {
        return this.prePlannedTrips;
    }
    
    
    public void setQueryHolder(List<QueryObject> queryHolder) {
        this.queryHolder = queryHolder;
    }
    
    private List<HistoryRideObject> historyObjHolder;
    
    
    public List<HistoryRideObject> getHistoryObjHolder() {
        return this.historyObjHolder;
    }
    
    
    public void setHistoryObjHolder(List<HistoryRideObject> hsitoryObjHolder) {
        this.historyObjHolder = hsitoryObjHolder;
    }
    
    /**
     * Holds offers of the Driver, within are hitchhiker
     */
    private List<OfferObject> offerHolder;
    
    
    public List<OfferObject> getOfferHolder() {
        return this.offerHolder;
    }
    
    
    public void setOfferHolder(List<OfferObject> offerHolder) {
        this.offerHolder = offerHolder;
    }
    
    
    public void setMyTrips(List<PrePlannedTrip> prePlannedTrips) {
        this.prePlannedTrips = prePlannedTrips;
    }
    
    
    public int getQueryId() {
        return this.queryId;
    }
    
    
    public void setQueryId(int qid) {
        this.queryId = qid;
    }
    
    
    // TODO: Use Trip class instead
    public int getTripId() {
        return this.tripId;
    }
    
    
    // TODO: Use Trip class instead
    public void setTripId(int tripId) {
        // TODO Check trip id
        this.tripId = tripId;
    }
    
    /**
     * Own profile
     */
    private Profile ownProfile;
    
    
    /**
     * Private Constructor = Singleton
     */
    private Model() {
    }
    
    
    /**
     * Returns an instance of {@link Model}
     * 
     * @return instance
     */
    public static Model getInstance() {
        if (theInstance != null) {
            return theInstance;
        } else {
            theInstance = new Model();
            return theInstance;
        }
    }
    
    
    /**
     * Returns the Session ID
     * 
     * @return sid
     */
    public String getSid() {
        return this.sid;
    }
    
    
    /**
     * Set the actual sid
     * 
     * @param sid
     */
    public void setSid(String sid) {
        this.sid = sid;
    }
    
    
    /**
     * Returns the own profile
     * 
     * @return {@link Profile}
     */
    public Profile getOwnProfile() {
        return this.ownProfile;
    }
    
    
    /**
     * Set {@link Profile}
     * 
     * @param ownProfile
     */
    public void setOwnProfile(Profile ownProfile) {
        this.ownProfile = ownProfile;
    }
    
    
    public void setOpenTrip(Trip trip) {
        this.openTrip = trip;
    }
    
    
    public int getOpenTripId() {
        return this.openTrip == null ? -1 : this.openTrip.getId();
    }
    
    
    /**
     * @return List is not sorted!!
     */
    public ArrayList<Trip> getPlannedTrips() {
        return this.plannedTrips;
    }
    
    
    public void addPlannedTrips(Trip trip) throws NullPointerException {
        if (trip == null) {
            throw new NullPointerException();
        }
        this.plannedTrips.add(trip);
    }
    
    
    public int getUserId() {
        return this.id;
    }
    
    
    public void setUserId(int id) {
        this.id = id;
    }
    
    
    public boolean isLoggedIn() {
        return this.sid != null && !this.sid.equals("");
    }
    
    
    public void logout() {
        theInstance = null;
    }
}
