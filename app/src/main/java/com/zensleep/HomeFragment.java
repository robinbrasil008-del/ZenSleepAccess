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
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import android.app.Activity;
import android.widget.FrameLayout;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.ViewGroup;
import android.animation.ArgbEvaluator;
import com.bumptech.glide.Glide;
import com.zensleep.TimerTextAnimator;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.HashMap;

public class HomeFragment extends Fragment {

    private RewardedAd mRewardedAd;
// ID de TESTE do Google para Anúncios Premiados. 
// Troque pelo seu ID real apenas quando for publicar o app!
    private final String REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";

    private AlertDialog loadingDialog;

    private InterstitialAd mInterstitialAd;
    private boolean adAlreadyShown = false;

    private boolean florestaUnlocked = false;

    private String cardSelecionado = "";

    private AdView adView;

    // ======= PLAYER (NOVO MIX / MULTI-SOM) =======
    private final HashMap<String, MediaPlayer> players = new HashMap<>();

    private CountDownTimer countDownTimer;

    private TextView txtTimer;

    private NeonBorderView neonBorderView;
    private Button btnDefinirTimer;
    private ButtonGlowAnimator buttonAnimator;
    private AnimatedTimerCardLayout timerCard;
    
    private CardGlowLayout cardChuva, cardFloresta, cardLareira, cardVento, cardGrilos, cardPassaros, cardRiacho, cardCafeteira;

    private ImageView timerIcon;

    private TimerTextAnimator timerAnimator = new TimerTextAnimator();

    // PLAY BUTTONS
    private ImageView btnPlayChuva;
    private ImageView btnPlayFloresta, btnPlayLareira, btnPlayVento,
            btnPlayGrilos, btnPlayPassaros, btnPlayRiacho, btnPlayCafeteira;

    // SEEKBARS (VOLUME POR CARD)
    private SeekBar seekChuva, seekFloresta, seekLareira,
            seekVento, seekGrilos, seekPassaros, seekRiacho, seekCafeteira;

    // FAVORITOS
    private ImageView starChuva;
    private ImageView starFloresta, starLareira, starVento,
            starGrilos, starPassaros, starRiacho, starCafeteira;

    private Button btnTimer;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        adView = view.findViewById(R.id.adView);

        loadInterstitialAd();

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        // ======= PLAY BUTTONS =======
        btnPlayChuva = view.findViewById(R.id.btnPlayChuva);

        btnPlayFloresta = view.findViewById(R.id.btnPlayFloresta);
        btnPlayLareira = view.findViewById(R.id.btnPlayLareira);
        btnPlayVento = view.findViewById(R.id.btnPlayVento);
        btnPlayGrilos = view.findViewById(R.id.btnPlayGrilos);
        btnPlayPassaros = view.findViewById(R.id.btnPlayPassaros);
        btnPlayRiacho = view.findViewById(R.id.btnPlayRiacho);
        btnPlayCafeteira = view.findViewById(R.id.btnPlayCafeteira);

        cardChuva = view.findViewById(R.id.cardChuva);
        cardFloresta = view.findViewById(R.id.cardFloresta);
        cardLareira = view.findViewById(R.id.cardLareira);
        cardVento = view.findViewById(R.id.cardVento);
        cardGrilos = view.findViewById(R.id.cardGrilos);
        cardPassaros = view.findViewById(R.id.cardPassaros);
        cardRiacho = view.findViewById(R.id.cardRiacho);
        cardCafeteira = view.findViewById(R.id.cardCafeteira);

        // ======= SEEKBARS =======
        seekChuva = view.findViewById(R.id.seekChuva);

        seekFloresta = view.findViewById(R.id.seekFloresta);
        seekLareira = view.findViewById(R.id.seekLareira);
        seekVento = view.findViewById(R.id.seekVento);
        seekGrilos = view.findViewById(R.id.seekGrilos);
        seekPassaros = view.findViewById(R.id.seekPassaros);
        seekRiacho = view.findViewById(R.id.seekRiacho);
        seekCafeteira = view.findViewById(R.id.seekCafeteira);

        // ======= FAVORITOS =======
        starChuva = view.findViewById(R.id.starChuva);

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
        btnTimer.setOnClickListener(v -> {

     if (neonBorderView.isSelected()) {
        // 🔴 SE ESTÁ RODANDO → PARA TUDO

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        AnimatedTimerCardLayout timerCard = requireView().findViewById(R.id.timerCard);
        timerCard.stopBorderAnimation();
        stopSound();
        stopHourglassAnimation();
        timerAnimator.stop(txtTimer);

        neonBorderView.setSelected(false);

        txtTimer.setText("00:00");

        } else {
        // 🟢 SE NÃO ESTÁ RODANDO → ABRE O DIALOG

        openTimerDialog(); // 👈 ESSE MÉTODO QUE VOCÊ JÁ TEM
        }

        });
        timerIcon = view.findViewById(R.id.timerIcon);
        timerIcon.setImageResource(R.drawable.hourglass_static);
        neonBorderView = view.findViewById(R.id.neonBorder);
        
        // ======= SETUP MIX (MULTI-SOM) =======
        setupSound("chuva", R.raw.chuva, btnPlayChuva, seekChuva);

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
        
    }

    private CardGlowLayout getCardByKey(String key) {
    switch (key) {
        case "chuva": return cardChuva;
        case "floresta": return cardFloresta;
        case "lareira": return cardLareira;
        case "vento": return cardVento;
        case "grilos": return cardGrilos;
        case "passaros": return cardPassaros;
        case "riacho": return cardRiacho;
        case "cafeteira": return cardCafeteira;
        default: return null;
    }
    }

    private void startHourglassAnimation() {
    if (timerIcon == null) return;

    Glide.with(this)
            .asGif()
            .load(R.drawable.hourglass_anim)
            .into(timerIcon);
    }

    private void stopHourglassAnimation() {
    if (timerIcon == null) return;

    Glide.with(this).clear(timerIcon);
    timerIcon.setImageResource(R.drawable.hourglass_static);
    }
    
    private void loadInterstitialAd() {
    AdRequest adRequest = new AdRequest.Builder().build();

    InterstitialAd.load(
            requireContext(),
            "ca-app-pub-8296610548842772/5975143413",
            adRequest,
            new InterstitialAdLoadCallback() {

                @Override
                public void onAdLoaded(InterstitialAd interstitialAd) {
                    mInterstitialAd = interstitialAd;

                    // NÃO MOSTRA AUTOMATICAMENTE AQUI ❌

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
                            }
                    );

                    showInterstitialIfReady();
                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    mInterstitialAd = null;
                }
            }
    );
    }

    public void showInterstitialIfReady() {

    if (mInterstitialAd != null && !adAlreadyShown && isAdded()) {

        Activity activity = getActivity();

        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {

            mInterstitialAd.show(activity);
            mInterstitialAd = null;

            adAlreadyShown = true; // 🚫 trava pra não repetir
        }
    }
    }

    private void loadRewardedAd() {
    AdRequest adRequest = new AdRequest.Builder().build();
    RewardedAd.load(requireContext(), REWARDED_AD_UNIT_ID,
            adRequest, new RewardedAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    mRewardedAd = null;
                }

                @Override
                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                    mRewardedAd = rewardedAd;
                }
            });
    }

    private boolean isCardUnlocked(String key) {
    // A chuva é sempre livre
    if (key.equals("chuva")) return true; 
    
    // TEMPORÁRIO PARA TESTE: Deixa os outros livres, bloqueia apenas a "floresta"
    if (!key.equals("floresta")) return true; 

    // Verifica no SharedPreferences se a floresta já foi desbloqueada
    SharedPreferences prefs = requireContext().getSharedPreferences("zen_unlocks", Context.MODE_PRIVATE);
    return prefs.getBoolean(key + "_unlocked", false);
}

private void unlockCard(String key) {
    SharedPreferences prefs = requireContext().getSharedPreferences("zen_unlocks", Context.MODE_PRIVATE);
    prefs.edit().putBoolean(key + "_unlocked", true).apply();
}

    // Ajustado para aceitar (String key, ImageView button) como pede o seu erro no log
private void showRewardedAdAndUnlock(String key, ImageView button) {
    // 1. Se o anúncio JÁ ESTÁ pronto no fundo, mostra direto
    if (mRewardedAd != null && isAdded() && getActivity() != null) {
        displayAd(key, button);
    } else {
        // 2. Se NÃO está pronto, mostra o seu GIF "aguarde.gif"
        showLoadingDialog();
        
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(requireContext(), REWARDED_AD_UNIT_ID,
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mRewardedAd = null;
                        hideLoadingDialog(); // Esconde o GIF se falhar
                        Toast.makeText(requireContext(), "Erro ao carregar vídeo. Tente novamente.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        hideLoadingDialog(); // Esconde o GIF pois carregou!
                        displayAd(key, button); // Mostra o vídeo
                    }
                });
    }
}

// Método auxiliar para exibir o vídeo
private void displayAd(String key, ImageView button) {
    mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
        @Override
        public void onAdDismissedFullScreenContent() {
            mRewardedAd = null;
            loadRewardedAd(); // Recarrega o próximo em background
        }
        @Override
        public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
            mRewardedAd = null;
        }
    });

    mRewardedAd.show(getActivity(), rewardItem -> {
        // O usuário ganhou a recompensa!
        unlockCard(key);
        Toast.makeText(requireContext(), "Som desbloqueado!", Toast.LENGTH_SHORT).show();
        
        // Opcional: Chama o clique do botão automaticamente agora que liberou
        if (button != null) {
            button.performClick();
        }
    });
}

    private void showLoadingDialog() {
    if (loadingDialog == null) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_loading, null);
        ImageView gifLoading = view.findViewById(R.id.gifLoading);
        
        // Usando o Glide (que você já tem) para animar o seu GIF!
        Glide.with(this).asGif().load(R.drawable.aguarde).into(gifLoading);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view);
        builder.setCancelable(false); // Impede que o usuário feche clicando fora
        
        loadingDialog = builder.create();
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
    
    if (!loadingDialog.isShowing()) {
        loadingDialog.show();
    }
}

private void hideLoadingDialog() {
    if (loadingDialog != null && loadingDialog.isShowing()) {
        loadingDialog.dismiss();
    }
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
        adAlreadyShown = false; // libera mostrar novamente
        showInterstitialIfReady();
    }

    // ======= SETUP DO SOM (MIX) =======
    private void setupSound(String key, int rawRes, ImageView button, SeekBar seekBar) {

        // garante progress default
        if (seekBar != null && seekBar.getProgress() <= 0) {
            seekBar.setProgress(80);
        }

        // clique play/pause individual
        button.setOnClickListener(v -> {

            if (!isCardUnlocked(key)) {
        showRewardedAdAndUnlock(key, button);
            }

            else {
            
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
            button.setImageResource(R.drawable.ic_media_pause);

            button.animate()
           .translationY(-60f)
           .scaleX(1.05f)
           .scaleY(1.05f)
           .setDuration(250)
           .setInterpolator(new android.view.animation.DecelerateInterpolator())
           .start();

            CardGlowLayout card = getCardByKey(key);
            if (card != null) {
            card.startGlow();
           }

            View parent = (View) button.getParent();
            ImageView eq = parent.findViewById(R.id.equalizer);

            if (eq != null) {
            eq.setVisibility(View.VISIBLE);

             Glide.with(requireContext())
             .asGif()
             .load(R.drawable.equalizer) // seu gif
             .into(eq);
        }
    
        
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

            }
            
              }):

           }
                                  
        private void stopSingle(String key, ImageView button) {
        MediaPlayer mp = players.get(key);
        if (mp != null) {
            try { mp.stop(); } catch (Exception ignored) {}
            try { mp.release(); } catch (Exception ignored) {}
        }
        players.remove(key);

        if (button != null) {
            button.setImageResource(R.drawable.ic_media_play);
        }

            button.animate()
           .translationY(0)
           .scaleX(1f)
           .scaleY(1f)
           .setDuration(200)
           .setInterpolator(new android.view.animation.DecelerateInterpolator())
           .start();

            CardGlowLayout card = getCardByKey(key);
            if (card != null) {
            card.stopGlow();
           }

            View parent = (View) button.getParent();
            ImageView eq = parent.findViewById(R.id.equalizer);

            if (eq != null) {
            eq.setVisibility(View.GONE);
            }

            SeekBar seekBar = getSeekBarByKey(key);
        if (seekBar != null) {
            seekBar.setVisibility(View.GONE);
          }
      }

    private SeekBar getSeekBarByKey(String key) {
    switch (key) {
        case "chuva": return seekChuva;
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

        // 🔥 EFEITO AO FOCAR
inputMinutes.setOnFocusChangeListener((v, hasFocus) -> {
    if (hasFocus) {
        v.animate().scaleX(1.03f).scaleY(1.03f).setDuration(120);
    } else {
        v.animate().scaleX(1f).scaleY(1f).setDuration(120);
    }
});

// 🔥 EFEITO AO DIGITAR
inputMinutes.addTextChangedListener(new android.text.TextWatcher() {
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(android.text.Editable s) {
        inputMinutes.animate()
            .alpha(0.7f)
            .setDuration(50)
            .withEndAction(() ->
                inputMinutes.animate().alpha(1f).setDuration(50)
            );
    }
});

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

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

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

            // 🔥 LIMITE DE 60 MINUTOS
            if (minutes > 60) {
                inputMinutes.setError("Máximo permitido é 60 minutos");
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

            AnimatedTimerCardLayout timerCard = requireView().findViewById(R.id.timerCard);
            timerCard.startBorderAnimation();

            startHourglassAnimation();

            timerAnimator.start(txtTimer);

            neonBorderView.setSelected(true);

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

                     AnimatedTimerCardLayout timerCard = requireView().findViewById(R.id.timerCard);
                     timerCard.stopBorderAnimation();

                    stopHourglassAnimation();

                    timerAnimator.stop(txtTimer);

                    neonBorderView.setSelected(false);
                    
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
            starChuva.setImageResource(R.drawable.btn_star_big_on);
            starChuva.setColorFilter(0xFFFF1744);
        } else {
            starChuva.setImageResource(R.drawable.btn_star_big_off);
            starChuva.setColorFilter(0xFFFFFFFF);
        }

        if (starFloresta != null) {
            if (florestaFav) {
                starFloresta.setImageResource(R.drawable.btn_star_big_on);
                starFloresta.setColorFilter(0xFFFF1744);
            } else {
                starFloresta.setImageResource(R.drawable.btn_star_big_off);
                starFloresta.setColorFilter(0xFFFFFFFF);
            }
        }

        if (starLareira != null) {
            if (lareiraFav) {
                starLareira.setImageResource(R.drawable.btn_star_big_on);
                starLareira.setColorFilter(0xFFFF1744);
            } else {
                starLareira.setImageResource(R.drawable.btn_star_big_off);
                starLareira.setColorFilter(0xFFFFFFFF);
            }
        }

        if (starVento != null) {
            if (ventoFav) {
                starVento.setImageResource(R.drawable.btn_star_big_on);
                starVento.setColorFilter(0xFFFF1744);
            } else {
                starVento.setImageResource(R.drawable.btn_star_big_off);
                starVento.setColorFilter(0xFFFFFFFF);
            }
        }

        if (starGrilos != null) {
            if (grilosFav) {
                starGrilos.setImageResource(R.drawable.btn_star_big_on);
                starGrilos.setColorFilter(0xFFFF1744);
            } else {
                starGrilos.setImageResource(R.drawable.btn_star_big_off);
                starGrilos.setColorFilter(0xFFFFFFFF);
            }
        }

        if (starPassaros != null) {
            if (passarosFav) {
                starPassaros.setImageResource(R.drawable.btn_star_big_on);
                starPassaros.setColorFilter(0xFFFF1744);
            } else {
                starPassaros.setImageResource(R.drawable.btn_star_big_off);
                starPassaros.setColorFilter(0xFFFFFFFF);
            }
        }

        if (starRiacho != null) {
            if (riachoFav) {
                starRiacho.setImageResource(R.drawable.btn_star_big_on);
                starRiacho.setColorFilter(0xFFFF1744);
            } else {
                starRiacho.setImageResource(R.drawable.btn_star_big_off);
                starRiacho.setColorFilter(0xFFFFFFFF);
            }
        }

        if (starCafeteira != null) {
            if (cafeteiraFav) {
                starCafeteira.setImageResource(R.drawable.btn_star_big_on);
                starCafeteira.setColorFilter(0xFFFF1744);
            } else {
                starCafeteira.setImageResource(R.drawable.btn_star_big_off);
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

        if (buttonAnimator != null) {
        buttonAnimator.stop();
        }
    }
}
                        
