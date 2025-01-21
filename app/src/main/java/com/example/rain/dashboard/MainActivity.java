package com.example.rain.dashboard;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.rain.onboarding.FirstOnboardingActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * MainActivity che gestisce la schermata principale dell'app dopo il login.
 * Configura la navigazione, il layout principale e verifica l'autenticazione dell'utente.
 */
public class MainActivity extends AppCompatActivity {

    // Binding per accedere ai componenti del layout tramite View Binding.
    private ActivityMainBinding binding;

    //private Button logoutButton;

    // Autenticazione Firebase.
    private FirebaseAuth auth;

    private AlertDialog permissionDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    }

    /**
     * Metodo per richiedere il permesso di inviare notifiche (solo per Android 13 e successivi).
     * Mostra un dialogo che spiega il motivo della richiesta.
     */
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Verifica versione Android.
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Mostra un dialogo che richiede il permesso.
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Permesso notifiche")
                        .setMessage("Concedi il permesso per ricevere notifiche importanti.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Richiede il permesso di inviare notifiche.
                            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
                        })
                        .setNegativeButton("Annulla", null);
                permissionDialog = builder.create();
                permissionDialog.show();
            }
        }
    }

    private boolean isFirstTime() {
        // Ottieni le SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);

        // Controlla se è la prima volta (di default è true)
        boolean isFirstTime = sharedPreferences.getBoolean("isFirstTime", true);

        if (isFirstTime) {
            // Aggiorna il valore nelle SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstTime", false);
            editor.apply();
        }

        return isFirstTime;
    }

    @Override
    protected void onDestroy() {
        if (permissionDialog != null && permissionDialog.isShowing()) {
            permissionDialog.dismiss();
        }
        super.onDestroy();
    }
}
