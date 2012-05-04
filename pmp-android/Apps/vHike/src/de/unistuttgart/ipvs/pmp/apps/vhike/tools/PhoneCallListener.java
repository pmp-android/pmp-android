package de.unistuttgart.ipvs.pmp.apps.vhike.tools;

import android.app.Activity;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.DriverViewActivity;

/**
 * 
 * @author Andre Nguyen
 * 
 *         Monitors phone call activities for returning to vHike after finishing phone call
 */
public class PhoneCallListener extends PhoneStateListener {
    
    Activity activity;
    private boolean isPhoneCalling = false;
    
    
    public PhoneCallListener(Activity activity) {
        this.activity = activity;
    }
    
    
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        
        if (TelephonyManager.CALL_STATE_RINGING == state) {
            // phone ringing
            Log.i(this, "RINGING, number: " + incomingNumber);
        }
        
        if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
            // active
            Log.i(this, "OFFHOOK");
            
            this.isPhoneCalling = true;
        }
        
        if (TelephonyManager.CALL_STATE_IDLE == state) {
            // run when class initial and phone call ended, 
            // need detect flag from CALL_STATE_OFFHOOK
            Log.i(this, "IDLE");
            
            if (this.isPhoneCalling) {
                
                Log.i(this, "restart app");
                
                // restart app
                Intent i = this.activity.getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(this.activity.getBaseContext().getPackageName());
                Intent intent = new Intent(this.activity, DriverViewActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // activity.startActivity(i);
                this.activity.startActivity(intent);
                
                this.isPhoneCalling = false;
            }
            
        }
    }
}
