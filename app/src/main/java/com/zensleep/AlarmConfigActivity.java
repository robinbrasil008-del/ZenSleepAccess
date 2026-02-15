package com.zensleep;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AlarmConfigActivity extends AppCompatActivity {

    public static final String PREFS = "zen_settings";
    public static final String KEY_ALARM_VOLUME = "alarm_volume";
    public static final String KEY_ALARM_VIBRATE = "alarm_vibrate";
    public static final String KEY_ALARM_SNOOZE = "alarm_snooze";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_config);

        SharedPreferences prefs = getSharedPreferences(PREFS, 0);

        ImageView btnBack = findViewById(R.id.btnBack);
        SeekBar seekVolume = findViewById(R.id.seekAlarmVolume);
        TextView txtVolume = findViewById(R.id.txtAlarmVolumeValue);
        Switch switchVibrate = findViewById(R.id.switchVibrate);
        TextView txtSnooze = findViewById(R.id.txtSnoozeValue);
        View btnSave = findViewById(R.id.btnSave);

        int savedVolume = prefs.getInt(KEY_ALARM_VOLUME, 80);
        boolean vibrate = prefs.getBoolean(KEY_ALARM_VIBRATE, true);
        int snooze = prefs.getInt(KEY_ALARM_SNOOZE, 5);

        seekVolume.setProgress(savedVolume);
        txtVolume.setText(savedVolume + "%");
        switchVibrate.setChecked(vibrate);
        txtSnooze.setText(snooze + " minutos");

        btnBack.setOnClickListener(v -> finish());

        seekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtVolume.setText(progress + "%");
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnSave.setOnClickListener(v -> {

            prefs.edit()
                    .putInt(KEY_ALARM_VOLUME, seekVolume.getProgress())
                    .putBoolean(KEY_ALARM_VIBRATE, switchVibrate.isChecked())
                    .putInt(KEY_ALARM_SNOOZE, snooze)
                    .apply();

            Toast.makeText(this, "Configuração salva ✅", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
