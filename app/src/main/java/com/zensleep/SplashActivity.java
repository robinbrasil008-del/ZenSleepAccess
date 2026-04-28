package com.zensleep;

import android.app.Activity; // Mudámos aqui
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

// Mudámos aqui também: "extends Activity" em vez de AppCompatActivity
public class SplashActivity extends Activity { 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 2000); 
    }
}
