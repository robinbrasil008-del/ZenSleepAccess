package com.zensleep;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private ImageButton currentButton;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton playChuva = findViewById(R.id.playChuva);
        ImageButton playMar = findViewById(R.id.playMar);

        playChuva.setOnClickListener(v -> toggleSound(R.raw.chuva, playChuva));
        playMar.setOnClickListener(v -> toggleSound(R.raw.mar, playMar));
    }

    private void toggleSound(int soundRes, ImageButton button) {

        // Se clicou no mesmo botão
        if (isPlaying && currentButton == button) {
            mediaPlayer.pause();
            button.setImageResource(android.R.drawable.ic_media_play);
            isPlaying = false;
            Toast.makeText(this, "Pausado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Se estava tocando outro som
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(this, soundRes);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // Atualiza botão anterior
        if (currentButton != null) {
            currentButton.setImageResource(android.R.drawable.ic_media_play);
        }

        button.setImageResource(android.R.drawable.ic_media_pause);
        currentButton = button;
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
