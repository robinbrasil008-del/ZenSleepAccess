package com.zensleep;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

public class AlarmService extends Service {

    private static final String CHANNEL_ID = "ZEN_ALARM_SERVICE";

    private Vibrator vibrator;
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("⏰ Alarme tocando")
                .setContentText("ZenSleep")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setOngoing(true)
                .build();

        startForeground(1, notification);

        acquireWakeLock();
        startVibrationOnly();
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alarme ZenSleep",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.enableVibration(true);

            NotificationManager manager =
                    getSystemService(NotificationManager.class);

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void acquireWakeLock() {

        PowerManager powerManager =
                (PowerManager) getSystemService(POWER_SERVICE);

        if (powerManager == null) return;

        wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "ZenSleep::AlarmWakeLock"
        );

        wakeLock.acquire(10 * 60 * 1000L);
    }

    private void startVibrationOnly() {

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if (vibrator != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                vibrator.vibrate(
                        VibrationEffect.createWaveform(
                                new long[]{0, 800, 800},
                                0
                        )
                );

            } else {

                vibrator.vibrate(new long[]{0, 800, 800}, 0);
            }
        }
    }

    @Override
    public void onDestroy() {

        if (vibrator != null) {
            vibrator.cancel();
        }

        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }

        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
