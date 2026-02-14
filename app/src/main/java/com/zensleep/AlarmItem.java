package com.zensleep;

import java.util.Arrays;

public class AlarmItem {

    public int id;
    public int hour;
    public int minute;
    public String label;
    public boolean enabled;

    // 🔥 7 posições: 0=Dom, 1=Seg, 2=Ter, 3=Qua, 4=Qui, 5=Sex, 6=Sab
    public boolean[] days;

    // 🔥 URI do som escolhido
    public String soundUri;

    public AlarmItem(int id,
                     int hour,
                     int minute,
                     String label,
                     boolean enabled,
                     boolean[] days,
                     String soundUri) {

        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.label = label;
        this.enabled = enabled;
        this.days = days != null ? days : new boolean[7];
        this.soundUri = soundUri;
    }

    public String timeText() {
        return String.format("%02d:%02d", hour, minute);
    }

    // 🔥 Texto bonito dos dias
    public String daysText() {

        String[] names = {"Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab"};

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < days.length; i++) {
            if (days[i]) {
                if (builder.length() > 0) builder.append(", ");
                builder.append(names[i]);
            }
        }

        if (builder.length() == 0) {
            return "Uma vez";
        }

        return builder.toString();
    }

    public boolean isRepeating() {
        for (boolean day : days) {
            if (day) return true;
        }
        return false;
    }
}
