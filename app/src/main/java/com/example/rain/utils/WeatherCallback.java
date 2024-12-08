package com.example.rain.utils;

import com.example.rain.items.WeatherItem;

import java.util.List;

public interface WeatherCallback {
    void onSuccess(List<WeatherItem> weatherItems);
    void onError(String error);
}