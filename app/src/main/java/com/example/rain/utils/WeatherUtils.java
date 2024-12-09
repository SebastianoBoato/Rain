package com.example.rain.utils;

import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.rain.Rain;
import com.example.rain.items.DailyWeatherItem;
import com.example.rain.items.HourlyWeatherItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherUtils {

    // TODO: bisognerebbe prendere la location dal database, per adesso comunque dovresti dare la possibilità di inserirla manualmente tipo app del meteo

    // ORDINE: baseUrl + forecast/current + key + city + days + other

    private final static String baseUrl = "https://api.weatherapi.com/v1";
    private final static String key = "4d9c1c64ddaa4009926173617241911"; // ?key=
    private final static String other = "&aqi=no&alerts=no";
    // per la location va anche bene scrivere 43.22686,81.4542 dove la prima è latitudine e la seconda longitudine
    // weatherState può essere current o forecast

    public static void getWeatherDetails(View view, String city, WeatherCallback callback) {

        String tempUrl = baseUrl + "/forecast.json" + "?key=" + key + "&q=" + city + "&days=3" + other;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, tempUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    List<HourlyWeatherItem> hourlyWeatherItemsToday = new ArrayList<>();
                    List<HourlyWeatherItem> hourlyWeatherItemsTomorrow = new ArrayList<>();
                    List<HourlyWeatherItem> hourlyWeatherItemsAfterTomorrow = new ArrayList<>();
                    DailyWeatherItem todayWeather, tomorrowWeather, afterTomorrowWeather;

                    // arrivo fino alla lista dei 3 giorni
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONObject jsonObjectForecast = jsonResponse.getJSONObject("forecast");
                    JSONArray jsonArrayForecastday = jsonObjectForecast.getJSONArray("forecastday");

                    // riempio la lista delle ore di oggi
                    JSONObject jsonObjectForecastDaily = jsonArrayForecastday.getJSONObject(0);
                    JSONArray jsonArrayHours = jsonObjectForecastDaily.getJSONArray("hour");
                    hourlyWeatherItemsToday = getHourlyWeather(jsonArrayHours);
                    // meteo complessivo di oggi
                    JSONObject jsonObjectDay = jsonObjectForecastDaily.getJSONObject("day");
                    todayWeather = getDailyWeather(jsonObjectDay);

                    // riempio la lista delle ore di domani
                    jsonObjectForecastDaily = jsonArrayForecastday.getJSONObject(1);
                    jsonArrayHours = jsonObjectForecastDaily.getJSONArray("hour");
                    hourlyWeatherItemsTomorrow = getHourlyWeather(jsonArrayHours);
                    // meteo complessivo di domani
                    jsonObjectDay = jsonObjectForecastDaily.getJSONObject("day");
                    tomorrowWeather = getDailyWeather(jsonObjectDay);

                    // riempio la lista delle ore di dopodomani
                    jsonObjectForecastDaily = jsonArrayForecastday.getJSONObject(2);
                    jsonArrayHours = jsonObjectForecastDaily.getJSONArray("hour");
                    hourlyWeatherItemsAfterTomorrow = getHourlyWeather(jsonArrayHours);
                    // meteo complessivo di dopodomani
                    jsonObjectDay = jsonObjectForecastDaily.getJSONObject("day");
                    afterTomorrowWeather = getDailyWeather(jsonObjectDay);

                    callback.onSuccess(hourlyWeatherItemsToday,
                            hourlyWeatherItemsTomorrow,
                            hourlyWeatherItemsAfterTomorrow,
                            todayWeather,
                            tomorrowWeather,
                            afterTomorrowWeather);

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

    private static List<HourlyWeatherItem> getHourlyWeather(JSONArray jsonArrayHours) throws JSONException {

        List<HourlyWeatherItem> hourlyWeatherItems = new ArrayList<>();

        // scorro le ore e aggiungo i dati all'ArrayList
        for (int i = 0; i < 24; ++i) {
            JSONObject jsonObjectHour = jsonArrayHours.getJSONObject(i);

            String time = jsonObjectHour.getString("time");
            String hour = time.substring(time.length() - 5);

            JSONObject jsonObjectCondition = jsonObjectHour.getJSONObject("condition");
            String condition = jsonObjectCondition.getString("text");
            String iconUrl = jsonObjectCondition.getString("icon");

            double temp = jsonObjectHour.getDouble("temp_c");
            int chanceOfRain = jsonObjectHour.getInt("chance_of_rain");
            double precip = jsonObjectHour.getDouble("precip_mm");

            hourlyWeatherItems.add(new HourlyWeatherItem(hour, condition, iconUrl, temp, chanceOfRain, precip));
        }

        return hourlyWeatherItems;
    }

    private static DailyWeatherItem getDailyWeather(JSONObject jsonObjectDay) throws JSONException {

        JSONObject jsonObjectCondition = jsonObjectDay.getJSONObject("condition");
        String condition = jsonObjectCondition.getString("text");
        String iconUrl = jsonObjectCondition.getString("icon");

        double maxTemp = jsonObjectDay.getDouble("maxtemp_c");
        double minTemp = jsonObjectDay.getDouble("mintemp_c");
        double avgTemp = jsonObjectDay.getDouble("avgtemp_c");

        int chanceOfRain = jsonObjectDay.getInt("daily_chance_of_rain");
        double precip = jsonObjectDay.getDouble("totalprecip_mm");

        return new DailyWeatherItem(condition, iconUrl, maxTemp, minTemp, avgTemp, chanceOfRain, precip);
    }
}
