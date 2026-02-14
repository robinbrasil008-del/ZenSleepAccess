package com.zensleep;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AlarmRingingActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );

        setContentView(R.layout.activity_alarm_ringing);

        TextView txtTitle = findViewById(R.id.txtAlarmTitle);
        Button btnStop = findViewById(R.id.btnStopAlarm);

        String label = getIntent().getStringExtra("alarm_label");

        if (label != null && !label.isEmpty()) {
            txtTitle.setText("⏰ " + label);
        }

        startAlarmSound();
        startVibration();

        btnStop.setOnClickListener(v -> stopAlarm());
    }

    private void startAlarmSound() {

        try {

            Uri alarmUri = Settings.System.DEFAULT_ALARM_ALERT_URI;

            if (alarmUri == null) {
                alarmUri = Settings.System.DEFAULT_NOTIFICATION_URI;
            }

            mediaPlayer = MediaPlayer.create(this, alarmUri);

            if (mediaPlayer == null) return;

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
                                new long[]{0, 500, 500},
                                0
                        )
                );

            } else {

                vibrator.vibrate(new long[]{0, 500, 500}, 0);
            }
        }
    }

    private void stopAlarm() {

        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception ignored) {}

        if (vibrator != null) {
            vibrator.cancel();
        }

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAlarm();
    }
}
