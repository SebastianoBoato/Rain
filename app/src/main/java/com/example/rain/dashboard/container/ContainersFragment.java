package com.example.rain.dashboard.container;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import com.example.rain.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * Fragment che gestisce la schermata dedicata ai contenitori.
 * Viene utilizzato per mostrare o interagire con i serbatoi registrati dall'utente.
 */
public class ContainersFragment extends Fragment {

    // Variabile per mantenere il riferimento alla vista radice del layout del Fragment.
    View view;

    private RecyclerView recyclerView;
    private ContainerAdapter adapter;
    private List<Container> containerList;
    private FirebaseFirestore db;
    private String userId;

    /**
     * Metodo chiamato per creare e restituire la vista associata al Fragment.
     * @param inflater oggetto per gonfiare (inflate) il layout del Fragment.
     * @param container il contenitore che ospita la vista del Fragment.
     * @param savedInstanceState dati salvati dallo stato precedente, se disponibili.
     * @return la vista creata per il Fragment.
     */

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_containers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inizializzazione Firebase
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        recyclerView = view.findViewById(R.id.containerRecyclerView);
        Button addContainerButton = view.findViewById(R.id.addContainerButton);

        // Configura RecyclerView
        containerList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Crea l'adapter e imposta il listener
        adapter = new ContainerAdapter(containerList, getContext(), "ContainerDetailActivity");
        recyclerView.setAdapter(adapter);

        // Carica i contenitori da Firebase
        loadContainers();

        // Pulsante per aggiungere un nuovo contenitore
        addContainerButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddContainerActivity.class);
            startActivity(intent);
        });

        // Path alla collection "containers" dell'utente specifico
        CollectionReference containersRef = db.collection("users").document(userId).collection("containers");

        // Aggiungi un listener per la collezione
        containersRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Firestore", "Errore nel listener: " + error.getMessage());
                return;
            }

            if (value != null) {
                for (DocumentChange docChange : value.getDocumentChanges()) {
                    switch (docChange.getType()) {
                        case ADDED:
                            Log.d("Firestore", "Nuovo container aggiunto: " + docChange.getDocument().getData());
                            break;
                        case MODIFIED:
                            Log.d("Firestore", "Container modificato: " + docChange.getDocument().getData());
                            break;
                        case REMOVED:
                            Log.d("Firestore", "Container rimosso: " + docChange.getDocument().getData());
                            break;
                    }
                }

                // Richiama il metodo per ricaricare i dati
                loadContainers();
            }
        });

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
                        Toast.makeText(getContext(), "Errore nel caricamento: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
