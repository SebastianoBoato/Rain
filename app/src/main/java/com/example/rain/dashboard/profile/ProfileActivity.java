package com.example.rain.dashboard.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rain.R;
import com.example.rain.dashboard.MainActivity;
import com.example.rain.dashboard.container.Container;
import com.example.rain.databinding.ActivityProfileBinding;
import com.example.rain.login.LoginActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

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

        // Inizializza il pulsante Torna indietro
        binding.comeBackButton.setOnClickListener(v -> finish()); // Chiude l'Activity e torna indietro

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user == null){
            Toast.makeText(this, "Errore: Utente non autenticato.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        db = FirebaseFirestore.getInstance();
        String userId = user.getUid();
        loadUserData(userId);

        setupButtons();

        // Percorso al documento dell'utente
        DocumentReference userDataRef = db.collection("users")
                .document(userId);

        // Aggiungi un listener in tempo reale per aggiornare dati utente
        userDataRef.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                Log.e("Firestore", "Errore nel listener: " + error.getMessage());
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                // Aggiorna i dati dell'interfaccia
                String firstName = snapshot.getString("firstName");
                String lastName = snapshot.getString("lastName");
                String email = snapshot.getString("email");

                if (firstName != null && lastName != null) {
                    binding.userName.setText(firstName + " " + lastName);
                    binding.nameValue.setText("Nome: " + firstName);
                    binding.surnameValue.setText("Cognome: " + lastName);
                }
                if (email != null) {
                    binding.emailValue.setText("Email: " + email);
                }

                if (snapshot.contains("location")) {
                    Map<String, Object> location = (Map<String, Object>) snapshot.get("location");
                    if (location != null) {
                        String address = (String) location.get("address");
                        String city = (String) location.get("city");
                        String province = (String) location.get("province");
                        String postalCode = (String) location.get("cap");
                        String civic = (String) location.get("civic");

                        binding.addressValue.setText(String.format("Indirizzo: %s", address!=null && civic!=null ? address+" "+civic : "N/A"));
                        binding.cityValue.setText(String.format("Città: %s", city != null ? city : "N/A"));
                        binding.provinceValue.setText(String.format("Provincia: %s", province != null ? province : "N/A"));
                        binding.postalCodeValue.setText(String.format("Codice postale: %s", postalCode != null ? postalCode : "N/A"));
                    }
                }
            }
        });

    }

    private void setupButtons() {
        // Bottone per l'informativa privacy
        binding.privacyPolicyButton.setOnClickListener(v -> showPrivacyDialog());

        // Bottone per modificare i dati
        binding.editDataButton.setOnClickListener(v -> toggleEditMode());

        // Bottone per il logout
        binding.logoutButton.setOnClickListener(v -> {
            auth.signOut(); // Effettua il logout
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Chiudi l'activity attuale
        });

        // Bottone per eliminare l'account
        binding.deleteAccountButton.setOnClickListener(v -> showDeleteAccountConfirmationDialog());

        //Bottone per cambio password
        binding.editPasswordButton.setOnClickListener(v -> showChangePasswordDialog());
    }

    private void loadUserData(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");
                        String email = documentSnapshot.getString("email");

                        if (firstName != null && lastName != null) {
                            binding.userName.setText(firstName + " " + lastName);
                            binding.nameValue.setText("Nome: " + firstName);
                            binding.surnameValue.setText("Cognome: " + lastName);
                        }
                        if (email != null) {
                            binding.emailValue.setText("Email: " + email);
                        }

                        if (documentSnapshot.contains("location")) {
                            Map<String, Object> location = (Map<String, Object>) documentSnapshot.get("location");
                            if (location != null) {
                                String address = (String) location.get("address");
                                String city = (String) location.get("city");
                                String province = (String) location.get("province");
                                String postalCode = (String) location.get("cap");
                                String civic = (String) location.get("civic");

                                binding.addressValue.setText(String.format("Indirizzo: %s", address!=null && civic!=null ? address+" "+civic : "N/A"));
                                binding.cityValue.setText(String.format("Città: %s", city != null ? city : "N/A"));
                                binding.provinceValue.setText(String.format("Provincia: %s", province != null ? province : "N/A"));
                                binding.postalCodeValue.setText(String.format("Codice postale: %s", postalCode != null ? postalCode : "N/A"));
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
                .setTitle("Informativa sulla Privacy")
                .setMessage("La tua privacy è importante per noi. I dati personali raccolti dalla nostra applicazione vengono utilizzati esclusivamente per la gestione del tuo account e per offrirti un servizio efficiente nella raccolta dell’acqua piovana.\n" +
                        "\n" +
                        "Le informazioni vengono archiviate e protette tramite Firebase, un sistema sicuro di Google che garantisce la crittografia e il rispetto delle normative sulla privacy. Non condividiamo i tuoi dati con terze parti, salvo obblighi di legge.\n" +
                        "\n" +
                        "Utilizzando l’app, accetti il trattamento delle tue informazioni come descritto.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void toggleEditMode() {
        if (!isEditing) {
            // Mostra i campi di modifica
            binding.nameValue.setVisibility(View.VISIBLE);
            binding.surnameValue.setVisibility(View.VISIBLE);
            binding.nameEditText.setVisibility(View.VISIBLE);
            binding.surnameEditText.setVisibility(View.VISIBLE);
            binding.addressEditText.setVisibility(View.VISIBLE);
            binding.cityEditText.setVisibility(View.VISIBLE);
            binding.provinceEditText.setVisibility(View.VISIBLE);
            binding.postalCodeEditText.setVisibility(View.VISIBLE);

            // Cambia il testo del pulsante
            binding.editDataButton.setText("Salva Dati");
            isEditing = true;
        } else {
            // Salva i dati nel database
            saveUserData();

            // Nascondi i campi di modifica
            binding.nameValue.setVisibility(View.GONE);
            binding.surnameValue.setVisibility(View.GONE);
            binding.nameEditText.setVisibility(View.GONE);
            binding.surnameEditText.setVisibility(View.GONE);
            binding.addressEditText.setVisibility(View.GONE);
            binding.cityEditText.setVisibility(View.GONE);
            binding.provinceEditText.setVisibility(View.GONE);
            binding.postalCodeEditText.setVisibility(View.GONE);

            // Cambia il testo del pulsante
            binding.editDataButton.setText("Modifica Dati");
            isEditing = false;
        }
    }

    private void saveUserData() {
        String name = binding.nameEditText.getText().toString().trim();
        String surname = binding.surnameEditText.getText().toString().trim();
        String address = binding.addressEditText.getText().toString().trim();
        String city = binding.cityEditText.getText().toString().trim();
        String province = binding.provinceEditText.getText().toString().trim();
        String postalCode = binding.postalCodeEditText.getText().toString().trim();

        Map<String, Object> updates = new HashMap<>();
        if (!name.isEmpty()) updates.put("firstName", name);
        if (!surname.isEmpty()) updates.put("lastName", surname);

        Map<String, Object> location = new HashMap<>();
        if (!address.isEmpty()) location.put("address", address);
        if (!city.isEmpty()) location.put("city", city);
        if (!province.isEmpty()) location.put("province", province);
        if (!postalCode.isEmpty()) location.put("cap", postalCode);

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

    private void showChangePasswordDialog() {
        // Crea il layout del dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        EditText currentPasswordEditText = dialogView.findViewById(R.id.currentPasswordEditText);
        EditText newPasswordEditText = dialogView.findViewById(R.id.newPasswordEditText);
        EditText confirmPasswordEditText = dialogView.findViewById(R.id.confirmPasswordEditText);

        // Mostra il dialog
        new AlertDialog.Builder(this)
                .setTitle("Modifica Password")
                .setView(dialogView)
                .setPositiveButton("Conferma", (dialog, which) -> {
                    String currentPassword = currentPasswordEditText.getText().toString().trim();
                    String newPassword = newPasswordEditText.getText().toString().trim();
                    String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                    if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                        Toast.makeText(ProfileActivity.this, "Tutti i campi devono essere compilati", Toast.LENGTH_SHORT).show();
                    } else if (!newPassword.equals(confirmPassword)) {
                        Toast.makeText(ProfileActivity.this, "Le password non corrispondono", Toast.LENGTH_SHORT).show();
                    } else {
                        reauthenticateAndChangePassword(currentPassword, newPassword);
                    }
                })
                .setNegativeButton("Annulla", (dialog, which) -> dialog.dismiss())
                .show();
    }


    private void reauthenticateAndChangePassword(String currentPassword, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

            user.reauthenticate(credential)
                    .addOnSuccessListener(aVoid -> {
                        // Password corretta, procedi con la modifica della password
                        changePassword(newPassword);
                    })
                    .addOnFailureListener(e -> {
                        // Se la ri-autenticazione fallisce (password errata)
                        Toast.makeText(ProfileActivity.this, "La password corrente è errata", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(ProfileActivity.this, "Utente non autenticato", Toast.LENGTH_SHORT).show();
        }
    }

    private void changePassword(String newPassword) {
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            user.updatePassword(newPassword)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ProfileActivity.this, "Password modificata con successo!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ProfileActivity.this, "Errore durante la modifica della password: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(ProfileActivity.this, "Utente non autenticato", Toast.LENGTH_SHORT).show();
        }
    }
}