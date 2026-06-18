package com.zensleep;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        // Agora o Java só precisa se preocupar com o botão de voltar
        ImageView btnBack = findViewById(R.id.btnBack);
        
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
}
