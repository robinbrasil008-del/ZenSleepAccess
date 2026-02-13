package com.zensleep;

public class Alarm {

    private int id;
    private int hour;
    private int minute;
    private String label;
    private boolean enabled;
    private String soundUri;

    public Alarm(int id, int hour, int minute, String label, boolean enabled, String soundUri) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.label = label;
        this.enabled = enabled;
        this.soundUri = soundUri;
    }

    public int getId() { return id; }
    public int getHour() { return hour; }
    public int getMinute() { return minute; }
    public String getLabel() { return label; }
    public boolean isEnabled() { return enabled; }
    public String getSoundUri() { return soundUri; }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getFormattedTime() {
        return String.format("%02d:%02d", hour, minute);
    }
}
