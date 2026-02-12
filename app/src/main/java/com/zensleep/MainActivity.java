package com.zensleep;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;

public class MainActivity extends Activity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.cardChuva).setOnClickListener(v -> tocarSom(R.raw.chuva));
        findViewById(R.id.cardMar).setOnClickListener(v -> tocarSom(R.raw.mar));
        findViewById(R.id.cardFloresta).setOnClickListener(v -> tocarSom(R.raw.floresta));
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
