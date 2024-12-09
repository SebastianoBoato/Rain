package com.example.rain.dashboard.weather;

import static java.security.AccessController.getContext;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.rain.R;
import com.example.rain.databinding.ActivityMainBinding;
import com.example.rain.databinding.ActivityTodayForecastBinding;
import com.example.rain.recyclerview.WeatherAdapter;

public class TodayForecastActivity extends AppCompatActivity {

    private ActivityTodayForecastBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTodayForecastBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.todayForecast, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.weatherRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //binding.weatherRecyclerView.setAdapter(new WeatherAdapter(getApplicationContext(), weatherItems));
    }
}