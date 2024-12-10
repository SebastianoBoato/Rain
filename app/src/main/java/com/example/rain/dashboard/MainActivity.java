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

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    //private Button logoutButton;

    private FirebaseAuth auth; // Firebase Authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inizializza Firebase Authentication
        auth = FirebaseAuth.getInstance();

        // Controlla se l'utente è già loggato
        if (auth.getCurrentUser() == null) {
            // Se l'utente è già autenticato, invialo alla MainActivity senza passare dal login
            Intent intent = new Intent(MainActivity.this, com.example.rain.login.LoginActivity.class);
            startActivity(intent);
            finish();  // Ferma la LoginActivity, così non può tornare indietro
        }

        // chiedi il permesso per mandare le notifiche
        requestNotificationPermission();

        // prendo gli oggetti di layout della activity
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        // questo onestamente non so che cosa fa, ma serve
        setContentView(binding.getRoot());

        // modalità schermo intero (circa)
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // imposto il navigation controller
        NavController navController = Navigation.findNavController(this, R.id.fragmentFrame);
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

        //TODO: implementare il logout (togli commenti se vuoi provare register e login + logout)
        /*logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();  // Effettua il logout
            Intent intent = new Intent(MainActivity.this, com.example.rain.login.LoginActivity.class);
            startActivity(intent);  // Riporta l'utente alla schermata di login
            finish();  // Ferma la MainActivity
        });*/

    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(this)
                        .setTitle("Permesso notifiche")
                        .setMessage("Concedi il permesso per ricevere notifiche importanti.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
                        })
                        .setNegativeButton("Annulla", null)
                        .show();
            }
        }
    }
}