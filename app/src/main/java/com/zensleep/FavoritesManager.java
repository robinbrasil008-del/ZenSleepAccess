package com.zensleep;

import android.content.Context;
import android.content.SharedPreferences;

public class FavoritesManager {

    private static final String PREF_NAME = "zen_favorites";

    public static void toggleFavorite(Context context, String sound) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isFav = prefs.getBoolean(sound, false);

        prefs.edit().putBoolean(sound, !isFav).apply();
    }

    public static boolean isFavorite(Context context, String sound) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(sound, false);
    }
}
