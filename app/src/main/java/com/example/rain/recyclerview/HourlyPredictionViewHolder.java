package com.example.rain.recyclerview;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rain.R;

public class HourlyPredictionViewHolder extends RecyclerView.ViewHolder {
    TextView hour, containerPredictionVolume;

    public HourlyPredictionViewHolder(@NonNull View itemView) {
        super(itemView);

        hour = itemView.findViewById(R.id.hour);
        containerPredictionVolume = itemView.findViewById(R.id.containerPredictionVolume);
    }
}
