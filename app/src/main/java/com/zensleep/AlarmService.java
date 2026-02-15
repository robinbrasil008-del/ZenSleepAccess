package com.zensleep;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
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

    public static final String PREFS = "zen_settings";
    public static final String KEY_ALARM_VOLUME = "alarm_volume";
    public static final String KEY_ALARM_VIBRATE = "alarm_vibrate";
    public static final String KEY_ALARM_SOUND = "alarm_sound";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        acquireWakeLock();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && "STOP_ALARM".equals(intent.getAction())) {
            stopSelf();
            return START_NOT_STICKY;
        }

        int alarmId = 1;
        String label = "ZenSleep";

        if (intent != null) {
            alarmId = intent.getIntExtra("alarm_id", 1);
            String extraLabel = intent.getStringExtra("alarm_label");
            if (extraLabel != null) label = extraLabel;
        }

        Intent fullScreenIntent =
                new Intent(this, AlarmRingingActivity.class);
        fullScreenIntent.putExtra("alarm_label", label);
        fullScreenIntent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP
        );

        PendingIntent fullScreenPendingIntent =
                PendingIntent.getActivity(
                        this,
                        alarmId,
                        fullScreenIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE
                );

        Intent stopIntent = new Intent(this, AlarmService.class);
        stopIntent.setAction("STOP_ALARM");

        PendingIntent stopPendingIntent =
                PendingIntent.getService(
                        this,
                        alarmId + 1000,
                        stopIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE
                );

        Notification notification =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("⏰ Alarme")
                        .setContentText(label)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setFullScreenIntent(fullScreenPendingIntent, true)
                        .addAction(
                                R.mipmap.ic_launcher,
                                "PARAR",
                                stopPendingIntent
                        )
                        .setOngoing(true)
                        .build();

        startForeground(alarmId, notification);

        startActivity(fullScreenIntent);

        startAlarm();

        return START_STICKY;
    }

    private void startAlarm() {

        SharedPreferences prefs = getSharedPreferences(PREFS, 0);

        int savedVolume = prefs.getInt(KEY_ALARM_VOLUME, 80);
        boolean vibrateEnabled = prefs.getBoolean(KEY_ALARM_VIBRATE, true);
        String savedSound = prefs.getString(KEY_ALARM_SOUND, "DEFAULT");

        try {

            Uri soundUri;

            if ("SOM_1".equals(savedSound)) {
                soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.som1);
            }
            else if ("SOM_2".equals(savedSound)) {
                soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.som2);
            }
            else if ("SOM_3".equals(savedSound)) {
                soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.som3);
            }
            else {
                soundUri = android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI;
            }

            mediaPlayer = new MediaPlayer();

            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
            );

            mediaPlayer.setDataSource(this, soundUri);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();

            float volume = savedVolume / 100f;
            mediaPlayer.setVolume(volume, volume);

            mediaPlayer.start();

        } catch (Exception e) {

            mediaPlayer = MediaPlayer.create(
                    this,
                    android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI
            );

            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        }

        if (vibrateEnabled) {

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

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Alarme ZenSleep",
                            NotificationManager.IMPORTANCE_HIGH
                    );

            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager manager =
                    getSystemService(NotificationManager.class);

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {

        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } catch (Exception ignored) {}
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
