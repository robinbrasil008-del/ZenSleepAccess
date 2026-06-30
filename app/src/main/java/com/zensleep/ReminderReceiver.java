package com.zensleep;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "zensleep_reminder_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 1. Cria o Canal de Notificação (Obrigatório para Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Lembrete de Dormir",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifica você na hora de relaxar");
            notificationManager.createNotificationChannel(channel);
        }

        // 2. Prepara o clique na notificação para abrir o app na tela inicial
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 3. Monta o visual da Notificação
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher) // Usando o ícone principal do seu app
                .setContentTitle("Hora de Relaxar 🌙")
                .setContentText("O ambiente perfeito está te esperando. Que tal um som de chuva para dormir bem hoje?")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // 4. Dispara a notificação na tela
        if (notificationManager != null) {
            notificationManager.notify(200, builder.build());
        }
    }
}

