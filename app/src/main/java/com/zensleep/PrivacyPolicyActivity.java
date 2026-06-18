package com.zensleep;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

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
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        // Agora o Java só precisa se preocupar com o botão de voltar
        ImageView btnBack = findViewById(R.id.btnBack);
        
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
}
