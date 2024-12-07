package com.example.rain;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

// QUESTA CLASSE E' LA COSA CHE VIENE CREATA PER PRIMA QUANDO SI AVVIA L'APP
// (non è una cosa visibile all'utente o con un'interfaccia)

public class Rain extends Application {

    // coda per le richieste API al meteo
    private static RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        // crea il canale per le notifiche (se non esiste già, quindi lo fa solo la prima volta)
        createNotificationChannel();
    }

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "weather_channel_id";
            String channelName = "Weather Channel";
            String channelDescription = "This is a weather notification channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            // Registra il canale con il NotificationManager
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
