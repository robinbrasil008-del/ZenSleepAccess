package com.zensleep;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AlarmStorage {

    private static final String PREFS = "alarm_storage";
    private static final String KEY = "alarms";

    public static void saveAlarms(Context context, List<Alarm> alarms) {
        try {
            JSONArray array = new JSONArray();

            for (Alarm alarm : alarms) {
                JSONObject obj = new JSONObject();
                obj.put("id", alarm.getId());
                obj.put("hour", alarm.getHour());
                obj.put("minute", alarm.getMinute());
                obj.put("label", alarm.getLabel());
                obj.put("enabled", alarm.isEnabled());
                obj.put("soundUri", alarm.getSoundUri());
                array.put(obj);
            }

            SharedPreferences prefs = context.getSharedPreferences(PREFS, 0);
            prefs.edit().putString(KEY, array.toString()).apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Alarm> loadAlarms(Context context) {
        List<Alarm> alarms = new ArrayList<>();

        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS, 0);
            String data = prefs.getString(KEY, null);

            if (data == null) return alarms;

            JSONArray array = new JSONArray(data);

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                alarms.add(new Alarm(
                        obj.getInt("id"),
                        obj.getInt("hour"),
                        obj.getInt("minute"),
                        obj.getString("label"),
                        obj.getBoolean("enabled"),
                        obj.getString("soundUri")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return alarms;
    }
}
