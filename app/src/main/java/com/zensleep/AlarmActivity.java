package com.zensleep;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
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
                } else {
                    cancelAlarm(item);
                }
            }

            @Override
            public void onDelete(AlarmItem item) {

                cancelAlarm(item);
                alarms.remove(item);
                AlarmStorage.save(AlarmActivity.this, alarms);

                adapter.notifyDataSetChanged();
                updateEmpty();
            }
        });

        recycler.setAdapter(adapter);

        btnAddAlarm.setOnClickListener(v -> checkExactAlarmPermission());

        updateEmpty();
    }

    private void updateEmpty() {

        if (alarms == null || alarms.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
        }
    }

    private void checkExactAlarmPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            if (!alarmManager.canScheduleExactAlarms()) {

                Toast.makeText(this,
                        "Permita alarmes exatos nas configurações",
                        Toast.LENGTH_LONG).show();

                Intent intent =
                        new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                return;
            }
        }

        openTimePicker();
    }

    private void openTimePicker() {

        Calendar now = Calendar.getInstance();

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) ->
                        openLabelDialog(hourOfDay, minute),
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );

        dialog.show();
    }

    private void openLabelDialog(int hour, int minute) {

        EditText input = new EditText(this);
        input.setHint("Nome do alarme");

        new AlertDialog.Builder(this)
                .setTitle("Nome do alarme")
                .setView(input)
                .setPositiveButton("Salvar", (d, which) -> {

                    String label = input.getText().toString().trim();

                    int id = AlarmStorage.nextId(alarms);

                    AlarmItem item =
                            new AlarmItem(id, hour, minute, label, true);

                    alarms.add(item);
                    AlarmStorage.save(this, alarms);

                    adapter.notifyDataSetChanged();
                    updateEmpty();

                    scheduleAlarm(item);
                })
                .setNegativeButton("Cancelar", null)
                .show();
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

        // 🔥 ESSENCIAL: deixa cada alarme único
        intent.setAction("ZEN_ALARM_" + item.id);

        intent.putExtra("alarm_id", item.id);
        intent.putExtra("alarm_label", item.label);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                item.id,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );

        Toast.makeText(this, "Alarme agendado ✅", Toast.LENGTH_SHORT).show();
    }

    private void cancelAlarm(AlarmItem item) {

        Intent intent = new Intent(this, AlarmReceiver.class);

        // 🔥 mesma action usada ao criar
        intent.setAction("ZEN_ALARM_" + item.id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                item.id,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.cancel(pendingIntent);
    }
}
