package com.zensleep;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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

    // 🔥 AGORA EXECUTA TODA VEZ QUE UM ALARME DISPARA
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        forceAlarmVolume();
        startAlarm();
        openFullScreen(intent);

        return START_REDELIVER_INTENT;
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alarme ZenSleep",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription("Canal do alarme");
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager manager =
                    getSystemService(NotificationManager.class);

            if (manager != null) {
                manager.createNotificationChannel(channel);
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

    private void startAlarm() {

        try {

            Uri alarmUri =
                    android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI;

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, alarmUri);

            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
            );

            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (Exception e) {
            e.printStackTrace();
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

    private void openFullScreen(Intent intent) {

        Intent i = new Intent(this, AlarmRingingActivity.class);

        if (intent != null) {
            i.putExtra("alarm_label",
                    intent.getStringExtra("alarm_label"));
        }

        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
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
