package com.zensleep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int alarmId = intent.getIntExtra("alarm_id", 9999);
        String label = intent.getStringExtra("alarm_label");

        Intent serviceIntent = new Intent(context, AlarmService.class);
        serviceIntent.putExtra("alarm_id", alarmId);
        serviceIntent.putExtra("alarm_label", label);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }
}
