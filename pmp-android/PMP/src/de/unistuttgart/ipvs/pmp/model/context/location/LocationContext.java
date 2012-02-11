package de.unistuttgart.ipvs.pmp.model.context.location;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import de.unistuttgart.ipvs.pmp.PMPApplication;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.model.context.IContext;
import de.unistuttgart.ipvs.pmp.model.context.IContextView;

public class LocationContext implements IContext, LocationListener {
    
    /**
     * Stop if you have a position as close as this
     */
    private static final float BEST_ACCURACY = 25.0f;
    
    /**
     * And as new as this
     */
    private static final long BEST_TIME_DELTA = 60000L;
    
    /**
     * But don't waste more battery than this period
     */
    private static final long MAXIMUM_LOCATION_ESTIMTAING_TIME = 25000L;
    
    /**
     * The maximum accuracy loss, if a newer update is to be added
     */
    private static final float MAXIMUM_ACCURACY_LOSS = 2.5f;
    
    /**
     * The maximum accuracy above which results are rejected
     */
    private static final float ACCURACY_REJECT_LIMIT = 1000.0f;
    
    /**
     * The maximal time difference above which results are rejected
     */
    private static final long TIME_DELTA_REJECT_LIMIT = 300000L;
    /**
     * The possibly waiting {@link Thread}.
     */
    private volatile Thread waiter;
    
    private IContextView view = null;
    
    private LocationContextState lastState;
    
    
    public LocationContext() {
        this.lastState = new LocationContextState();
    }
    
    
    @Override
    public String getIdentifier() {
        return "LocationContext";
    }
    
    
    @Override
    public String getName() {
        return PMPApplication.getContext().getString(R.string.contexts_location_name);
    }
    
    
    @Override
    public String getDescription() {
        return PMPApplication.getContext().getString(R.string.contexts_location_desc);
    }
    
    
    @Override
    public Drawable getIcon() {
        return PMPApplication.getContext().getResources().getDrawable(R.drawable.contexts_location_icon);
    }
    
    
    @Override
    public IContextView getView(Context context) {
        if (this.view == null) {
            this.view = new LocationContextView(context);
        }
        return this.view;
    }
    
    
    @Override
    public long update(Context context) {
        this.lastState.unset();
        this.waiter = null;
        
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        
        onLocationChanged(lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
        onLocationChanged(lm.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        
        // if these were already fine, no need to do anything
        if ((this.lastState.getAccuracy() <= BEST_ACCURACY)
                && (this.lastState.getTime() >= System.currentTimeMillis() - BEST_TIME_DELTA)) {
            return 0L;
        }
        
        this.waiter = Thread.currentThread();
        
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        
        try {
            Thread.sleep(MAXIMUM_LOCATION_ESTIMTAING_TIME);
        } catch (InterruptedException e) {
            // Do nothing, desired behavior
        } finally {
            lm.removeUpdates(this);
        }
        
        // reject, if not sufficing
        if (this.lastState.isSet()
                && ((this.lastState.getAccuracy() > ACCURACY_REJECT_LIMIT) || (System.currentTimeMillis()
                        - this.lastState.getTime() > TIME_DELTA_REJECT_LIMIT))) {
            this.lastState.unset();
        }
        
        return 0L;
    }
    
    
    @Override
    public boolean getLastState(String condition) {
        if (this.lastState.isSet()) {
            LocationContextCondition lcc = LocationContextCondition.parse(condition);
            return lcc.satisfiedIn(this.lastState);
        } else {
            return false;
        }
    }
    
    
    @Override
    public void onLocationChanged(Location location) {
        boolean update = false;
        
        // if it's better, take it
        update |= (location.getAccuracy() < this.lastState.getAccuracy());
        // if it's not too worse, but newer, take it
        update |= (location.getAccuracy() < this.lastState.getAccuracy() * MAXIMUM_ACCURACY_LOSS)
                && (location.getTime() > this.lastState.getTime());
        
        if (update) {
            this.lastState.update(location);
        }
        
        // if that's enough, interrupt the waiting
        if ((this.waiter != null) && (this.lastState.getAccuracy() <= BEST_ACCURACY)
                && (this.lastState.getTime() >= System.currentTimeMillis() - BEST_TIME_DELTA)) {
            this.waiter.interrupt();
        }
    }
    
    
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Don't need this
    }
    
    
    @Override
    public void onProviderEnabled(String provider) {
        // Don't need this
    }
    
    
    @Override
    public void onProviderDisabled(String provider) {
        // Don't need this
    }
}