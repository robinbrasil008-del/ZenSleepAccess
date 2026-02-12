package com.zensleep;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class FavoritesFragment extends Fragment {

    private LinearLayout favoritesContainer;
    private TextView emptyText;
    private MediaPlayer mediaPlayer;

    public FavoritesFragment() {
        super(R.layout.fragment_favorites);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        favoritesContainer = view.findViewById(R.id.favoritesContainer);
        emptyText = view.findViewById(R.id.emptyText);

        loadFavorites();
    }

    private void loadFavorites() {

        favoritesContainer.removeAllViews();

        boolean hasFavorites = false;

        if (FavoritesManager.isFavorite(requireContext(), "chuva")) {
            addCard("🌧", "Chuva", R.raw.chuva);
            hasFavorites = true;
        }

        if (FavoritesManager.isFavorite(requireContext(), "mar")) {
            addCard("🌊", "Ondas do Mar", R.raw.mar);
            hasFavorites = true;
        }

        emptyText.setVisibility(hasFavorites ? View.GONE : View.VISIBLE);
    }

    private void addCard(String emoji, String title, int soundRes) {

        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(40, 40, 40, 40);
        card.setBackgroundResource(R.drawable.bg_card);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(0, 0, 0, 30);
        card.setLayoutParams(params);

        TextView emojiView = new TextView(requireContext());
        emojiView.setText(emoji);
        emojiView.setTextSize(40);

        TextView titleView = new TextView(requireContext());
        titleView.setText(title);
        titleView.setTextSize(18);
        titleView.setTextColor(0xFFFFFFFF);

        ImageView playButton = new ImageView(requireContext());
        playButton.setImageResource(android.R.drawable.ic_media_play);
        playButton.setPadding(20, 20, 20, 20);

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

        favoritesContainer.addView(card);
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
