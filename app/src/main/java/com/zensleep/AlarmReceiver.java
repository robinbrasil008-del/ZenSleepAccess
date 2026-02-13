package com.zensleep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        try {

            int alarmId = intent.getIntExtra("alarm_id", -1);
            String label = intent.getStringExtra("alarm_label");

            Intent ringingIntent = new Intent(context, AlarmRingingActivity.class);
            ringingIntent.putExtra("alarm_id", alarmId);
            ringingIntent.putExtra("alarm_label", label);

            ringingIntent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            context.startActivity(ringingIntent);

        } catch (Exception e) {
            Log.e("AlarmReceiver", "Erro ao iniciar AlarmRingingActivity", e);
        }
    }
}
