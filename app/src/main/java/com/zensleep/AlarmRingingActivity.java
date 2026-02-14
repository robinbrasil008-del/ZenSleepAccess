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

        // 🔥 Mostrar sobre tela bloqueada
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            );
        }

        setContentView(R.layout.activity_alarm_ringing);

        TextView txtTitle = findViewById(R.id.txtAlarmTitle);
        Button btnStop = findViewById(R.id.btnStopAlarm);

        String label = getIntent().getStringExtra("alarm_label");
        if (label != null && !label.isEmpty()) {
            txtTitle.setText("⏰ " + label);
        }

        // 🔥 INICIA O SERVICE QUE TOCA O ALARME
        Intent serviceIntent = new Intent(this, AlarmService.class);
        startForegroundService(serviceIntent);

        btnStop.setOnClickListener(v -> {
            stopService(new Intent(this, AlarmService.class));
            finish();
        });
    }
}
