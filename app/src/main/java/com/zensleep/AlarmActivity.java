package com.zensleep;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

public class AlarmActivity extends AppCompatActivity {

    private static final int REQUEST_SOUND = 500;

    private AlarmManager alarmManager;
    private RecyclerView recycler;
    private Button btnAddAlarm;

    private List<AlarmItem> alarms;
    private AlarmAdapter adapter;

    private Uri selectedSoundUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        recycler = findViewById(R.id.recyclerAlarms);
        btnAddAlarm = findViewById(R.id.btnAddAlarm);

        alarms = AlarmStorage.load(this);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AlarmAdapter(alarms, new AlarmAdapter.Listener() {

            @Override
            public void onToggle(AlarmItem item, boolean enabled) {
                item.enabled = enabled;
                AlarmStorage.save(AlarmActivity.this, alarms);

                if (enabled) scheduleAlarm(item);
                else cancelAlarm(item);
            }

            @Override
            public void onDelete(AlarmItem item) {
                cancelAlarm(item);
                alarms.remove(item);
                AlarmStorage.save(AlarmActivity.this, alarms);
                adapter.notifyDataSetChanged();
            }
        });

        recycler.setAdapter(adapter);

        btnAddAlarm.setOnClickListener(v -> checkExactAlarmPermission());
    }

    private void checkExactAlarmPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
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
                        openCreateDialog(hourOfDay, minute),
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );

        dialog.show();
    }

    private void openCreateDialog(int hour, int minute) {

        selectedSoundUri = null;

        View customView =
                getLayoutInflater().inflate(R.layout.dialog_alarm_create, null);

        EditText inputLabel =
                customView.findViewById(R.id.inputLabel);

        Button btnSelectSound =
                customView.findViewById(R.id.btnSelectSound);

        CheckBox daySun = customView.findViewById(R.id.daySun);
        CheckBox dayMon = customView.findViewById(R.id.dayMon);
        CheckBox dayTue = customView.findViewById(R.id.dayTue);
        CheckBox dayWed = customView.findViewById(R.id.dayWed);
        CheckBox dayThu = customView.findViewById(R.id.dayThu);
        CheckBox dayFri = customView.findViewById(R.id.dayFri);
        CheckBox daySat = customView.findViewById(R.id.daySat);

        btnSelectSound.setOnClickListener(v -> {

            Intent intent =
                    new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);

            intent.putExtra(
                    RingtoneManager.EXTRA_RINGTONE_TYPE,
                    RingtoneManager.TYPE_ALARM
            );

            startActivityForResult(intent, REQUEST_SOUND);
        });

        new AlertDialog.Builder(this)
                .setTitle("Novo Alarme")
                .setView(customView)
                .setPositiveButton("Salvar", (d, which) -> {

                    String label =
                            inputLabel.getText().toString().trim();

                    boolean[] selectedDays = new boolean[]{
                            daySun.isChecked(),
                            dayMon.isChecked(),
                            dayTue.isChecked(),
                            dayWed.isChecked(),
                            dayThu.isChecked(),
                            dayFri.isChecked(),
                            daySat.isChecked()
                    };

                    int id = AlarmStorage.nextId(alarms);

                    AlarmItem item =
                            new AlarmItem(
                                    id,
                                    hour,
                                    minute,
                                    label,
                                    true,
                                    selectedDays,
                                    selectedSoundUri == null ?
                                            null :
                                            selectedSoundUri.toString()
                            );

                    alarms.add(item);
                    AlarmStorage.save(this, alarms);
                    adapter.notifyDataSetChanged();

                    scheduleAlarm(item);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SOUND &&
                resultCode == RESULT_OK &&
                data != null) {

            selectedSoundUri =
                    data.getParcelableExtra(
                            RingtoneManager.EXTRA_RINGTONE_PICKED_URI
                    );
        }
    }

    private void scheduleAlarm(AlarmItem item) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, item.hour);
        calendar.set(Calendar.MINUTE, item.minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (item.isRepeating()) {

            int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            int nextDayOffset = -1;

            for (int i = 0; i < 7; i++) {

                int checkDay = (today - 1 + i) % 7;

                if (item.days[checkDay]) {
                    nextDayOffset = i;
                    break;
                }
            }

            if (nextDayOffset >= 0) {
                calendar.add(Calendar.DAY_OF_YEAR, nextDayOffset);
            }

        } else {

            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction("ZEN_ALARM_" + item.id);
        intent.putExtra("alarm_id", item.id);
        intent.putExtra("alarm_label", item.label);
        intent.putExtra("alarm_sound", item.soundUri);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        this,
                        item.id,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE
                );

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );

        Toast.makeText(this,
                "Alarme agendado ✅",
                Toast.LENGTH_SHORT).show();
    }

    private void cancelAlarm(AlarmItem item) {

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction("ZEN_ALARM_" + item.id);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        this,
                        item.id,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE
                );

        alarmManager.cancel(pendingIntent);
    }
}
