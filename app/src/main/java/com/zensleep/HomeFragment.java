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
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.HashMap;

public class HomeFragment extends Fragment {

    private InterstitialAd mInterstitialAd;

    private AdView adView;

    // ======= PLAYER (NOVO MIX / MULTI-SOM) =======
    private final HashMap<String, MediaPlayer> players = new HashMap<>();

    private CountDownTimer countDownTimer;

    private TextView txtTimer;

    // PLAY BUTTONS
    private ImageView btnPlayChuva, btnPlayMar;
    private ImageView btnPlayFloresta, btnPlayLareira, btnPlayVento,
            btnPlayGrilos, btnPlayPassaros, btnPlayRiacho, btnPlayCafeteira;

    // SEEKBARS (VOLUME POR CARD)
    private SeekBar seekChuva, seekMar, seekFloresta, seekLareira,
            seekVento, seekGrilos, seekPassaros, seekRiacho, seekCafeteira;

    // FAVORITOS
    private ImageView starChuva, starMar;
    private ImageView starFloresta, starLareira, starVento,
            starGrilos, starPassaros, starRiacho, starCafeteira;

    private Button btnTimer;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        UnityAds.show(getActivity(), "Interstitial_Android");

        adView = view.findViewById(R.id.adView);

        loadInterstitialAd();

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        // ======= PLAY BUTTONS =======
        btnPlayChuva = view.findViewById(R.id.btnPlayChuva);
        btnPlayMar = view.findViewById(R.id.btnPlayMar);

        btnPlayFloresta = view.findViewById(R.id.btnPlayFloresta);
        btnPlayLareira = view.findViewById(R.id.btnPlayLareira);
        btnPlayVento = view.findViewById(R.id.btnPlayVento);
        btnPlayGrilos = view.findViewById(R.id.btnPlayGrilos);
        btnPlayPassaros = view.findViewById(R.id.btnPlayPassaros);
        btnPlayRiacho = view.findViewById(R.id.btnPlayRiacho);
        btnPlayCafeteira = view.findViewById(R.id.btnPlayCafeteira);

        // ======= SEEKBARS =======
        seekChuva = view.findViewById(R.id.seekChuva);
        seekMar = view.findViewById(R.id.seekMar);

        seekFloresta = view.findViewById(R.id.seekFloresta);
        seekLareira = view.findViewById(R.id.seekLareira);
        seekVento = view.findViewById(R.id.seekVento);
        seekGrilos = view.findViewById(R.id.seekGrilos);
        seekPassaros = view.findViewById(R.id.seekPassaros);
        seekRiacho = view.findViewById(R.id.seekRiacho);
        seekCafeteira = view.findViewById(R.id.seekCafeteira);

        // ======= FAVORITOS =======
        starChuva = view.findViewById(R.id.starChuva);
        starMar = view.findViewById(R.id.starMar);

        starFloresta = view.findViewById(R.id.starFloresta);
        starLareira = view.findViewById(R.id.starLareira);
        starVento = view.findViewById(R.id.starVento);
        starGrilos = view.findViewById(R.id.starGrilos);
        starPassaros = view.findViewById(R.id.starPassaros);
        starRiacho = view.findViewById(R.id.starRiacho);
        starCafeteira = view.findViewById(R.id.starCafeteira);

        // ======= TIMER =======
        txtTimer = view.findViewById(R.id.txtTimer);
        btnTimer = view.findViewById(R.id.btnTimer);

        // ======= SETUP MIX (MULTI-SOM) =======
        setupSound("chuva", R.raw.chuva, btnPlayChuva, seekChuva);
        setupSound("mar", R.raw.mar, btnPlayMar, seekMar);

        setupSound("floresta", R.raw.floresta, btnPlayFloresta, seekFloresta);
        setupSound("lareira", R.raw.lareira, btnPlayLareira, seekLareira);
        setupSound("vento", R.raw.vento_suave, btnPlayVento, seekVento);
        setupSound("grilos", R.raw.grilos, btnPlayGrilos, seekGrilos);
        setupSound("passaros", R.raw.passaros, btnPlayPassaros, seekPassaros);
        setupSound("riacho", R.raw.riacho, btnPlayRiacho, seekRiacho);
        setupSound("cafeteira", R.raw.cafeteira, btnPlayCafeteira, seekCafeteira);

        // ======= FAVORITOS CLIQUES =======
        updateStars();

        starChuva.setOnClickListener(v -> {
            FavoritesManager.toggleFavorite(requireContext(), "chuva");
            updateStars();
        });

        starMar.setOnClickListener(v -> {
            FavoritesManager.toggleFavorite(requireContext(), "mar");
            updateStars();
        });

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

        BannerView banner = new BannerView(getActivity(), "Banner_Android", new UnityBannerSize(320, 50));

FrameLayout bannerLayout = view.findViewById(R.id.banner_container);

bannerLayout.addView(banner);

banner.load();

        if (UnityAds.isInitialized()) {
    UnityAds.show(getActivity(), "Interstitial_Android");
        }
    }

    private void loadInterstitialAd() {

    AdRequest adRequest = new AdRequest.Builder().build();

    InterstitialAd.load(requireContext(),
            "ca-app-pub-8296610548842772/8938304987", // ID TESTE
            adRequest,
            new InterstitialAdLoadCallback() {

                @Override
                public void onAdLoaded(InterstitialAd interstitialAd) {
                    mInterstitialAd = interstitialAd;

                    new android.os.Handler().postDelayed(() -> {
    if (mInterstitialAd != null) {
        mInterstitialAd.show(requireActivity());
    }
}, 800); // 0.8 segundos após abrir

                    mInterstitialAd.setFullScreenContentCallback(
                            new FullScreenContentCallback() {

                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    mInterstitialAd = null;
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(
                                        com.google.android.gms.ads.AdError adError) {
                                    mInterstitialAd = null;
                                }
                            });
                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    mInterstitialAd = null;
                }
            });
    }

    // ======= VOLUME MASTER (CONFIG) =======
    private float getSavedVolume() {
        SharedPreferences prefs =
                requireContext().getSharedPreferences("zen_settings", 0);
        int volumePercent = prefs.getInt("volume", 80);
        return volumePercent / 100f;
    }

    private float computeFinalVolume(SeekBar seekBar) {
        float master = getSavedVolume();                 // 0..1
        float card = (seekBar != null ? (seekBar.getProgress() / 100f) : 0.8f); // 0..1
        float finalVol = master * card;
        if (finalVol < 0f) finalVol = 0f;
        if (finalVol > 1f) finalVol = 1f;
        return finalVol;
    }

    private void applyVolumeForKey(String key, SeekBar seekBar) {
        MediaPlayer mp = players.get(key);
        if (mp != null) {
            float v = computeFinalVolume(seekBar);
            mp.setVolume(v, v);
        }
    }

    private void applyMasterVolumeToAll() {
        // Reaplica master*card em todo mundo (útil quando muda no settings)
        applyVolumeForKey("chuva", seekChuva);
        applyVolumeForKey("mar", seekMar);
        applyVolumeForKey("floresta", seekFloresta);
        applyVolumeForKey("lareira", seekLareira);
        applyVolumeForKey("vento", seekVento);
        applyVolumeForKey("grilos", seekGrilos);
        applyVolumeForKey("passaros", seekPassaros);
        applyVolumeForKey("riacho", seekRiacho);
        applyVolumeForKey("cafeteira", seekCafeteira);
    }

    @Override
    public void onResume() {
        super.onResume();
        applyMasterVolumeToAll();
    }

    // ======= SETUP DO SOM (MIX) =======
    private void setupSound(String key, int rawRes, ImageView button, SeekBar seekBar) {

        // garante progress default
        if (seekBar != null && seekBar.getProgress() <= 0) {
            seekBar.setProgress(80);
        }

        // clique play/pause individual
        button.setOnClickListener(v -> {
            if (players.containsKey(key)) {
                stopSingle(key, button);
                return;
            }

            MediaPlayer mp = MediaPlayer.create(requireContext(), rawRes);
            mp.setLooping(true);

            players.put(key, mp);

            // aplica volume MASTER * CARD
            applyVolumeForKey(key, seekBar);

            mp.start();
            button.setImageResource(android.R.drawable.ic_media_pause);
        
            // se acabar por algum motivo, limpa estado
            mp.setOnErrorListener((m, what, extra) -> {
                stopSingle(key, button);
                return true;
            });

        // volume por card
        if (seekBar != null) {
            
            seekBar.setVisibility(View.VISIBLE);
            
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                    applyVolumeForKey(key, sb);
                }

                @Override public void onStartTrackingTouch(SeekBar sb) {}
                @Override public void onStopTrackingTouch(SeekBar sb) {}
                 });
            
              }
            
           });
        
        }
                                  
        private void stopSingle(String key, ImageView button) {
        MediaPlayer mp = players.get(key);
        if (mp != null) {
            try { mp.stop(); } catch (Exception ignored) {}
            try { mp.release(); } catch (Exception ignored) {}
        }
        players.remove(key);

        if (button != null) {
            button.setImageResource(android.R.drawable.ic_media_play);
        }

            SeekBar seekBar = getSeekBarByKey(key);
        if (seekBar != null) {
            seekBar.setVisibility(View.GONE);
          }
      }

    private SeekBar getSeekBarByKey(String key) {
    switch (key) {
        case "chuva": return seekChuva;
        case "mar": return seekMar;
        case "floresta": return seekFloresta;
        case "lareira": return seekLareira;
        case "vento": return seekVento;
        case "grilos": return seekGrilos;
        case "passaros": return seekPassaros;
        case "riacho": return seekRiacho;
        case "cafeteira": return seekCafeteira;
        default: return null;
      }
    }

    // ======= STOP GERAL (PARA TUDO) =======
    private void stopSound() {
        // Para TODOS os sons (mantém nome stopSound pra não quebrar o resto)
        stopSingle("chuva", btnPlayChuva);
        stopSingle("mar", btnPlayMar);
        stopSingle("floresta", btnPlayFloresta);
        stopSingle("lareira", btnPlayLareira);
        stopSingle("vento", btnPlayVento);
        stopSingle("grilos", btnPlayGrilos);
        stopSingle("passaros", btnPlayPassaros);
        stopSingle("riacho", btnPlayRiacho);
        stopSingle("cafeteira", btnPlayCafeteira);
    }

    // ======= TIMER (MANTIDO + ALARME) =======
    private void openTimerDialog() {

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_timer, null);

        EditText inputMinutes = dialogView.findViewById(R.id.inputMinutes);
        Button btnStartTimer = dialogView.findViewById(R.id.btnStartTimer);
        Switch switchTimerAlarm = dialogView.findViewById(R.id.switchTimerAlarm);

        // 🔥 Card clicável para abrir configurações do despertador
        View cardTimerAlarm = dialogView.findViewById(R.id.cardTimerAlarm);
        if (cardTimerAlarm != null) {
            cardTimerAlarm.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), AlarmConfigActivity.class);
                startActivity(intent);
            });
        }

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

            boolean shouldTriggerAlarm = switchTimerAlarm != null && switchTimerAlarm.isChecked();

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
                    stopSound(); // para todos
                }

            }.start();

            dialog.dismiss();
        });

        dialog.show();
    }

    // ======= FAVORITOS (MANTIDO) =======
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
