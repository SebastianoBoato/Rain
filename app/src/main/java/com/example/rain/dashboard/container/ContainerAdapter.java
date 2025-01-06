package com.example.rain.dashboard.container;

import com.example.rain.R;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContainerAdapter extends RecyclerView.Adapter<ContainerAdapter.ContainerViewHolder> {

    private List<Container> containerList;
    private Context context;
    private String targetActivity; // Parametro per definire quale Activity deve essere aperta

    public ContainerAdapter(List<Container> containerList, Context context, String targetActivity) {
        this.containerList = containerList;
        this.context = context;
        this.targetActivity = targetActivity;
    }

    @NonNull
    @Override
    public ContainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container, parent, false);
        return new ContainerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContainerViewHolder holder, int position) {
        Container container = containerList.get(position);
        holder.nameTextView.setText(String.format("Nome: %s", container.getName()));
        holder.shapeTextView.setText(String.format("Forma: %s", container.getShape()));
        holder.currentVolumeTextView.setText(String.format("Quantità attuale: %.2f L", container.getCurrentVolume()/1000));

        if (container.getShape().equalsIgnoreCase("Cerchio") || container.getShape().equalsIgnoreCase("Ellisse")) {
            holder.imageView.setImageResource(R.drawable.circle_icon);
        } else if (container.getShape().equalsIgnoreCase("Quadrato") || container.getShape().equalsIgnoreCase("Rettangolo")) {
            holder.imageView.setImageResource(R.drawable.square_icon);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent;
            if("ContainerDetailActivity".equals(targetActivity)){
                intent = new Intent(context, ContainerDetailActivity.class);
            }else if("UseWaterActivity".equals(targetActivity)){
                intent = new Intent(context, UseWaterActivity.class);
            }else{
                return;
            }
            intent.putExtra("container", container);  // Passa il contenitore come Serializable
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return containerList.size();
    }

    static class ContainerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView, shapeTextView, currentVolumeTextView;

        public ContainerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            nameTextView = itemView.findViewById(R.id.name);
            shapeTextView = itemView.findViewById(R.id.shape);
            currentVolumeTextView = itemView.findViewById(R.id.currentVolume);
        }
    }
}
