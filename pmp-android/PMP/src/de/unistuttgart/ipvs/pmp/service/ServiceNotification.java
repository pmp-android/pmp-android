/*
 * Copyright 2012 pmp-android development team
 * Project: PMP
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
package de.unistuttgart.ipvs.pmp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import de.unistuttgart.ipvs.pmp.PMPApplication;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.main.ActivityMain;

/**
 * Facade to easily use a {@link Notification} for when the {@link PMPService} is working.
 * 
 * @author Tobias Kuhn
 * 
 */
public class ServiceNotification {
    
    private static final int NOTIFICATION_ID = 1;
    private static boolean bound = false;
    private static boolean working = false;
    private static boolean displaying = false;
    
    
    /**
     * Informs the Notification whether the service is bound or not.
     * 
     * @param isBound
     */
    public static void setBound(boolean isBound) {
        bound = isBound;
        updateDisplay(PMPApplication.getContext());
    }
    
    
    /**
     * Informs the Notification whether the service is working or not.
     * 
     * @param isWorking
     */
    public static void setWorking(boolean isWorking) {
        working = isWorking;
        updateDisplay(PMPApplication.getContext());
    }
    
    
    /**
     * Updates the display of the notification.
     * 
     * @param context
     */
    private static void updateDisplay(Context context) {
        // get manager
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        // if need for display
        if (!displaying && (bound || working)) {
            Notification notification = makeNotification(context);
            
            if (bound && working) {
                notification.icon = R.drawable.svc_nfc_bound_working;
            } else if (bound) {
                notification.icon = R.drawable.svc_nfc_bound;
            } else if (working) {
                notification.icon = R.drawable.svc_nfc_working;
            }
            
            // go go go
            nm.notify(NOTIFICATION_ID, notification);
            displaying = true;
            
        } else if (displaying) {
            if (!(bound || working)) {
                // need for not displaying
                nm.cancel(NOTIFICATION_ID);
                displaying = false;
                
            } else {
                // need for updating
                Notification notification = makeNotification(context);
                
                if (bound && working) {
                    notification.icon = R.drawable.svc_nfc_bound_working;
                } else if (bound) {
                    notification.icon = R.drawable.svc_nfc_bound;
                } else if (working) {
                    notification.icon = R.drawable.svc_nfc_working;
                }
                
                nm.notify(NOTIFICATION_ID, notification);
                displaying = true;
            }
        }
    }
    
    
    /**
     * @param context
     * @return a new {@link Notification} for the {@link PMPService}
     */
    private static Notification makeNotification(Context context) {
        // make the notification
        Notification notification = new Notification(R.drawable.icon, context.getString(R.string.svc_nfc_title),
                System.currentTimeMillis());
        
        // have to specify that we open the main activity
        PendingIntent pi = PendingIntent.getActivity(context.getApplicationContext(), 0, new Intent(context,
                ActivityMain.class), 0);
        
        // remember we are a working service
        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        
        // have to specify these
        notification
                .setLatestEventInfo(PMPApplication.getContext(), context.getString(R.string.svc_nfc_extended_title),
                        context.getString(R.string.svc_nfc_description), pi);
        
        return notification;
    }
}
