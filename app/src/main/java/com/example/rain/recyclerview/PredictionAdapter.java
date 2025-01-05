package com.example.rain.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rain.R;
import com.example.rain.dashboard.weather.DailyForecastActivity;
import com.example.rain.dashboard.weather.FillingPredictionActivity;
import com.example.rain.dashboard.weather.HourlyFillingPredictionActivity;
import com.example.rain.items.FillingPredictionItem;
import com.example.rain.items.HourlyWeatherItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PredictionAdapter extends RecyclerView.Adapter<PredictionViewHolder> {

    Context context;
    List<FillingPredictionItem> fillingPredictionItems;
    List<HourlyWeatherItem> hourlyWeatherItems;

    public PredictionAdapter(Context context, List<FillingPredictionItem> fillingPredictionItems, List<HourlyWeatherItem> hourlyWeatherItems) {
        this.context = context;
        this.fillingPredictionItems = fillingPredictionItems;
        this.hourlyWeatherItems = hourlyWeatherItems;
    }

    @NonNull
    @Override
    public PredictionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PredictionViewHolder(LayoutInflater.from(context).inflate(R.layout.item_filling_prediction, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionViewHolder holder, int i) {
        holder.containerName.setText(fillingPredictionItems.get(i).getContainerName());
        holder.containerTotalVolume.setText(String.format(Locale.US, "%.2fL", fillingPredictionItems.get(i).getContainerTotalVolume()));
        holder.containerCurrentVolume.setText(String.format(Locale.US, "%.2fL", fillingPredictionItems.get(i).getContainerCurrentVolume()));
        holder.containerVolumeIncrease.setText(String.format(Locale.US, "%.2fL", fillingPredictionItems.get(i).getContainerVolumeIncrease()));
        holder.containerPredictionVolume.setText(String.format(Locale.US, "%.2fL", fillingPredictionItems.get(i).getContainerPredictionVolume()));

        //TODO: aggiungere le immagini per le altre due forme
        String shape = fillingPredictionItems.get(i).getContainerShape();
        if (shape.equals("Rettangolo") || shape.equals("Quadrato")) {
            holder.containerShape.setImageResource(R.drawable.square_icon);
        }
        else if (shape.equals("Cerchio") || shape.equals("Ellisse")) {
            holder.containerShape.setImageResource(R.drawable.circle_icon);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<HourlyWeatherItem> hourlyWeatherItemsArrayList = new ArrayList<>(hourlyWeatherItems);
                Intent intent = new Intent(context, HourlyFillingPredictionActivity.class);
                intent.putExtra("hourlyWeather", hourlyWeatherItemsArrayList);
                intent.putExtra("containerName", fillingPredictionItems.get(holder.getAbsoluteAdapterPosition()).getContainerName());
                intent.putExtra("containerTotalVolume", fillingPredictionItems.get(holder.getAbsoluteAdapterPosition()).getContainerTotalVolume());
                intent.putExtra("containerCurrentVolume", fillingPredictionItems.get(holder.getAbsoluteAdapterPosition()).getContainerCurrentVolume());
                intent.putExtra("containerShape", fillingPredictionItems.get(holder.getAbsoluteAdapterPosition()).getContainerShape());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fillingPredictionItems.size();
    }
}
