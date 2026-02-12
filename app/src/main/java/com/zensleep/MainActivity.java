package com.zensleep;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ImageButton playChuva;
    ImageButton playMar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playChuva = findViewById(R.id.playChuva);
        playMar = findViewById(R.id.playMar);

        playChuva.setOnClickListener(v ->
                Toast.makeText(this, "Chuva tocando 🌧", Toast.LENGTH_SHORT).show()
        );

        playMar.setOnClickListener(v ->
                Toast.makeText(this, "Mar tocando 🌊", Toast.LENGTH_SHORT).show()
        );
    }
}
