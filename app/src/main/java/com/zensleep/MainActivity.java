package com.zensleep;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Handler;
import android.os.Looper;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.ads.MobileAds;
import com.ironsource.mediationsdk.IronSource;
import androidx.annotation.NonNull;
import android.widget.FrameLayout;

import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    LinearLayout navHome, navFav, navSettings;

    private ActivityResultLauncher<String> notificationPermissionLauncher;

    private static final String APP_KEY = "257178685";

    private AppUpdateManager appUpdateManager;
    private static final int MY_REQUEST_CODE = 100;

    private static boolean splashJaMostrada = false; // Essa variável fica guardada na memória

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 1. O JAVA MUDA PARA O TEMA PRINCIPAL SILENCIOSAMENTE
        setTheme(R.style.Theme_ZenSleep);
        
        // 2. LÊ O SEU BOTÃO
                // 1. LÊ O SEU BOTÃO
        android.content.SharedPreferences prefs = getSharedPreferences("zen_settings", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("dark_mode", true);

        // 2. APLICA O TEMA NA MARRA (Adeus decisão do Android)
        if (isDark) {
            setTheme(R.style.Theme_ZenSleep); // Puxa o tema escuro (sem o .Escuro no nome)
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            setTheme(R.style.Theme_ZenSleep_Claro); // Puxa o tema claro fixo
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        }
        
        // 3. CONSTRÓI A TELA (A ordem inquebrável)
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);

                if (!splashJaMostrada) { 
            
            final android.widget.RelativeLayout splashCapa = new android.widget.RelativeLayout(this);
            splashCapa.setBackgroundResource(R.drawable.fundo_zen); 
            
            // 1. Criamos um "pacote" invisível para empilhar o ícone e o texto no centro
            android.widget.LinearLayout pacoteCentral = new android.widget.LinearLayout(this);
            pacoteCentral.setOrientation(android.widget.LinearLayout.VERTICAL);
            pacoteCentral.setGravity(android.view.Gravity.CENTER);
            
            // 2. O ÍCONE (Aumentei de 350 para 450 para ficar com mais destaque!)
            android.widget.ImageView icone = new android.widget.ImageView(this);
            icone.setImageResource(R.mipmap.ic_launcher);
            android.widget.LinearLayout.LayoutParams parametrosIcone = new android.widget.LinearLayout.LayoutParams(450, 450);
            pacoteCentral.addView(icone, parametrosIcone);

            // 3. O TEXTO "ZenSleep"
            android.widget.TextView nomeApp = new android.widget.TextView(this);
            nomeApp.setText("ZenSleep");
            nomeApp.setTextColor(android.graphics.Color.WHITE); // Branco para ler bem por cima da imagem
            nomeApp.setTextSize(34f); // Tamanho da letra
            nomeApp.setTypeface(null, android.graphics.Typeface.BOLD); // Letra em negrito
            
            android.widget.LinearLayout.LayoutParams parametrosTexto = new android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            parametrosTexto.topMargin = 30; // Espaço de respiro entre o ícone e o texto
            pacoteCentral.addView(nomeApp, parametrosTexto);

            // 4. Coloca o pacote inteiro bem no centro da Capa
            android.widget.RelativeLayout.LayoutParams regraCentro = new android.widget.RelativeLayout.LayoutParams(
                    android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
            regraCentro.addRule(android.widget.RelativeLayout.CENTER_IN_PARENT);
            splashCapa.addView(pacoteCentral, regraCentro);

            // Adiciona a capa na tela
            addContentView(splashCapa, new android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT));

            // Tira a capa suavemente após 2 segundos
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                splashCapa.animate().alpha(0f).setDuration(600).withEndAction(() -> {
                    splashCapa.setVisibility(android.view.View.GONE);
                });
            }, 5000);

            splashJaMostrada = true; 
        }

        // ====================================================

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
        // =======================================================================
        
        MobileAds.initialize(this);

        IronSource.init(this, "SEU_APP_KEY");
        
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

    IronSource.loadInterstitial();

}, 2000);

        // ==========================
        // 🔔 PERMISSÃO NOTIFICAÇÃO
        // ==========================
        notificationPermissionLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        isGranted -> {}
                );

        checkNotificationPermission();

        // ==========================
        // 📱 MENU
        // ==========================
        navHome = findViewById(R.id.navHome);
        navFav = findViewById(R.id.navFav);
        navSettings = findViewById(R.id.navSettings);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            selectMenu(navHome);
        }

        navHome.setOnClickListener(v -> {
            loadFragment(new HomeFragment());
            selectMenu(navHome);
        });

        navFav.setOnClickListener(v -> {
            loadFragment(new FavoritesFragment());
            selectMenu(navFav);
        });

        navSettings.setOnClickListener(v -> {
            loadFragment(new SettingsFragment());
            selectMenu(navSettings);
        });

            checkForAppUpdate();

    }

    private void checkNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                notificationPermissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                );

            }
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void selectMenu(LinearLayout selected) {

        resetMenuColors(navHome);
        resetMenuColors(navFav);
        resetMenuColors(navSettings);

        highlightMenu(selected);
    }

    private void resetMenuColors(LinearLayout menu) {
        TextView icon = (TextView) menu.getChildAt(0);
        TextView text = (TextView) menu.getChildAt(1);

        icon.setTextColor(0xFF94A3B8);
        text.setTextColor(0xFF94A3B8);
    }

    private void highlightMenu(LinearLayout menu) {
        TextView icon = (TextView) menu.getChildAt(0);
        TextView text = (TextView) menu.getChildAt(1);

        icon.setTextColor(0xFFFFFFFF);
        text.setTextColor(0xFFFFFFFF);
    }

        private void checkForAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                  && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        MY_REQUEST_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Este método garante que se o usuário minimizar o app durante a atualização,
    // o bloqueio continue quando ele voltar.
    @Override
    protected void onResume() {
        super.onResume();
        if (appUpdateManager != null) {
            appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this,
                            MY_REQUEST_CODE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
