package com.zensleep;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

public class AlarmService extends Service {

    private static final String CHANNEL_ID = "ZEN_ALARM_SERVICE";

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("⏰ Alarme tocando")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setOngoing(true)
                .build();

        startForeground(1, notification);

        acquireWakeLock();
        startAlarm();
    }

    private void acquireWakeLock() {
        PowerManager powerManager =
                (PowerManager) getSystemService(POWER_SERVICE);

        wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "ZenSleep::AlarmWakeLock"
        );

        wakeLock.acquire(10 * 60 * 1000L); // 10 minutos
    }

    private void startAlarm() {

        mediaPlayer = MediaPlayer.create(
                this,
                android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI
        );

        if (mediaPlayer != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaPlayer.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .build()
                );
            }

            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if (vibrator != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                        VibrationEffect.createWaveform(
                                new long[]{0, 700, 700},
                                0
                        )
                );
            } else {
                vibrator.vibrate(new long[]{0, 700, 700}, 0);
            }
        }
    }

    @Override
    public void onDestroy() {

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

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
