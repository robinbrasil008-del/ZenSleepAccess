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

    private CountDownTimer countDownTimer;

    private TextView txtTimer;
    private Button btnTimer;

    private HashMap<String, MediaPlayer> players = new HashMap<>();
    private HashMap<String, Integer> individualVolumes = new HashMap<>();

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        txtTimer = view.findViewById(R.id.txtTimer);
        btnTimer = view.findViewById(R.id.btnTimer);

        setupSound(view, "chuva", R.raw.chuva, R.id.btnPlayChuva, R.id.seekChuva);
        setupSound(view, "mar", R.raw.mar, R.id.btnPlayMar, R.id.seekMar);
        setupSound(view, "floresta", R.raw.floresta, R.id.btnPlayFloresta, R.id.seekFloresta);
        setupSound(view, "lareira", R.raw.lareira, R.id.btnPlayLareira, R.id.seekLareira);
        setupSound(view, "vento", R.raw.vento_suave, R.id.btnPlayVento, R.id.seekVento);
        setupSound(view, "grilos", R.raw.grilos, R.id.btnPlayGrilos, R.id.seekGrilos);
        setupSound(view, "passaros", R.raw.passaros, R.id.btnPlayPassaros, R.id.seekPassaros);
        setupSound(view, "riacho", R.raw.riacho, R.id.btnPlayRiacho, R.id.seekRiacho);
        setupSound(view, "cafeteira", R.raw.cafeteira, R.id.btnPlayCafeteira, R.id.seekCafeteira);

        btnTimer.setOnClickListener(v -> openTimerDialog());
    }

    private void setupSound(View view, String key, int rawRes, int btnId, int seekId) {

        ImageView button = view.findViewById(btnId);
        SeekBar seekBar = view.findViewById(seekId);

        individualVolumes.put(key, 80);
        seekBar.setProgress(80);

        button.setOnClickListener(v -> {

            if (players.containsKey(key)) {
                MediaPlayer mp = players.get(key);
                if (mp != null) mp.release();
                players.remove(key);
                button.setImageResource(android.R.drawable.ic_media_play);
                return;
            }

            MediaPlayer mp = MediaPlayer.create(requireContext(), rawRes);
            mp.setLooping(true);
            mp.start();

            players.put(key, mp);
            applyVolume(key);

            button.setImageResource(android.R.drawable.ic_media_pause);
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                individualVolumes.put(key, progress);
                applyVolume(key);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private float getGlobalVolume() {
        SharedPreferences prefs =
                requireContext().getSharedPreferences("zen_settings", 0);
        return prefs.getInt("volume", 80) / 100f;
    }

    private void applyVolume(String key) {
        if (!players.containsKey(key)) return;

        MediaPlayer mp = players.get(key);
        if (mp == null) return;

        float global = getGlobalVolume();
        float individual = individualVolumes.get(key) / 100f;

        float finalVolume = global * individual;
        mp.setVolume(finalVolume, finalVolume);
    }

    private void stopAllSounds() {
        for (MediaPlayer mp : players.values()) {
            if (mp != null) mp.release();
        }
        players.clear();
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

            int minutes = Integer.parseInt(inputMinutes.getText().toString());
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
                    stopAllSounds();
                }

            }.start();

            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopAllSounds();

        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
}
