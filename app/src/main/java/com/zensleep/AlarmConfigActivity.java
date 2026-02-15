package com.zensleep;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    public static final String KEY_ALARM_SOUND = "alarm_sound";

    private static final int PICK_AUDIO_REQUEST = 1001;

    private TextView txtAlarmSound;
    private String selectedSound = "SOM_1"; // padrão

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_config);

        SharedPreferences prefs = getSharedPreferences(PREFS, 0);

        ImageView btnBack = findViewById(R.id.btnBack);
        LinearLayout cardAlarmSound = findViewById(R.id.cardAlarmSound);
        txtAlarmSound = findViewById(R.id.txtAlarmSound);

        SeekBar seekVolume = findViewById(R.id.seekAlarmVolume);
        TextView txtVolume = findViewById(R.id.txtAlarmVolumeValue);
        Switch switchVibrate = findViewById(R.id.switchVibrate);
        TextView txtSnooze = findViewById(R.id.txtSnoozeValue);
        View btnSave = findViewById(R.id.btnSave);

        int savedVolume = prefs.getInt(KEY_ALARM_VOLUME, 80);
        boolean vibrate = prefs.getBoolean(KEY_ALARM_VIBRATE, true);
        int snooze = prefs.getInt(KEY_ALARM_SNOOZE, 5);
        selectedSound = prefs.getString(KEY_ALARM_SOUND, "SOM_1");

        // Atualiza nome do som
        updateSoundText();

        seekVolume.setProgress(savedVolume);
        txtVolume.setText(savedVolume + "%");
        switchVibrate.setChecked(vibrate);
        txtSnooze.setText(snooze + " minutos");

        btnBack.setOnClickListener(v -> finish());

        // 🔥 ABRIR SELETOR DE SOM
        cardAlarmSound.setOnClickListener(v -> openSoundDialog());

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
                    .putString(KEY_ALARM_SOUND, selectedSound)
                    .apply();

            Toast.makeText(this, "Configuração salva ✅", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void openSoundDialog() {

        String[] options = {
                "Som 1",
                "Som 2",
                "Som 3",
                "Upload de áudio"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolher Som do Alarme");

        builder.setItems(options, (dialog, which) -> {

            switch (which) {
                case 0:
                    selectedSound = "SOM_1";
                    break;
                case 1:
                    selectedSound = "SOM_2";
                    break;
                case 2:
                    selectedSound = "SOM_3";
                    break;
                case 3:
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, PICK_AUDIO_REQUEST);
                    return;
            }

            updateSoundText();
        });

        builder.show();
    }

    private void updateSoundText() {

        if (selectedSound == null) {
            txtAlarmSound.setText("🎵 Padrão");
            return;
        }

        switch (selectedSound) {
            case "SOM_1":
                txtAlarmSound.setText("🎵 Som 1");
                break;
            case "SOM_2":
                txtAlarmSound.setText("🎵 Som 2");
                break;
            case "SOM_3":
                txtAlarmSound.setText("🎵 Som 3");
                break;
            default:
                txtAlarmSound.setText("🎵 Áudio Personalizado");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                selectedSound = uri.toString();
                updateSoundText();
            }
        }
    }
}
