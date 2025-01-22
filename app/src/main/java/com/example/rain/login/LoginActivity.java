package com.example.rain.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.rain.onboarding.FirstOnboardingActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextView registerLink, onboardingLink;

    private EditText emailField, passwordField;
    private Button loginButton;

    private FirebaseAuth auth; // Firebase Authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inizializza Firebase Authentication
        /*auth = FirebaseAuth.getInstance();

        // Controlla se l'utente è già loggato
        if (auth.getCurrentUser() != null) {
            // Se l'utente è già autenticato, invialo alla MainActivity senza passare dal login
            Intent intent = new Intent(LoginActivity.this, com.example.rain.dashboard.MainActivity.class);
            startActivity(intent);
            finish();  // Ferma la LoginActivity, così non può tornare indietro
        }*/

        // Inizializza il TextView per il link di registrazione
        registerLink = findViewById(R.id.register_link);
        // Imposta il listener sul link di registrazione
        registerLink.setOnClickListener(v -> {
            // Avvia la RegisterActivity
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Inizializza il TextView per il link di registrazione
        onboardingLink = findViewById(R.id.onboarding_link);
        // Imposta il listener sul link di registrazione
        onboardingLink.setOnClickListener(v -> {
            // Avvia la RegisterActivity
            Intent intent = new Intent(LoginActivity.this, FirstOnboardingActivity.class);
            startActivity(intent);
        });

        // Inizializza i campi
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);

        // Inizializza Firebase Authentication
        auth = FirebaseAuth.getInstance();

        // Listener sul bottone di login
        loginButton.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        // Verifica che i campi non siano vuoti
        if (email.isEmpty()) {
            emailField.setError("Inserisci l'email");
            return;
        }
        if (password.isEmpty()) {
            passwordField.setError("Inserisci la password");
            return;
        }

        // Esegui il login con Firebase Authentication
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Login riuscito
                        Toast.makeText(LoginActivity.this, "Accesso effettuato!", Toast.LENGTH_SHORT).show();
                        // Vai alla MainActivity
                        Intent intent = new Intent(LoginActivity.this, com.example.rain.dashboard.MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Mostra errore se il login fallisce
                        Toast.makeText(LoginActivity.this, "Login fallito: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}