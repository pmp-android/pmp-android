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

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

import de.unistuttgart.ipvs.pmp.apps.vhike.R;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.dialog.ContactDialog;
import de.unistuttgart.ipvs.pmp.resourcegroups.contact.aidl.IContact;

/**
 * Overlay for passengers, handling the drawable icon, tap actions
 * 
 * @author Andre Nguyen
 * 
 */
@SuppressWarnings("rawtypes")
public class PassengerOverlay extends ItemizedOverlay {
    
    private Context mContext;
    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
    private String name;
    private GeoPoint mGps;
    
    private ContactDialog contactDialog;
    private int itsMe;
    
    
    /**
     * set passenger icon through drawable and context for onTap method
     * 
     * @param defaultMarker
     * @param context
     */
    public PassengerOverlay(Drawable defaultMarker, Context context, MapView mapView, IContact iContact,
            String name,
            GeoPoint gps, ContactDialog contactDialog, int itsMe) {
        super(boundCenterBottom(defaultMarker));
        this.mContext = context;
        this.name = name;
        this.mGps = gps;
        
        this.contactDialog = contactDialog;
        this.itsMe = itsMe;
    }
    
    
    public void addOverlay(OverlayItem overlay) {
        this.mOverlays.add(overlay);
        populate();
    }
    
    
    /**
     * Opens a dialog containing short information about the passenger
     */
    @Override
    protected boolean onTap(int i) {
        OverlayItem item = this.mOverlays.get(i);
        
        //if 0 passenger, if 1 user
        if (this.itsMe == 0) {
            this.contactDialog.show();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this.mContext);
            dialog.setTitle(item.getTitle());
            dialog.setMessage(item.getSnippet());
            dialog.show();
        }
        return true;
    }
    
    
    @Override
    protected OverlayItem createItem(int i) {
        return this.mOverlays.get(i);
    }
    
    
    @Override
    public int size() {
        return this.mOverlays.size();
    }
    
    
    @Override
    public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
        super.draw(canvas, mapView, shadow);
        Projection projection = mapView.getProjection();
        
        Point point = new Point();
        Paint paint = new Paint();
        paint.setColor(this.mContext.getResources().getColor(R.color.orange));
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        projection.toPixels(this.mGps, point);
        
        canvas.drawText(this.name, point.x - 20, point.y - 55, paint);
        
        return true;
    }
    
}
