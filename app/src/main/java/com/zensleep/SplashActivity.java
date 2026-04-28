package com.zensleep;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Usa aquele desenho que você criou (imagem + ícone) como fundo
        setContentView(new android.view.View(this));
        getWindow().setBackgroundDrawableResource(R.drawable.splash_background);

        // Aguarda 2 segundos (2000ms) e abre a tela principal
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish(); // Fecha a tela de abertura para não voltar nela
        }, 2000); 
    }
}

