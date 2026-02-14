package com.zensleep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.content.ContextCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String label = intent.getStringExtra("alarm_label");

        // 🔥 1️⃣ Inicia o serviço que toca o som
        Intent serviceIntent = new Intent(context, AlarmService.class);
        serviceIntent.putExtra("alarm_label", label);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, serviceIntent);
        } else {
            context.startService(serviceIntent);
        }

        // 🔥 2️⃣ Abre a tela do alarme
        Intent activityIntent =
                new Intent(context, AlarmRingingActivity.class);

        activityIntent.putExtra("alarm_label", label);

        activityIntent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP
        );

        context.startActivity(activityIntent);
    }
}
