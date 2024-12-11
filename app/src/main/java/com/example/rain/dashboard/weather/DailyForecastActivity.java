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

/**
 * Activity che mostra le previsioni meteo giornaliere.
 * Consente di visualizzare una lista di previsioni orarie per il giorno corrente.
 */
public class DailyForecastActivity extends AppCompatActivity {

    // Binding per accedere ai componenti del layout associato all'Activity.
    private ActivityDailyForecastBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflating del layout con View Binding.
        binding = ActivityDailyForecastBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Abilita modalità Edge-to-Edge per un'esperienza utente a schermo intero.
        EdgeToEdge.enable(this);

        // Imposta i margini di padding per rispettare le barre di sistema (status bar e navigation bar).
        ViewCompat.setOnApplyWindowInsetsListener(binding.todayForecast, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Recupera la lista delle previsioni meteo orarie passata tramite l'Intent.
        List<HourlyWeatherItem> hourlyWeatherItems = getIntent().getParcelableArrayListExtra("weatherList");

        // Configura il RecyclerView per mostrare le previsioni orarie.
        // Usa un layout manager lineare per visualizzare gli elementi in una lista verticale.
        binding.weatherRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Imposta l'adapter che fornisce i dati al RecyclerView.
        binding.weatherRecyclerView.setAdapter(new WeatherAdapter(getApplicationContext(), hourlyWeatherItems));
    }
}
