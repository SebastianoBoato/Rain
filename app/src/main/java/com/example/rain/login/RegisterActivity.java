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
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText emailField, firstNameField, lastNameField, passwordField, locationField, cityField, addressField, civicField, capField, provinceField;
    private CheckBox nearbyCheckbox;
    private Button registerButton, goToLoginButton;
    private FusedLocationProviderClient fusedLocationClient;

    private FirebaseAuth auth; // Firebase Authentication
    private FirebaseFirestore db; // Firebase Firestore

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1; // Codice di richiesta permesso
    private static final String API_KEY = "f9b1fdde170b4dbb9585f0d558b4c2db"; // Sostituisci con la tua API Key OpenCage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inizializzazione dei campi
        emailField = findViewById(R.id.email);
        firstNameField = findViewById(R.id.first_name);
        lastNameField = findViewById(R.id.last_name);
        passwordField = findViewById(R.id.password);
        nearbyCheckbox = findViewById(R.id.nearby_checkbox);
        provinceField = findViewById(R.id.province_field);
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
            findViewById(R.id.province_layout).setVisibility(View.GONE);
            findViewById(R.id.location_layout).setVisibility(View.GONE);
            findViewById(R.id.address_layout).setVisibility(View.GONE);
            findViewById(R.id.civic_layout).setVisibility(View.GONE);
            findViewById(R.id.cap_layout).setVisibility(View.GONE);
        } else {
            // Mostra i campi di posizione manuale
            findViewById(R.id.province_layout).setVisibility(View.VISIBLE);
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
        boolean isNearby = nearbyCheckbox.isChecked();
        String province = provinceField.getText().toString();
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
            if (province.isEmpty()) {
                provinceField.setError("Inserisci la provincia");
                hasError = true;
            }

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
                                            getAddressFromCoordinates(email, firstName, lastName, location.getLatitude(), location.getLongitude());
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Impossibile ottenere la posizione", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // Usa i dati manuali per la posizione
                            Map<String, String> locationMap = new HashMap<>();
                            locationMap.put("province", province);
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

    private void getAddressFromCoordinates(String email, String firstName, String lastName, double latitude, double longitude) {
        // Configura Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.opencagedata.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OpenCageService service = retrofit.create(OpenCageService.class);
        String coordinates = latitude + "," + longitude;

        // Effettua la chiamata API
        Call<OpenCageResponse> call = service.reverseGeocode(coordinates, API_KEY);
        call.enqueue(new retrofit2.Callback<OpenCageResponse>() {
            @Override
            public void onResponse(Call<OpenCageResponse> call, retrofit2.Response<OpenCageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OpenCageResponse.Result result = response.body().results.get(0);

                    // Estrai i dettagli dell'indirizzo
                    String city = result.components.city;
                    String road = result.components.road;
                    String houseNumber = result.components.houseNumber;
                    String postcode = result.components.postcode;
                    String province = result.components.state; // Estrai la provincia

                    // Salva nel database Firebase
                    saveUserToDatabase(email, firstName, lastName, city, road, houseNumber, postcode, province);
                } else {
                    Toast.makeText(RegisterActivity.this, "Errore nel recupero dell'indirizzo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OpenCageResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Errore di rete: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserToDatabase(String email, String firstName, String lastName, String city, String road, String houseNumber, String postcode, String province) {
        String userId = auth.getCurrentUser().getUid();

        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);

        Map<String, String> locationMap = new HashMap<>();
        locationMap.put("city", city);
        locationMap.put("address", road);
        locationMap.put("civic", houseNumber);
        locationMap.put("cap", postcode);
        locationMap.put("province", province);

        userData.put("location", locationMap);

        db.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> Toast.makeText(RegisterActivity.this, "Registrazione completata!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Errore durante il salvataggio dei dati.", Toast.LENGTH_SHORT).show());
    }

    public interface OpenCageService {
        @GET("geocode/v1/json")
        Call<OpenCageResponse> reverseGeocode(
                @Query("q") String coordinates,
                @Query("key") String apiKey
        );
    }

    public static class OpenCageResponse {
        @SerializedName("results")
        public List<Result> results;

        public static class Result {
            @SerializedName("formatted")
            public String formatted;

            @SerializedName("components")
            public Components components;
        }

        public static class Components {
            @SerializedName("city")
            public String city;

            @SerializedName("road")
            public String road;

            @SerializedName("house_number")
            public String houseNumber;

            @SerializedName("postcode")
            public String postcode;

            @SerializedName("state")
            public String state; // Questo rappresenta la provincia
        }
    }
}