package com.example.rain.utils;

import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

public class NotificationUtils {
    // ID del canale (deve corrispondere a quello creato nel NotificationChannel)
    private static final String CHANNEL_ID = "weather_channel_id";

    // questa funzione vuole un context come primo parametro che sia un'activity o un getApplicationContext()
    public static void sendNotification(Context context, String title, String message) {

        // Crea il contenuto della notifica
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.sym_def_app_icon) // Icona della notifica
                .setContentTitle(title) // Titolo
                .setContentText(message) // Testo della notifica
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message)) // Usa BigTextStyle per il testo lungo
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Priorità
                .setAutoCancel(true); // Cancella la notifica quando viene cliccata

        // Ottieni il NotificationManager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // ID unico della notifica
        int notificationId = 1;

        // Mostra la notifica
        notificationManager.notify(notificationId, builder.build());
    }
}
