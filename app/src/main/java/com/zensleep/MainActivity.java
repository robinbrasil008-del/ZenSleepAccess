package com.zensleep;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = findViewById(R.id.btnTeste);

        btn.setOnClickListener(v ->
                Toast.makeText(this, "App funcionando!", Toast.LENGTH_SHORT).show()
        );
    }
}
