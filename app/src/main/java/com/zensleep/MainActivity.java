package com.zensleep;

import android.app.AlertDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private ImageButton currentButton;
    private boolean isPlaying = false;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton playChuva = findViewById(R.id.playChuva);
        ImageButton playMar = findViewById(R.id.playMar);
        Button btnTimer = findViewById(R.id.btnTimer);

        playChuva.setOnClickListener(v -> toggleSound(R.raw.chuva, playChuva));
        playMar.setOnClickListener(v -> toggleSound(R.raw.mar, playMar));

        btnTimer.setOnClickListener(v -> showTimerDialog());
    }

    private void toggleSound(int soundRes, ImageButton button) {

        if (isPlaying && currentButton == button) {
            mediaPlayer.pause();
            button.setImageResource(android.R.drawable.ic_media_play);
            isPlaying = false;
            return;
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(this, soundRes);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        if (currentButton != null) {
            currentButton.setImageResource(android.R.drawable.ic_media_play);
        }

        button.setImageResource(android.R.drawable.ic_media_pause);
        currentButton = button;
        isPlaying = true;
    }

    private void showTimerDialog() {

        String[] options = {"5 minutos", "10 minutos", "30 minutos", "60 minutos"};

        new AlertDialog.Builder(this)
                .setTitle("Definir Timer")
                .setItems(options, (dialog, which) -> {

                    int minutes = 5;

                    switch (which) {
                        case 0: minutes = 5; break;
                        case 1: minutes = 10; break;
                        case 2: minutes = 30; break;
                        case 3: minutes = 60; break;
                    }

                    startTimer(minutes);
                })
                .show();
    }

    private void startTimer(int minutes) {

        if (!isPlaying) {
            Toast.makeText(this, "Nenhum som está tocando", Toast.LENGTH_SHORT).show();
            return;
        }

        if (timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }

        timerRunnable = () -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                isPlaying = false;

                if (currentButton != null) {
                    currentButton.setImageResource(android.R.drawable.ic_media_play);
                }

                Toast.makeText(this, "Timer finalizado", Toast.LENGTH_LONG).show();
            }
        };

        timerHandler.postDelayed(timerRunnable, minutes * 60 * 1000);

        Toast.makeText(this, "Timer de " + minutes + " minutos iniciado", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        if (timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }
}
