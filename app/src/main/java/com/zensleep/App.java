package com.zensleep; // Verifique se este é o nome exato da sua pasta/pacote

import android.app.Application;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // 1. Lê a preferência antes de QUALQUER tela existir
        SharedPreferences prefs = getSharedPreferences("zen_settings", MODE_PRIVATE);
        
        // 2. O 'true' garante que na primeira instalação, o padrão é ESCURO
        boolean isDarkMode = prefs.getBoolean("dark_mode", true);
        
        // 3. Força o tema no aplicativo inteiro antes dele desenhar a primeira tela
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}

