package com.example.rain.recyclerview;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rain.R;

public class PredictionViewHolder extends RecyclerView.ViewHolder {

    TextView containerName, containerTotalVolume, containerCurrentVolume, containerVolumeIncrease, containerPredictionVolume;
    ImageView containerShape;

    public PredictionViewHolder(@NonNull View itemView) {
        super(itemView);

        containerName = itemView.findViewById(R.id.containerName);
        containerTotalVolume = itemView.findViewById(R.id.containerTotalVolume);
        containerCurrentVolume = itemView.findViewById(R.id.containerCurrentVolume);
        containerVolumeIncrease = itemView.findViewById(R.id.containerVolumeIncrease);
        containerPredictionVolume = itemView.findViewById(R.id.containerPredictionVolume);
        containerShape = itemView.findViewById(R.id.containerShape);
    }
}
