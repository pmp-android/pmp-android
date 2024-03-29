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
package de.unistuttgart.ipvs.pmp.apps.vhike.gui.maps.Route;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.maps.ViewModel;

/**
 * Overlay for a route from a driver to passenger
 * 
 * @author Andre Nguyen
 * 
 */
public class RoadOverlay extends com.google.android.maps.Overlay {
    
    Road mRoad;
    ArrayList<GeoPoint> mPoints;
    
    
    public RoadOverlay(Road road, MapView mv, boolean firstDraw) {
        this.mRoad = road;
        if (road.mRoute.length > 0) {
            this.mPoints = new ArrayList<GeoPoint>();
            for (double[] element : road.mRoute) {
                this.mPoints.add(new GeoPoint((int) (element[1] * 1000000),
                        (int) (element[0] * 1000000)));
            }
            int moveToLat = (this.mPoints.get(0).getLatitudeE6() + (this.mPoints.get(this.mPoints.size() - 1)
                    .getLatitudeE6() - this.mPoints
                    .get(0).getLatitudeE6()) / 2);
            int moveToLong = (this.mPoints.get(0).getLongitudeE6() + (this.mPoints.get(
                    this.mPoints.size() - 1)
                    .getLongitudeE6() - this.mPoints
                    .get(0).getLongitudeE6()) / 2);
            GeoPoint moveTo = new GeoPoint(moveToLat, moveToLong);
            
            String distance = parseDistance(this.mRoad.mDescription);
            String time = parseTime(this.mRoad.mDescription);
            String from = parseFrom(this.mRoad.mName);
            String to = parseTo(this.mRoad.mName);
            ViewModel.getInstance().setEtInfoText(from, to, distance + " km", time);
            int zoom = 0;
            try {
                zoom = zoomLevel(Double.valueOf(distance));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                zoom = 15;
            }
            
            MapController mapController = mv.getController();
            mapController.animateTo(moveTo);
            
            mapController.setZoom(zoom);
            
        }
    }
    
    
    /**
     * Inputs roadDescrition and parses the distance
     * 
     * @param roadDescription
     * @return distance
     */
    private String parseDistance(String roadDescription) {
        String[] temp;
        String dist = "";
        temp = roadDescription.split("mi");
        temp = temp[0].split("\\:");
        dist = temp[1];
        return dist;
    }
    
    
    private String parseTime(String roadDescription) {
        String[] temp;
        temp = roadDescription.split("\\(");
        temp = temp[1].split("\\)");
        return temp[0];
    }
    
    
    private String parseFrom(String roadName) {
        String[] temp;
        temp = roadName.split(" to ");
        return temp[0];
    }
    
    
    private String parseTo(String roadName) {
        String[] temp;
        temp = roadName.split(" to ");
        return temp[1];
    }
    
    
    /**
     * Calculates a fitting zoomLevel according to a routes distance
     * 
     * @param distance
     * @return zoomLevel
     */
    public static byte zoomLevel(double distance) {
        byte zoom = 1;
        double E = 40075;
        Log.i("Astrology", "result: " + (Math.log(E / distance) / Math.log(2) + 1));
        zoom = (byte) Math.round(Math.log(E / distance) / Math.log(2) + 1);
        // to avoid exeptions
        if (zoom > 21) {
            zoom = 21;
        }
        if (zoom < 1) {
            zoom = 1;
        }
        
        return zoom;
    }
    
    
    @Override
    public boolean draw(Canvas canvas, MapView mv, boolean shadow, long when) {
        super.draw(canvas, mv, shadow);
        drawPath(mv, canvas);
        return true;
    }
    
    
    public void drawPath(MapView mv, Canvas canvas) {
        int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        try {
            for (int i = 0; i < this.mPoints.size(); i++) {
                Point point = new Point();
                mv.getProjection().toPixels(this.mPoints.get(i), point);
                x2 = point.x;
                y2 = point.y;
                if (i > 0) {
                    canvas.drawLine(x1, y1, x2, y2, paint);
                }
                x1 = x2;
                y1 = y2;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            ViewModel.getInstance().getDriverOverlayList(mv).clear();
            ViewModel.getInstance().clearRoutes();
            ViewModel.getInstance().setBtnInfoVisibility(false);
            ViewModel.getInstance().setEtInfoVisibility(false);
            
        }
    }
}
