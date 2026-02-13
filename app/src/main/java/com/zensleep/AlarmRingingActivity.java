package com.zensleep;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AlarmRingingActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔥 Mostrar mesmo com tela bloqueada
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );

        setContentView(R.layout.activity_alarm_ringing);

        Button btnStop = findViewById(R.id.btnStopAlarm);

        startAlarmSound();
        startVibration();

        btnStop.setOnClickListener(v -> stopAlarm());
    }

    private void startAlarmSound() {

        try {

            // 🔥 Primeiro tenta usar seu som personalizado
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound);

            // Se não existir usa som padrão do sistema
            if (mediaPlayer == null) {

                Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

                if (alarmUri == null) {
                    alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                }

                mediaPlayer = MediaPlayer.create(this, alarmUri);
            }

            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startVibration() {

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if (vibrator != null) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

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
