package com.example.rain.dashboard.container;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rain.R;
import com.example.rain.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
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

        // Collega le TextView
        TextView nameTextView = findViewById(R.id.name);
        TextView shapeTextView = findViewById(R.id.shape);
        TextView param1TextView = findViewById(R.id.param1);
        TextView param2TextView = findViewById(R.id.param2);
        TextView heightTextView = findViewById(R.id.height);
        TextView roofAreaTextView = findViewById(R.id.roofArea);
        TextView areaTextView = findViewById(R.id.baseArea);
        TextView totalVolumeTextView = findViewById(R.id.totalVolume);
        TextView currentQuantityTextView = findViewById(R.id.currentQuantity);

        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);

        // Inizializza il pulsante Torna indietro
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish()); // Chiude l'Activity e torna indietro

        // Recupera il Container dall'Intent
        Container container = (Container) getIntent().getSerializableExtra("container");
        containerId = container.getId();  // Salva l'ID del contenitore per la modifica o eliminazione

        // Popola le TextView con i dati del Container
        if (container != null) {
            nameTextView.setText(container.getName());
            shapeTextView.setText("Forma: " + container.getShape());
            param1TextView.setText("Parametro 1: " + container.getParam1() + " cm");
            param2TextView.setText(container.getParam2() != null ? "Parametro 2: " + container.getParam2() + " cm" : "Parametro 2: Non esistente");
            heightTextView.setText("Altezza: " + container.getHeight() + " cm");
            if(container.getRoofArea() != null){
                roofAreaTextView.setVisibility(View.VISIBLE);
                roofAreaTextView.setText("Area del tetto: " + container.getRoofArea() + " m");
            }
            areaTextView.setText("Area di base: " + container.getBaseArea() + " cm");
            totalVolumeTextView.setText("Volume totale: " + container.getTotalVolume() + " L");
            currentQuantityTextView.setText("Quantità attuale: " + container.getCurrentVolume() + " L");
        }

        // Imposta un listener in tempo reale sul documento del container
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        /*if (user == null) {
            Toast.makeText(this, "Errore: Utente non autenticato.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }*/

        // Percorso al documento del container
        DocumentReference containerRef = db.collection("users")
                .document(user.getUid())
                .collection("containers")
                .document(containerId);

        // Aggiungi un listener in tempo reale
        containerRef.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                Log.e("Firestore", "Errore nel listener: " + error.getMessage());
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                // Aggiorna i dati dell'interfaccia
                Container updatedContainer = snapshot.toObject(Container.class);
                if (updatedContainer != null) {
                    nameTextView.setText(updatedContainer.getName());
                    shapeTextView.setText("Forma: " + updatedContainer.getShape());
                    param1TextView.setText("Parametro 1: " + updatedContainer.getParam1() + " cm");
                    param2TextView.setText(updatedContainer.getParam2() != null ? "Parametro 2: " + updatedContainer.getParam2() + " cm" : "Parametro 2: Non esistente");
                    heightTextView.setText("Altezza: " + updatedContainer.getHeight() + " cm");
                    roofAreaTextView.setText("Area del tetto: " + updatedContainer.getRoofArea() + " m");
                    areaTextView.setText("Area di base: " + updatedContainer.getBaseArea() + " cm");
                    totalVolumeTextView.setText("Volume totale: " + updatedContainer.getTotalVolume() + " L");
                    currentQuantityTextView.setText(String.format("Quantità attuale: %.2f", updatedContainer.getCurrentVolume()) + "L");
                }
            }
        });

        // Inizializza il pulsante per utilizzo acqua
        Button useWaterButton = findViewById(R.id.useWaterButton);
        useWaterButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, UseWaterActivity.class);
            intent.putExtra("container", container);  // Passa il contenitore come Serializable
            this.startActivity(intent);
        });

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
        builder.setTitle("Modifica Nome Contenitore");

        // Layout per il dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_container, null);
        builder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.nameEditText);

        // Imposta i valori iniziali
        nameEditText.setText(container.getName());

        builder.setPositiveButton("Salva", (dialog, which) -> {
            String newName = nameEditText.getText().toString();
            if (!newName.isEmpty()) {
                // Aggiorna i dati nel database
                updateContainer(container.getId(), newName);
                dialog.dismiss();
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
    private void updateContainer(String containerId, String newName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Errore: Utente non autenticato.", Toast.LENGTH_SHORT).show();
            // Reindirizza alla schermata di login
            Intent loginIntent = new Intent(this, LoginActivity.class); // Sostituisci LoginActivity con il nome della tua attività di login
            startActivity(loginIntent);
            finish(); // Termina l'attività corrente per impedire il ritorno a questa schermata
            return;
        }

        if (containerId == null || containerId.isEmpty()) {
            Toast.makeText(this, "Errore: ID del contenitore non valido.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newName == null || newName.isEmpty()) {
            Toast.makeText(this, "Errore: Nome del contenitore non valido.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Percorso: users/{userId}/containers/{containerId}
        DocumentReference containerRef = db.collection("users")
                .document(user.getUid()) // Documento dell'utente autenticato
                .collection("containers")
                .document(containerId);

        // Aggiorna il campo 'name' nel documento del contenitore
        containerRef.update("name", newName)
                .addOnSuccessListener(aVoid -> {
                    // Aggiorna correttamente la TextView
                    nameTextView.setText(newName); // Assicurati che la TextView venga aggiornata correttamente
                    Toast.makeText(this, "Contenitore aggiornato con successo", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Errore nell'aggiornamento del contenitore: ", e);
                    Toast.makeText(this, "Errore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
