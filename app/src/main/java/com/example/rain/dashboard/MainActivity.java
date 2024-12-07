package com.example.rain.dashboard;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

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

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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