package com.zensleep;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private MediaPlayer mediaPlayer;
    private TextView txtTimer;
    private ImageView btnPlayChuva, btnPlayMar;
    private ImageView starChuva, starMar;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        btnPlayChuva = view.findViewById(R.id.btnPlayChuva);
        btnPlayMar = view.findViewById(R.id.btnPlayMar);
        starChuva = view.findViewById(R.id.starChuva);
        starMar = view.findViewById(R.id.starMar);

        updateStars();

        btnPlayChuva.setOnClickListener(v -> toggleSound(R.raw.chuva));
        btnPlayMar.setOnClickListener(v -> toggleSound(R.raw.mar));

        starChuva.setOnClickListener(v -> {
            FavoritesManager.toggleFavorite(getContext(), "chuva");
            updateStars();
        });

        starMar.setOnClickListener(v -> {
            FavoritesManager.toggleFavorite(getContext(), "mar");
            updateStars();
        });
    }

    private void updateStars() {

        if (FavoritesManager.isFavorite(getContext(), "chuva")) {
            starChuva.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            starChuva.setImageResource(android.R.drawable.btn_star_big_off);
        }

        if (FavoritesManager.isFavorite(getContext(), "mar")) {
            starMar.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            starMar.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }

    private void toggleSound(int soundRes) {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.release();
            mediaPlayer = null;
            return;
        }

        mediaPlayer = MediaPlayer.create(getContext(), soundRes);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }
}
