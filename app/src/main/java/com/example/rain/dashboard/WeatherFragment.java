package com.example.rain.dashboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rain.databinding.FragmentWeatherBinding;
import com.example.rain.items.WeatherItem;
import com.example.rain.recyclerview.WeatherAdapter;
import com.example.rain.utils.WeatherCallback;
import com.example.rain.utils.WeatherUtils;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class WeatherFragment extends Fragment {

    private View view;
    private FragmentWeatherBinding binding;

    // TODO: bisognerebbe prendere la location dal database, per adesso comunque dovresti dare la possibilità di inserirla manualmente tipo app del meteo

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentWeatherBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        WeatherUtils.getWeatherDetails1day(view, "London", new WeatherCallback() {
            @Override
            public void onSuccess(List<WeatherItem> weatherItems) {
                binding.weatherRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.weatherRecyclerView.setAdapter(new WeatherAdapter(getContext(), weatherItems));
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