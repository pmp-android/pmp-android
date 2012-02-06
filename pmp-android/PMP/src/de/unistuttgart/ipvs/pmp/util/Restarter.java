package de.unistuttgart.ipvs.pmp.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import de.unistuttgart.ipvs.pmp.service.PMPService;

/**
 * Util class for restarting the pmp after a dex code remove to be prepared for injecting a new code version.
 * 
 * @author Jakob Jarosch
 * 
 */
public class Restarter {
    
    public static final String RESTARTER_IDENTIFIER = "scheduledByRestarter";
    
    
    /**
     * Kills the referenced {@link Application} and restarts the {@link Activity}.
     * 
     * @param activity
     *            Activity which should be restarted.
     */
    public static final void killAppAndRestartActivity(Activity activity) {
        PendingIntent pi = PendingIntent.getActivity(activity.getBaseContext(), 0, new Intent(activity.getIntent()),
                activity.getIntent().getFlags());
        AlarmManager am = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC, System.currentTimeMillis() + 1000L, pi);
        System.exit(0);
    }
    
    
    /**
     * Restarts the given service in <code>in</code> time.
     */
    public static void scheduleServiceRestart(Context context, long in) {
        Intent intent = new Intent(context, PMPService.class);
        intent.putExtra(RESTARTER_IDENTIFIER, true);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC, System.currentTimeMillis() + in, pi);
    }
}