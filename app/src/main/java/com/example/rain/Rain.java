package com.example.rain;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Build;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.rain.broadcast.AutoFillContainersBroadcastReceiver;
import com.example.rain.dashboard.MainActivity;
import com.example.rain.login.LoginActivity;
import com.example.rain.onboarding.FirstOnboardingActivity;
import com.google.firebase.auth.FirebaseAuth;

// QUESTA CLASSE E' LA COSA CHE VIENE CREATA PER PRIMA QUANDO SI AVVIA L'APP
// (non è una cosa visibile all'utente o con un'interfaccia)
public class Rain extends Application {

    // coda per le richieste API al meteo
    private static RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        // inizializzazione della coda
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        // crea il canale per le notifiche
        createNotificationChannel();

        // imposta il riempimento automatico dei container in base alla pioggia caduta
        setupAutoFillContainersAlarm();
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

    private void setupAutoFillContainersAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AutoFillContainersBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Imposta l'allarme ripetuto
        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
        }
    }
}
