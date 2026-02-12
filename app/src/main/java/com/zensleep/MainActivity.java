package com.zensleep;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    TextView txtTimer;
    CountDownTimer countDownTimer;

    ImageView btnPlayChuva;
    ImageView btnPlayMar;

    boolean isPlayingChuva = false;
    boolean isPlayingMar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlayChuva = findViewById(R.id.btnPlayChuva);
        btnPlayMar = findViewById(R.id.btnPlayMar);
        Button btnTimer = findViewById(R.id.btnTimer);
        txtTimer = findViewById(R.id.txtTimer);

        btnPlayChuva.setOnClickListener(v -> toggleSound(R.raw.chuva, btnPlayChuva, true));
        btnPlayMar.setOnClickListener(v -> toggleSound(R.raw.mar, btnPlayMar, false));

        btnTimer.setOnClickListener(v -> openTimerDialog());
    }

    private void toggleSound(int soundRes, ImageView button, boolean isChuva) {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.release();
            mediaPlayer = null;

            btnPlayChuva.setImageResource(android.R.drawable.ic_media_play);
            btnPlayMar.setImageResource(android.R.drawable.ic_media_play);

            isPlayingChuva = false;
            isPlayingMar = false;
            return;
        }

        mediaPlayer = MediaPlayer.create(this, soundRes);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        button.setImageResource(android.R.drawable.ic_media_pause);

        if (isChuva) {
            isPlayingChuva = true;
            isPlayingMar = false;
        } else {
            isPlayingMar = true;
            isPlayingChuva = false;
        }
    }

    private void openTimerDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_timer, null);

        EditText inputMinutes = view.findViewById(R.id.inputMinutes);
        Button btnStartTimer = view.findViewById(R.id.btnStartTimer);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        btnStartTimer.setOnClickListener(v -> {
            String minutesStr = inputMinutes.getText().toString();
            if (!minutesStr.isEmpty()) {

                int minutes = Integer.parseInt(minutesStr);
                long millis = minutes * 60 * 1000;

                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }

                countDownTimer = new CountDownTimer(millis, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long seconds = millisUntilFinished / 1000;
                        long min = seconds / 60;
                        long sec = seconds % 60;
                        txtTimer.setText(String.format("%02d:%02d", min, sec));
                    }

                    @Override
                    public void onFinish() {
                        txtTimer.setText("00:00");
                        if (mediaPlayer != null) {
                            mediaPlayer.pause();
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }

                        btnPlayChuva.setImageResource(android.R.drawable.ic_media_play);
                        btnPlayMar.setImageResource(android.R.drawable.ic_media_play);
                    }
                }.start();

                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
