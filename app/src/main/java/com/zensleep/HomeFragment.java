package com.zensleep;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private MediaPlayer mediaPlayer;
    private CountDownTimer countDownTimer;

    private TextView txtTimer;
    private ImageView btnPlayChuva, btnPlayMar;
    private ImageView starChuva, starMar;
    private Button btnTimer;

    private boolean isChuvaPlaying = false;
    private boolean isMarPlaying = false;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        btnPlayChuva = view.findViewById(R.id.btnPlayChuva);
        btnPlayMar = view.findViewById(R.id.btnPlayMar);
        starChuva = view.findViewById(R.id.starChuva);
        starMar = view.findViewById(R.id.starMar);
        txtTimer = view.findViewById(R.id.txtTimer);
        btnTimer = view.findViewById(R.id.btnTimer);

        updateStars();

        btnPlayChuva.setOnClickListener(v -> toggleChuva());
        btnPlayMar.setOnClickListener(v -> toggleMar());

        starChuva.setOnClickListener(v -> {
            FavoritesManager.toggleFavorite(requireContext(), "chuva");
            updateStars();
        });

        starMar.setOnClickListener(v -> {
            FavoritesManager.toggleFavorite(requireContext(), "mar");
            updateStars();
        });

        btnTimer.setOnClickListener(v -> openTimerDialog());
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStars();
    }

    private void toggleChuva() {

        if (isChuvaPlaying) {
            stopSound();
            return;
        }

        stopSound();

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.chuva);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        btnPlayChuva.setImageResource(android.R.drawable.ic_media_pause);
        btnPlayMar.setImageResource(android.R.drawable.ic_media_play);

        isChuvaPlaying = true;
        isMarPlaying = false;
    }

    private void toggleMar() {

        if (isMarPlaying) {
            stopSound();
            return;
        }

        stopSound();

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.mar);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        btnPlayMar.setImageResource(android.R.drawable.ic_media_pause);
        btnPlayChuva.setImageResource(android.R.drawable.ic_media_play);

        isMarPlaying = true;
        isChuvaPlaying = false;
    }

    private void stopSound() {

        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (Exception ignored) {}
            mediaPlayer.release();
            mediaPlayer = null;
        }

        btnPlayChuva.setImageResource(android.R.drawable.ic_media_play);
        btnPlayMar.setImageResource(android.R.drawable.ic_media_play);

        isChuvaPlaying = false;
        isMarPlaying = false;
    }

    private void openTimerDialog() {

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_timer, null);

        EditText inputMinutes = dialogView.findViewById(R.id.inputMinutes);
        Button btnStartTimer = dialogView.findViewById(R.id.btnStartTimer);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        btnStartTimer.setOnClickListener(v -> {

            String minutesStr = inputMinutes.getText().toString().trim();

            if (!minutesStr.isEmpty()) {

                int minutes;
                try {
                    minutes = Integer.parseInt(minutesStr);
                } catch (Exception e) {
                    inputMinutes.setError("Digite um número válido");
                    return;
                }

                if (minutes <= 0) {
                    inputMinutes.setError("Digite um tempo maior que 0");
                    return;
                }

                long millis = minutes * 60L * 1000L;

                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }

                countDownTimer = new CountDownTimer(millis, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {

                        long seconds = millisUntilFinished / 1000;
                        long min = seconds / 60;
                        long sec = seconds % 60;

                        if (txtTimer != null) {
                            txtTimer.setText(String.format("%02d:%02d", min, sec));
                        }
                    }

                    @Override
                    public void onFinish() {
                        if (txtTimer != null) {
                            txtTimer.setText("00:00");
                        }
                        stopSound();
                    }

                }.start();

                dialog.dismiss();
            } else {
                inputMinutes.setError("Informe os minutos");
            }
        });

        dialog.show();
    }

    private void updateStars() {

        if (getContext() == null) return;

        boolean chuvaFav = FavoritesManager.isFavorite(requireContext(), "chuva");
        boolean marFav = FavoritesManager.isFavorite(requireContext(), "mar");

        if (chuvaFav) {
            starChuva.setImageResource(android.R.drawable.btn_star_big_on);
            starChuva.setColorFilter(0xFFFFC107); // amarelo
        } else {
            starChuva.setImageResource(android.R.drawable.btn_star_big_off);
            starChuva.setColorFilter(0xFFFFFFFF); // branco
        }

        if (marFav) {
            starMar.setImageResource(android.R.drawable.btn_star_big_on);
            starMar.setColorFilter(0xFFFFC107);
        } else {
            starMar.setImageResource(android.R.drawable.btn_star_big_off);
            starMar.setColorFilter(0xFFFFFFFF);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        stopSound();

        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
}
