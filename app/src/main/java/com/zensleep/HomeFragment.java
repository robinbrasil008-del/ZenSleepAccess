package com.zensleep;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.HashMap;

public class HomeFragment extends Fragment {

    private MediaPlayer mediaPlayer;
    private CountDownTimer countDownTimer;

    // 🔥 NOVO SISTEMA MULTI PLAYER
    private HashMap<String, MediaPlayer> players = new HashMap<>();

    private TextView txtTimer;
    private ImageView btnPlayChuva, btnPlayMar;
    private ImageView btnPlayFloresta, btnPlayLareira, btnPlayVento,
            btnPlayGrilos, btnPlayPassaros, btnPlayRiacho, btnPlayCafeteira;

    private SeekBar seekChuva, seekMar, seekFloresta, seekLareira,
            seekVento, seekGrilos, seekPassaros, seekRiacho, seekCafeteira;

    private ImageView starChuva, starMar;
    private ImageView starFloresta, starLareira, starVento,
            starGrilos, starPassaros, starRiacho, starCafeteira;

    private Button btnTimer;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        btnPlayChuva = view.findViewById(R.id.btnPlayChuva);
        btnPlayMar = view.findViewById(R.id.btnPlayMar);
        btnPlayFloresta = view.findViewById(R.id.btnPlayFloresta);
        btnPlayLareira = view.findViewById(R.id.btnPlayLareira);
        btnPlayVento = view.findViewById(R.id.btnPlayVento);
        btnPlayGrilos = view.findViewById(R.id.btnPlayGrilos);
        btnPlayPassaros = view.findViewById(R.id.btnPlayPassaros);
        btnPlayRiacho = view.findViewById(R.id.btnPlayRiacho);
        btnPlayCafeteira = view.findViewById(R.id.btnPlayCafeteira);

        seekChuva = view.findViewById(R.id.seekChuva);
        seekMar = view.findViewById(R.id.seekMar);
        seekFloresta = view.findViewById(R.id.seekFloresta);
        seekLareira = view.findViewById(R.id.seekLareira);
        seekVento = view.findViewById(R.id.seekVento);
        seekGrilos = view.findViewById(R.id.seekGrilos);
        seekPassaros = view.findViewById(R.id.seekPassaros);
        seekRiacho = view.findViewById(R.id.seekRiacho);
        seekCafeteira = view.findViewById(R.id.seekCafeteira);

        setupSound("chuva", R.raw.chuva, btnPlayChuva, seekChuva);
        setupSound("mar", R.raw.mar, btnPlayMar, seekMar);
        setupSound("floresta", R.raw.floresta, btnPlayFloresta, seekFloresta);
        setupSound("lareira", R.raw.lareira, btnPlayLareira, seekLareira);
        setupSound("vento", R.raw.vento_suave, btnPlayVento, seekVento);
        setupSound("grilos", R.raw.grilos, btnPlayGrilos, seekGrilos);
        setupSound("passaros", R.raw.passaros, btnPlayPassaros, seekPassaros);
        setupSound("riacho", R.raw.riacho, btnPlayRiacho, seekRiacho);
        setupSound("cafeteira", R.raw.cafeteira, btnPlayCafeteira, seekCafeteira);

        txtTimer = view.findViewById(R.id.txtTimer);
        btnTimer = view.findViewById(R.id.btnTimer);

        btnTimer.setOnClickListener(v -> openTimerDialog());
    }

    // 🔥 MÉTODO NOVO PARA MULTI SOM
    private void setupSound(String key, int rawRes, ImageView button, SeekBar seekBar) {

        button.setOnClickListener(v -> {

            if (players.containsKey(key)) {
                MediaPlayer mp = players.get(key);
                if (mp != null) {
                    mp.release();
                }
                players.remove(key);
                button.setImageResource(android.R.drawable.ic_media_play);
                return;
            }

            MediaPlayer mp = MediaPlayer.create(requireContext(), rawRes);
            mp.setLooping(true);
            mp.start();

            float volume = seekBar.getProgress() / 100f;
            mp.setVolume(volume, volume);

            players.put(key, mp);
            button.setImageResource(android.R.drawable.ic_media_pause);
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (players.containsKey(key)) {
                    MediaPlayer mp = players.get(key);
                    if (mp != null) {
                        float volume = progress / 100f;
                        mp.setVolume(volume, volume);
                    }
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void openTimerDialog() {

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_timer, null);

        EditText inputMinutes = dialogView.findViewById(R.id.inputMinutes);
        Button btnStartTimer = dialogView.findViewById(R.id.btnStartTimer);
        Switch switchTimerAlarm = dialogView.findViewById(R.id.switchTimerAlarm);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        btnStartTimer.setOnClickListener(v -> {

            String minutesStr = inputMinutes.getText().toString().trim();
            if (minutesStr.isEmpty()) {
                inputMinutes.setError("Informe os minutos");
                return;
            }

            int minutes = Integer.parseInt(minutesStr);
            long millis = minutes * 60L * 1000L;

            if (countDownTimer != null) countDownTimer.cancel();

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

                    // 🔥 PARA TODOS OS SONS
                    for (MediaPlayer mp : players.values()) {
                        if (mp != null) mp.release();
                    }
                    players.clear();
                }

            }.start();

            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        for (MediaPlayer mp : players.values()) {
            if (mp != null) mp.release();
        }
        players.clear();

        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
}
