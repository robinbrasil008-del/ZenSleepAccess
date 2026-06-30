package com.zensleep;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import java.util.Calendar;

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
        
        cardSleepReminder.setOnClickListener(v -> {
            int currentHour = prefs.getInt(KEY_HOUR, 22);
            int currentMinute = prefs.getInt(KEY_MINUTE, 0);

            android.app.TimePickerDialog timePicker = new android.app.TimePickerDialog(requireContext(),
                (viewPicker, hourOfDay, minuteOfHour) -> {
                    prefs.edit()
                        .putInt(KEY_HOUR, hourOfDay)
                        .putInt(KEY_MINUTE, minuteOfHour)
                        .apply();
                    
                    if (!switchSleepReminder.isChecked()) {
                        // Se tava desligado, liga a chave (ela já vai chamar o agendamento no Listener abaixo)
                        switchSleepReminder.setChecked(true);
                    } else {
                        // Se já estava ligado, atualiza o texto e reagenda para o novo horário
                        txtReminderTime.setText(String.format(java.util.Locale.getDefault(), "Notificar às %02d:%02d", hourOfDay, minuteOfHour));
                        agendarLembrete(hourOfDay, minuteOfHour); 
                    }
                }, currentHour, currentMinute, true);
            timePicker.show();
        });

        switchSleepReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_REMINDER, isChecked).apply();

            int h = prefs.getInt(KEY_HOUR, 22);
            int m = prefs.getInt(KEY_MINUTE, 0);

            if (isChecked) {
                txtReminderStatus.setText("On");
                txtReminderStatus.setTextColor(android.graphics.Color.parseColor("#00FF00"));
                txtReminderTime.setText(String.format(java.util.Locale.getDefault(), "Notificar às %02d:%02d", h, m));
                
                // LIGA A NOTIFICAÇÃO 🚀
                agendarLembrete(h, m);
            } else {
                txtReminderStatus.setText("Off");
                txtReminderStatus.setTextColor(android.graphics.Color.parseColor("#94A3B8"));
                txtReminderTime.setText("Notificar na hora de relaxar");
                
                // DESLIGA A NOTIFICAÇÃO 🛑
                cancelarLembrete();
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

            // =========================
    // 🔥 MÉTODOS DO MOTOR DE NOTIFICAÇÃO
    // =========================
    private void agendarLembrete(int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Se o horário escolhido já passou hoje, agenda para disparar amanhã
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Agenda um alarme que se repete todo dia nesse horário
        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
        }
    }

    private void cancelarLembrete() {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    }
}
