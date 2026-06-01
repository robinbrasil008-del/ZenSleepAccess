package com.zensleep;

// 🔥 CORREÇÕES NAS IMPORTAÇÕES: Trocamos o AlertDialog pelo Dialog puro
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import androidx.appcompat.widget.SwitchCompat; 
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
    private String selectedSound = "SOM_1" "SOM_2" "SOM_3";

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
        
        SwitchCompat switchVibrate = findViewById(R.id.switchVibrate); 
        
        TextView txtSnooze = findViewById(R.id.txtSnoozeValue);
        View btnSave = findViewById(R.id.btnSave);

        int savedVolume = prefs.getInt(KEY_ALARM_VOLUME, 80);
        boolean vibrate = prefs.getBoolean(KEY_ALARM_VIBRATE, true);
        int snooze = prefs.getInt(KEY_ALARM_SNOOZE, 5);
        selectedSound = prefs.getString(KEY_ALARM_SOUND, "SOM_1");

        updateSoundText();

        seekVolume.setProgress(savedVolume);
        txtVolume.setText(savedVolume + "%");
        switchVibrate.setChecked(vibrate);
        txtSnooze.setText(snooze + " minutos");

        btnBack.setOnClickListener(v -> finish());
        
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
        
        // Usamos o LayoutInflater da própria Activity
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_select_sound, null);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);

        // 4. Setamos o fundo transparente de forma segura, agora que não há conflito
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // 5. Puxamos os IDs diretamente da variável do dialog
        TextView option1 = dialog.findViewById(R.id.optionSound1);
        TextView option2 = dialog.findViewById(R.id.optionSound2);
        TextView option3 = dialog.findViewById(R.id.optionSound3);
        TextView optionUpload = dialog.findViewById(R.id.optionUpload);

        option1.setOnClickListener(v -> {
            selectedSound = "SOM_1";
            updateSoundText();
            dialog.dismiss();
        });

        option2.setOnClickListener(v -> {
            selectedSound = "SOM_2";
            updateSoundText();
            dialog.dismiss();
        });

        option3.setOnClickListener(v -> {
            selectedSound = "SOM_3";
            updateSoundText();
            dialog.dismiss();
        });

        optionUpload.setOnClickListener(v -> {
            openAudioPicker();
            dialog.dismiss();
        });

        // 6. Mostramos o menu
        dialog.show();
    }

    private void openAudioPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, PICK_AUDIO_REQUEST);
    }

    private void updateSoundText() {
        if (selectedSound == null) {
            txtAlarmSound.setText("Padrão");
            return;
        }

        switch (selectedSound) {
            case "SOM_1":
                txtAlarmSound.setText("Som Padrão 1");
                break;
            case "SOM_2":
                txtAlarmSound.setText("Som Padrão 2");
                break;
            case "SOM_3":
                txtAlarmSound.setText("Som Padrão 3");
                break;
            default:
                txtAlarmSound.setText("📁 Escolher Audio do Dispositivo");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                getContentResolver().takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );

                selectedSound = uri.toString();
                updateSoundText();
            }
        }
    }
}
