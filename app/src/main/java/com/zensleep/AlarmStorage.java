package com.zensleep;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AlarmStorage {

    private static final String PREF = "alarm_storage";
    private static final String KEY = "alarms";

    public static void save(Context context, List<AlarmModel> alarms) {
        SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY, new Gson().toJson(alarms)).apply();
    }

    public static List<AlarmModel> load(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY, null);

        if (json == null) return new ArrayList<>();

        Type type = new TypeToken<List<AlarmModel>>(){}.getType();
        return new Gson().fromJson(json, type);
    }
}
