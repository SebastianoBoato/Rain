package com.example.rain.dashboard.weather;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.rain.databinding.ActivityFillingPredictionBinding;
import com.example.rain.items.DailyWeatherItem;
import com.example.rain.items.FillingPredictionItem;
import com.example.rain.items.HourlyWeatherItem;
import com.example.rain.recyclerview.PredictionAdapter;
import com.example.rain.utils.OneElementCallback;
import com.example.rain.utils.WeatherUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class FillingPredictionActivity extends AppCompatActivity {

    private ActivityFillingPredictionBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFillingPredictionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.fillingPrediction, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DailyWeatherItem todayWeather = getIntent().getParcelableExtra("todayWeather");
        List<HourlyWeatherItem> hourlyWeatherItems = getIntent().getParcelableArrayListExtra("hourlyWeather");

        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        WeatherUtils.getFillingPredictions(db, user, hourlyWeatherItems, new OneElementCallback<List<FillingPredictionItem>>() {
            @Override
            public void onSuccess(List<FillingPredictionItem> fillingPredictionItems) {
                binding.predictionRecyclerView.setLayoutManager(new LinearLayoutManager(FillingPredictionActivity.this));
                binding.predictionRecyclerView.setAdapter(new PredictionAdapter(FillingPredictionActivity.this, fillingPredictionItems, hourlyWeatherItems));
            }

            @Override
            public void onError(String error) {
                Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG).show();
            }
        });
    }
}