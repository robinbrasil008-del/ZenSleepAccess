package com.zensleep;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private MediaPlayer mediaPlayer;
    private TextView txtTimer;
    private ImageView btnPlayChuva, btnPlayMar;
    private CountDownTimer countDownTimer;

    private boolean isChuvaPlaying = false;
    private boolean isMarPlaying = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btnPlayChuva = view.findViewById(R.id.btnPlayChuva);
        btnPlayMar = view.findViewById(R.id.btnPlayMar);
        Button btnTimer = view.findViewById(R.id.btnTimer);
        txtTimer = view.findViewById(R.id.txtTimer);

        btnPlayChuva.setOnClickListener(v -> toggleSound(R.raw.chuva, true));
        btnPlayMar.setOnClickListener(v -> toggleSound(R.raw.mar, false));

        btnTimer.setOnClickListener(v -> openTimerDialog());

        return view;
    }

    private void toggleSound(int soundRes, boolean isChuva) {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.release();
            mediaPlayer = null;
            btnPlayChuva.setImageResource(android.R.drawable.ic_media_play);
            btnPlayMar.setImageResource(android.R.drawable.ic_media_play);
            isChuvaPlaying = false;
            isMarPlaying = false;
            return;
        }

        mediaPlayer = MediaPlayer.create(getContext(), soundRes);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        if (isChuva) {
            btnPlayChuva.setImageResource(android.R.drawable.ic_media_pause);
            btnPlayMar.setImageResource(android.R.drawable.ic_media_play);
        } else {
            btnPlayMar.setImageResource(android.R.drawable.ic_media_pause);
            btnPlayChuva.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    private void openTimerDialog() {

        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_timer, null);

        EditText inputMinutes = view.findViewById(R.id.inputMinutes);
        Button btnStartTimer = view.findViewById(R.id.btnStartTimer);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .create();

        btnStartTimer.setOnClickListener(v -> {

            String minutesStr = inputMinutes.getText().toString();

            if (!minutesStr.isEmpty()) {

                int minutes = Integer.parseInt(minutesStr);
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
                        txtTimer.setText(String.format("%02d:%02d", min, sec));
                    }

                    @Override
                    public void onFinish() {
                        txtTimer.setText("00:00");
                        if (mediaPlayer != null) {
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }
                        btnPlayChuva.setImageResource(android.R.drawable.ic_media_play);
                        btnPlayMar.setImageResource(android.R.drawable.ic_media_play);
                    }
                }.start();

                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
