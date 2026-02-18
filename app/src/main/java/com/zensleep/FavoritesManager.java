package com.zensleep;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

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

    // 🔥 NOVO: Remove um favorito diretamente
    public static void removeFavorite(Context context, String sound) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(sound).apply();
    }

    // 🔥 NOVO: Limpa todos os favoritos
    public static void clearFavorites(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    // 🔥 NOVO: Verifica se existe pelo menos 1 favorito
    public static boolean hasAnyFavorite(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return !prefs.getAll().isEmpty();
    }

    // 🔥 NOVO: Retorna todos favoritos (chave + valor)
    public static Map<String, ?> getAllFavorites(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return new HashMap<>(prefs.getAll());
    }
}
