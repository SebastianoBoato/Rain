package com.example.rain.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.rain.items.DailyWeatherItem;
import com.example.rain.items.HourlyWeatherItem;
import com.example.rain.utils.NotificationUtils;
import com.example.rain.utils.OneElementCallback;
import com.example.rain.utils.WeatherCallback;
import com.example.rain.utils.WeatherUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AutoFillContainersBroadcastReceiver extends BroadcastReceiver {

    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    public void onReceive(Context context, Intent intent) {

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        WeatherUtils.getLocation(db, user, new OneElementCallback<String>() {
            @Override
            public void onSuccess(String location) {

                WeatherUtils.getWeatherDetails(location, new WeatherCallback() {
                    @Override
                    public void onSuccess(List<HourlyWeatherItem> hourlyWeatherItemsToday,
                                          List<HourlyWeatherItem> hourlyWeatherItemsTomorrow,
                                          List<HourlyWeatherItem> hourlyWeatherItemsAfterTomorrow,
                                          DailyWeatherItem todayWeather,
                                          DailyWeatherItem tomorrowWeather,
                                          DailyWeatherItem afterTomorrowWeather) {

                        WeatherUtils.autoFillContainers(db, user, todayWeather, context);
                    }

                    @Override
                    public void onError(String error) {
                        NotificationUtils.sendNotification(context, "Rain: Errore nella connessione al server", "Errore nello scaricamento dei dati per il riempimento dei contenitori");
                    }
                });
            }

            @Override
            public void onError(String error) {
                NotificationUtils.sendNotification(context, "Rain: Errore nella connessione al server", "Errore nello scaricamento dei dati per il riempimento dei contenitori");
            }
        });
    }
}
