package com.zensleep;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.SeekBar;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.HashMap;

public class FavoritesFragment extends Fragment {

    private final HashMap<String, MediaPlayer> players = new HashMap<>();

    private TextView emptyText;

    public FavoritesFragment() {
        super(R.layout.fragment_favorites);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        emptyText = view.findViewById(R.id.emptyText);

        boolean hasFavorites = false;

        loadFavorites(view);

        // ===== PLAYERS IGUAL HOME =====
        setupSound(view, "chuva", R.raw.chuva,
                R.id.btnPlayChuva, R.id.seekChuva);

        setupSound(view, "floresta", R.raw.floresta,
                R.id.btnPlayFloresta, R.id.seekFloresta);

        setupSound(view, "lareira", R.raw.lareira,
                R.id.btnPlayLareira, R.id.seekLareira);

        setupSound(view, "vento", R.raw.vento_suave,
                R.id.btnPlayVento, R.id.seekVento);

        setupSound(view, "grilos", R.raw.grilos,
                R.id.btnPlayGrilos, R.id.seekGrilos);

        setupSound(view, "passaros", R.raw.passaros,
                R.id.btnPlayPassaros, R.id.seekPassaros);

        setupSound(view, "riacho", R.raw.riacho,
                R.id.btnPlayRiacho, R.id.seekRiacho);

        setupSound(view, "cafeteira", R.raw.cafeteira,
                R.id.btnPlayCafeteira, R.id.seekCafeteira);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites(getView());
    }

    private void loadFavorites(View root) {

    if (root == null) return;

    // 🔥 ORDEM DOS FAVORITOS
    java.util.List<String> order =
            FavoritesManager.getFavoritesOrder(requireContext());

    // 🔥 TODOS OS CARDS
    android.view.ViewGroup parent =
            (android.view.ViewGroup) root.findViewById(R.id.cardsContainer);

    if (parent == null) return;

    java.util.Map<String, View> map = new java.util.HashMap<>();

    map.put("chuva", root.findViewById(R.id.cardChuva));
    map.put("floresta", root.findViewById(R.id.cardFloresta));
    map.put("lareira", root.findViewById(R.id.cardLareira));
    map.put("vento", root.findViewById(R.id.cardVento));
    map.put("grilos", root.findViewById(R.id.cardGrilos));
    map.put("passaros", root.findViewById(R.id.cardPassaros));
    map.put("riacho", root.findViewById(R.id.cardRiacho));
    map.put("cafeteira", root.findViewById(R.id.cardCafeteira));
   
    hasFavorites = true;

    // 🔥 REMOVE TODOS
    parent.removeAllViews();

    // 🔥 ADICIONA NA ORDEM CORRETA
    for (String key : order) {
        View card = map.get(key);
        if (card != null) {
            parent.addView(card);
        }
    }

        emptyText.setVisibility(hasFavorites ? View.GONE : View.VISIBLE);
        
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

    // 🔥 LÓGICA COMPLETA COPIADA DO HOME
    private void setupSound(View root, String key, int rawRes,
                            int btnId, int seekId) {

        ImageView button = root.findViewById(btnId);
        SeekBar seekBar = root.findViewById(seekId);

        ImageView btnFav = null;

switch (key) {
    case "chuva":
        btnFav = root.findViewById(R.id.starChuva);
        break;
    case "floresta":
        btnFav = root.findViewById(R.id.starFloresta);
        break;
    case "lareira":
        btnFav = root.findViewById(R.id.starLareira);
        break;
    case "vento":
        btnFav = root.findViewById(R.id.starVento);
        break;
    case "grilos":
        btnFav = root.findViewById(R.id.starGrilos);
        break;
    case "passaros":
        btnFav = root.findViewById(R.id.starPassaros);
        break;
    case "riacho":
        btnFav = root.findViewById(R.id.starRiacho);
        break;
    case "cafeteira":
        btnFav = root.findViewById(R.id.starCafeteira);
        break;
}

if (btnFav != null) {

    btnFav.setOnClickListener(v -> {

        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Remover dos favoritos")
                .setMessage("Deseja remover este som dos favoritos?")
                .setPositiveButton("Sim", (dialog, which) -> {

                    // 🔥 REMOVE
                    FavoritesManager.removeFavorite(requireContext(), key);

                    // 🔥 PARA O SOM SE ESTIVER TOCANDO
                    if (players.containsKey(key)) {
                        stopSingle(key, button, seekBar);
                    }

                    // 🔥 ATUALIZA A TELA
                    loadFavorites(getView());

                })
                .setNegativeButton("Cancelar", null)
                .show();
    });
}

        if (button == null) return;

        button.setOnClickListener(v -> {

            if (players.containsKey(key)) {
                stopSingle(key, button, seekBar);
                return;
            }

            MediaPlayer mp = MediaPlayer.create(requireContext(), rawRes);
            mp.setLooping(true);

            players.put(key, mp);

            // volume inicial
            if (seekBar != null) {
                seekBar.setProgress(80);
                float volume = seekBar.getProgress() / 100f;
                mp.setVolume(volume, volume);
                seekBar.setVisibility(View.VISIBLE);
            }

            mp.start();
            button.setImageResource(R.drawable.ic_media_pause);

            // animação botão
            button.animate()
                    .translationY(-60f)
                    .scaleX(1.05f)
                    .scaleY(1.05f)
                    .setDuration(250)
                    .start();

            // glow
            View card = (View) button.getParent().getParent();
            if (card instanceof CardGlowLayout) {
                ((CardGlowLayout) card).startGlow();
            }

            // equalizer
            View parent = (View) button.getParent();
            ImageView eq = parent.findViewById(R.id.equalizer);

            if (eq != null) {
                eq.setVisibility(View.VISIBLE);

                Glide.with(requireContext())
                        .asGif()
                        .load(R.drawable.equalizer)
                        .into(eq);
            }

            // volume dinâmico
            if (seekBar != null) {
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                        float volume = progress / 100f;
                        mp.setVolume(volume, volume);
                    }

                    @Override public void onStartTrackingTouch(SeekBar sb) {}
                    @Override public void onStopTrackingTouch(SeekBar sb) {}
                });
            }
        });
    }

    private void stopSingle(String key, ImageView button, SeekBar seekBar) {

        MediaPlayer mp = players.get(key);
        if (mp != null) {
            try { mp.stop(); } catch (Exception ignored) {}
            try { mp.release(); } catch (Exception ignored) {}
        }

        players.remove(key);

        button.setImageResource(R.drawable.ic_media_play);

        button.animate()
                .translationY(0)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(200)
                .start();

        // glow off
        View card = (View) button.getParent().getParent();
        if (card instanceof CardGlowLayout) {
            ((CardGlowLayout) card).stopGlow();
        }

        // equalizer off
        View parent = (View) button.getParent();
        ImageView eq = parent.findViewById(R.id.equalizer);
        if (eq != null) {
            eq.setVisibility(View.GONE);
        }

        if (seekBar != null) {
            seekBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        for (MediaPlayer mp : players.values()) {
            try { mp.stop(); } catch (Exception ignored) {}
            try { mp.release(); } catch (Exception ignored) {}
        }
        players.clear();
    }
}
