package com.zensleep;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnChuva = findViewById(R.id.btnChuva);
        Button btnMar = findViewById(R.id.btnMar);
        Button btnFloresta = findViewById(R.id.btnFloresta);
        Button btnParar = findViewById(R.id.btnParar);

        btnChuva.setOnClickListener(v -> tocarSom(R.raw.chuva));
        btnMar.setOnClickListener(v -> tocarSom(R.raw.mar));
        btnFloresta.setOnClickListener(v -> tocarSom(R.raw.floresta));
        btnParar.setOnClickListener(v -> pararSom());
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
