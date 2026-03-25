package com.zensleep;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class FavoritesFragment extends Fragment {

    private GridLayout favoritesGrid;
    private TextView emptyText;
    private MediaPlayer mediaPlayer;

    public FavoritesFragment() {
        super(R.layout.fragment_favorites);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        favoritesGrid = view.findViewById(R.id.favoritesGrid);
        emptyText = view.findViewById(R.id.emptyText);

        loadFavorites();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {

        favoritesGrid.removeAllViews();

        boolean hasFavorites = false;

        if (FavoritesManager.isFavorite(requireContext(), "chuva")) {
            addCard("Chuva", "chuva", R.raw.chuva);
            hasFavorites = true;
        }

        if (FavoritesManager.isFavorite(requireContext(), "floresta")) {
            addCard("Floresta", "floresta", R.raw.floresta);
            hasFavorites = true;
        }

        if (FavoritesManager.isFavorite(requireContext(), "lareira")) {
            addCard("Lareira", "lareira", R.raw.lareira);
            hasFavorites = true;
        }

        if (FavoritesManager.isFavorite(requireContext(), "vento_suave")) {
            addCard("Vento Suave", "vento_suave", R.raw.vento_suave);
            hasFavorites = true;
        }

        if (FavoritesManager.isFavorite(requireContext(), "grilos")) {
            addCard("Noite com Grilos", "grilos", R.raw.grilos);
            hasFavorites = true;
        }

        if (FavoritesManager.isFavorite(requireContext(), "passaros")) {
            addCard("Pássaros", "passaros", R.raw.passaros);
            hasFavorites = true;
        }

        if (FavoritesManager.isFavorite(requireContext(), "riacho")) {
            addCard("Riacho", "riacho", R.raw.riacho);
            hasFavorites = true;
        }

        if (FavoritesManager.isFavorite(requireContext(), "cafeteira")) {
            addCard("Cafeteira", "cafeteira", R.raw.cafeteira);
            hasFavorites = true;
        }

        emptyText.setVisibility(hasFavorites ? View.GONE : View.VISIBLE);
    }

    // 🔥 CARD IGUAL AO HOME (SEM CRASH)
    private void addCard(String title, String key, int soundRes) {

    View card = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_sound, favoritesGrid, false);

    ImageView imgBg = card.findViewById(R.id.imgBackground);
    ImageView btnPlay = card.findViewById(R.id.btnPlay);
    ImageView btnFav = card.findViewById(R.id.starFavorite);
    ImageView equalizer = card.findViewById(R.id.equalizer);
    android.widget.SeekBar seekBar = card.findViewById(R.id.seekBar);
    TextView txtTitle = card.findViewById(R.id.txtTitle);

    txtTitle.setText(title);
    imgBg.setImageResource(getImageByKey(key));

    // 🔥 DEFINE O GIF DO EQUALIZER (OBRIGATÓRIO)
    equalizer.setImageResource(R.drawable.equalizer);

    btnPlay.setOnClickListener(v -> {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.release();
            mediaPlayer = null;

            btnPlay.setImageResource(R.drawable.ic_media_play);
            equalizer.setVisibility(View.GONE);
            seekBar.setVisibility(View.GONE);

            return;
        }

        mediaPlayer = MediaPlayer.create(requireContext(), soundRes);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        btnPlay.setImageResource(R.drawable.ic_media_pause);

        // 🔥 MOSTRA ELEMENTOS
        equalizer.setVisibility(View.VISIBLE);
        seekBar.setVisibility(View.VISIBLE);

        // 🔥 CONFIGURA SEEKBAR FUNCIONAL
        seekBar.setMax(100);

        new Thread(() -> {
            while (mediaPlayer != null && mediaPlayer.isPlaying()) {
                try {
                    int progress = (mediaPlayer.getCurrentPosition() * 100)
                            / mediaPlayer.getDuration();

                    requireActivity().runOnUiThread(() ->
                            seekBar.setProgress(progress)
                    );

                    Thread.sleep(500);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    });

    favoritesGrid.addView(card);
    }

    // 🔥 IMAGENS IGUAIS AO HOME
    private int getImageByKey(String key) {
        switch (key) {
            case "chuva": return R.drawable.bg_chuva;
            case "floresta": return R.drawable.bg_floresta;
            case "lareira": return R.drawable.bg_lareira;
            case "vento_suave": return R.drawable.bg_vento_suave;
            case "grilos": return R.drawable.bg_noite_grilos;
            case "passaros": return R.drawable.bg_passaros;
            case "riacho": return R.drawable.bg_riacho;
            case "cafeteira": return R.drawable.bg_cafeteira;
            default: return R.drawable.bg_card;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
