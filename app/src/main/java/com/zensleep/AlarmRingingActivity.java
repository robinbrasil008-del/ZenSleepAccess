package com.zensleep;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AlarmRingingActivity extends AppCompatActivity {

    private Ringtone ringtone;
    private Vibrator vibrator;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔥 Mostrar sobre tela bloqueada (Android moderno)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );

        setContentView(R.layout.activity_alarm_ringing);

        TextView txtTitle = findViewById(R.id.txtAlarmTitle);
        Button btnStop = findViewById(R.id.btnStopAlarm);

        String label = getIntent().getStringExtra("alarm_label");
        if (label != null && !label.isEmpty()) {
            txtTitle.setText("⏰ " + label);
        }

        forceAlarmVolume();
        startAlarmSound();
        startVibration();

        btnStop.setOnClickListener(v -> stopAlarm());
    }

    // 🔥 GARANTE VOLUME DE ALARME
    private void forceAlarmVolume() {

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

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

    private void startAlarmSound() {

        try {

            Uri alarmUri =
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

            if (alarmUri == null) {
                alarmUri =
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }

            ringtone = RingtoneManager.getRingtone(this, alarmUri);

            if (ringtone != null) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    ringtone.setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_ALARM)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                    .build()
                    );
                }

                ringtone.play();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startVibration() {

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

    private void stopAlarm() {

        try {

            if (ringtone != null && ringtone.isPlaying()) {
                ringtone.stop();
            }

            if (vibrator != null) {
                vibrator.cancel();
            }

        } catch (Exception ignored) {}

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAlarm();
    }
}
