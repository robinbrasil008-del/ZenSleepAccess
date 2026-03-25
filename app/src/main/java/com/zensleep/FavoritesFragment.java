package com.zensleep;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

public class FavoritesFragment extends Fragment {

    private MediaPlayer mediaPlayer;

    public FavoritesFragment() {
        super(R.layout.fragment_favorites);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        loadFavorites(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites(getView());
    }

    private void loadFavorites(View root) {

        if (root == null) return;

        toggle(root, R.id.cardChuva, "chuva");
        toggle(root, R.id.cardFloresta, "floresta");
        toggle(root, R.id.cardLareira, "lareira");
        toggle(root, R.id.cardVento, "vento_suave");
        toggle(root, R.id.cardGrilos, "grilos");
        toggle(root, R.id.cardPassaros, "passaros");
        toggle(root, R.id.cardRiacho, "riacho");
        toggle(root, R.id.cardCafeteira, "cafeteira");
    }

    private void toggle(View root, int id, String key) {

        View card = root.findViewById(id);

        if (card == null) return;

        card.setVisibility(
            FavoritesManager.isFavorite(requireContext(), key)
                ? View.VISIBLE
                : View.GONE
        );
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
