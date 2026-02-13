package com.zensleep;

import java.io.Serializable;
import java.util.UUID;

public class AlarmModel implements Serializable {

    public String id;
    public int hour;
    public int minute;
    public String label;
    public boolean enabled;
    public String ringtoneUri;

    public AlarmModel(int hour, int minute, String label, String ringtoneUri) {
        this.id = UUID.randomUUID().toString();
        this.hour = hour;
        this.minute = minute;
        this.label = label;
        this.enabled = true;
        this.ringtoneUri = ringtoneUri;
    }

    public String getFormattedTime() {
        return String.format("%02d:%02d", hour, minute);
    }
}
