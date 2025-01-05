package com.example.rain.dashboard.weather;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.rain.R;
import com.example.rain.databinding.ActivityFillingPredictionBinding;
import com.example.rain.databinding.ActivityHourlyFillingPredictionBinding;
import com.example.rain.items.HourlyFillingPredictionItem;
import com.example.rain.items.HourlyWeatherItem;
import com.example.rain.recyclerview.HourlyPredictionAdapter;
import com.example.rain.recyclerview.PredictionAdapter;
import com.example.rain.utils.OneElementCallback;
import com.example.rain.utils.WeatherUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HourlyFillingPredictionActivity extends AppCompatActivity {

    private ActivityHourlyFillingPredictionBinding binding;
    private FirebaseFirestore db;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHourlyFillingPredictionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.hourlyFillingPrediction, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // prendo i dati dell'intent
        List<HourlyWeatherItem> hourlyWeatherItems = getIntent().getParcelableArrayListExtra("hourlyWeather");
        String containerName = getIntent().getStringExtra("containerName");
        String containerShape = getIntent().getStringExtra("containerShape");
        double containerTotalVolume = getIntent().getDoubleExtra("containerTotalVolume", 0.0);
        double containerCurrentVolume = getIntent().getDoubleExtra("containerCurrentVolume", 0.0);

        // imposto le textview
        binding.containerName.setText(containerName);
        binding.containerTotalVolume.setText(String.format(Locale.US, "%.2fL", containerTotalVolume));
        binding.containerCurrentVolume.setText(String.format(Locale.US, "%.2fL", containerCurrentVolume));
        //TODO: aggiungere le immagini per le altre due forme
        if (containerShape.equals("Rettangolo") || containerShape.equals("Quadrato")) {
            binding.containerIcon.setImageResource(R.drawable.square_icon);
        }
        else if (containerShape.equals("Cerchio") || containerShape.equals("Ellisse")) {
            binding.containerIcon.setImageResource(R.drawable.circle_icon);
        }

        // prendo l'area del container dal db
        WeatherUtils.getArea(db, user, containerName, new OneElementCallback<Double>() {
            @Override
            public void onSuccess(Double area) {

                List<HourlyFillingPredictionItem> hourlyFillingPredictionItems = new ArrayList<>();
                double increase = containerCurrentVolume;

                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);

                for (HourlyWeatherItem item : hourlyWeatherItems) {
                    // non mi interessano le ore passate della giornata, voglio partire dall'ora attuale
                    if (Integer.parseInt(item.getTime().substring(0, 2)) >= hour ) {
                        increase += ( (item.getPrecip() / 10) * area ) / 1000;
                        if (increase > containerTotalVolume) { increase = containerTotalVolume; }
                        hourlyFillingPredictionItems.add(new HourlyFillingPredictionItem(item.getTime(), increase));
                    }
                }

                binding.predictionRecyclerView.setLayoutManager(new LinearLayoutManager(HourlyFillingPredictionActivity.this));
                binding.predictionRecyclerView.setAdapter(new HourlyPredictionAdapter(getApplicationContext(), hourlyFillingPredictionItems));
            }

            @Override
            public void onError(String error) {
                Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG).show();
            }
        });
    }
}