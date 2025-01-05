package com.example.rain.dashboard;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.rain.dashboard.weather.DailyForecastActivity;
import com.example.rain.dashboard.weather.FillingPredictionActivity;
import com.example.rain.databinding.FragmentWeatherBinding;
import com.example.rain.items.DailyWeatherItem;
import com.example.rain.items.HourlyWeatherItem;
import com.example.rain.utils.OneElementCallback;
import com.example.rain.utils.WeatherCallback;
import com.example.rain.utils.WeatherUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeatherFragment extends Fragment {

    private View view;
    private FragmentWeatherBinding binding;
    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentWeatherBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // devo usare questi trick power con le anonymous class Callback perché in realtà tutto quello che faccio lo posso fare solo dopo la risposta dei listener
        // cioè i dati del database ce li avrò nel listener della query, i dati del meteo nel listener della chiamata API

        WeatherUtils.getLocation(db, user, new OneElementCallback<String>() {
            @Override
            public void onSuccess(String location) {

                WeatherUtils.getWeatherDetails(location, new WeatherCallback() {
                    @Override
                    public void onSuccess(List<HourlyWeatherItem> hourlyWeatherItemsToday,
                                          List<HourlyWeatherItem> hourlyWeatherItemsTomorrow,
                                          List<HourlyWeatherItem> hourlyWeatherItemsAfterTomorrow,
                                          DailyWeatherItem todayWeather,
                                          DailyWeatherItem tomorrowWeather,
                                          DailyWeatherItem afterTomorrowWeather) {

                        // imposto il frame di oggi
                        binding.weatherCondition.setText(todayWeather.getCondition());
                        Glide.with(WeatherFragment.this).load("https:" + todayWeather.getIconUrl()).into(binding.weatherIcon);
                        binding.weatherMaxTemp.setText(String.format(Locale.US, "%.1f°C", todayWeather.getMaxTemp()));
                        binding.weatherMinTemp.setText(String.format(Locale.US, "%.1f°C", todayWeather.getMinTemp()));
                        binding.weatherAvgTemp.setText(String.format(Locale.US, "%.1f°C", todayWeather.getAvgTemp()));
                        binding.weatherPrecip.setText(String.format(Locale.US, "%.2fmm", todayWeather.getPrecip()));
                        binding.weatherChanceOfRain.setText(String.format(Locale.US, "%d%%", todayWeather.getChanceOfRain()));

                        // imposto il frame di domani
                        Glide.with(WeatherFragment.this).load("https:" + tomorrowWeather.getIconUrl()).into(binding.tomorrowWeatherIcon);
                        binding.tomorrowWeatherPrecip.setText(String.format(Locale.US, "%.2fmm", tomorrowWeather.getPrecip()));

                        // imposto il frame di dopodomani
                        Glide.with(WeatherFragment.this).load("https:" + afterTomorrowWeather.getIconUrl()).into(binding.afterTomorrowWeatherIcon);
                        binding.afterTomorrowWeatherPrecip.setText(String.format(Locale.US, "%.2fmm", afterTomorrowWeather.getPrecip()));

                        binding.todayForecastFrame.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ArrayList<HourlyWeatherItem> hourlyWeatherItemsTodayArrayList = new ArrayList<>(hourlyWeatherItemsToday);
                                Intent intent = new Intent(getActivity(), DailyForecastActivity.class);
                                intent.putParcelableArrayListExtra("weatherList", hourlyWeatherItemsTodayArrayList);
                                startActivity(intent);
                            }
                        });

                        binding.tomorrowForecastFrame.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ArrayList<HourlyWeatherItem> hourlyWeatherItemsTomorrowArrayList = new ArrayList<>(hourlyWeatherItemsTomorrow);
                                Intent intent = new Intent(getActivity(), DailyForecastActivity.class);
                                intent.putParcelableArrayListExtra("weatherList", hourlyWeatherItemsTomorrowArrayList);
                                startActivity(intent);
                            }
                        });

                        binding.afterTomorrowForecastFrame.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ArrayList<HourlyWeatherItem> hourlyWeatherItemsAfterTomorrowArrayList = new ArrayList<>(hourlyWeatherItemsAfterTomorrow);
                                Intent intent = new Intent(getActivity(), DailyForecastActivity.class);
                                intent.putParcelableArrayListExtra("weatherList", hourlyWeatherItemsAfterTomorrowArrayList);
                                startActivity(intent);
                            }
                        });

                        binding.fillingEstimate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ArrayList<HourlyWeatherItem> hourlyWeatherItemsTodayArrayList = new ArrayList<>(hourlyWeatherItemsToday);
                                Intent intent = new Intent(getActivity(), FillingPredictionActivity.class);
                                intent.putExtra("todayWeather", todayWeather);
                                intent.putExtra("hourlyWeather", hourlyWeatherItemsTodayArrayList);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        Snackbar.make(view, error, Snackbar.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                Snackbar.make(view, error, Snackbar.LENGTH_LONG).show();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}