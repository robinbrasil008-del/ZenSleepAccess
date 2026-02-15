package com.zensleep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null &&
            Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            // 🔥 Aqui você pode restaurar alarmes salvos se quiser
            // Por enquanto não vamos reagendar nada automaticamente
            // apenas deixamos preparado para o futuro

        }
    }
}
