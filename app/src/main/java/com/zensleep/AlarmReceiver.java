package com.zensleep;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "ZEN_ALARM_CHANNEL";

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent alarmIntent = new Intent(context, AlarmRingingActivity.class);
        alarmIntent.putExtra("alarm_label",
                intent.getStringExtra("alarm_label"));

        alarmIntent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP
        );

        PendingIntent fullScreenPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        alarmIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE
                );

        NotificationManager notificationManager =
                (NotificationManager)
                        context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 🔥 Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Alarmes ZenSleep",
                            NotificationManager.IMPORTANCE_HIGH
                    );

            channel.setDescription("Canal de alarmes");
            channel.setLockscreenVisibility(
                    android.app.Notification.VISIBILITY_PUBLIC
            );

            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("⏰ Alarme")
                        .setContentText("Seu alarme está tocando")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setFullScreenIntent(fullScreenPendingIntent, true)
                        .setAutoCancel(true);

        notificationManager.notify(1001, builder.build());
    }
}
