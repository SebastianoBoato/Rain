package com.example.rain.dashboard.container;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rain.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SelectContainerActivity extends AppCompatActivity {

    // Variabile per mantenere il riferimento alla vista radice del layout del Fragment.
    View view;

    private RecyclerView recyclerView;
    private ContainerAdapter adapter;
    private List<Container> containerList;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Se necessario
        setContentView(R.layout.activity_select_container);

        // Inizializzazione Firebase
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Trova le viste direttamente
        recyclerView = findViewById(R.id.containerRecyclerView);

        // Configura RecyclerView
        containerList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Crea l'adapter e imposta il listener
        adapter = new ContainerAdapter(containerList, this, "UseWaterActivity");
        recyclerView.setAdapter(adapter);

        // Carica i contenitori da Firebase
        loadContainers();

        // Inizializza il pulsante Torna indietro
        Button backButton = findViewById(R.id.comeBackButton);
        backButton.setOnClickListener(v -> finish()); // Chiude l'Activity e torna indietro
    }


    private void loadContainers() {
        db.collection("users")
                .document(userId)
                .collection("containers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        containerList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String containerId = document.getId();
                            String name = document.getString("name");
                            String shape = document.getString("shape");
                            Double param1 = document.getDouble("param1");
                            Double height = document.getDouble("height");
                            Double roofArea = document.getDouble("roofArea");
                            Double area = document.getDouble("baseArea");
                            Double totalVolume = document.getDouble("totalVolume");
                            Double currentVolume = document.getDouble("currentVolume");
                            if(!shape.equals("Cerchio") && !shape.equals("Quadrato")){
                                Double param2 = document.getDouble("param2");
                                containerList.add(new Container(containerId, name, shape, param1, param2, height, roofArea, area, totalVolume, currentVolume));
                            }else{
                                containerList.add(new Container(containerId, name, shape, param1, height, roofArea, area, totalVolume, currentVolume));
                            }

                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Errore nel caricamento: " + task.getException(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
}