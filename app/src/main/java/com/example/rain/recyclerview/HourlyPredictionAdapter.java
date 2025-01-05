package com.example.rain.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rain.R;
import com.example.rain.items.HourlyFillingPredictionItem;

import java.util.List;
import java.util.Locale;

public class HourlyPredictionAdapter extends RecyclerView.Adapter<HourlyPredictionViewHolder> {

    Context context;
    List<HourlyFillingPredictionItem> hourlyFillingPredictionItems;

    public HourlyPredictionAdapter(Context context, List<HourlyFillingPredictionItem> hourlyFillingPredictionItems) {
        this.context = context;
        this.hourlyFillingPredictionItems = hourlyFillingPredictionItems;
    }

    @NonNull
    @Override
    public HourlyPredictionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HourlyPredictionViewHolder(LayoutInflater.from(context).inflate(R.layout.item_hourly_filling_prediction, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyPredictionViewHolder holder, int position) {
        holder.hour.setText(hourlyFillingPredictionItems.get(position).getHour());
        holder.containerPredictionVolume.setText(String.format(Locale.US, "%.2fL", hourlyFillingPredictionItems.get(position).getPredictionVolume()));
    }

    @Override
    public int getItemCount() {
        return hourlyFillingPredictionItems.size();
    }
}
