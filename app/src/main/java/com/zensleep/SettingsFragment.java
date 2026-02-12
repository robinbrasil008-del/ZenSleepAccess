package com.zensleep;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    public static final String PREFS = "zen_settings";
    public static final String KEY_ANIM = "animations";
    public static final String KEY_DARK = "dark_mode";
    public static final String KEY_VOL = "volume";

    public SettingsFragment() {
        super(R.layout.fragment_settings);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS, 0);

        Switch switchAnimations = view.findViewById(R.id.switchAnimations);
        Switch switchDarkMode = view.findViewById(R.id.switchDarkMode);
        SeekBar seekVolume = view.findViewById(R.id.seekVolume);
        TextView txtVolumeValue = view.findViewById(R.id.txtVolumeValue);
        LinearLayout btnPrivacy = view.findViewById(R.id.btnPrivacy);

        // Carrega valores salvos
        boolean animEnabled = prefs.getBoolean(KEY_ANIM, true);
        boolean darkEnabled = prefs.getBoolean(KEY_DARK, true);
        int volume = prefs.getInt(KEY_VOL, 80);

        switchAnimations.setChecked(animEnabled);
        switchDarkMode.setChecked(darkEnabled);
        seekVolume.setProgress(volume);
        txtVolumeValue.setText(volume + "%");

        // Salva animações
        switchAnimations.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean(KEY_ANIM, isChecked).apply()
        );

        // Salva tema escuro (neste app seu fundo já é escuro, mas a config fica salva)
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean(KEY_DARK, isChecked).apply()
        );

        // Volume REAL (o HomeFragment vai ler isso e aplicar no MediaPlayer)
        seekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtVolumeValue.setText(progress + "%");
                prefs.edit().putInt(KEY_VOL, progress).apply();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Política de privacidade
        btnPrivacy.setOnClickListener(v -> {
            Intent i = new Intent(requireContext(), PrivacyPolicyActivity.class);
            startActivity(i);
        });
    }
}
