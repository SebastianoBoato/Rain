package com.example.rain.dashboard.weather;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.rain.databinding.ActivityDailyForecastBinding;
import com.example.rain.items.HourlyWeatherItem;
import com.example.rain.recyclerview.WeatherAdapter;

import java.util.List;

public class DailyForecastActivity extends AppCompatActivity {

    private ActivityDailyForecastBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDailyForecastBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.todayForecast, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // prendo la lista di meteo orario passata con l'Intent
        List<HourlyWeatherItem> hourlyWeatherItems = getIntent().getParcelableArrayListExtra("weatherList");

        binding.weatherRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.weatherRecyclerView.setAdapter(new WeatherAdapter(getApplicationContext(), hourlyWeatherItems));
    }
}