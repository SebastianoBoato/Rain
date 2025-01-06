package com.example.rain.dashboard.container;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.firestore.FirebaseFirestore;

public class UseWaterActivity extends AppCompatActivity {

    private String containerId;
    private Button backButton, saveButton;
    EditText waterInput;

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

        waterInput = findViewById(R.id.waterInput);

        // Inizializza il pulsante Torna indietro
        backButton = findViewById(R.id.comeBackButton);
        backButton.setOnClickListener(v -> finish()); // Chiude l'Activity e torna indietro

        // Popola le TextView con i dati del Container
        if (container != null) {
            // Ottieni il contenitore passato tramite Intent
            containerId = container.getId();  // Salva l'ID del contenitore per la modifica o eliminazione

            nameTextView.setText("Nome: " + container.getName());
            totalVolumeTextView.setText("Quantità totale: " + container.getTotalVolume()/1000 + " L");
            currentQuantityTextView.setText("Quantità attuale: " + container.getCurrentVolume()/1000 + " L");
        }

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(view -> {
            String input = waterInput.getText().toString();

            if (!input.isEmpty()) {
                double waterUsed = Double.parseDouble(input);

                // Verifica se il valore è valido
                if (waterUsed < 0) {
                    Toast.makeText(this, "La quantità di acqua non può essere negativa", Toast.LENGTH_SHORT).show();
                } else if (waterUsed > (container.getCurrentVolume()/1000)) {
                    Toast.makeText(this, "Non puoi usare più acqua di quella disponibile nel contenitore", Toast.LENGTH_SHORT).show();
                } else {
                    // Procedi con l'aggiornamento della quantità di acqua utilizzata
                    double newQuantity = container.getCurrentVolume() - waterUsed*1000;
                    updateWaterUsage(newQuantity, containerId);
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

        // Percorso: users/{userId}/containers/{containerId}
        DocumentReference containerRef = db.collection("users")
                .document(user.getUid()) // Documento dell'utente autenticato
                .collection("containers")
                .document(containerId);


        // Aggiorna il campo 'name' nel documento del contenitore
        containerRef.update("currentVolume", newQuantity)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Contenitore aggiornato con successo", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Errore nell'aggiornamento del contenitore: ", e);
                    Toast.makeText(this, "Errore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}