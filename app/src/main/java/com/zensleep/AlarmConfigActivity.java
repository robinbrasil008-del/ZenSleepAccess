package com.zensleep;

import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
    private String selectedSound = "SOM_1";

    // 🔥 Variáveis para controlo do MediaPlayer e estados dos botões
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

        SeekBar seekAlarmVolume = findViewById(R.id.seekAlarmVolume);
        SwitchCompat switchAlarmVibrate = findViewById(R.id.switchAlarmVibrate);
        Button btnSave = findViewById(R.id.btnSave);

        // Carregar configurações guardadas
        selectedSound = prefs.getString(KEY_ALARM_SOUND, "SOM_1");
        updateSoundText();

        if (seekAlarmVolume != null) {
            seekAlarmVolume.setProgress(prefs.getInt(KEY_ALARM_VOLUME, 50));
        }
        if (switchAlarmVibrate != null) {
            switchAlarmVibrate.setChecked(prefs.getBoolean(KEY_ALARM_VIBRATE, true));
        }

        // Cliques dos botões principais
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (cardAlarmSound != null) {
            cardAlarmSound.setOnClickListener(v -> showSoundSelectionDialog());
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(KEY_ALARM_SOUND, selectedSound);
                if (seekAlarmVolume != null) {
                    editor.putInt(KEY_ALARM_VOLUME, seekAlarmVolume.getProgress());
                }
                if (switchAlarmVibrate != null) {
                    editor.putBoolean(KEY_ALARM_VIBRATE, switchAlarmVibrate.isChecked());
                }
                editor.apply();

                Toast.makeText(this, "Configurações guardadas!", Toast.LENGTH_SHORT).show();
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

        // Mapeamento dos cliques de texto para selecionar e fechar
        TextView optionSound1 = dialogView.findViewById(R.id.optionSound1);
        TextView optionSound2 = dialogView.findViewById(R.id.optionSound2);
        TextView optionSound3 = dialogView.findViewById(R.id.optionSound3);
        TextView optionUpload = dialogView.findViewById(R.id.optionUpload);

        // Mapeamento dos novos botões de Play/Pause independentes
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

        // 🔥 Lógica de clique para tocar o áudio antes de escolher
        // Nota: Altere 'R.raw.som_1' para o nome exato do arquivo de áudio na sua pasta res/raw
        if (btnPlaySound1 != null) {
            btnPlaySound1.setOnClickListener(v -> togglePlaySound("SOM_1", btnPlaySound1, R.raw.som1));
        }
        if (btnPlaySound2 != null) {
            btnPlaySound2.setOnClickListener(v -> togglePlaySound("SOM_2", btnPlaySound2, R.raw.som2));
        }
        if (btnPlaySound3 != null) {
            btnPlaySound3.setOnClickListener(v -> togglePlaySound("SOM_3", btnPlaySound3, R.raw.som3));
        }

        // Garante que se o utilizador fechar tocando fora, a música para imediatamente
        dialog.setOnDismissListener(dialogInterface -> stopAnyPlayback());

        dialog.show();
    }

    private void togglePlaySound(String soundKey, ImageView playButton, int audioResId) {
        // 1. Caso o mesmo som já esteja ativo: alterna entre Pause e Play
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

        // 2. Se clicou num som diferente, para o áudio anterior primeiro e reseta o ícone
        stopAnyPlayback();

        // 3. Inicializa e começa a reprodução do novo som selecionado
        try {
            mediaPlayer = MediaPlayer.create(this, audioResId);
            if (mediaPlayer != null) {
                currentPlayingSound = soundKey;
                currentPlayingButton = playButton;

                mediaPlayer.start();
                playButton.setImageResource(R.drawable.ic_media_pause);

                // Quando o áudio chegar ao fim sozinho, reinicia o estado visual do botão para Play
                mediaPlayer.setOnCompletionListener(mp -> stopAnyPlayback());
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao reproduzir áudio", Toast.LENGTH_SHORT).show();
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

        // Restaura o ícone do botão que estava ativo de volta para PLAY
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
        stopAnyPlayback(); // Evita fugas de memória (Memory Leaks) caso a Activity seja destruída
    }
}

