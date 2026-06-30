package com.zensleep;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    public static final String PREFS = "zen_settings";

    public static final String KEY_REMINDER = "sleep_reminder";

    public static final String KEY_VOL = "volume";

    public SettingsFragment() {
        super(R.layout.fragment_settings);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS, 0);

        // 🔥 CARD DO DESPERTADOR
        LinearLayout cardAlarm = view.findViewById(R.id.cardAlarm);

        // OUTROS CONTROLES
        Switch switchSleepReminder = view.findViewById(R.id.switchSleepReminder);
        TextView txtReminderStatus = view.findViewById(R.id.txtReminderStatus);
        SeekBar seekVolume = view.findViewById(R.id.seekVolume);
        TextView txtVolumeValue = view.findViewById(R.id.txtVolumeValue);
        LinearLayout btnPrivacy = view.findViewById(R.id.btnPrivacy);

        // =========================
        // 🔥 CARREGA VALORES
        // =========================

        int volume = prefs.getInt(KEY_VOL, 80);
        boolean reminderEnabled = prefs.getBoolean(KEY_REMINDER, false); // Começa desligado por padrão
switchSleepReminder.setChecked(reminderEnabled);

// Ajusta a cor inicial do texto On/Off
if (reminderEnabled) {
    txtReminderStatus.setText("On");
    txtReminderStatus.setTextColor(android.graphics.Color.parseColor("#00FF00"));
} else {
    txtReminderStatus.setText("Off");
    txtReminderStatus.setTextColor(android.graphics.Color.parseColor("#94A3B8"));
}

        seekVolume.setProgress(volume);
        txtVolumeValue.setText(volume + "%");

        // =========================
        // ⏰ ABRIR TELA NOVA DE CONFIGURAÇÃO
        // =========================
        cardAlarm.setOnClickListener(v -> {
            Intent i = new Intent(requireContext(), AlarmConfigActivity.class);
            startActivity(i);
        });

        // =========================
        // 🌙 LEMBRETE DE DORMIR
        // =========================
switchSleepReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
        // Salva a preferência na memória
    prefs.edit().putBoolean(KEY_REMINDER, isChecked).apply();

    // Muda a cor e o texto dinamicamente
    if (isChecked) {
        txtReminderStatus.setText("On");
        txtReminderStatus.setTextColor(android.graphics.Color.parseColor("#00FF00"));
    } else {
        txtReminderStatus.setText("Off");
        txtReminderStatus.setTextColor(android.graphics.Color.parseColor("#94A3B8"));
    }
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
