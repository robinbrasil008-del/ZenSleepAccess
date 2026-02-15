package com.zensleep;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AlarmRingingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔥 GARANTE ABERTURA COM TELA BLOQUEADA
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        );

        setContentView(R.layout.activity_alarm_ringing);

        TextView txtTitle = findViewById(R.id.txtAlarmTitle);
        Button btnStop = findViewById(R.id.btnStopAlarm);

        String label = getIntent().getStringExtra("alarm_label");

        if (label != null && !label.isEmpty()) {
            txtTitle.setText("⏰ " + label);
        } else {
            txtTitle.setText("⏰ Alarme");
        }

        btnStop.setOnClickListener(v -> {
            try {
                Intent stopIntent = new Intent(this, AlarmService.class);
                stopIntent.setAction("STOP_ALARM");
                startService(stopIntent);
            } catch (Exception ignored) {}

            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 🔥 SEGURANÇA EXTRA: garante que o serviço pare
        try {
            Intent stopIntent = new Intent(this, AlarmService.class);
            stopIntent.setAction("STOP_ALARM");
            startService(stopIntent);
        } catch (Exception ignored) {}
    }
}
