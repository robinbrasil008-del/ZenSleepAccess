private void scheduleAlarm(int hour, int minute) {

    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, minute);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    if (calendar.before(Calendar.getInstance())) {
        calendar.add(Calendar.DAY_OF_MONTH, 1);
    }

    // 🔥 ID único baseado no tempo
    int alarmId = (int) System.currentTimeMillis();

    Intent intent = new Intent(this, AlarmReceiver.class);
    intent.putExtra("alarm_id", alarmId);
    intent.putExtra("alarm_label", "Alarme");

    PendingIntent pendingIntent = PendingIntent.getBroadcast(
            this,
            alarmId, // 🔥 AGORA É ÚNICO
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
    );

    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

    if (alarmManager != null) {
        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );
    }

    Toast.makeText(this, "Alarme agendado!", Toast.LENGTH_SHORT).show();
}
