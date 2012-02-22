package de.unistuttgart.ipvs.pmp.apps.vhike.gui;

import java.util.Timer;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.api.PMP;
import de.unistuttgart.ipvs.pmp.api.PMPResourceIdentifier;
import de.unistuttgart.ipvs.pmp.apps.vhike.Constants;
import de.unistuttgart.ipvs.pmp.apps.vhike.ctrl.Controller;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.dialog.vhikeDialogs;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.maps.Check4Queries;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.maps.LocationUpdateHandler;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.maps.ViewModel;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.Model;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.Profile;
import de.unistuttgart.ipvs.pmp.resourcegroups.location.aidl.IAbsoluteLocation;

/**
 * DriverViewActivity displays driver with his perimeter, found hitchhikers, a list of found hitchhikers, the
 * possibility to update the available seats, send offers or reject found hitchhikers and to pick up potential
 * passengers and to
 * end a trip
 * 
 * @author Andre Nguyen
 * 
 */
public class DriverViewActivity extends MapActivity {
    
    // Re
    private static final String RG_NAME = "de.unistuttgart.ipvs.pmp.resourcegroups.location";
    private static final String R_NAME = "absoluteLocationResource";
    
    private static final PMPResourceIdentifier R_ID = PMPResourceIdentifier.make(RG_NAME, R_NAME);
    
    private Context context;
    private MapView mapView;
    private MapController mapController;
    private LocationManager locationManager;
    private LocationUpdateHandler luh;
    
    private Timer timer;
    private Handler handler;
    
    private Controller ctrl;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driverview);
        
        handler = new Handler();
        ctrl = new Controller();
        ViewModel.getInstance().initPassengersList();
        
        setMapView();
        showHitchhikers();
        startTripByUpdating();
        
        vhikeDialogs.getInstance().getAnnouncePD(DriverViewActivity.this).dismiss();
        vhikeDialogs.getInstance().clearAnnouncPD();
        
    }
    
    
    public DriverViewActivity() {
        this.context = DriverViewActivity.this;
    }
    
    
    /**
     * adds passengers (hitchhikers) to the notification slider
     */
    private void showHitchhikers() {
        
        ListView pLV = (ListView) findViewById(R.id.ListView_SearchingHitchhikers);
        pLV.setClickable(true);
        pLV.setAdapter(ViewModel.getInstance().getDriverAdapter(context, mapView));
    }
    
    
    /**
     * adds hitchhiker/passenger to hitchhiker list
     * 
     * @param hitchhiker
     */
    public void addHitchhiker(Profile hitchhiker) {
        ViewModel.getInstance().getHitchPassengers().add(hitchhiker);
        ViewModel.getInstance().getDriverAdapter(context, mapView).notifyDataSetChanged();
    }
    
    
    /**
     * displays the map from xml file and sets the zoom buttons
     */
    @SuppressWarnings("deprecation")
    private void setMapView() {
        mapView = (MapView) findViewById(R.id.driverMapView);
        LinearLayout zoomView = (LinearLayout) mapView.getZoomControls();
        
        zoomView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        
        zoomView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        zoomView.setVerticalScrollBarEnabled(true);
        mapView.addView(zoomView);
        
        // mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
    }
    
    
    /**
     * get current location and notify server that a trip was announced for
     * possible passengers to search for
     */
    private void startTripByUpdating() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        luh = new LocationUpdateHandler(context, locationManager, mapView, mapController, 0);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, luh);
        
//        IBinder binder = PMP.get().getResourceFromCache(R_ID);
//        IAbsoluteLocation loc = IAbsoluteLocation.Stub.asInterface(binder);
//        try {
//            loc.startLocationLookup(5000, 10.0F);
//            
//            this.handler.post(new Runnable() {
//                
//                public void run() {
//                    Toast.makeText(DriverViewActivity.this, "Location Resource loaded.", Toast.LENGTH_SHORT).show();
//                }
//            });
//            
            // startContinousLookup();
            // Start Check4Queries Class to check for queries
            Check4Queries c4q = new Check4Queries();
            timer = new Timer();
            timer.schedule(c4q, 300, 10000);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        } catch (SecurityException e) {
//            e.printStackTrace();
//            this.handler.post(new Runnable() {
//                
//                public void run() {
//                    Toast.makeText(DriverViewActivity.this, "Please enable the Service Feature.", Toast.LENGTH_SHORT)
//                            .show();
//                }
//            });
//        }
//        
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.driverview_menu, menu);
        return true;
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.mi_endTrip):
                
                switch (ctrl.endTrip(Model.getInstance().getSid(), Model.getInstance().getTripId())) {
                    case (Constants.STATUS_UPDATED): {
                        
                        ViewModel.getInstance().clearDriverOverlayList();
                        ViewModel.getInstance().clearViewModel();
                        ViewModel.getInstance().clearHitchPassengers();
                        ViewModel.getInstance().clearDriverNotificationAdapter();
                        locationManager.removeUpdates(luh);
                        
                        timer.cancel();
                        
                        Toast.makeText(DriverViewActivity.this, "Trip ended", Toast.LENGTH_LONG).show();
                        this.finish();
                        break;
                    }
                    case (Constants.STATUS_UPTODATE): {
                        Toast.makeText(DriverViewActivity.this, "Up to date", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case (Constants.STATUS_NOTRIP): {
                        Toast.makeText(DriverViewActivity.this, "No trip", Toast.LENGTH_SHORT).show();
                        DriverViewActivity.this.finish();
                        break;
                    }
                    case (Constants.STATUS_HASENDED): {
                        Toast.makeText(DriverViewActivity.this, "Trip ended", Toast.LENGTH_SHORT).show();
                        DriverViewActivity.this.finish();
                        break;
                    }
                    case (Constants.STATUS_INVALID_USER):
                        Toast.makeText(DriverViewActivity.this, "Invalid user", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            
            case (R.id.mi_updateData):
                vhikeDialogs.getInstance().getUpdateDataDialog(context).show();
                break;
        }
        return true;
    }
    
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
}
