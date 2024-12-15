package com.example.rain.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rain.R;
import com.example.rain.items.FillingPredictionItem;

import java.util.List;
import java.util.Locale;

public class PredictionAdapter extends RecyclerView.Adapter<PredictionViewHolder> {

    Context context;
    List<FillingPredictionItem> fillingPredictionItems;

    public PredictionAdapter(Context context, List<FillingPredictionItem> fillingPredictionItems) {
        this.context = context;
        this.fillingPredictionItems = fillingPredictionItems;
    }

    @NonNull
    @Override
    public PredictionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PredictionViewHolder(LayoutInflater.from(context).inflate(R.layout.item_filling_prediction, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionViewHolder holder, int i) {
        holder.containerName.setText(fillingPredictionItems.get(i).getContainerName());
        holder.containerTotalVolume.setText(String.format(Locale.US, "%.1fL", fillingPredictionItems.get(i).getContainerTotalVolume()));
        holder.containerCurrentVolume.setText(String.format(Locale.US, "%.1fL", fillingPredictionItems.get(i).getContainerCurrentVolume()));
        holder.containerVolumeIncrease.setText(String.format(Locale.US, "%.1fL", fillingPredictionItems.get(i).getContainerVolumeIncrease()));
        holder.containerPredictionVolume.setText(String.format(Locale.US, "%.1fL", fillingPredictionItems.get(i).getContainerPredictionVolume()));

        //TODO: aggiungere le immagini per le altre due forme
        String shape = fillingPredictionItems.get(i).getContainerShape();
        if (shape.equals("Rettangolo") || shape.equals("Quadrato")) {
            holder.containerShape.setImageResource(R.drawable.square_icon);
        }
        else if (shape.equals("Cerchio") || shape.equals("Ellisse")) {
            holder.containerShape.setImageResource(R.drawable.circle_icon);
        }
    }

    @Override
    public int getItemCount() {
        return fillingPredictionItems.size();
    }
}
