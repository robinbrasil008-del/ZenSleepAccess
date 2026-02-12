package com.zensleep;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private TextView txtTimer;

    private ImageView btnPlayChuva;
    private ImageView btnPlayMar;

    private LinearLayout screenHome;
    private LinearLayout screenFav;
    private LinearLayout screenSettings;

    private CountDownTimer countDownTimer;

    private boolean isChuvaPlaying = false;
    private boolean isMarPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TELAS
        screenHome = findViewById(R.id.screenHome);
        screenFav = findViewById(R.id.screenFav);
        screenSettings = findViewById(R.id.screenSettings);

        // BOTÕES
        btnPlayChuva = findViewById(R.id.btnPlayChuva);
        btnPlayMar = findViewById(R.id.btnPlayMar);
        Button btnTimer = findViewById(R.id.btnTimer);
        txtTimer = findViewById(R.id.txtTimer);

        TextView navHome = findViewById(R.id.navHome);
        TextView navFav = findViewById(R.id.navFav);
        TextView navSettings = findViewById(R.id.navSettings);

        // SONS
        btnPlayChuva.setOnClickListener(v -> toggleChuva());
        btnPlayMar.setOnClickListener(v -> toggleMar());

        // TIMER
        btnTimer.setOnClickListener(v -> openTimerDialog());

        // MENU
        navHome.setOnClickListener(v -> {
            showHome();
            navHome.setTextColor(getColor(android.R.color.white));
            navFav.setTextColor(0xFF94A3B8);
            navSettings.setTextColor(0xFF94A3B8);
        });

        navFav.setOnClickListener(v -> {
            showFav();
            navFav.setTextColor(getColor(android.R.color.white));
            navHome.setTextColor(0xFF94A3B8);
            navSettings.setTextColor(0xFF94A3B8);
        });

        navSettings.setOnClickListener(v -> {
            showSettings();
            navSettings.setTextColor(getColor(android.R.color.white));
            navHome.setTextColor(0xFF94A3B8);
            navFav.setTextColor(0xFF94A3B8);
        });
    }

    private void showHome() {
        screenHome.setVisibility(View.VISIBLE);
        screenFav.setVisibility(View.GONE);
        screenSettings.setVisibility(View.GONE);
    }

    private void showFav() {
        screenHome.setVisibility(View.GONE);
        screenFav.setVisibility(View.VISIBLE);
        screenSettings.setVisibility(View.GONE);
    }

    private void showSettings() {
        screenHome.setVisibility(View.GONE);
        screenFav.setVisibility(View.GONE);
        screenSettings.setVisibility(View.VISIBLE);
    }

    private void toggleChuva() {
        if (isChuvaPlaying) {
            stopSound();
            return;
        }

        stopSound();

        mediaPlayer = MediaPlayer.create(this, R.raw.chuva);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        btnPlayChuva.setImageResource(android.R.drawable.ic_media_pause);
        btnPlayMar.setImageResource(android.R.drawable.ic_media_play);

        isChuvaPlaying = true;
        isMarPlaying = false;
    }

    private void toggleMar() {
        if (isMarPlaying) {
            stopSound();
            return;
        }

        stopSound();

        mediaPlayer = MediaPlayer.create(this, R.raw.mar);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        btnPlayMar.setImageResource(android.R.drawable.ic_media_pause);
        btnPlayChuva.setImageResource(android.R.drawable.ic_media_play);

        isMarPlaying = true;
        isChuvaPlaying = false;
    }

    private void stopSound() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        btnPlayChuva.setImageResource(android.R.drawable.ic_media_play);
        btnPlayMar.setImageResource(android.R.drawable.ic_media_play);

        isChuvaPlaying = false;
        isMarPlaying = false;
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
                long millis = minutes * 60L * 1000L;

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
                        stopSound();
                    }
                }.start();

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSound();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
