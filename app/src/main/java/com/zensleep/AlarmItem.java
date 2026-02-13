package com.zensleep;

public class AlarmItem {
    public int id;
    public int hour;
    public int minute;
    public String label;
    public boolean enabled;

    public AlarmItem(int id, int hour, int minute, String label, boolean enabled) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.label = label;
        this.enabled = enabled;
    }

    public String timeText() {
        return String.format("%02d:%02d", hour, minute);
    }
}
