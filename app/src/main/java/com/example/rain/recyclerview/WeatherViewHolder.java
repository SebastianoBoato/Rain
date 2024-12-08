package com.example.rain.recyclerview;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rain.R;


public class WeatherViewHolder extends RecyclerView.ViewHolder {

    TextView weatherTime, weatherCondition, weatherPrecip, weatherChanceOfRain, weatherTemp;
    ImageView weatherIcon;

    public WeatherViewHolder(@NonNull View itemView) {
        super(itemView);

        weatherTime = itemView.findViewById(R.id.weatherTime);
        weatherCondition = itemView.findViewById(R.id.weatherCondition);
        weatherPrecip = itemView.findViewById(R.id.weatherPrecip);
        weatherChanceOfRain= itemView.findViewById(R.id.weatherChanceOfRain);
        weatherTemp = itemView.findViewById(R.id.weatherTemp);
        weatherIcon = itemView.findViewById(R.id.weatherIcon);
    }
}
