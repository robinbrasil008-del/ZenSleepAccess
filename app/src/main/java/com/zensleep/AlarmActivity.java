package com.zensleep;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

public class AlarmActivity extends AppCompatActivity {

    private AlarmManager alarmManager;

    private RecyclerView recycler;
    private TextView emptyText;
    private Button btnAddAlarm;

    private List<AlarmItem> alarms;
    private AlarmAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        recycler = findViewById(R.id.recyclerAlarms);
        emptyText = findViewById(R.id.emptyAlarms);
        btnAddAlarm = findViewById(R.id.btnAddAlarm);

        alarms = AlarmStorage.load(this);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AlarmAdapter(alarms, new AlarmAdapter.Listener() {
            @Override
            public void onToggle(AlarmItem item, boolean enabled) {
                item.enabled = enabled;
                AlarmStorage.save(AlarmActivity.this, alarms);

                if (enabled) {
                    scheduleAlarm(item);
                    Toast.makeText(AlarmActivity.this, "Alarme ativado ✅", Toast.LENGTH_SHORT).show();
                } else {
                    cancelAlarm(item);
                    Toast.makeText(AlarmActivity.this, "Alarme desativado ❌", Toast.LENGTH_SHORT).show();
                }
                updateEmpty();
            }

            @Override
            public void onDelete(AlarmItem item) {
                cancelAlarm(item);
                alarms.remove(item);
                AlarmStorage.save(AlarmActivity.this, alarms);
                adapter.notifyDataSetChanged();
                updateEmpty();
                Toast.makeText(AlarmActivity.this, "Alarme excluído 🗑", Toast.LENGTH_SHORT).show();
            }
        });
        recycler.setAdapter(adapter);

        btnAddAlarm.setOnClickListener(v -> openTimePicker());

        updateEmpty();
    }

    private void updateEmpty() {
        if (alarms == null || alarms.isEmpty()) {
            emptyText.setVisibility(TextView.VISIBLE);
            recycler.setVisibility(RecyclerView.GONE);
        } else {
            emptyText.setVisibility(TextView.GONE);
            recycler.setVisibility(RecyclerView.VISIBLE);
        }
    }

    private void openTimePicker() {
        Calendar now = Calendar.getInstance();

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> openLabelDialog(hourOfDay, minute),
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );

        dialog.show();
    }

    private void openLabelDialog(int hour, int minute) {
        EditText input = new EditText(this);
        input.setHint("Nome do alarme (ex: Acordar)");

        new AlertDialog.Builder(this)
                .setTitle("Nome do alarme")
                .setView(input)
                .setPositiveButton("Salvar", (d, which) -> {
                    String label = input.getText().toString().trim();
                    addAlarm(hour, minute, label);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void addAlarm(int hour, int minute, String label) {
        int id = AlarmStorage.nextId(alarms);

        AlarmItem item = new AlarmItem(id, hour, minute, label, true);
        alarms.add(item);

        AlarmStorage.save(this, alarms);
        adapter.notifyDataSetChanged();
        updateEmpty();

        scheduleAlarm(item);

        Toast.makeText(this, "Alarme adicionado ✅", Toast.LENGTH_SHORT).show();
    }

    private void scheduleAlarm(AlarmItem item) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, item.hour);
        calendar.set(Calendar.MINUTE, item.minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("alarm_id", item.id);
        intent.putExtra("alarm_label", item.label == null ? "" : item.label);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                item.id, // ✅ requestCode único
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        try {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        } catch (Exception e) {
            // fallback
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }
    }

    private void cancelAlarm(AlarmItem item) {
        Intent intent = new Intent(this, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                item.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.cancel(pendingIntent);
    }
}
