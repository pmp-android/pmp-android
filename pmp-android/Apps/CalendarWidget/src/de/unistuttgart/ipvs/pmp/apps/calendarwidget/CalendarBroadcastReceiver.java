package de.unistuttgart.ipvs.pmp.apps.calendarwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CalendarBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, WidgetUpdateService.class));
    }
}