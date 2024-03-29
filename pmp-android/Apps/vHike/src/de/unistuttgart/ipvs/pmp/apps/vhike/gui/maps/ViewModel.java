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
package de.unistuttgart.ipvs.pmp.apps.vhike.gui.maps;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.apps.vhike.Constants;
import de.unistuttgart.ipvs.pmp.apps.vhike.ctrl.Controller;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.adapter.NotificationAdapter;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.dialog.ContactDialog;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.maps.Route.Road;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.maps.Route.RoadOverlay;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.maps.Route.RoadProvider;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.Model;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.Profile;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.OfferObject;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.PositionObject;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.QueryObject;
import de.unistuttgart.ipvs.pmp.resourcegroups.contact.aidl.IContact;
import de.unistuttgart.ipvs.pmp.resourcegroups.location.aidl.IAbsoluteLocation;
import de.unistuttgart.ipvs.pmp.resourcegroups.vHikeWS.aidl.IvHikeWebservice;

/**
 * MapModel grants access to all elements needed to work with the map view
 * 
 * @author Andres Nguyen
 * 
 */
public class ViewModel {
    
    private List<ViewObject> lvo;
    Controller ctrl;
    private List<ViewObject> banned;
    MapView mapView;
    Context context;
    private float my_lat = 0;
    private float my_lon = 0;
    private boolean newFound = false;
    
    private boolean locationIsCanceled = false;
    private boolean queryIsCanceled = false;
    private boolean acceptedIsCanceled = false;
    private IvHikeWebservice ws;
    private IContact iContact;
    
    private Road mRoad;
    
    
    public void setvHikeWSRGandCreateController(IvHikeWebservice ws) {
        this.ws = ws;
        this.ctrl = new Controller(ws);
    }
    
    
    public IvHikeWebservice getvHikeRG() {
        return this.ws;
    }
    
    
    public void setContactRG(IContact iContact) {
        this.iContact = iContact;
    }
    
    
    public void cancelLocation() {
        this.locationIsCanceled = true;
    }
    
    
    public void startLocation() {
        this.locationIsCanceled = false;
    }
    
    
    public void cancelQuery() {
        this.queryIsCanceled = true;
    }
    
    
    public void startQuery() {
        this.queryIsCanceled = false;
    }
    
    
    public boolean locationIsCanceled() {
        return this.locationIsCanceled;
    }
    
    
    public boolean queryIsCanceled() {
        return this.queryIsCanceled;
    }
    
    
    public void startAccepted() {
        this.acceptedIsCanceled = false;
    }
    
    
    public boolean acceptedIsCanceled() {
        return this.acceptedIsCanceled;
    }
    
    
    public void resetTimers() {
        this.locationIsCanceled = false;
        this.queryIsCanceled = false;
        this.acceptedIsCanceled = false;
    }
    
    
    public List<ViewObject> getLVO() {
        return this.lvo;
    }
    
    
    private ViewModel() {
        this.lvo = new ArrayList<ViewObject>();
        this.banned = new ArrayList<ViewObject>();
    }
    
    
    public void addToBanned(ViewObject vObject) {
        this.banned.add(vObject);
        removeFromlvo(vObject);
    }
    
    
    private void removeFromlvo(ViewObject toRMVvObject) {
        int i = 0;
        for (ViewObject vObject : this.lvo) {
            
            if (vObject.getProfile().getID() == toRMVvObject.getProfile().getID()) {
                this.lvo.remove(i);
            }
            i++;
        }
    }
    
    
    public void updateView(int whichHitcher) {
        if (whichHitcher == 0) {
            updateLQO(new ArrayList<QueryObject>());
            ViewModel.getInstance().getDriverAdapter(this.context, this.mapView).notifyDataSetChanged();
        } else {
            updateLOO(new ArrayList<OfferObject>());
            ViewModel.getInstance().getPassengerAdapter(this.context, this.mapView).notifyDataSetChanged();
        }
        this.mapView.invalidate();
    }
    
    
    public void setNewFound() {
        this.newFound = false;
    }
    
    
    public void setMyPosition(float my_lat, float my_lon, int whichHitcher) {
        this.my_lat = my_lat;
        this.my_lon = my_lon;
        updateView(whichHitcher);
    }
    
    
    public float getMy_lat() {
        return this.my_lat;
    }
    
    
    public float getMy_lon() {
        return this.my_lon;
    }
    
    
    public void clearViewModel() {
        clearlvo();
        clearBanList();
        this.my_lat = 0;
        this.my_lon = 0;
    }
    
    
    private void clearlvo() {
        this.lvo.clear();
    }
    
    
    public void updateLQO(List<QueryObject> queries) {
        
        ViewModel.getInstance().clearDriverOverlayList();
        ViewModel.getInstance().getHitchPassengers().clear();
        this.newFound = false;
        
        // Add me to the mapView
        int my_new_lat = (int) (this.my_lat * 1E6);
        int my_new_lon = (int) (this.my_lon * 1E6);
        GeoPoint my_point = new GeoPoint(my_new_lat, my_new_lon);
        ViewModel.getInstance().add2DriverOverlay(this.context, my_point,
                Model.getInstance().getOwnProfile(),
                this.mapView, 0, my_point);
        
        try {
            for (QueryObject queryObject : queries) {
                float lat = queryObject.getCur_lat();
                float lon = queryObject.getCur_lon();
                if (!isInBanned(queryObject.getUserid())) {
                    if (isInLVO(queryObject.getUserid())) {
                        updateViewObject(queryObject.getUserid(), lat, lon);
                    } else {
                        Profile profile = this.ctrl.getProfile(Model.getInstance().getSid(),
                                queryObject.getUserid());
                        ViewObject vObject = new ViewObject(this.ws, lat, lon, profile);
                        vObject.setqObject(queryObject);
                        this.lvo.add(vObject);
                        this.newFound = true;
                    }
                }
            }
        } catch (Exception ex) {
        }
        
        for (ViewObject vObject : this.lvo) {
            int lat = (int) (vObject.getLat() * 1E6);
            int lng = (int) (vObject.getLon() * 1E6);
            GeoPoint gpsPassenger = new GeoPoint(lat, lng);
            if (vObject.getStatus() != Constants.V_OBJ_SATUS_PICKED_UP) {
                ViewModel.getInstance().add2DriverOverlay(this.context, gpsPassenger, vObject.getProfile(),
                        this.mapView, 1, my_point);
                // if drawing route is enabled for user, draw route
                if (isRouteDrawn(vObject.getProfile().getUsername())) {
                    
                    PositionObject myPos = this.ctrl.getUserPosition(Model.getInstance().getSid(), Model
                            .getInstance()
                            .getOwnProfile().getID());
                    PositionObject foundPos = this.ctrl.getUserPosition(Model.getInstance().getSid(), vObject
                            .getProfile()
                            .getID());
                    
                    double fromLat = myPos.getLat(), fromLon = myPos.getLon(), toLat = foundPos.getLat(), toLon = foundPos
                            .getLon();
                    String url = RoadProvider.getUrl(fromLat, fromLon, toLat, toLon);
                    InputStream is = getConnection(url);
                    this.mRoad = RoadProvider.getRoute(is);
                    this.driverOrpassenger = 0;
                    this.mHandler.sendEmptyMessage(0);
                }
            }
            ViewModel.getInstance().getHitchPassengers().add(vObject.getProfile());
            
            // Popup slider if new found
            if (this.newFound) {
                ViewModel.getInstance().fireNotification(this.context, vObject.getProfile(),
                        vObject.getProfile().getID(), 1, this.mapView, 0);
            }
            getDriverAdapter(this.context, this.mapView).notifyDataSetChanged();
            this.mapView.invalidate();
            this.mapView.postInvalidate();
        }
    }
    
    
    public void updateLOO(List<OfferObject> loo) {
        
        clearPassengerOverlayList();
        getHitchDrivers().clear();
        this.newFound = false;
        
        // Add me to the mapView
        int my_new_lat = (int) (this.my_lat * 1E6);
        int my_new_lon = (int) (this.my_lon * 1E6);
        GeoPoint my_point = new GeoPoint(my_new_lat, my_new_lon);
        add2PassengerOverlay(this.context, my_point, Model.getInstance().getOwnProfile(), this.mapView, 0,
                Model
                        .getInstance().getOwnProfile().getID());
        
        try {
            for (OfferObject offerObject : loo) {
                float lat = offerObject.getLat();
                float lng = offerObject.getLon();
                if (!isInBanned(offerObject.getUser_id())) {
                    if (isInLVO(offerObject.getUser_id())) {
                        updateViewObject(offerObject.getUser_id(), lat, lng);
                    } else {
                        Profile driver = this.ctrl.getProfile(Model.getInstance().getSid(),
                                offerObject.getUser_id());
                        ViewObject vObject = new ViewObject(this.ws, lat, lng, driver);
                        vObject.setoObject(offerObject);
                        this.lvo.add(vObject);
                        this.newFound = true;
                    }
                }
            }
        } catch (Exception ex) {
        }
        
        for (ViewObject vObject : this.lvo) {
            int lat = (int) (vObject.getLat() * 1E6);
            int lng = (int) (vObject.getLon() * 1E6);
            GeoPoint gpsDriver = new GeoPoint(lat, lng);
            add2PassengerOverlay(this.context, gpsDriver, vObject.getProfile(), this.mapView, 1, vObject
                    .getProfile()
                    .getID());
            // if route drawing is enabled
            if (isRouteDrawn(vObject.getProfile().getUsername())) {
                
                PositionObject myPos = this.ctrl.getUserPosition(Model.getInstance().getSid(), Model
                        .getInstance()
                        .getOwnProfile().getID());
                PositionObject foundPos = this.ctrl.getUserPosition(Model.getInstance().getSid(), vObject
                        .getProfile()
                        .getID());
                
                double fromLat = myPos.getLat(), fromLon = myPos.getLon(), toLat = foundPos.getLat(), toLon = foundPos
                        .getLon();
                String url = RoadProvider.getUrl(fromLat, fromLon, toLat, toLon);
                InputStream is = getConnection(url);
                this.mRoad = RoadProvider.getRoute(is);
                this.driverOrpassenger = 1;
                this.mHandler.sendEmptyMessage(0);
            }
            getHitchDrivers().add(vObject.getProfile());
            
            // notify user on hitchhiker found
            if (this.newFound) {
                fireNotification(this.context, vObject.getProfile(), vObject.getProfile().getID(), 0,
                        this.mapView, 1);
            }
            getPassengerAdapter(this.context, this.mapView).notifyDataSetChanged();
            this.mapView.invalidate();
            this.mapView.postInvalidate();
        }
    }
    
    
    /**
     * Updates the viewObject
     */
    private void updateViewObject(int userid, float lat, float lon) {
        for (ViewObject vObject : this.lvo) {
            if (vObject.getProfile().getID() == userid) {
                vObject.updatePos(lat, lon);
            }
        }
    }
    
    
    private void clearBanList() {
        this.banned.clear();
    }
    
    
    private boolean isInBanned(int userid) {
        boolean isInBanned = false;
        for (ViewObject vObject : this.banned) {
            if (vObject.getProfile().getID() == userid) {
                isInBanned = true;
            }
        }
        return isInBanned;
    }
    
    
    private boolean isInLVO(int userid) {
        boolean isInLVO = false;
        for (ViewObject vObject : this.lvo) {
            if (vObject.getProfile().getID() == userid) {
                isInLVO = true;
            }
        }
        return isInLVO;
    }
    
    private static ViewModel instance;
    private List<Overlay> mapDriverOverlays;
    private List<Overlay> mapPassengerOverlays;
    private String destination;
    private String destinationPassenger;
    private int numSeats = 0;
    private SlidingDrawer slider_Driver;
    private SlidingDrawer slider_Passenger;
    
    private List<Profile> hitchDrivers;
    private List<Profile> hitchPassengers;
    private List<Spinner> spinnersDest = new ArrayList<Spinner>();
    
    private Spinner clickedSpinner;
    
    private NotificationAdapter driverAdapter;
    private NotificationAdapter passengerAdapter;
    
    private Button btn_road_info;
    private EditText et_road_info;
    
    private boolean allowStartSearch4Query = false;
    
    
    public void setStartSearch4Query() {
        this.allowStartSearch4Query = true;
    }
    
    
    public void denyStartSearch4Query() {
        this.allowStartSearch4Query = false;
    }
    
    
    public boolean allowStartSearch4Query() {
        return this.allowStartSearch4Query;
    }
    
    
    public void setRoadInfoBtn(Button btnInfo) {
        this.btn_road_info = btnInfo;
    }
    
    
    public void setRoadInfoEt(EditText etInfo) {
        this.et_road_info = etInfo;
    }
    
    
    public void setBtnInfoVisibility(boolean visible) {
        if (visible) {
            this.btn_road_info.setVisibility(View.VISIBLE);
        } else {
            this.btn_road_info.setVisibility(View.GONE);
        }
    }
    
    
    public void setEtInfoVisibility(boolean visible) {
        if (visible) {
            this.et_road_info.setVisibility(View.VISIBLE);
        } else {
            this.et_road_info.setVisibility(View.GONE);
        }
    }
    
    
    public void setEtInfoText(String from, String to, String distance, String time) {
        this.et_road_info.setText("\n    From: " + from + " \n    To: " + to + " \n    Distance: " + distance
                + " \n    Time: " + time + "  \n\n   ");
    }
    
    
    public void resetRoadInfo() {
        this.et_road_info.setText("\n   From: \n   To: \n   Distance: \n   Time: \n");
    }
    
    
    public static ViewModel getInstance() {
        if (instance == null) {
            instance = new ViewModel();
        }
        return instance;
    }
    
    // Map containing all roads
    public HashMap<String, RoadOverlay> getAddedRoutes;
    // Map determining if route is drawn
    public HashMap<String, Boolean> getDrawnRoutes;
    public int driverOrpassenger;
    
    
    public void initRouteList() {
        this.getAddedRoutes = new HashMap<String, RoadOverlay>();
        this.getDrawnRoutes = new HashMap<String, Boolean>();
    }
    
    
    public RoadOverlay getRouteOverlay(String userRoute) {
        return this.getAddedRoutes.get(userRoute);
    }
    
    
    public boolean isRouteDrawn(String name) {
        try {
            if (this.getDrawnRoutes.get(name) == null) {
                return false;
            } else if (this.getDrawnRoutes.get(name)) {
                return true;
            } else {
                return false;
            }
        } catch (NullPointerException ne) {
            return false;
        }
    }
    
    
    public void removeRoute(RoadOverlay routeOverlay, boolean driver) {
        if (driver) {
            this.mapDriverOverlays.remove(routeOverlay);
        } else {
            this.mapPassengerOverlays.remove(routeOverlay);
        }
    }
    
    
    public void clearRoutes() {
        if (this.getDrawnRoutes != null) {
            this.getDrawnRoutes.clear();
        }
        if (this.getAddedRoutes != null) {
            this.getAddedRoutes.clear();
        }
        this.getDrawnRoutes = null;
        this.getAddedRoutes = null;
    }
    
    String foundUser;
    
    
    public void setFoundUsername(String userName) {
        this.foundUser = userName;
    }
    
    Handler mHandler = new Handler() {
        
        @Override
        public void handleMessage(android.os.Message msg) {
            RoadOverlay roadOverlay = new RoadOverlay(ViewModel.this.mRoad, ViewModel.this.mapView, false);
            if (ViewModel.this.driverOrpassenger == 0) {
                ViewModel.getInstance().getDriverOverlayList(ViewModel.this.mapView).add(roadOverlay);
            } else {
                ViewModel.getInstance().getPassengerOverlayList(ViewModel.this.mapView).add(roadOverlay);
            }
            ViewModel.getInstance().getDrawnRoutes.put(ViewModel.this.foundUser, true);
            ViewModel.getInstance().getAddedRoutes.put(ViewModel.this.foundUser, roadOverlay);
            ViewModel.this.mapView.invalidate();
        };
    };
    
    
    public InputStream getConnection(String url) {
        InputStream is = null;
        try {
            URLConnection conn = new URL(url).openConnection();
            is = conn.getInputStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;
    }
    
    
    public void updatePosition(IAbsoluteLocation loc, int whichHitcher) throws RemoteException {
        
        setMyPosition((float) loc.getLatitude(), (float) loc.getLongitude(), whichHitcher);
        
        /**
         * send server updated latitude and longitude
         */
        switch (this.ctrl.userUpdatePos(Model.getInstance().getSid(), (float) loc.getLatitude(),
                (float) loc.getLongitude())) {
            case Constants.STATUS_UPDATED:
                Toast.makeText(this.context, "Status updated", Toast.LENGTH_SHORT).show();
                break;
            case Constants.STATUS_UPTODATE:
                Toast.makeText(this.context, "Status up to date", Toast.LENGTH_SHORT).show();
                break;
            case Constants.STATUS_ERROR:
                Toast.makeText(this.context, "Error Update position", Toast.LENGTH_SHORT).show();
                break;
        }
        
    }
    
    
    /**
     * List containing all spinners/stop overs
     * 
     * @return
     */
    public List<Spinner> getDestinationSpinners() {
        return this.spinnersDest;
    }
    
    
    public void setClickedSpinner(Spinner spinner) {
        this.clickedSpinner = spinner;
    }
    
    
    public Spinner getClickedSpinner() {
        return this.clickedSpinner;
    }
    
    
    /**
     * Holds all overlays of the the drivers Mapview
     * 
     * @param mapView
     * @return
     */
    public List<Overlay> getDriverOverlayList(MapView mapView) {
        if (this.mapDriverOverlays == null) {
            this.mapDriverOverlays = mapView.getOverlays();
        }
        return this.mapDriverOverlays;
    }
    
    
    public void clearDriverOverlayList() {
        if (this.mapDriverOverlays != null) {
            this.mapDriverOverlays.clear();
            this.mapDriverOverlays = null;
        }
    }
    
    
    /**
     * Holds all overlays of the passengers MapView
     * 
     * @param mapView
     * @return
     */
    public List<Overlay> getPassengerOverlayList(MapView mapView) {
        if (this.mapPassengerOverlays == null) {
            this.mapPassengerOverlays = mapView.getOverlays();
        }
        return this.mapPassengerOverlays;
    }
    
    
    public void clearPassengerOverlayList() {
        if (this.mapPassengerOverlays != null) {
            this.mapPassengerOverlays.clear();
            this.mapPassengerOverlays = null;
        }
        
    }
    
    
    /**
     * set destination in RideActivity
     * 
     * @param spDestination
     */
    public void setDestination(Spinner spDestination) {
        this.destination = "";
        for (int i = 0; i < this.spinnersDest.size(); i++) {
            this.destination += ";" + this.spinnersDest.get(i).getSelectedItem().toString();
        }
        this.destination = this.destination + ";";
    }
    
    
    public void setDestination4Passenger(Spinner spDestination) {
        this.destinationPassenger = "";
        this.destinationPassenger = spDestination.getSelectedItem().toString();
    }
    
    
    public String getDestination4Passenger() {
        return this.destinationPassenger;
    }
    
    
    public void clearDestinations() {
        this.spinnersDest.clear();
    }
    
    
    /**
     * set number of seats available/needed depending on users wishes
     * 
     * @param spNumSeats
     */
    public void setNumSeats(Spinner spNumSeats) {
        this.numSeats = spNumSeats.getSelectedItemPosition() + 1;
        if (this.numSeats == 0) {
            this.numSeats = 1;
        }
    }
    
    
    public void setNewNumSeats(int newSeatNumber) {
        this.numSeats = newSeatNumber;
    }
    
    
    /**
     * get destination set by user
     * 
     * @return
     */
    public String getDestination() {
        return this.destination;
    }
    
    
    /**
     * get number of seats available set by driver or number of seats needed by
     * a passenger
     * 
     * @return
     */
    public int getNumSeats() {
        return this.numSeats;
    }
    
    
    public void initDriversList() {
        this.hitchDrivers = new ArrayList<Profile>();
    }
    
    
    public void initPassengersList() {
        this.hitchPassengers = new ArrayList<Profile>();
    }
    
    
    /**
     * list of all drivers who sent an invitation to passengers
     * 
     * @return
     */
    public List<Profile> getHitchDrivers() {
        if (this.hitchDrivers == null) {
            this.hitchDrivers = new ArrayList<Profile>();
        }
        return this.hitchDrivers;
    }
    
    
    public void clearHitchDrivers() {
        if (this.hitchDrivers != null) {
            this.hitchDrivers.clear();
            this.hitchDrivers = null;
        }
    }
    
    
    /**
     * list of passengers within perimeter of a driver
     * 
     * @return
     */
    public List<Profile> getHitchPassengers() {
        if (this.hitchPassengers == null) {
            this.hitchPassengers = new ArrayList<Profile>();
        }
        return this.hitchPassengers;
    }
    
    
    public void clearHitchPassengers() {
        if (this.hitchPassengers != null) {
            this.hitchPassengers.clear();
            this.hitchPassengers = null;
        }
    }
    
    
    /**
     * Adapter to show found drivers
     * 
     * @param context
     * @return
     */
    public NotificationAdapter getDriverAdapter(Context context, MapView mapView) {
        this.context = context;
        this.mapView = mapView;
        if (this.driverAdapter == null) {
            this.driverAdapter = new NotificationAdapter(this.ws, context, getHitchPassengers(), 0, mapView,
                    this.iContact);
        }
        return this.driverAdapter;
    }
    
    
    public void clearDriverNotificationAdapter() {
        if (this.driverAdapter != null) {
            this.driverAdapter = null;
            this.slider_Driver = null;
        }
    }
    
    
    /**
     * Adapter to show found passengers
     * 
     * @param context
     * @return
     */
    public NotificationAdapter getPassengerAdapter(Context context, MapView mapView) {
        this.context = context;
        this.mapView = mapView;
        if (this.passengerAdapter == null) {
            this.passengerAdapter = new NotificationAdapter(this.ws, context, getHitchDrivers(), 1, mapView,
                    this.iContact);
        }
        return this.passengerAdapter;
    }
    
    
    public void clearPassengerNotificationAdapter() {
        if (this.passengerAdapter != null) {
            this.passengerAdapter = null;
        }
    }
    
    
    /**
     * Simulating notifications per button click if button is pressed slider is
     * opened and user receives a notification via the status bar
     * 
     * @param context
     * @param profile
     */
    public void fireNotification(Context context, Profile profile, int profileID, int which1,
            MapView mapView,
            int whichSlider) {
        
        // get reference to notificationManager
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
        
        int icon = 0;
        CharSequence contentTitle;
        CharSequence contentText;
        CharSequence tickerText;
        
        // instantiate the notification
        if (which1 == 0) {
            icon = R.drawable.map_driver;
        } else {
            icon = R.drawable.map_passenger;
        }
        contentTitle = "vHike found a hitchhiker";
        contentText = "A Hitchhiker was found";
        tickerText = "vHike found a hitchhiker!";
        
        long when = System.currentTimeMillis();
        
        Notification notification = new Notification(icon, tickerText, when);
        notification.defaults |= Notification.DEFAULT_SOUND;
        
        // define the notification's message and PendingContent
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, null, 0);
        
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        
        // pass notification to notificationManager
        mNotificationManager.notify(0, notification);
        
        if (whichSlider == 0) {
            this.slider_Driver = (SlidingDrawer) ((Activity) context).findViewById(R.id.notiSlider);
            this.slider_Driver.open();
            getDriverAdapter(context, mapView).notifyDataSetChanged();
        } else {
            this.slider_Passenger = (SlidingDrawer) ((Activity) context).findViewById(R.id.slidingDrawer);
            this.slider_Passenger.open();
            getPassengerAdapter(context, mapView).notifyDataSetChanged();
        }
        
    }
    
    
    /**
     * add a passenger or driver to DriverOverlay
     * 
     * @param context
     * @param gpsPassenger
     * @param passenger
     * @param mapView
     */
    public void add2DriverOverlay(Context context, GeoPoint gps, Profile passenger, MapView mapView,
            int which1,
            GeoPoint mypoint) {
        Drawable drawable;
        if (which1 == 0) {
            Profile me = Model.getInstance().getOwnProfile();
            drawable = context.getResources().getDrawable(R.drawable.map_driver);
            DriverOverlay driverOverlay = new DriverOverlay(drawable, context, gps, me.getUsername(),
                    new ContactDialog(context, mapView, me.getUsername(), this.iContact, passenger,
                            this.ctrl, 0),
                    false);
            OverlayItem opDriverItem = new OverlayItem(gps, "Hop in man", "User: " + passenger.getUsername()
                    + ", Rating: " + passenger.getRating_avg());
            driverOverlay.addOverlay(opDriverItem);
            
            ViewModel.getInstance().getDriverOverlayList(mapView).add(driverOverlay);
            Log.i(this, "DriverOverlay: added Driver");
            mapView.invalidate();
        } else {
            drawable = context.getResources().getDrawable(R.drawable.map_passenger);
            
            PassengerOverlay passengerOverlay = new PassengerOverlay(drawable, context, mapView,
                    this.iContact,
                    passenger.getUsername(), gps, new ContactDialog(context, mapView,
                            passenger.getUsername(),
                            this.iContact, passenger, this.ctrl, 0), 0);
            OverlayItem opPassengerItem = new OverlayItem(gps, String.valueOf(passenger.getID()),
                    passenger.getUsername());
            passengerOverlay.addOverlay(opPassengerItem);
            
            // add found passenger to overlay
            ViewModel.getInstance().getDriverOverlayList(mapView).add(passengerOverlay);
            Log.i(this, "DriverOverlay: added Passenger");
            mapView.invalidate();
        }
    }
    
    
    /**
     * add a passenger or driver to PassengerOverlay
     * 
     * @param context
     * @param gpsDriver
     * @param driver
     * @param mapView
     */
    public void add2PassengerOverlay(Context context, GeoPoint gps, Profile profile, MapView mapView,
            int which1,
            int userID) {
        Drawable drawable;
        if (which1 == 0) {
            drawable = context.getResources().getDrawable(R.drawable.map_passenger);
            Profile me = Model.getInstance().getOwnProfile();
            PassengerOverlay passengerOverlay = new PassengerOverlay(drawable, context, mapView,
                    this.iContact,
                    me.getUsername(), gps, null, 1);
            OverlayItem opDriverItem = new OverlayItem(gps, "I need a ride", "User: " + profile.getUsername()
                    + ", Rating: " + profile.getRating_avg());
            passengerOverlay.addOverlay(opDriverItem);
            
            ViewModel.getInstance().getPassengerOverlayList(mapView).add(passengerOverlay);
            Log.i(this, "PassengerOverlay: added Passenger");
            mapView.invalidate();
        } else {
            drawable = context.getResources().getDrawable(R.drawable.map_driver);
            DriverOverlay driverOverlay = new DriverOverlay(drawable, context, gps, profile.getUsername(),
                    new ContactDialog(context, mapView, profile.getUsername(), this.iContact, profile,
                            this.ctrl, 1),
                    true);
            OverlayItem opPassengerItem = new OverlayItem(gps, "Hop in man", "User: " + profile.getUsername()
                    + ", Rating: " + profile.getRating_avg());
            driverOverlay.addOverlay(opPassengerItem);
            
            // add found passenger to overlay
            ViewModel.getInstance().getPassengerOverlayList(mapView).add(driverOverlay);
            Log.i(this, "PassengerOverlay: added Driver");
            mapView.invalidate();
        }
    }
    
}
