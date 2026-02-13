package com.zensleep;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    TextView navHome, navFav, navSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 🔥 APLICA TEMA ANTES DO super.onCreate
        SharedPreferences prefs = getSharedPreferences("zen_settings", MODE_PRIVATE);
        boolean darkEnabled = prefs.getBoolean("dark_mode", true);

        AppCompatDelegate.setDefaultNightMode(
                darkEnabled
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navHome = findViewById(R.id.navHome);
        navFav = findViewById(R.id.navFav);
        navSettings = findViewById(R.id.navSettings);

        // 🔥 EVITA RECARREGAR FRAGMENTO AO RECRIAR ACTIVITY
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

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void selectMenu(TextView selected) {

        navHome.setTextColor(0xFF94A3B8);
        navFav.setTextColor(0xFF94A3B8);
        navSettings.setTextColor(0xFF94A3B8);

        selected.setTextColor(getColor(android.R.color.white));
    }
}
