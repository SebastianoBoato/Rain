package com.example.rain.dashboard.container;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rain.R;
import com.example.rain.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UseWaterActivity extends AppCompatActivity {

    private String containerId1, containerId;
    private Button saveButton, resetWaterButton;
    private ImageButton backButton;
    EditText waterInput;
    private double currentQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_use_water);

        // Recupera il Container dall'Intent
        Container container = (Container) getIntent().getSerializableExtra("container");

        // Collega le TextView
        TextView nameTextView = findViewById(R.id.name);
        TextView totalVolumeTextView = findViewById(R.id.totalVolume);
        TextView currentQuantityTextView = findViewById(R.id.currentQuantity);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        containerId = container.getId();

        /*if (user == null) {
            Toast.makeText(this, "Errore: Utente non autenticato.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }*/
        // Riferimento al documento del container
        DocumentReference containerRef = db.collection("users")
                .document(user.getUid())
                .collection("containers")
                .document(containerId);

        // Aggiungi un listener in tempo reale al documento del container
        containerRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e("Firestore", "Errore nel listener: ", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                // Recupera i dati aggiornati del container
                Container updatedContainer = documentSnapshot.toObject(Container.class);
                if (updatedContainer != null) {
                    // Aggiorna i dati nell'interfaccia
                    nameTextView.setText(updatedContainer.getName());
                    totalVolumeTextView.setText("Volume totale: " + updatedContainer.getTotalVolume() + " L");
                    currentQuantity = updatedContainer.getCurrentVolume();
                    currentQuantityTextView.setText("Quantità attuale: " + String.format("%.2f", currentQuantity) + " L");
                }
            }
        });

        waterInput = findViewById(R.id.waterInput);

        // Inizializza il pulsante Torna indietro
        backButton = findViewById(R.id.comeBackButton);
        backButton.setOnClickListener(v -> finish()); // Chiude l'Activity e torna indietro

        // Popola le TextView con i dati del Container
        if (container != null) {
            // Ottieni il contenitore passato tramite Intent
            containerId = container.getId();  // Salva l'ID del contenitore per la modifica o eliminazione

            nameTextView.setText("Nome: " + container.getName());
            totalVolumeTextView.setText("Quantità totale: " + container.getTotalVolume() + " L");
            currentQuantity = container.getCurrentVolume(); //litri
            currentQuantityTextView.setText("Quantità attuale: " + String.format("%.2f", currentQuantity) +"L");
        }

        resetWaterButton = findViewById(R.id.resetWaterButton);
        resetWaterButton.setOnClickListener(view -> {
            double newQuantity = 0;
            updateWaterUsage(newQuantity, containerId);
            currentQuantityTextView.setText("Quantità attuale: " + String.format("%.2f", newQuantity) + "L");
            finish();
        });

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(view -> {
            String input = waterInput.getText().toString();

            if (!input.isEmpty()) {
                double waterUsed = Double.parseDouble(input); //L

                // Verifica se il valore è valido
                if (waterUsed < 0) {
                    Toast.makeText(this, "La quantità di acqua non può essere negativa", Toast.LENGTH_SHORT).show();
                } else if (waterUsed > currentQuantity) {
                    Toast.makeText(this, "Non puoi usare più acqua di quella disponibile nel contenitore", Toast.LENGTH_SHORT).show();
                } else {
                    // Procedi con l'aggiornamento della quantità di acqua utilizzata
                    double newQuantity = currentQuantity - waterUsed;
                    updateWaterUsage(newQuantity, containerId);
                    updateUsageHistory(waterUsed);
                    currentQuantityTextView.setText("Quantità attuale: " + String.format("%.2f", newQuantity) + "L");
                    finish();
                }
            } else {
                Toast.makeText(this, "Inserisci una quantità di acqua", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateWaterUsage(double newQuantity, String containerId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        /*if (user == null) {
            Toast.makeText(this, "Errore: Utente non autenticato.", Toast.LENGTH_SHORT).show();
            // Reindirizza alla schermata di login
            Intent loginIntent = new Intent(this, LoginActivity.class); // Sostituisci LoginActivity con il nome della tua attività di login
            startActivity(loginIntent);
            finish(); // Termina l'attività corrente per impedire il ritorno a questa schermata
            return;
        }*/

        if (containerId == null || containerId.isEmpty()) {
            Toast.makeText(this, "Errore: ID del contenitore non valido.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Percorso: users/{userId}/containers/{containerId}
        DocumentReference containerRef = db.collection("users")
                .document(user.getUid()) // Documento dell'utente autenticato
                .collection("containers")
                .document(containerId);


        // Aggiorna il campo 'currentVolume' nel documento del contenitore
        containerRef.update("currentVolume", newQuantity)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Contenitore aggiornato con successo", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Errore nell'aggiornamento del contenitore: ", e);
                    Toast.makeText(this, "Errore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUsageHistory(double waterUsed) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        if (user == null) {
            Toast.makeText(this, "Errore: Utente non autenticato.", Toast.LENGTH_SHORT).show();
            // Reindirizza alla schermata di login
            Intent loginIntent = new Intent(this, LoginActivity.class); // Sostituisci LoginActivity con il nome della tua attività di login
            startActivity(loginIntent);
            finish(); // Termina l'attività corrente per impedire il ritorno a questa schermata
            return;
        }

        // Ottieni la data attuale
        Date currentDate = new Date();

        // Definisci il formato di data desiderato (gg/MM/yyyy)
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        // Format della data
        String formattedDate = sdf.format(currentDate);


        Map<String, Object> usageHistory = new HashMap<>();
        usageHistory.put("date", formattedDate);
        usageHistory.put("usedVolume", waterUsed);

        db.collection("users")
                .document(userId)
                .collection("usage_history")
                .whereEqualTo("date", formattedDate) // Controlla se esiste già un documento con la stessa data
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // Documento con la data specificata trovato
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId(); // Ottieni l'ID del documento
                                db.collection("users")
                                        .document(userId)
                                        .collection("usage_history")
                                        .document(documentId)
                                        .update("usedVolume", FieldValue.increment((double) waterUsed)) // Incrementa il volume
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Volume aggiornato con successo", Toast.LENGTH_SHORT).show();
                                            finish(); // Torna al Fragment precedente
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Errore nell'aggiornamento: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            // Nessun documento con la data specificata, crea un nuovo documento
                            db.collection("users")
                                    .document(userId)
                                    .collection("usage_history")
                                    .add(usageHistory)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(this, "Documento aggiunto con successo", Toast.LENGTH_SHORT).show();
                                        finish(); // Torna al Fragment precedente
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Errore nell'aggiunta: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(this, "Errore nella query: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}