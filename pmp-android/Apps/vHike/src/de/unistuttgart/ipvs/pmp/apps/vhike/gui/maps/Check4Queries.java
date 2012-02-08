package de.unistuttgart.ipvs.pmp.apps.vhike.gui.maps;

import java.util.List;
import java.util.TimerTask;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.apps.vhike.ctrl.Controller;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.Model;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.Profile;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.QueryObject;

public class Check4Queries extends TimerTask {
    
    private Handler handler;
    private Controller ctrl;
    private MapView mapView;
    private Context context;
    //    private Location location;
    
    private double lat;
    private double lng;
    
    
    public Check4Queries(MapView mapView, Context context, double lat, double lng) {
        this.mapView = mapView;
        this.context = context;
        //        this.location = location;
        this.lat = lat;
        this.lng = lng;
        
        handler = new Handler();
        ctrl = new Controller();
    }
    
    
    @Override
    public void run() {
        handler.post(new Runnable() {
            
            @Override
            public void run() {
                Log.i(this, "Searching...");
                
                MapModel.getInstance().clearDriverOverlayList();
                MapModel.getInstance().getHitchPassengers().clear();
                
                Profile me = Model.getInstance().getOwnProfile();
                int lati = (int) (Model.getInstance().getLatitude() * 1E6);
                int lngi = (int) (Model.getInstance().getLongtitude() * 1E6);
                GeoPoint gps = new GeoPoint(lati, lngi);
                MapModel.getInstance().add2DriverOverlay(context, gps, me, mapView, 0, 0);
                
                /**
                 * search for passenger within perimeter (10 km for testing
                 * purposes)
                 */
                List<QueryObject> lqo = ctrl.searchQuery(Model.getInstance().getSid(), (float) lat, (float) lng, 10000);
                
                if (lqo != null) {
                    for (int i = 0; i < lqo.size(); i++) {
                        int lat = (int) (lqo.get(i).getCur_lat() * 1E6);
                        int lng = (int) (lqo.get(i).getCur_lon() * 1E6);
                        GeoPoint gpsPassenger = new GeoPoint(lat, lng);
                        
                        // create Profile of found passenger
                        Profile passenger = ctrl.getProfile(Model.getInstance().getSid(), lqo.get(i).getUserid());
                        
                        if (!Model.getInstance().isInBannList(lqo.get(i).getUserid())) {
                            // add an passenger to overlay
                            MapModel.getInstance().add2DriverOverlay(context, gpsPassenger, passenger, mapView, 1,
                                    lqo.get(i).getUserid());
                            
                            // add up ID for statusbar notification
                            
                            // add passenger to slider list
                            MapModel.getInstance().getHitchPassengers().add(passenger);
                            
                            if (!Model.getInstance().isFinded(lqo.get(i).getUserid())) {
                                // notify user
                                MapModel.getInstance().fireNotification(context, passenger, lqo.get(i).getUserid(), 0,
                                        0, mapView);
                            }
                            
                            // notify list
                            MapModel.getInstance().getDriverAdapter(context, mapView).notifyDataSetChanged();
                            mapView.invalidate();
                            mapView.postInvalidate();
                            
                            Model.getInstance().addToFoundUsers(lqo.get(i).getUserid());
                            Log.i(this, "Found passenger");
                        }
                        
                    }
                } else {
                    Toast.makeText(context, "No hitchhikers within perimeter", Toast.LENGTH_SHORT).show();
                }
            }
            
        });
    }
    
}