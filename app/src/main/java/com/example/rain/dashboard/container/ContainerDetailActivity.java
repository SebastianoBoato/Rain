package com.example.rain.dashboard.container;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rain.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import java.util.Locale;
import java.text.DecimalFormat;

public class ContainerDetailActivity extends AppCompatActivity {

    private TextView nameTextView, shapeTextView, param1TextView, param2TextView, heightTextView;
    private TextView roofAreaTextView, baseAreaTextView, totalVolumeTextView, currentVolumeTextView;
    private Button editButton, deleteButton;
    private String containerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container_detail);

        // Recupera il Container dall'Intent
        Container container = (Container) getIntent().getSerializableExtra("container");

        // Collega le TextView
        TextView nameTextView = findViewById(R.id.name);
        TextView shapeTextView = findViewById(R.id.shape);
        TextView param1TextView = findViewById(R.id.param1);
        TextView param2TextView = findViewById(R.id.param2);
        TextView heightTextView = findViewById(R.id.height);
        TextView roofAreaTextView = findViewById(R.id.roofArea);
        TextView areaTextView = findViewById(R.id.baseArea);
        TextView totalVolumeTextView = findViewById(R.id.totalVolume);
        TextView currentVolumeTextView = findViewById(R.id.currentVolume);

        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);

        // Inizializza il pulsante Torna indietro
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish()); // Chiude l'Activity e torna indietro

        // Ottieni il contenitore passato tramite Intent
        containerId = container.getId();  // Salva l'ID del contenitore per la modifica o eliminazione

        // Popola le TextView con i dati del Container
        if (container != null) {
            nameTextView.setText("Nome: " + container.getName());
            shapeTextView.setText("Forma: " + container.getShape());
            param1TextView.setText("Parametro 1: " + container.getParam1());
            param2TextView.setText(container.getParam2() != null ? "Parametro 2: " + container.getParam2() : "Parametro 2: Non applicabile");
            heightTextView.setText("Altezza: " + container.getHeight());
            roofAreaTextView.setText("Area del tetto: " + container.getRoofArea());
            areaTextView.setText("Area di base: " + container.getBaseArea());
            totalVolumeTextView.setText("Volume totale: " + container.getTotalVolume());
            currentVolumeTextView.setText("Volume attuale: " + container.getCurrentVolume());
        }

        // Pulsante per modificare
        editButton.setOnClickListener(v -> {
            showEditDialog(container);
        });

        // Pulsante per eliminare
        deleteButton.setOnClickListener(v -> {
            deleteContainer(containerId);
        });
    }

    // Metodo per visualizzare il dialog di modifica
    private void showEditDialog(Container container) {
        // Crea un AlertDialog con EditText per modificare i dati
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifica Contenitore");

        // Layout per il dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_container, null);
        builder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
        EditText volumeEditText = dialogView.findViewById(R.id.volumeEditText);

        // Imposta i valori iniziali
        nameEditText.setText(container.getName());
        volumeEditText.setText(String.format(Locale.getDefault(), "%.2f", container.getCurrentVolume()));

        builder.setPositiveButton("Salva", (dialog, which) -> {
            String newName = nameEditText.getText().toString();
            String newVolumeStr = volumeEditText.getText().toString();

            if (!newName.isEmpty() && !newVolumeStr.isEmpty()) {
                // Converte il volume in Double, se necessario
                Double newVolume = Double.parseDouble(newVolumeStr);
                // Aggiorna i dati nel database
                updateContainer(container.getId(), newName, newVolume);
                // Aggiorna i dati nell'interfaccia utente
                nameTextView.setText(newName);
                currentVolumeTextView.setText(String.format(Locale.getDefault(), "%.2f", newVolume));
            } else {
                Toast.makeText(ContainerDetailActivity.this, "Compila tutti i campi", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Annulla", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.show();
    }

    // Metodo per aggiornare il contenitore nel database Firebase
    private void updateContainer(String containerId, String newName, Double newVolume) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Ottieni il documento del contenitore
        DocumentReference containerRef = db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("containers")
                .document(containerId);

        // Crea una mappa dei nuovi dati da aggiornare
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("name", newName);
        updatedData.put("currentVolume", newVolume);

        // Aggiorna i dati nel database
        containerRef.update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ContainerDetailActivity.this, "Contenitore aggiornato", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ContainerDetailActivity.this, "Errore nell'aggiornamento: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Metodo per eliminare il contenitore
    private void deleteContainer(String containerId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("containers")
                .document(containerId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Elimina con successo, puoi tornare indietro o fare altre operazioni
                    Toast.makeText(this, "Contenitore eliminato", Toast.LENGTH_SHORT).show();
                    finish();  // Torna alla schermata precedente
                })
                .addOnFailureListener(e -> {
                    // Gestisci errore
                    Toast.makeText(this, "Errore nell'eliminazione: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
