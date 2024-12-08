package com.example.rain.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rain.R;
import com.example.rain.items.WeatherItem;

import java.util.List;
import java.util.Locale;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherViewHolder> {

    Context context;
    List<WeatherItem> weatherItems;

    public WeatherAdapter(Context context, List<WeatherItem> weatherItems) {
        this.context = context;
        this.weatherItems = weatherItems;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new WeatherViewHolder(LayoutInflater.from(context).inflate(R.layout.item_weather, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder weatherViewHolder, int i) {
        weatherViewHolder.weatherTime.setText(weatherItems.get(i).getTime());
        // TODO: icona da mettere
        weatherViewHolder.weatherCondition.setText(weatherItems.get(i).getCondition());
        weatherViewHolder.weatherPrecip.setText(String.format(Locale.US, "%.2fmm", weatherItems.get(i).getPrecip()));
        weatherViewHolder.weatherChanceOfRain.setText(String.format(Locale.US, "(%d%%)", weatherItems.get(i).getChanceOfRain()));
        weatherViewHolder.weatherTemp.setText(String.format(Locale.US, "%.1f°C", weatherItems.get(i).getTemp()));
    }

    @Override
    public int getItemCount() {
        return weatherItems.size();
    }
}
