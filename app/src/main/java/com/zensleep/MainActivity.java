package com.zensleep;

import android.app.AlertDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout cardChuva = findViewById(R.id.cardChuva);
        LinearLayout cardMar = findViewById(R.id.cardMar);
        Button btnTimer = findViewById(R.id.btnTimer);

        cardChuva.setOnClickListener(v -> playSound(R.raw.chuva));
        cardMar.setOnClickListener(v -> playSound(R.raw.mar));

        btnTimer.setOnClickListener(v -> showTimerDialog());
    }

    private void playSound(int soundResId) {

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(this, soundResId);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    private void showTimerDialog() {

        View view = getLayoutInflater().inflate(R.layout.dialog_timer, null);

        EditText inputMinutes = view.findViewById(R.id.inputMinutes);
        Button btnStart = view.findViewById(R.id.btnStartTimer);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        btnStart.setOnClickListener(v -> {

            String value = inputMinutes.getText().toString().trim();

            if (value.isEmpty()) {
                Toast.makeText(this, "Digite um tempo válido", Toast.LENGTH_SHORT).show();
                return;
            }

            int minutes = Integer.parseInt(value);

            if (minutes <= 0) {
                Toast.makeText(this, "Tempo inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            startTimer(minutes);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void startTimer(int minutes) {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        long millis = minutes * 60L * 1000L;

        countDownTimer = new CountDownTimer(millis, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                // opcional: atualizar contador na tela
            }

            @Override
            public void onFinish() {
                stopSound();
                Toast.makeText(MainActivity.this,
                        "Timer finalizado",
                        Toast.LENGTH_SHORT).show();
            }
        };

        countDownTimer.start();

        Toast.makeText(this,
                "Timer iniciado por " + minutes + " minutos",
                Toast.LENGTH_SHORT).show();
    }

    private void stopSound() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSound();
    }
}
