package com.zensleep;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    LinearLayout navHome, navFav, navSettings;

    private ActivityResultLauncher<String> notificationPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // ==========================
        // 🌙 APLICA TEMA
        // ==========================
        SharedPreferences prefs = getSharedPreferences("zen_settings", MODE_PRIVATE);
        boolean darkEnabled = prefs.getBoolean("dark_mode", true);

        AppCompatDelegate.setDefaultNightMode(
                darkEnabled
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        MobileAds.initialize(this);
        setContentView(R.layout.activity_main);

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
}
