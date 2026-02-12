package com.zensleep;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton playChuva = findViewById(R.id.playChuva);
        ImageButton playMar = findViewById(R.id.playMar);

        playChuva.setOnClickListener(v -> playSound(R.raw.chuva));
        playMar.setOnClickListener(v -> playSound(R.raw.mar));
    }

    private void playSound(int soundRes) {

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(this, soundRes);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        isPlaying = true;

        Toast.makeText(this, "Som iniciado", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
