package com.zensleep;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private MediaPlayer mediaPlayer;
    private CountDownTimer countDownTimer;

    private TextView txtTimer;
    private ImageView btnPlayChuva, btnPlayMar;
    private ImageView starChuva, starMar;
    private Button btnTimer;

    private boolean isChuvaPlaying = false;
    private boolean isMarPlaying = false;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        btnPlayChuva = view.findViewById(R.id.btnPlayChuva);
        btnPlayMar = view.findViewById(R.id.btnPlayMar);
        starChuva = view.findViewById(R.id.starChuva);
        starMar = view.findViewById(R.id.starMar);
        txtTimer = view.findViewById(R.id.txtTimer);
        btnTimer = view.findViewById(R.id.btnTimer);

        updateStars();

        btnPlayChuva.setOnClickListener(v -> toggleChuva());
        btnPlayMar.setOnClickListener(v -> toggleMar());

        starChuva.setOnClickListener(v -> {
            FavoritesManager.toggleFavorite(requireContext(), "chuva");
            updateStars();
        });

        starMar.setOnClickListener(v -> {
            FavoritesManager.toggleFavorite(requireContext(), "mar");
            updateStars();
        });

        btnTimer.setOnClickListener(v -> openTimerDialog());
    }

    private float getSavedVolume() {
        SharedPreferences prefs =
                requireContext().getSharedPreferences("zen_settings", 0);
        int volumePercent = prefs.getInt("volume", 80);
        return volumePercent / 100f;
    }

    private void applyVolume() {
        if (mediaPlayer != null) {
            float volume = getSavedVolume();
            mediaPlayer.setVolume(volume, volume);
        }
    }

    private void toggleChuva() {
        if (isChuvaPlaying) {
            stopSound();
            return;
        }

        stopSound();

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.chuva);
        mediaPlayer.setLooping(true);
        applyVolume();
        mediaPlayer.start();

        btnPlayChuva.setImageResource(android.R.drawable.ic_media_pause);
        btnPlayMar.setImageResource(android.R.drawable.ic_media_play);

        isChuvaPlaying = true;
        isMarPlaying = false;
    }

    private void toggleMar() {
        if (isMarPlaying) {
            stopSound();
            return;
        }

        stopSound();

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.mar);
        mediaPlayer.setLooping(true);
        applyVolume();
        mediaPlayer.start();

        btnPlayMar.setImageResource(android.R.drawable.ic_media_pause);
        btnPlayChuva.setImageResource(android.R.drawable.ic_media_play);

        isMarPlaying = true;
        isChuvaPlaying = false;
    }

    private void stopSound() {
        if (mediaPlayer != null) {
            try { mediaPlayer.stop(); } catch (Exception ignored) {}
            mediaPlayer.release();
            mediaPlayer = null;
        }

        btnPlayChuva.setImageResource(android.R.drawable.ic_media_play);
        btnPlayMar.setImageResource(android.R.drawable.ic_media_play);

        isChuvaPlaying = false;
        isMarPlaying = false;
    }

    private void openTimerDialog() {

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_timer, null);

        EditText inputMinutes = dialogView.findViewById(R.id.inputMinutes);
        Button btnStartTimer = dialogView.findViewById(R.id.btnStartTimer);
        Switch switchTimerAlarm = dialogView.findViewById(R.id.switchTimerAlarm);

        // 🔥 NOVO: Card clicável para abrir configurações do despertador
        View cardTimerAlarm = dialogView.findViewById(R.id.cardTimerAlarm);
        cardTimerAlarm.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AlarmConfigActivity.class);
            startActivity(intent);
        });

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        btnStartTimer.setOnClickListener(v -> {

            String minutesStr = inputMinutes.getText().toString().trim();

            if (minutesStr.isEmpty()) {
                inputMinutes.setError("Informe os minutos");
                return;
            }

            int minutes;
            try {
                minutes = Integer.parseInt(minutesStr);
            } catch (Exception e) {
                inputMinutes.setError("Número inválido");
                return;
            }

            if (minutes <= 0) {
                inputMinutes.setError("Tempo maior que 0");
                return;
            }

            long millis = minutes * 60L * 1000L;

            boolean shouldTriggerAlarm = switchTimerAlarm.isChecked();

            if (shouldTriggerAlarm) {

                AlarmManager alarmManager =
                        (AlarmManager) requireContext()
                                .getSystemService(Context.ALARM_SERVICE);

                if (alarmManager != null) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (!alarmManager.canScheduleExactAlarms()) {

                            Toast.makeText(
                                    requireContext(),
                                    "Ative a permissão de alarmes exatos",
                                    Toast.LENGTH_LONG
                            ).show();

                            Intent intent =
                                    new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);

                            intent.setData(Uri.parse("package:" +
                                    requireContext().getPackageName()));

                            startActivity(intent);
                            return;
                        }
                    }

                    Intent receiverIntent =
                            new Intent(requireContext(), AlarmReceiver.class);

                    receiverIntent.putExtra("alarm_id", 9999);
                    receiverIntent.putExtra("alarm_label", "Tempo finalizado");

                    PendingIntent pendingIntent =
                            PendingIntent.getBroadcast(
                                    requireContext(),
                                    9999,
                                    receiverIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT |
                                            PendingIntent.FLAG_IMMUTABLE
                            );

                    long triggerTime =
                            System.currentTimeMillis() + millis;

                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerTime,
                            pendingIntent
                    );
                }
            }

            if (countDownTimer != null) {
                countDownTimer.cancel();
            }

            countDownTimer = new CountDownTimer(millis, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / 1000;
                    long min = seconds / 60;
                    long sec = seconds % 60;

                    txtTimer.setText(
                            String.format("%02d:%02d", min, sec)
                    );
                }

                @Override
                public void onFinish() {
                    txtTimer.setText("00:00");
                    stopSound();
                }

            }.start();

            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateStars() {

        boolean chuvaFav =
                FavoritesManager.isFavorite(requireContext(), "chuva");
        boolean marFav =
                FavoritesManager.isFavorite(requireContext(), "mar");

        if (chuvaFav) {
            starChuva.setImageResource(android.R.drawable.btn_star_big_on);
            starChuva.setColorFilter(0xFFFFC107);
        } else {
            starChuva.setImageResource(android.R.drawable.btn_star_big_off);
            starChuva.setColorFilter(0xFFFFFFFF);
        }

        if (marFav) {
            starMar.setImageResource(android.R.drawable.btn_star_big_on);
            starMar.setColorFilter(0xFFFFC107);
        } else {
            starMar.setImageResource(android.R.drawable.btn_star_big_off);
            starMar.setColorFilter(0xFFFFFFFF);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopSound();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
}
