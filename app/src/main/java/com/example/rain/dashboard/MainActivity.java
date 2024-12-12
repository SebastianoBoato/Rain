package com.example.rain.dashboard;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.rain.R;
import com.example.rain.databinding.ActivityMainBinding;
import com.example.rain.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * MainActivity che gestisce la schermata principale dell'app dopo il login.
 * Configura la navigazione, il layout principale e verifica l'autenticazione dell'utente.
 */
public class MainActivity extends AppCompatActivity {

    // Binding per accedere ai componenti del layout tramite View Binding.
    private ActivityMainBinding binding;

    // Autenticazione Firebase.
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inizializza Firebase Authentication.
        auth = FirebaseAuth.getInstance();

        // Verifica se l'utente è autenticato.
        if (auth.getCurrentUser() == null) {
            // Se non è autenticato, reindirizza alla schermata di login.
            Intent intent = new Intent(MainActivity.this, com.example.rain.login.LoginActivity.class);
            startActivity(intent);
            finish(); // Termina MainActivity per impedire il ritorno alla schermata principale.
        }

        // Richiedi permesso per inviare notifiche (necessario per Android 13+).
        requestNotificationPermission();

        // Inizializza il layout tramite View Binding.
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Abilita modalità Edge-to-Edge per uno schermo a tutto schermo.
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configura il Navigation Controller con la Bottom Navigation.
        NavController navController = Navigation.findNavController(this, R.id.fragmentFrame);
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

        // TODO: Implementare il logout (codice preparato, ma commentato per ora).
        /*
        logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();  // Effettua il logout.
            Intent intent = new Intent(MainActivity.this, com.example.rain.login.LoginActivity.class);
            startActivity(intent);  // Torna alla schermata di login.
            finish();  // Termina MainActivity.
        });
        */
    }

    /**
     * Metodo per richiedere il permesso di inviare notifiche (solo per Android 13 e successivi).
     * Mostra un dialogo che spiega il motivo della richiesta.
     */
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Verifica versione Android.
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Mostra un dialogo che richiede il permesso.
                new AlertDialog.Builder(this)
                        .setTitle("Permesso notifiche")
                        .setMessage("Concedi il permesso per ricevere notifiche importanti.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Richiede il permesso di inviare notifiche.
                            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
                        })
                        .setNegativeButton("Annulla", null)
                        .show();
            }
        }
    }
}
