package com.zensleep;

import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout cardChuva = findViewById(R.id.cardChuva);
        LinearLayout cardMar = findViewById(R.id.cardMar);
        LinearLayout cardFloresta = findViewById(R.id.cardFloresta);

        cardChuva.setOnClickListener(v -> tocarSom(R.raw.chuva));
        cardMar.setOnClickListener(v -> tocarSom(R.raw.mar));
        cardFloresta.setOnClickListener(v -> tocarSom(R.raw.floresta));
    }

    private void tocarSom(int somId) {
        pararSom();
        mediaPlayer = MediaPlayer.create(this, somId);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    private void pararSom() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pararSom();
    }
}
