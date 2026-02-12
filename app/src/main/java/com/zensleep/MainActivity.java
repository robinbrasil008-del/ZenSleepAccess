package com.zensleep;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    TextView navHome, navFav, navSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navHome = findViewById(R.id.navHome);
        navFav = findViewById(R.id.navFav);
        navSettings = findViewById(R.id.navSettings);

        // Carrega Home por padrão
        loadFragment(new HomeFragment());
        selectMenu(navHome);

        navHome.setOnClickListener(v -> {
            loadFragment(new HomeFragment());
            selectMenu(navHome);
        });

        navFav.setOnClickListener(v -> {
            loadFragment(new FavoritesFragment()); // 🔥 CORRIGIDO AQUI
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
