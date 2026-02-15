package com.zensleep;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class SettingsFragment extends Fragment {

    public static final String PREFS = "zen_settings";

    public static final String KEY_DARK = "dark_mode";
    public static final String KEY_VOL = "volume";

    public SettingsFragment() {
        super(R.layout.fragment_settings);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS, 0);

        // 🔥 CARD DO DESPERTADOR (SEM SWITCH)
        LinearLayout cardAlarm = view.findViewById(R.id.cardAlarm);

        // OUTROS CONTROLES
        Switch switchDarkMode = view.findViewById(R.id.switchDarkMode);
        SeekBar seekVolume = view.findViewById(R.id.seekVolume);
        TextView txtVolumeValue = view.findViewById(R.id.txtVolumeValue);
        LinearLayout btnPrivacy = view.findViewById(R.id.btnPrivacy);

        // =========================
        // 🔥 CARREGA VALORES
        // =========================
        boolean darkEnabled = prefs.getBoolean(KEY_DARK, true);
        int volume = prefs.getInt(KEY_VOL, 80);

        switchDarkMode.setChecked(darkEnabled);
        seekVolume.setProgress(volume);
        txtVolumeValue.setText(volume + "%");

        // =========================
        // ⏰ CONFIGURAR DESPERTADOR (BOTTOM SHEET CORRETO)
        // =========================
        cardAlarm.setOnClickListener(v -> {

            BottomSheetDialog dialog = new BottomSheetDialog(requireContext());

            View sheetView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.dialog_alarm_info, null); // 🔥 AQUI FOI CORRIGIDO

            dialog.setContentView(sheetView);
            dialog.show();
        });

        // =========================
        // 🌙 TEMA ESCURO
        // =========================
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {

            prefs.edit().putBoolean(KEY_DARK, isChecked).apply();

            AppCompatDelegate.setDefaultNightMode(
                    isChecked
                            ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        // =========================
        // 🔊 VOLUME
        // =========================
        seekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtVolumeValue.setText(progress + "%");
                prefs.edit().putInt(KEY_VOL, progress).apply();
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // =========================
        // 🔐 POLÍTICA
        // =========================
        btnPrivacy.setOnClickListener(v -> {
            Intent i = new Intent(requireContext(), PrivacyPolicyActivity.class);
            startActivity(i);
        });
    }
}
