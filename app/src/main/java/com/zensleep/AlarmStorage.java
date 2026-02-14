package com.zensleep;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AlarmStorage {

    private static final String PREFS = "zen_alarms";
    private static final String KEY_LIST = "alarm_list";

    public static List<AlarmItem> load(Context context) {

        SharedPreferences prefs =
                context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        String json = prefs.getString(KEY_LIST, null);

        if (json == null) return new ArrayList<>();

        try {

            Type type =
                    new TypeToken<List<AlarmItem>>() {}.getType();

            List<AlarmItem> list =
                    new Gson().fromJson(json, type);

            if (list == null) return new ArrayList<>();

            // 🔥 GARANTE QUE NÃO VENHA NULL NOS NOVOS CAMPOS
            for (AlarmItem item : list) {

                if (item.days == null) {
                    item.days = new boolean[7];
                }

                if (item.soundUri == null) {
                    item.soundUri = "";
                }
            }

            return list;

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void save(Context context, List<AlarmItem> list) {

        SharedPreferences prefs =
                context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        String json = new Gson().toJson(list);

        prefs.edit()
                .putString(KEY_LIST, json)
                .apply();
    }

    public static int nextId(List<AlarmItem> list) {

        int max = 1000;

        for (AlarmItem a : list) {
            if (a.id > max) max = a.id;
        }

        return max + 1;
    }
}
