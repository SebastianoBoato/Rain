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

import java.util.ArrayList;
import java.util.List;

public class WeatherFragment extends Fragment {

    private View view;
    private FragmentWeatherBinding binding;
    private List<WeatherItem> weatherItems;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentWeatherBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        weatherItems = new ArrayList<>();

        weatherItems.add(new WeatherItem("12:00", "Pioggia", 13.4, 56, 0.91));

        binding.weatherRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.weatherRecyclerView.setAdapter(new WeatherAdapter(getContext(), weatherItems));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}