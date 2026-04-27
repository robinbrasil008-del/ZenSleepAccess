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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        
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
