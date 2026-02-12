package com.zensleep;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private MediaPlayer mediaPlayer;
    private int tocando = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configura cada card
        configurarCard(R.id.includeChuva, "🌧", "Chuva", R.raw.chuva);
        configurarCard(R.id.includeMar, "🌊", "Ondas do Mar", R.raw.mar);
        configurarCard(R.id.includeFloresta, "🌲", "Floresta", R.raw.floresta);
    }

    private void configurarCard(int includeId, String emoji, String titulo, int som) {
        View card = findViewById(includeId);

        TextView txtEmoji = card.findViewById(R.id.txtEmoji);
        TextView txtTitle = card.findViewById(R.id.txtTitle);
        FrameLayout btnPlay = card.findViewById(R.id.btnPlay);
        TextView fav = card.findViewById(R.id.txtFav);

        txtEmoji.setText(emoji);
        txtTitle.setText(titulo);

        // Favorito (visual)
        fav.setOnClickListener(v -> {
            String atual = fav.getText().toString();
            fav.setText(atual.equals("♡") ? "❤" : "♡");
        });

        // Play
        btnPlay.setOnClickListener(v -> tocarSom(som));
        card.setOnClickListener(v -> tocarSom(som)); // clicar no card também toca
    }

    private void tocarSom(int somId) {
        if (tocando == somId && mediaPlayer != null) {
            pararSom();
            return;
        }

        pararSom();
        mediaPlayer = MediaPlayer.create(this, somId);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        tocando = somId;
    }

    private void pararSom() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        tocando = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pararSom();
    }
}
