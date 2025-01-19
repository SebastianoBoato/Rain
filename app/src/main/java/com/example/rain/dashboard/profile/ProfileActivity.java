package com.example.rain.dashboard.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rain.R;
import com.example.rain.dashboard.MainActivity;
import com.example.rain.databinding.ActivityProfileBinding;
import com.example.rain.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private ActivityProfileBinding binding;

    private boolean isEditing = false; // Per tenere traccia dello stato di modifica

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Abilita EdgeToEdge (se necessario)
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // Controlla se l'utente è autenticato
        if (user != null) {
            String userId = user.getUid();
            loadUserData(userId);
        } else {
            binding.userName.setText("Utente non autenticato");
        }

        setupButtons();
    }

    private void setupButtons() {
        // Bottone per l'informativa privacy
        binding.privacyPolicyButton.setOnClickListener(v -> showPrivacyDialog());

        // Bottone per modificare i dati
        binding.editDataButton.setOnClickListener(v -> toggleEditMode());

        // Bottone per tornare alla home
        binding.backToHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.putExtra("navigate_to", "HomeFragment");
            startActivity(intent);
            finish(); // Chiudi l'activity attuale
        });

        // Bottone per il logout
        binding.logoutButton.setOnClickListener(v -> {
            auth.signOut(); // Effettua il logout
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Chiudi l'activity attuale
        });

        // Bottone per eliminare l'account
        binding.deleteAccountButton.setOnClickListener(v -> showDeleteAccountConfirmationDialog());
    }

    private void loadUserData(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");
                        String email = documentSnapshot.getString("email");
                        String phone = documentSnapshot.getString("phone");

                        if (firstName != null && lastName != null) {
                            binding.userName.setText(firstName + " " + lastName);
                        }
                        if (email != null) {
                            binding.emailValue.setText(email);
                        }
                        if (phone != null) {
                            binding.phoneValue.setText(phone);
                        }

                        if (documentSnapshot.contains("location")) {
                            Map<String, Object> location = (Map<String, Object>) documentSnapshot.get("location");
                            if (location != null) {
                                String address = (String) location.get("address");
                                String city = (String) location.get("city");
                                String province = (String) location.get("province");
                                String postalCode = (String) location.get("postalCode");
                                Double latitude = (Double) location.get("latitude");
                                Double longitude = (Double) location.get("longitude");

                                binding.addressValue.setText(String.format("Indirizzo: %s", address != null ? address : "N/A"));
                                binding.cityValue.setText(String.format("Città: %s", city != null ? city : "N/A"));
                                binding.provinceValue.setText(String.format("Provincia: %s", province != null ? province : "N/A"));
                                binding.postalCodeValue.setText(String.format("Codice postale: %s", postalCode != null ? postalCode : "N/A"));
                                binding.coordinatesValue.setText((latitude != null && longitude != null) ?
                                        "Lat: " + latitude + ", Lon: " + longitude : "N/A");
                            }
                        }
                    } else {
                        binding.userName.setText("Dati non trovati");
                    }
                })
                .addOnFailureListener(e -> binding.userName.setText("Errore nel caricamento dati"));
    }

    private void showPrivacyDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Informativa Privacy")
                .setMessage("Questa è una breve informativa sulla privacy. Assicuriamo che i tuoi dati saranno trattati nel rispetto delle normative vigenti.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void toggleEditMode() {
        if (!isEditing) {
            // Mostra i campi di modifica
            binding.emailEditText.setVisibility(View.VISIBLE);
            binding.phoneEditText.setVisibility(View.VISIBLE);
            binding.addressEditText.setVisibility(View.VISIBLE);
            binding.cityEditText.setVisibility(View.VISIBLE);
            binding.provinceEditText.setVisibility(View.VISIBLE);
            binding.postalCodeEditText.setVisibility(View.VISIBLE);
            binding.latitudeEditText.setVisibility(View.VISIBLE);
            binding.longitudeEditText.setVisibility(View.VISIBLE);

            // Cambia il testo del pulsante
            binding.editDataButton.setText("Salva Dati");
            isEditing = true;
        } else {
            // Salva i dati nel database
            saveUserData();

            // Nascondi i campi di modifica
            binding.emailEditText.setVisibility(View.GONE);
            binding.phoneEditText.setVisibility(View.GONE);
            binding.addressEditText.setVisibility(View.GONE);
            binding.cityEditText.setVisibility(View.GONE);
            binding.provinceEditText.setVisibility(View.GONE);
            binding.postalCodeEditText.setVisibility(View.GONE);
            binding.latitudeEditText.setVisibility(View.GONE);
            binding.longitudeEditText.setVisibility(View.GONE);

            // Cambia il testo del pulsante
            binding.editDataButton.setText("Modifica Dati");
            isEditing = false;
        }
    }

    private void saveUserData() {
        String email = binding.emailEditText.getText().toString().trim();
        String phone = binding.phoneEditText.getText().toString().trim();
        String address = binding.addressEditText.getText().toString().trim();
        String city = binding.cityEditText.getText().toString().trim();
        String province = binding.provinceEditText.getText().toString().trim();
        String postalCode = binding.postalCodeEditText.getText().toString().trim();
        String latitudeStr = binding.latitudeEditText.getText().toString().trim();
        String longitudeStr = binding.longitudeEditText.getText().toString().trim();

        Double latitude = latitudeStr.isEmpty() ? null : Double.parseDouble(latitudeStr);
        Double longitude = longitudeStr.isEmpty() ? null : Double.parseDouble(longitudeStr);

        Map<String, Object> updates = new HashMap<>();
        if (!email.isEmpty()) updates.put("email", email);
        if (!phone.isEmpty()) updates.put("phone", phone);

        Map<String, Object> location = new HashMap<>();
        if (!address.isEmpty()) location.put("address", address);
        if (!city.isEmpty()) location.put("city", city);
        if (!province.isEmpty()) location.put("province", province);
        if (!postalCode.isEmpty()) location.put("postalCode", postalCode);
        if (latitude != null) location.put("latitude", latitude);
        if (longitude != null) location.put("longitude", longitude);

        if (!location.isEmpty()) updates.put("location", location);

        if (!updates.isEmpty()) {
            db.collection("users").document(user.getUid())
                    .update(updates)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Dati aggiornati con successo!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Errore durante l'aggiornamento: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Nessun dato modificato", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteAccountConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Elimina Account")
                .setMessage("Sei sicuro di voler eliminare il tuo account? Questa azione è irreversibile e tutti i tuoi dati verranno cancellati definitivamente.")
                .setPositiveButton("Elimina", (dialog, which) -> showPasswordConfirmationDialog())
                .setNegativeButton("Annulla", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showPasswordConfirmationDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_password_confirmation, null);
        android.widget.EditText passwordEditText = dialogView.findViewById(R.id.passwordEditText);

        new AlertDialog.Builder(this)
                .setTitle("Conferma Password")
                .setView(dialogView)
                .setPositiveButton("Conferma", (dialog, which) -> {
                    String password = passwordEditText.getText().toString();
                    if (!password.isEmpty()) {
                        reAuthenticateAndDelete(password);
                    } else {
                        Toast.makeText(this, "Inserisci la password", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annulla", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void reAuthenticateAndDelete(String password) {
        // Mostra il dialog di progresso
        AlertDialog progressDialog = new AlertDialog.Builder(this)
                .setMessage("Eliminazione account in corso...")
                .setCancelable(false)
                .show();

        // Riautenticazione dell'utente
        auth.signInWithEmailAndPassword(user.getEmail(), password)
                .addOnSuccessListener(authResult -> {
                    // Procedi con l'eliminazione
                    deleteUserData(progressDialog);
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Password non corretta", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteUserData(AlertDialog progressDialog) {
        String userId = user.getUid();

        // Prima elimina i dati da Firestore
        db.collection("users").document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Dopo aver eliminato i dati, elimina l'account Firebase
                    user.delete()
                            .addOnSuccessListener(aVoid1 -> {
                                progressDialog.dismiss();
                                Toast.makeText(ProfileActivity.this,
                                        "Account eliminato con successo", Toast.LENGTH_SHORT).show();

                                // Reindirizza alla schermata di login
                                auth.signOut();
                                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(ProfileActivity.this,
                                        "Errore durante l'eliminazione dell'account: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this,
                            "Errore durante l'eliminazione dei dati: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}