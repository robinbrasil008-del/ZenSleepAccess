package com.zensleep;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoritesManager {

    private static final String PREF_NAME = "zen_favorites";
    private static final String ORDER_KEY = "favorites_order";

    // 🔥 FAVORITAR / DESFAVORITAR COM ORDEM
    public static void toggleFavorite(Context context, String sound) {

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isFav = prefs.getBoolean(sound, false);

        List<String> list = getFavoritesOrder(context);

        if (isFav) {
            // ❌ REMOVE
            prefs.edit().remove(sound).apply();
            list.remove(sound);

        } else {
            // ✅ ADICIONA NO TOPO
            prefs.edit().putBoolean(sound, true).apply();

            list.remove(sound); // evita duplicado
            list.add(0, sound); // 🔥 MAIS NOVO NO TOPO
        }

        saveOrder(context, list);
    }

    public static boolean isFavorite(Context context, String sound) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(sound, false);
    }

    // 🔥 ORDEM DOS FAVORITOS
    public static List<String> getFavoritesOrder(Context context) {

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String data = prefs.getString(ORDER_KEY, "");

        List<String> list = new ArrayList<>();

        if (!data.isEmpty()) {
            String[] items = data.split(",");
            for (String item : items) {
                if (!item.isEmpty()) {
                    list.add(item);
                }
            }
        }

        return list;
    }

    private static void saveOrder(Context context, List<String> list) {

        StringBuilder sb = new StringBuilder();

        for (String item : list) {
            sb.append(item).append(",");
        }

        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(ORDER_KEY, sb.toString())
                .apply();
    }

    // 🔥 REMOVE DIRETO
    public static void removeFavorite(Context context, String sound) {

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        List<String> list = getFavoritesOrder(context);
        list.remove(sound);

        prefs.edit().remove(sound).apply();
        saveOrder(context, list);
    }

    // 🔥 LIMPA TUDO
    public static void clearFavorites(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    // 🔥 VERIFICA SE TEM FAVORITO
    public static boolean hasAnyFavorite(Context context) {
        return !getFavoritesOrder(context).isEmpty();
    }

    // 🔥 COMPATIBILIDADE (SE PRECISAR)
    public static Map<String, ?> getAllFavorites(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return new HashMap<>(prefs.getAll());
    }
}
