/**
 * This service provides methods for checking Service Features and using IPC
 */
package de.unistuttgart.ipvs.pmp.apps.vhike.ctrl;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;
import de.unistuttgart.ipvs.pmp.api.PMP;
import de.unistuttgart.ipvs.pmp.api.PMPResourceIdentifier;
import de.unistuttgart.ipvs.pmp.api.handler.PMPRequestResourceHandler;
import de.unistuttgart.ipvs.pmp.apps.vhike.Constants;
import de.unistuttgart.ipvs.pmp.resourcegroups.location.aidl.IAbsoluteLocation;
import de.unistuttgart.ipvs.pmp.resourcegroups.notification.aidl.INotification;
import de.unistuttgart.ipvs.pmp.resourcegroups.vHikeWS.aidl.IvHikeWebservice;

/**
 * @author Dang Huynh
 * 
 */
public class vHikeService extends Service {
    
    private static vHikeService instance;
    private static final String TAG = "vHikeService";
    private static final String[] serviceFeatures = {
            "useAbsoluteLocation", "hideExactLocation", "hideContactInfo",
            "contactPremium", "notification", "vhikeWebService" };
    private static final PMPResourceIdentifier RGLocationID = PMPResourceIdentifier.make(
            "de.unistuttgart.ipvs.pmp.resourcegroups.location", "absoluteLocationResource");
    private static final PMPResourceIdentifier RGVHikeID = PMPResourceIdentifier.make(
            "de.unistuttgart.ipvs.pmp.resourcegroups.vHikeWS", "vHikeWebserviceResource");
    private static final PMPResourceIdentifier RGNotificationID = PMPResourceIdentifier.make(
            "de.unistuttgart.ipvs.pmp.resourcegroups.notification", "NotificationResource");
    private static final PMPResourceIdentifier[] resourceGroupIDs = { RGLocationID, RGVHikeID, RGNotificationID };
    
    
    /**
     * 
     * @return Returns an instance of vHikeService
     */
    public static vHikeService getInstance() {
        if (instance == null) {
            instance = new vHikeService();
        }
        return instance;
    }
    
    
    /**
     * Private constructor
     */
    public vHikeService() {
        app = de.unistuttgart.ipvs.pmp.apps.vhike.Application.getApp();
    }
    
    private Application app = null;
    
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate");
    }
    
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.v(TAG, "onstartcommand");
        return START_STICKY;
    }
    
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }
    
    
    /**
     * 
     * @return an instance of PMP
     */
    //    private IPMP getPMP() {
    //        try {
    //            if (pmp == null) {
    //                app = getApplication();
    //                Log.v(TAG, app.getPackageName());
    //                if (app == null) {
    //                    throw new NullPointerException("Application is null. Please start the vHikeService first");
    //                }
    //                pmp = PMP.get(app);
    //            }
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //        return pmp;
    //    }
    
    /**
     * Check whether a service feature is enabled
     * 
     * @param serviceFeatureID
     *            See {@link Constants}.SF_
     * @return True if the service feature is enabled, false otherwise
     */
    public boolean isServiceFeatureEnabled(int serviceFeatureID) {
        try {
            return PMP.get(app).areServiceFeaturesEnabled(serviceFeatures[serviceFeatureID]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    
    /**
     * Check whether a combination of service features is enabled
     * 
     * @param serviceFeatureIDs
     *            See {@link Constants}.SF_
     * @return True if the service features are enabled, false otherwise
     */
    public boolean areServiceFeatureEnabled(int[] serviceFeatureIDs) {
        try {
            List<String> l = new ArrayList<String>(serviceFeatureIDs.length);
            for (Integer i : serviceFeatureIDs) {
                l.add(serviceFeatures[i]);
            }
            return PMP.get(app).areServiceFeaturesEnabled(l);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    
    /**
     * Show the activity to request a service feature
     * 
     * @param activity
     *            (this)
     * @param serviceFeatureID
     *            See {@link Constants}.SF_
     */
    public void requestServiceFeature(Activity activity, int serviceFeatureID) {
        Log.v(TAG, app.getPackageName());
        Log.v(TAG, activity.getLocalClassName());
        PMP.get(app).requestServiceFeatures(activity, serviceFeatures[serviceFeatureID]);
    }
    
    
    /**
     * Show the activity to request a set of service features
     * 
     * @param activity
     * @param serviceFeatureIDs
     */
    public void requestServiceFeatures(Activity activity, int[] serviceFeatureIDs) {
        List<String> l = new ArrayList<String>(serviceFeatureIDs.length);
        for (int i : serviceFeatureIDs) {
            l.add(serviceFeatures[i]);
        }
        PMP.get(app).requestServiceFeatures(activity, l);
    }
    
    private IBinder binder;
    private IAbsoluteLocation loc;
    private IvHikeWebservice ws;
    private INotification noti;
    
    
    /*    private IBinder getResourceGroup(final PMPResourceIdentifier id) {
    //        try {
    //            binder = null;
    //            //            PMP.get(app) = PMP.get(getApplication());
    //            if (PMP.get(app).isResourceCached(id)) {
    //                binder = PMP.get(app).getResourceFromCache(id);
    //            } else {
    //                //                Handler handler = new Handler();
    //                //                latch = new CountDownLatch(1);
    //                //                handler.postAtFrontOfQueue(new ResourceGroupBinder(id, latch));
    //                //                latch.await(2, TimeUnit.SECONDS);
    //                //                Log.v(TAG, "latch wait");
    //                
    //                //                BinderTask t = new BinderTask();
    //                //                t.execute(id);
    //                ////                if (t.getStatus() != null) {
    //                ////                    Log.v(TAG, "task" + t.getStatus().toString());
    //                ////                } else {
    //                ////                    System.out.println("task status");
    //                ////                }
    //                //                System.out.println("waiting");
    //                ////                int wait = 100;
    //                ////                while (binderLock && (wait > 0)) {
    //                ////                    System.out.println("wait");
    //                ////                    Thread.sleep(1000);
    //                ////                    System.out.println("wake");
    //                ////                    wait--;
    //                ////                }
    //                //                synchronized (sync) {
    //                //                                        
    //                //                }
    //                //                System.out.println("wait over");
    //                //                
    //                //                //                PMP.get(app).getResource(id, new PMPRequestResourceHandler() {
    //                //                //                    
    //                //                //                    @Override
    //                //                //                    public void onReceiveResource(PMPResourceIdentifier resource, IBinder binder) {
    //                //                //                        super.onReceiveResource(resource, binder);
    //                //                //                        Log.i(TAG, "on receiving");
    //                //                //                        resourceReady();
    //                //                //                    }
    //                //                //                });
    //                //                ////                latch = new CountDownLatch(1);
    //                //                //                ResourceGroupBinder rgb = new ResourceGroupBinder(id, latch);
    //                //                //                (new Thread(rgb)).start();
    //                //                //                Log.i(TAG, "waiting...");
    //                //                //                latch.await(20, TimeUnit.SECONDS);
    //                //                //                Log.v(TAG, "returned");
    //                //                //                synchronized (binder) {
    //                //                //                    binder.wait(1000);
    //                //                //                }
    //                //                //                
    //                //                //                //                synchronized (Thread.currentThread()) {
    //                //                //                //                    wait(10000);
    //                //                //                //                }
    //                //                //                //                binder = rgb.getBinder();
    //                //                if (PMP.get(app).isResourceCached(id)) {
    //                //                    binder = PMP.get(app).getResourceFromCache(id);
    //                //                }
    //            }
    //        } catch (Exception e) {
    //            // TODO handle exception
    //            e.printStackTrace();
    //        }
    //        return binder;
    //    }
        
        
        //    private IBinder getRG(PMPResourceIdentifier id) {
        //        try {
        //            PMP.get(app).getResource(id);
        //            int wait = 100;
        //            while (!PMP.get(app).isResourceCached(id) && wait > 0) {
        //                Thread.currentThread().wait(100);
        //                wait--;
        //            }
        //            if (PMP.get(app).isResourceCached(id)) {
        //                return PMP.get(app).getResourceFromCache(id);
        //            }
        //        } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        }
        //        return null;
        //    }*/
    
    public IInterface requestResourceGroup(ResourceGroupReadyActivity activity, int resourceGroupId) {
        switch (resourceGroupId) {
            case Constants.RG_LOCATION:
                if (loc == null)
                    reloadResourceGroup(activity, resourceGroupId);
                else
                    return loc;
                break;
            case Constants.RG_NOTIFICATION:
                if (noti == null)
                    reloadResourceGroup(activity, resourceGroupId);
                else
                    return noti;
                break;
            case Constants.RG_VHIKE_WEBSERVICE:
                if (ws == null)
                    reloadResourceGroup(activity, resourceGroupId);
                else
                    return ws;
                break;
        }
        return null;
    }
    
    
    public void loadResourceGroup(ResourceGroupReadyActivity activity, int resourceGroupId) {
        switch (resourceGroupId) {
            case Constants.RG_LOCATION:
            case Constants.RG_NOTIFICATION:
            case Constants.RG_VHIKE_WEBSERVICE:
                reloadResourceGroup(activity, resourceGroupId);
                break;
        }
        //    }
        //    
        //    
        //    private void resourceGroupReady(ResourceGroupReadyActivity activity, int resourceGroupId) {
        //        switch (resourceGroupId) {
        //            case Constants.RG_LOCATION:
        //                activity.onResourceGroupReady(loc, resourceGroupId);
        //                break;
        //            case Constants.RG_VHIKE_WEBSERVICE:
        //                activity.onResourceGroupReady(ws, resourceGroupId);
        //                break;
        //            case Constants.RG_NOTIFICATION:
        //                activity.onResourceGroupReady(noti, resourceGroupId);
        //                break;
        //        }
    }
    
    
    private void getResourceFromCache(ResourceGroupReadyActivity activity, int resourceGroupId, IBinder binder) {
        if (binder == null) {
            this.binder = PMP.get(app).getResourceFromCache(resourceGroupIDs[resourceGroupId]);
        } else {
            this.binder = binder;
        }
        switch (resourceGroupId) {
            case Constants.RG_LOCATION:
                loc = IAbsoluteLocation.Stub.asInterface(binder);
                activity.onResourceGroupReady(loc, resourceGroupId);
                break;
            case Constants.RG_VHIKE_WEBSERVICE:
                ws = IvHikeWebservice.Stub.asInterface(binder);
                activity.onResourceGroupReady(ws, resourceGroupId);
                break;
            case Constants.RG_NOTIFICATION:
                noti = INotification.Stub.asInterface(binder);
                activity.onResourceGroupReady(noti, resourceGroupId);
                break;
        }
    }
    
    
    private void reloadResourceGroup(final ResourceGroupReadyActivity activity, final int resourceGroupId) {
        if (PMP.get(app).isResourceCached(resourceGroupIDs[resourceGroupId])) {
            getResourceFromCache(activity, resourceGroupId, null);
        } else {
            PMP.get(app).getResource(resourceGroupIDs[resourceGroupId], new PMPRequestResourceHandler() {
                
                @Override
                public void onReceiveResource(PMPResourceIdentifier resource, IBinder binder) {
                    super.onReceiveResource(resource, binder);
                    Log.i(TAG, "on receiving");
                    getResourceFromCache(activity, resourceGroupId, binder);
                }
            });
        }
    }
    
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
        //    private class ResourceGroupBinder implements Runnable {
        //        
        //        PMPResourceIdentifier id;
        //        CountDownLatch latch;
        //        IBinder binder;
        //        boolean ready = false;
        //        
        //        
        //        public ResourceGroupBinder(PMPResourceIdentifier id, CountDownLatch latch) {
        //            this.id = id;
        //            this.latch = latch;
        //        }
        //        
        //        
        //        public IBinder getBinder() {
        //            return binder;
        //        }
        //        
        //        
        //        @Override
        //        public void run() {
        //            try {
        //                Log.i(TAG, "getting");
        //                Log.i(TAG, app.getPackageName());
        //                Log.i(TAG, id.getResourceGroup());
        //                //                while (!PMP.get(app).isResourceCached(id) && wait > 0) {
        //                //                    System.out.println("sleeping");
        //                //                    Thread.sleep(100);
        //                //                    System.out.println("wake");
        //                //                    wait--;
        //                //                }
        //                PMP.get(app).getResource(id, new PMPRequestResourceHandler() {
        //                    
        //                    @Override
        //                    public void onReceiveResource(PMPResourceIdentifier resource, IBinder binder) {
        //                        super.onReceiveResource(resource, binder);
        //                        Log.i(TAG, "on receiving");
        //                        ready = true;
        //                        //                        resourceReady();
        //                    }
        //                });
        //                
        //                int wait = 100;
        //                while (!ready && wait > 100) {
        //                    Thread.sleep(100);
        //                    wait--;
        //                }
        ////                latch.countDown();
        ////                Log.i(TAG, "wait over");
        //                //                System.out.println("wait over");
        //                //                vHikeService.this.resourceReady();
        //                
        //                //                PMP.get(app).getResource(id, new PMPRequestResourceHandler() {
        //                //
        //                //                    @Override
        //                //                    public void onReceiveResource(PMPResourceIdentifier resource, IBinder binder) {
        //                //                        Log.d(TAG, "received");
        //                //                        ResourceGroupBinder.this.binder = PMP.get(app).getResourceFromCache(id);
        //                //                        ResourceGroupBinder.this.latch.countDown();
        //                //                    }
        //                //
        //                //
        //                //                    @Override
        //                //                    public void onBindingFailed() {
        //                //                        Log.v(TAG, "failed");
        //                //                        Toast.makeText(getApplicationContext(), "Binding FAILED", Toast.LENGTH_LONG);
        //                //                        ResourceGroupBinder.this.latch.countDown();
        //                //                    }
        //                //                });
        //            } catch (Exception e) {
        //                e.printStackTrace();
        //            }
        //        }
        //    }
        //    
        //    private class BinderTask extends AsyncTask<PMPResourceIdentifier, Boolean, IBinder> {
        //
        //        @Override
        //        protected IBinder doInBackground(PMPResourceIdentifier... params) {
        //            System.out.println("doing background");
        //            PMP.get(app).getResource(params[0], new PMPRequestResourceHandler() {
        //                
        //                @Override
        //                public void onReceiveResource(PMPResourceIdentifier resource, IBinder binder) {
        //                    super.onReceiveResource(resource, binder);
        //                    Log.i(TAG, "on receiving");
        //                    binderLock = false;
        //                }
        //            });
        //            return null;
        //        }
        //    }
    }
    
}
