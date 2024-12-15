package com.example.rain.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rain.R;
import com.example.rain.items.HourlyWeatherItem;

import java.util.List;
import java.util.Locale;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherViewHolder> {

    Context context;
    List<HourlyWeatherItem> hourlyWeatherItems;

    public WeatherAdapter(Context context, List<HourlyWeatherItem> hourlyWeatherItems) {
        this.context = context;
        this.hourlyWeatherItems = hourlyWeatherItems;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new WeatherViewHolder(LayoutInflater.from(context).inflate(R.layout.item_weather, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder weatherViewHolder, int i) {
        weatherViewHolder.weatherTime.setText(hourlyWeatherItems.get(i).getTime());
        Glide.with(weatherViewHolder.itemView).load("https:" + hourlyWeatherItems.get(i).getIconUrl()).into(weatherViewHolder.weatherIcon);
        weatherViewHolder.weatherCondition.setText(hourlyWeatherItems.get(i).getCondition());
        weatherViewHolder.weatherPrecip.setText(String.format(Locale.US, "%.2fmm", hourlyWeatherItems.get(i).getPrecip()));
        weatherViewHolder.weatherChanceOfRain.setText(String.format(Locale.US, "(%d%%)", hourlyWeatherItems.get(i).getChanceOfRain()));
        weatherViewHolder.weatherTemp.setText(String.format(Locale.US, "%.1f°C", hourlyWeatherItems.get(i).getTemp()));
    }

    @Override
    public int getItemCount() {
        return hourlyWeatherItems.size();
    }
}
