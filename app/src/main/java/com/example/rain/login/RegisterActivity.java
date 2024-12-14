package com.example.rain.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rain.R;

import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText emailField, firstNameField, lastNameField, passwordField, locationField, cityField, addressField, civicField, capField;
    private Switch notificationPreference;
    private CheckBox nearbyCheckbox;
    private Button registerButton, goToLoginButton;
    private FusedLocationProviderClient fusedLocationClient;

    private FirebaseAuth auth; // Firebase Authentication
    private FirebaseFirestore db; // Firebase Firestore

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1; // Codice di richiesta permesso

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inizializzazione dei campi
        emailField = findViewById(R.id.email);
        firstNameField = findViewById(R.id.first_name);
        lastNameField = findViewById(R.id.last_name);
        passwordField = findViewById(R.id.password);
        notificationPreference = findViewById(R.id.notification_preference);
        nearbyCheckbox = findViewById(R.id.nearby_checkbox);
        cityField = findViewById(R.id.city_field);
        addressField = findViewById(R.id.address_field);
        civicField = findViewById(R.id.civic_field);
        capField = findViewById(R.id.cap_field);
        registerButton = findViewById(R.id.register_button);
        goToLoginButton = findViewById(R.id.go_to_login_button);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Inizializza Firebase Authentication e Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Imposta CheckBox come attivo di default
        nearbyCheckbox.setChecked(true);  // Imposta come predefinito
        toggleLocationFieldsVisibility(nearbyCheckbox.isChecked());

        // Aggiungi listener per il CheckBox
        nearbyCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            toggleLocationFieldsVisibility(isChecked);
        });

        goToLoginButton.setOnClickListener(v -> {
            // Torna alla schermata di login
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        registerButton.setOnClickListener(v -> registerUser());
    }

    //Serve per far vedere o no i campi della posizione
    private void toggleLocationFieldsVisibility(boolean isNearby) {
        if (isNearby) {
            // Nascondi i campi di posizione manuale
            findViewById(R.id.location_layout).setVisibility(View.GONE);
            findViewById(R.id.address_layout).setVisibility(View.GONE);
            findViewById(R.id.civic_layout).setVisibility(View.GONE);
            findViewById(R.id.cap_layout).setVisibility(View.GONE);
        } else {
            // Mostra i campi di posizione manuale
            findViewById(R.id.location_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.address_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.civic_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.cap_layout).setVisibility(View.VISIBLE);
        }
    }

    private void registerUser() {
        String email = emailField.getText().toString();
        String firstName = firstNameField.getText().toString();
        String lastName = lastNameField.getText().toString();
        String password = passwordField.getText().toString();
        boolean notificationsEnabled = notificationPreference.isChecked();
        boolean isNearby = nearbyCheckbox.isChecked();
        String city = cityField.getText().toString();
        String address = addressField.getText().toString();
        String civic = civicField.getText().toString();
        String cap = capField.getText().toString();

        boolean hasError = false;

        // Verifica dei campi obbligatori
        if (email.isEmpty()) {
            emailField.setError("Inserisci l'email");
            hasError = true;
        }

        if (firstName.isEmpty()) {
            firstNameField.setError("Inserisci il nome");
            hasError = true;
        }

        if (lastName.isEmpty()) {
            lastNameField.setError("Inserisci il cognome");
            hasError = true;
        }

        if (password.isEmpty()) {
            passwordField.setError("Inserisci la password");
            hasError = true;
        }

        // Verifica campi posizione se la checkbox non è selezionata
        if (!isNearby) {
            if (city.isEmpty()) {
                cityField.setError("Inserisci la città");
                hasError = true;
            }

            if (address.isEmpty()) {
                addressField.setError("Inserisci l'indirizzo");
                hasError = true;
            }

            if(civic.isEmpty()) {
                civicField.setError("Inserisci il civico");
                hasError = true;
            }

            if (cap.isEmpty()) {
                capField.setError("Inserisci il CAP");
                hasError = true;
            }
        }

        // Se ci sono errori, interrompi il metodo
        if (hasError) {
            return;
        }

        // Crea l'utente in Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Utente creato con successo
                        String userId = auth.getCurrentUser().getUid();
                        // Crea l'oggetto userData
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("email", email);
                        userData.put("firstName", firstName);
                        userData.put("lastName", lastName);
                        //userData.put("password", password);
                        userData.put("notificationsEnabled", notificationsEnabled);

                        // Salva i dati aggiuntivi in Firestore
                        if (isNearby) {
                            // Ottieni la posizione GPS e salva
                            if (ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                    ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // Richiesta dei permessi di geolocalizzazione
                                ActivityCompat.requestPermissions(RegisterActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                        LOCATION_PERMISSION_REQUEST_CODE);
                                return;
                            }
                            fusedLocationClient.getLastLocation()
                                    .addOnSuccessListener(location -> {
                                        if (location != null) {
                                            // Aggiungi la posizione GPS a userData
                                            Map<String, Double> locationMap = new HashMap<>();
                                            locationMap.put("latitude", location.getLatitude());
                                            locationMap.put("longitude", location.getLongitude());
                                            userData.put("location", locationMap);

                                            // Salva i dati con la posizione GPS
                                            db.collection("users").document(userId)
                                                    .set(userData)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Toast.makeText(RegisterActivity.this, "Registrazione completata!", Toast.LENGTH_SHORT).show();
                                                        //finish(); // Torna alla schermata precedente
                                                    })
                                                    .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Errore durante il salvataggio dei dati.", Toast.LENGTH_SHORT).show());
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Impossibile ottenere la posizione", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // Usa i dati manuali per la posizione
                            Map<String, String> locationMap = new HashMap<>();
                            locationMap.put("city", city);
                            locationMap.put("address", address);
                            locationMap.put("civic", civic);
                            locationMap.put("cap", cap);
                            userData.put("location", locationMap);

                            // Salva i dati con la posizione manuale
                            db.collection("users").document(userId)
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(RegisterActivity.this, "Registrazione completata!", Toast.LENGTH_SHORT).show();
                                        //finish(); // Torna alla schermata precedente
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Errore durante il salvataggio dei dati.", Toast.LENGTH_SHORT).show());
                        }
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();  // Ferma la RegisterActivity
                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseAuthUserCollisionException) {
                            // Errore specifico: email già in uso
                            Toast.makeText(RegisterActivity.this, "L'email è già registrata. Per favore, usa un'altra email.", Toast.LENGTH_SHORT).show();
                            // Mostra il pulsante per tornare al login
                            goToLoginButton.setVisibility(View.VISIBLE);
                        } else {
                            // Altro tipo di errore
                            Toast.makeText(RegisterActivity.this, "Registrazione fallita: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}