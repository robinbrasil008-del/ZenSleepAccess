package com.zensleep;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        loadFavorites(); // 🔥 atualiza sempre
    }

    private void loadFavorites() {

        favoritesGrid.removeAllViews();

        boolean hasFavorites = false;

        if (FavoritesManager.isFavorite(requireContext(), "chuva")) {
            addCard("🌧", "Chuva", R.raw.chuva);
            hasFavorites = true;
        }

        if (FavoritesManager.isFavorite(requireContext(), "mar")) {
            addCard("🌊", "Ondas do Mar", R.raw.mar);
            hasFavorites = true;
        }

        // 🔥 NOVOS SONS

        if (FavoritesManager.isFavorite(requireContext(), "floresta")) {
            addCard("🌲", "Floresta", R.raw.floresta);
            hasFavorites = true;
        }

        if (FavoritesManager.isFavorite(requireContext(), "lareira")) {
            addCard("🔥", "Lareira", R.raw.lareira);
            hasFavorites = true;
        }

        // ✅ CORRIGIDO
        if (FavoritesManager.isFavorite(requireContext(), "vento_suave")) {
            addCard("🌬", "Vento Suave", R.raw.vento_suave);
            hasFavorites = true;
        }

        // ✅ CORRIGIDO
        if (FavoritesManager.isFavorite(requireContext(), "grilos")) {
            addCard("✨", "Noite com Grilos", R.raw.grilos);
            hasFavorites = true;
        }

        if (FavoritesManager.isFavorite(requireContext(), "passaros")) {
            addCard("🐦", "Pássaros", R.raw.passaros);
            hasFavorites = true;
        }

        if (FavoritesManager.isFavorite(requireContext(), "riacho")) {
            addCard("🏞", "Riacho", R.raw.riacho);
            hasFavorites = true;
        }

        if (FavoritesManager.isFavorite(requireContext(), "cafeteira")) {
            addCard("☕", "Cafeteira", R.raw.cafeteira);
            hasFavorites = true;
        }

        emptyText.setVisibility(hasFavorites ? View.GONE : View.VISIBLE);
    }

    private void addCard(String emoji, String title, int soundRes) {

        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(android.view.Gravity.CENTER);
        card.setPadding(20,20,20,20);
        card.setBackgroundResource(R.drawable.bg_card);

        GridLayout.LayoutParams params =
                new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 400;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED,1f);
        params.setMargins(16,16,16,16);
        card.setLayoutParams(params);

        TextView emojiView = new TextView(requireContext());
        emojiView.setText(emoji);
        emojiView.setTextSize(40);

        TextView titleView = new TextView(requireContext());
        titleView.setText(title);
        titleView.setTextColor(0xFFFFFFFF);
        titleView.setTextSize(16);

        ImageView playButton = new ImageView(requireContext());
        playButton.setImageResource(android.R.drawable.ic_media_play);
        playButton.setPadding(20,20,20,20);

        playButton.setOnClickListener(v -> {

            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.release();
                mediaPlayer = null;
                playButton.setImageResource(android.R.drawable.ic_media_play);
                return;
            }

            mediaPlayer = MediaPlayer.create(requireContext(), soundRes);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();

            playButton.setImageResource(android.R.drawable.ic_media_pause);
        });

        card.addView(emojiView);
        card.addView(titleView);
        card.addView(playButton);

        favoritesGrid.addView(card);
    }

    // 🔥 SEGURANÇA EXTRA (evita vazamento de memória)

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
