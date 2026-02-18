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

    // ✅ NOVOS BOTÕES
    private ImageView btnPlayFloresta, btnPlayLareira, btnPlayVento,
            btnPlayGrilos, btnPlayPassaros, btnPlayRiacho, btnPlayCafeteira;

    private ImageView starChuva, starMar;

    // ✅ NOVAS ESTRELAS
    private ImageView starFloresta, starLareira, starVento,
            starGrilos, starPassaros, starRiacho, starCafeteira;

    private Button btnTimer;

    private boolean isChuvaPlaying = false;
    private boolean isMarPlaying = false;

    // ✅ CONTROLE DO SOM ATUAL (pra evitar tocar 2 ao mesmo tempo)
    private String currentSoundKey = null;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        btnPlayChuva = view.findViewById(R.id.btnPlayChuva);
        btnPlayMar = view.findViewById(R.id.btnPlayMar);

        // ✅ NOVOS findViewById (precisa existir no XML)
        btnPlayFloresta = view.findViewById(R.id.btnPlayFloresta);
        btnPlayLareira = view.findViewById(R.id.btnPlayLareira);
        btnPlayVento = view.findViewById(R.id.btnPlayVento);
        btnPlayGrilos = view.findViewById(R.id.btnPlayGrilos);
        btnPlayPassaros = view.findViewById(R.id.btnPlayPassaros);
        btnPlayRiacho = view.findViewById(R.id.btnPlayRiacho);
        btnPlayCafeteira = view.findViewById(R.id.btnPlayCafeteira);

        starChuva = view.findViewById(R.id.starChuva);
        starMar = view.findViewById(R.id.starMar);

        // ✅ NOVOS favoritos (precisa existir no XML)
        starFloresta = view.findViewById(R.id.starFloresta);
        starLareira = view.findViewById(R.id.starLareira);
        starVento = view.findViewById(R.id.starVento);
        starGrilos = view.findViewById(R.id.starGrilos);
        starPassaros = view.findViewById(R.id.starPassaros);
        starRiacho = view.findViewById(R.id.starRiacho);
        starCafeteira = view.findViewById(R.id.starCafeteira);

        txtTimer = view.findViewById(R.id.txtTimer);
        btnTimer = view.findViewById(R.id.btnTimer);

        updateStars();

        btnPlayChuva.setOnClickListener(v -> toggleChuva());
        btnPlayMar.setOnClickListener(v -> toggleMar());

        // ✅ NOVOS clique play/pause
        btnPlayFloresta.setOnClickListener(v -> toggleGeneric("floresta", R.raw.floresta, btnPlayFloresta));
        btnPlayLareira.setOnClickListener(v -> toggleGeneric("lareira", R.raw.lareira, btnPlayLareira));
        btnPlayVento.setOnClickListener(v -> toggleGeneric("vento", R.raw.vento_suave, btnPlayVento));
        btnPlayGrilos.setOnClickListener(v -> toggleGeneric("grilos", R.raw.grilos, btnPlayGrilos));
        btnPlayPassaros.setOnClickListener(v -> toggleGeneric("passaros", R.raw.passaros, btnPlayPassaros));
        btnPlayRiacho.setOnClickListener(v -> toggleGeneric("riacho", R.raw.riacho, btnPlayRiacho));
        btnPlayCafeteira.setOnClickListener(v -> toggleGeneric("cafeteira", R.raw.cafeteira, btnPlayCafeteira));

        starChuva.setOnClickListener(v -> {
            FavoritesManager.toggleFavorite(requireContext(), "chuva");
            updateStars();
        });

        starMar.setOnClickListener(v -> {
            FavoritesManager.toggleFavorite(requireContext(), "mar");
            updateStars();
        });

        // ✅ NOVOS favoritos
        starFloresta.setOnClickListener(v -> {
            FavoritesManager.toggleFavorite(requireContext(), "floresta");
            updateStars();
        });

        starLareira.setOnClickListener(v -> {
            FavoritesManager.toggleFavorite(requireContext(), "lareira");
            updateStars();
        });

        starVento.setOnClickListener(v -> {
            FavoritesManager.toggleFavorite(requireContext(), "vento");
            updateStars();
        });

        starGrilos.setOnClickListener(v -> {
            FavoritesManager.toggleFavorite(requireContext(), "grilos");
            updateStars();
        });

        starPassaros.setOnClickListener(v -> {
            FavoritesManager.toggleFavorite(requireContext(), "passaros");
            updateStars();
        });

        starRiacho.setOnClickListener(v -> {
            FavoritesManager.toggleFavorite(requireContext(), "riacho");
            updateStars();
        });

        starCafeteira.setOnClickListener(v -> {
            FavoritesManager.toggleFavorite(requireContext(), "cafeteira");
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

        // ✅ reseta ícones dos novos também
        resetNewButtonsToPlay();

        isChuvaPlaying = true;
        isMarPlaying = false;
        currentSoundKey = "chuva";
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

        // ✅ reseta ícones dos novos também
        resetNewButtonsToPlay();

        isMarPlaying = true;
        isChuvaPlaying = false;
        currentSoundKey = "mar";
    }

    // ✅ NOVO: toggle genérico (não mexe no timer)
    private void toggleGeneric(String key, int rawRes, ImageView button) {

        // se clicou no mesmo que já tá tocando -> para
        if (currentSoundKey != null && currentSoundKey.equals(key) && mediaPlayer != null) {
            stopSound();
            return;
        }

        stopSound();

        mediaPlayer = MediaPlayer.create(requireContext(), rawRes);
        mediaPlayer.setLooping(true);
        applyVolume();
        mediaPlayer.start();

        // reseta todos pra play e só esse fica pause
        btnPlayChuva.setImageResource(android.R.drawable.ic_media_play);
        btnPlayMar.setImageResource(android.R.drawable.ic_media_play);
        resetNewButtonsToPlay();

        button.setImageResource(android.R.drawable.ic_media_pause);

        isChuvaPlaying = false;
        isMarPlaying = false;
        currentSoundKey = key;
    }

    private void resetNewButtonsToPlay() {
        if (btnPlayFloresta != null) btnPlayFloresta.setImageResource(android.R.drawable.ic_media_play);
        if (btnPlayLareira != null) btnPlayLareira.setImageResource(android.R.drawable.ic_media_play);
        if (btnPlayVento != null) btnPlayVento.setImageResource(android.R.drawable.ic_media_play);
        if (btnPlayGrilos != null) btnPlayGrilos.setImageResource(android.R.drawable.ic_media_play);
        if (btnPlayPassaros != null) btnPlayPassaros.setImageResource(android.R.drawable.ic_media_play);
        if (btnPlayRiacho != null) btnPlayRiacho.setImageResource(android.R.drawable.ic_media_play);
        if (btnPlayCafeteira != null) btnPlayCafeteira.setImageResource(android.R.drawable.ic_media_play);
    }

    private void stopSound() {
        if (mediaPlayer != null) {
            try { mediaPlayer.stop(); } catch (Exception ignored) {}
            mediaPlayer.release();
            mediaPlayer = null;
        }

        btnPlayChuva.setImageResource(android.R.drawable.ic_media_play);
        btnPlayMar.setImageResource(android.R.drawable.ic_media_play);

        resetNewButtonsToPlay();

        isChuvaPlaying = false;
        isMarPlaying = false;
        currentSoundKey = null;
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

        boolean florestaFav =
                FavoritesManager.isFavorite(requireContext(), "floresta");
        boolean lareiraFav =
                FavoritesManager.isFavorite(requireContext(), "lareira");
        boolean ventoFav =
                FavoritesManager.isFavorite(requireContext(), "vento");
        boolean grilosFav =
                FavoritesManager.isFavorite(requireContext(), "grilos");
        boolean passarosFav =
                FavoritesManager.isFavorite(requireContext(), "passaros");
        boolean riachoFav =
                FavoritesManager.isFavorite(requireContext(), "riacho");
        boolean cafeteiraFav =
                FavoritesManager.isFavorite(requireContext(), "cafeteira");

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

        // ✅ NOVOS
        if (starFloresta != null) {
            if (florestaFav) {
                starFloresta.setImageResource(android.R.drawable.btn_star_big_on);
                starFloresta.setColorFilter(0xFFFFC107);
            } else {
                starFloresta.setImageResource(android.R.drawable.btn_star_big_off);
                starFloresta.setColorFilter(0xFFFFFFFF);
            }
        }

        if (starLareira != null) {
            if (lareiraFav) {
                starLareira.setImageResource(android.R.drawable.btn_star_big_on);
                starLareira.setColorFilter(0xFFFFC107);
            } else {
                starLareira.setImageResource(android.R.drawable.btn_star_big_off);
                starLareira.setColorFilter(0xFFFFFFFF);
            }
        }

        if (starVento != null) {
            if (ventoFav) {
                starVento.setImageResource(android.R.drawable.btn_star_big_on);
                starVento.setColorFilter(0xFFFFC107);
            } else {
                starVento.setImageResource(android.R.drawable.btn_star_big_off);
                starVento.setColorFilter(0xFFFFFFFF);
            }
        }

        if (starGrilos != null) {
            if (grilosFav) {
                starGrilos.setImageResource(android.R.drawable.btn_star_big_on);
                starGrilos.setColorFilter(0xFFFFC107);
            } else {
                starGrilos.setImageResource(android.R.drawable.btn_star_big_off);
                starGrilos.setColorFilter(0xFFFFFFFF);
            }
        }

        if (starPassaros != null) {
            if (passarosFav) {
                starPassaros.setImageResource(android.R.drawable.btn_star_big_on);
                starPassaros.setColorFilter(0xFFFFC107);
            } else {
                starPassaros.setImageResource(android.R.drawable.btn_star_big_off);
                starPassaros.setColorFilter(0xFFFFFFFF);
            }
        }

        if (starRiacho != null) {
            if (riachoFav) {
                starRiacho.setImageResource(android.R.drawable.btn_star_big_on);
                starRiacho.setColorFilter(0xFFFFC107);
            } else {
                starRiacho.setImageResource(android.R.drawable.btn_star_big_off);
                starRiacho.setColorFilter(0xFFFFFFFF);
            }
        }

        if (starCafeteira != null) {
            if (cafeteiraFav) {
                starCafeteira.setImageResource(android.R.drawable.btn_star_big_on);
                starCafeteira.setColorFilter(0xFFFFC107);
            } else {
                starCafeteira.setImageResource(android.R.drawable.btn_star_big_off);
                starCafeteira.setColorFilter(0xFFFFFFFF);
            }
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
