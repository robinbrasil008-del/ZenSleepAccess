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
    public static final String KEY_HOUR = "reminder_hour";
    public static final String KEY_MINUTE = "reminder_minute";

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
        LinearLayout cardSleepReminder = view.findViewById(R.id.cardSleepReminder);
        Switch switchSleepReminder = view.findViewById(R.id.switchSleepReminder);
        TextView txtReminderStatus = view.findViewById(R.id.txtReminderStatus);
        TextView txtReminderTime = view.findViewById(R.id.txtReminderTime);
        SeekBar seekVolume = view.findViewById(R.id.seekVolume);
        TextView txtVolumeValue = view.findViewById(R.id.txtVolumeValue);
        LinearLayout btnPrivacy = view.findViewById(R.id.btnPrivacy);

        // =========================
        // 🔥 CARREGA VALORES
        // =========================
        int volume = prefs.getInt(KEY_VOL, 80);
        boolean reminderEnabled = prefs.getBoolean(KEY_REMINDER, false);
        int savedHour = prefs.getInt(KEY_HOUR, 22); // Padrão: 22h
        int savedMinute = prefs.getInt(KEY_MINUTE, 0); // Padrão: 00m

        switchSleepReminder.setChecked(reminderEnabled);
        seekVolume.setProgress(volume);
        txtVolumeValue.setText(volume + "%");

        // Ajusta os textos iniciais baseados no status do switch
        if (reminderEnabled) {
            txtReminderStatus.setText("On");
            txtReminderStatus.setTextColor(android.graphics.Color.parseColor("#00FF00"));
            txtReminderTime.setText(String.format(java.util.Locale.getDefault(), "Notificar às %02d:%02d", savedHour, savedMinute));
        } else {
            txtReminderStatus.setText("Off");
            txtReminderStatus.setTextColor(android.graphics.Color.parseColor("#94A3B8"));
            txtReminderTime.setText("Notificar na hora de relaxar");
        }

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
        
        // Abre o relógio ao clicar no Card inteiro
        cardSleepReminder.setOnClickListener(v -> {
            int currentHour = prefs.getInt(KEY_HOUR, 22);
            int currentMinute = prefs.getInt(KEY_MINUTE, 0);

            android.app.TimePickerDialog timePicker = new android.app.TimePickerDialog(requireContext(),
                (viewPicker, hourOfDay, minuteOfHour) -> {
                    // Salva o novo horário escolhido
                    prefs.edit()
                        .putInt(KEY_HOUR, hourOfDay)
                        .putInt(KEY_MINUTE, minuteOfHour)
                        .apply();
                    
                    // Se estiver desativado, ativa automaticamente ao escolher a hora
                    if (!switchSleepReminder.isChecked()) {
                        switchSleepReminder.setChecked(true);
                    } else {
                        // Se já estava ativo, apenas atualiza o texto do horário
                        txtReminderTime.setText(String.format(java.util.Locale.getDefault(), "Notificar às %02d:%02d", hourOfDay, minuteOfHour));
                    }
                }, currentHour, currentMinute, true); // 'true' força o formato 24 horas
            timePicker.show();
        });

        // Controla a ativação/desativação pela chave
        switchSleepReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_REMINDER, isChecked).apply();

            if (isChecked) {
                txtReminderStatus.setText("On");
                txtReminderStatus.setTextColor(android.graphics.Color.parseColor("#00FF00"));
                
                int h = prefs.getInt(KEY_HOUR, 22);
                int m = prefs.getInt(KEY_MINUTE, 0);
                txtReminderTime.setText(String.format(java.util.Locale.getDefault(), "Notificar às %02d:%02d", h, m));
            } else {
                txtReminderStatus.setText("Off");
                txtReminderStatus.setTextColor(android.graphics.Color.parseColor("#94A3B8"));
                txtReminderTime.setText("Notificar na hora de relaxar");
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
