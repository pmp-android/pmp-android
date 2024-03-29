/*
 * Copyright 2012 pmp-android development team
 * Project: NotificationTestApp
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
package de.unistuttgart.ipvs.pmp.apps.notificationtestapp.gui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.api.PMP;
import de.unistuttgart.ipvs.pmp.api.PMPResourceIdentifier;
import de.unistuttgart.ipvs.pmp.api.handler.PMPRequestResourceHandler;
import de.unistuttgart.ipvs.pmp.resourcegroups.notification.aidl.INotification;

public class NotificationTestAppActivity extends Activity {
    
    private static final String RG_NAME = "de.unistuttgart.ipvs.pmp.resourcegroups.notification";
    private static final String R_NAME = "notificationResource";
    
    private static final PMPResourceIdentifier R_ID = PMPResourceIdentifier.make(RG_NAME, R_NAME);
    
    public Handler handler;
    public int nee;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.handler = new Handler();
        PMP.get(getApplication());
        setContentView(de.unistuttgart.ipvs.pmp.apps.notificationtestapp.R.layout.main);
    }
    
    
    @Override
    protected void onResume() {
        super.onResume();
        
        PMP.get().getResource(R_ID, new PMPRequestResourceHandler() {
            
            @Override
            public void onReceiveResource(PMPResourceIdentifier resource, IBinder binder, boolean isMocked) {
                resourceCached();
            }
            
            
            @Override
            public void onBindingFailed() {
                Toast.makeText(NotificationTestAppActivity.this, "Binding Resource failed", Toast.LENGTH_LONG).show();
            }
        });
    }
    
    
    @Override
    protected void onPause() {
        super.onPause();
    }
    
    
    private void resourceCached() {
        IBinder binder = PMP.get().getResourceFromCache(R_ID);
        
        if (binder == null) {
            
            this.handler.post(new Runnable() {
                
                public void run() {
                    Toast.makeText(NotificationTestAppActivity.this,
                            "PMP said something like 'resource group does not exists'.", Toast.LENGTH_SHORT).show();
                }
            });
            
            return;
        }
        
        INotification not = INotification.Stub.asInterface(binder);
        try {
            not.notify("TestNotification", "Hello", "This is the notification from the Testapp");
            
            this.handler.post(new Runnable() {
                
                public void run() {
                    Toast.makeText(NotificationTestAppActivity.this, "Notification Resource loaded.",
                            Toast.LENGTH_SHORT).show();
                }
            });
            
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
            this.handler.post(new Runnable() {
                
                public void run() {
                    Toast.makeText(NotificationTestAppActivity.this, "Please enable the Service Feature.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        
    };
}
