package com.zensleep;

import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch; 
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;


public class AlarmConfigActivity extends AppCompatActivity {

    public static final String PREFS = "zen_settings";
    public static final String KEY_ALARM_VOLUME = "alarm_volume";
    public static final String KEY_ALARM_VIBRATE = "alarm_vibrate";
    public static final String KEY_ALARM_SNOOZE = "alarm_snooze";
    public static final String KEY_ALARM_SOUND = "alarm_sound";

    private static final int PICK_AUDIO_REQUEST = 1001;

    private TextView txtAlarmSound;
    private String selectedSound = "SOM_1";

    private MediaPlayer mediaPlayer;
    private ImageView currentPlayingButton = null;
    private String currentPlayingSound = "";

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
        TextView txtVibrateStatus = findViewById(R.id.txtVibrateStatus);

// 2. Configura o estado inicial ao abrir a tela
if (switchVibrate.isChecked()) {
    txtVibrateStatus.setText("On");
    txtVibrateStatus.setTextColor(android.graphics.Color.parseColor("#00FF00")); // Verde
} else {
    txtVibrateStatus.setText("Off");
    txtVibrateStatus.setTextColor(android.graphics.Color.parseColor("#94A3B8")); // Cinza
}

// 3. Listener para mudar conforme o usuário interage
switchVibrate.setOnCheckedChangeListener((buttonView, isChecked) -> {
    if (isChecked) {
        txtVibrateStatus.setText("On");
        txtVibrateStatus.setTextColor(android.graphics.Color.parseColor("#00FF00"));
    } else {
        txtVibrateStatus.setText("Off");
        txtVibrateStatus.setTextColor(android.graphics.Color.parseColor("#94A3B8"));
    }
});
        
        TextView txtSnooze = findViewById(R.id.txtSnoozeValue);
        View btnSave = findViewById(R.id.btnSave);

        // ======= MODO IMERSIVO (ESCONDE AS BARRAS DE STATUS E NAVEGAÇÃO) =======
        // Avisa o sistema que o app vai gerenciar a tela inteira
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        WindowInsetsControllerCompat controller = 
            WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        if (controller != null) {
            // Esconde a barra de bateria/hora (topo) e a barra de botões (baixo)
            controller.hide(WindowInsetsCompat.Type.systemBars());
            
            // Faz com que as barras apareçam rapidinho se o usuário deslizar o dedo da borda da tela,
            // e sumam sozinhas depois de alguns segundos (comportamento padrão de tela cheia).
            controller.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            );
        }

        // 🔥 AQUI ESTÃO AS VARIÁVEIS! Elas precisam existir antes de serem usadas abaixo.
        int savedVolume = prefs.getInt(KEY_ALARM_VOLUME, 80);
        boolean vibrate = prefs.getBoolean(KEY_ALARM_VIBRATE, true);
        int snooze = prefs.getInt(KEY_ALARM_SNOOZE, 5);
        selectedSound = prefs.getString(KEY_ALARM_SOUND, "SOM_1");

        updateSoundText();

        if (seekVolume != null) {
            seekVolume.setProgress(savedVolume);
            seekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (txtVolume != null) txtVolume.setText(progress + "%");
                }
                @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }
        
        if (txtVolume != null) {
            txtVolume.setText(savedVolume + "%");
        }
        
        // Agora o Android Studio vai encontrar o "vibrate" sem problemas
        if (switchVibrate != null) {
            switchVibrate.setChecked(vibrate);
        }
        
        if (txtSnooze != null) {
            txtSnooze.setText(snooze + " minutos");
        }

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
        
        if (cardAlarmSound != null) cardAlarmSound.setOnClickListener(v -> showSoundSelectionDialog());

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(KEY_ALARM_SOUND, selectedSound);
                
                if (seekVolume != null) {
                    editor.putInt(KEY_ALARM_VOLUME, seekVolume.getProgress());
                }
                
                if (switchVibrate != null) {
                    editor.putBoolean(KEY_ALARM_VIBRATE, switchVibrate.isChecked());
                }
                
                editor.putInt(KEY_ALARM_SNOOZE, snooze);
                editor.apply();

                Toast.makeText(this, "Configuração salva ✅", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }

    private void showSoundSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_select_sound, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView optionSound1 = dialogView.findViewById(R.id.optionSound1);
        TextView optionSound2 = dialogView.findViewById(R.id.optionSound2);
        TextView optionSound3 = dialogView.findViewById(R.id.optionSound3);
        TextView optionUpload = dialogView.findViewById(R.id.optionUpload);

        ImageView btnPlaySound1 = dialogView.findViewById(R.id.btnPlaySound1);
        ImageView btnPlaySound2 = dialogView.findViewById(R.id.btnPlaySound2);
        ImageView btnPlaySound3 = dialogView.findViewById(R.id.btnPlaySound3);

        if (optionSound1 != null) {
            optionSound1.setOnClickListener(v -> {
                selectedSound = "SOM_1";
                updateSoundText();
                stopAnyPlayback();
                dialog.dismiss();
            });
        }

        if (optionSound2 != null) {
            optionSound2.setOnClickListener(v -> {
                selectedSound = "SOM_2";
                updateSoundText();
                stopAnyPlayback();
                dialog.dismiss();
            });
        }

        if (optionSound3 != null) {
            optionSound3.setOnClickListener(v -> {
                selectedSound = "SOM_3";
                updateSoundText();
                stopAnyPlayback();
                dialog.dismiss();
            });
        }

        if (optionUpload != null) {
            optionUpload.setOnClickListener(v -> {
                openAudioPicker();
                stopAnyPlayback();
                dialog.dismiss();
            });
        }

        if (btnPlaySound1 != null) {
            btnPlaySound1.setOnClickListener(v -> togglePlaySound("SOM_1", btnPlaySound1, R.raw.som1));
        }
        if (btnPlaySound2 != null) {
            btnPlaySound2.setOnClickListener(v -> togglePlaySound("SOM_2", btnPlaySound2, R.raw.som2));
        }
        if (btnPlaySound3 != null) {
            btnPlaySound3.setOnClickListener(v -> togglePlaySound("SOM_3", btnPlaySound3, R.raw.som3));
        }

        dialog.setOnDismissListener(dialogInterface -> stopAnyPlayback());
        dialog.show();
    }

    private void togglePlaySound(String soundKey, ImageView playButton, int audioResId) {
        if (mediaPlayer != null && currentPlayingSound.equals(soundKey)) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playButton.setImageResource(R.drawable.ic_media_play);
            } else {
                mediaPlayer.start();
                playButton.setImageResource(R.drawable.ic_media_pause);
            }
            return;
        }

        stopAnyPlayback();

        try {
            mediaPlayer = MediaPlayer.create(this, audioResId);
            if (mediaPlayer != null) {
                currentPlayingSound = soundKey;
                currentPlayingButton = playButton;

                mediaPlayer.start();
                playButton.setImageResource(R.drawable.ic_media_pause);

                mediaPlayer.setOnCompletionListener(mp -> stopAnyPlayback());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopAnyPlayback() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mediaPlayer = null;
        }

        if (currentPlayingButton != null) {
            currentPlayingButton.setImageResource(R.drawable.ic_media_play);
            currentPlayingButton = null;
        }
        currentPlayingSound = "";
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAnyPlayback();
    }
}
