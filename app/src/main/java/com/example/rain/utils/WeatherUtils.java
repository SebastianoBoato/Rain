package com.example.rain.utils;

import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.rain.Rain;
import com.example.rain.items.WeatherItem;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherUtils {

    // TODO: bisognerebbe prendere la location dal database, per adesso comunque dovresti dare la possibilità di inserirla manualmente tipo app del meteo
    // TODO: ricordati dell'icona del meteo!!!

    // ORDINE: baseUrl + forecast/current + key + city + days + other

    private final static String baseUrl = "https://api.weatherapi.com/v1";
    private final static String key = "4d9c1c64ddaa4009926173617241911"; // ?key=
    private final static String other = "&aqi=no&alerts=no";
    // per la location va anche bene scrivere 43.22686,81.4542 dove la prima è latitudine e la seconda longitudine
    // weatherState può essere current o forecast

    public static void getWeatherDetails1day(View view, String city, WeatherCallback callback) {

        String tempUrl = baseUrl + "/forecast.json" + "?key=" + key + "&q=" + city + "&days=1" + other;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, tempUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    List<WeatherItem> weatherItems = new ArrayList<>();

                    // arrivo fino alla lista di ore
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONObject jsonObjectForecast = jsonResponse.getJSONObject("forecast");
                    JSONArray jsonArrayForecastday = jsonObjectForecast.getJSONArray("forecastday");
                    JSONObject jsonObjectForecastday = jsonArrayForecastday.getJSONObject(0);
                    JSONArray jsonArrayHours = jsonObjectForecastday.getJSONArray("hour");

                    // scorro le ore e aggiungo i dati all'ArrayList
                    for (int i = 0; i < 24; ++i) {
                        JSONObject jsonObjectHour = jsonArrayHours.getJSONObject(i);

                        String time = jsonObjectHour.getString("time");
                        String hour = time.substring(time.length() - 5);

                        JSONObject jsonObjectCondition = jsonObjectHour.getJSONObject("condition");
                        String condition = jsonObjectCondition.getString("text");

                        double temp = jsonObjectHour.getDouble("temp_c");
                        int chanceOfRain = jsonObjectHour.getInt("chance_of_rain");
                        double precip = jsonObjectHour.getDouble("precip_mm");

                        weatherItems.add(new WeatherItem(hour, condition, temp, chanceOfRain, precip));
                    }

                    callback.onSuccess(weatherItems);

                } catch (JSONException e) {
                    callback.onError("Errore nel parsing del JSON: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError("Errore nella richiesta: " + error.getMessage());
            }
        });

        Rain.getRequestQueue().add(stringRequest);
    }

}
