package com.example.rain.utils;

import com.example.rain.items.DailyWeatherItem;
import com.example.rain.items.HourlyWeatherItem;

import java.util.List;

public interface WeatherCallback {
    void onSuccess(List<HourlyWeatherItem> hourlyWeatherItemsToday,
                   List<HourlyWeatherItem> hourlyWeatherItemsTomorrow,
                   List<HourlyWeatherItem> hourlyWeatherItemsAfterTomorrow,
                   DailyWeatherItem todayWeather,
                   DailyWeatherItem tomorrowWeather,
                   DailyWeatherItem afterTomorrowWeather);
    void onError(String error);
}