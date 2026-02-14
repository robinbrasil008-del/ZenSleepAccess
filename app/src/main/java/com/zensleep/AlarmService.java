package com.zensleep;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
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

        int alarmId = 0;
        String label = "ZenSleep";
        String soundUriString = null;

        if (intent != null) {
            alarmId = intent.getIntExtra("alarm_id", 0);
            String extraLabel = intent.getStringExtra("alarm_label");
            if (extraLabel != null) label = extraLabel;

            soundUriString = intent.getStringExtra("alarm_sound");
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

        forceAlarmVolume();
        startAlarm(soundUriString);

        return START_STICKY;
    }

    private void startAlarm(String soundUriString) {

        try {

            Uri soundUri;

            if ("SOM_1".equals(soundUriString)) {
                soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.som1);
            }
            else if ("SOM_2".equals(soundUriString)) {
                soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.som2);
            }
            else if ("SOM_3".equals(soundUriString)) {
                soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.som3);
            }
            else if (soundUriString != null && soundUriString.startsWith("content://")) {
                soundUri = Uri.parse(soundUriString);
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
            mediaPlayer.start();

        } catch (Exception e) {

            // 🔥 Fallback automático
            try {
                mediaPlayer = MediaPlayer.create(
                        this,
                        android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI
                );
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            } catch (Exception ignored) {}
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

    private void forceAlarmVolume() {

        AudioManager audioManager =
                (AudioManager) getSystemService(AUDIO_SERVICE);

        if (audioManager != null) {

            int maxVolume =
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);

            audioManager.setStreamVolume(
                    AudioManager.STREAM_ALARM,
                    maxVolume,
                    0
            );
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
