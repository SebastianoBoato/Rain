package com.example.rain.dashboard.container;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rain.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddContainerActivity extends AppCompatActivity {

    private Spinner shapeSpinner;
    private EditText parameter1, parameter2, roofArea, height, name;
    private CheckBox roofAreaCheckbox;
    private Button saveButton;

    private TextView parameter2Label, roofAreaLabel;

    private FirebaseFirestore db;
    private String userId;

    // Classe di supporto che raccoglie i dati del contenitore
    private static class ContainerData {
        String containerName;
        String shape;
        Double param1;
        Double param2;
        Double height1;
        Double roofArea1;

        public ContainerData(String containerName, String shape, Double param1, Double param2, Double height1, Double roofArea1) {
            this.containerName = containerName;
            this.shape = shape;
            this.param1 = param1;
            this.param2 = param2;
            this.height1 = height1;
            this.roofArea1 = roofArea1;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_container);

        // Inizializza il pulsante Torna indietro
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish()); // Chiude l'Activity e torna indietro

        // Inizializza Firebase
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        shapeSpinner = findViewById(R.id.shapeSpinner);

        parameter1 = findViewById(R.id.parameter1); // diametro/lunghezza
        parameter2 = findViewById(R.id.parameter2); // larghezza
        height = findViewById(R.id.height); // altezza
        roofArea = findViewById(R.id.roofArea); // superficie tetto
        name = findViewById(R.id.containerName); //nome serbatoio

        roofAreaCheckbox = findViewById(R.id.areaCheckbox);
        saveButton = findViewById(R.id.saveButton);

        parameter2Label = findViewById(R.id.parameter2Label);
        roofAreaLabel = findViewById(R.id.roofAreaLabel);

        // Mostra o nascondi parametro 2
        shapeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedShape = parent.getItemAtPosition(position).toString();
                parameter2.setVisibility(selectedShape.equals("Cerchio") || selectedShape.equals("Quadrato") ? View.GONE : View.VISIBLE);
                parameter2Label.setVisibility(selectedShape.equals("Cerchio") || selectedShape.equals("Quadrato") ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Imposta CheckBox come attivo di default
        roofAreaCheckbox.setChecked(true);  // Imposta come predefinito
        roofAreaLabel.setVisibility(View.GONE);

        // Aggiungi listener per il CheckBox
        roofAreaCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            roofArea.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            roofAreaLabel.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        });

        // Salva il contenitore
        saveButton.setOnClickListener(v -> {
            saveContainer();
            Intent resultIntent = new Intent();
            setResult(Activity.RESULT_OK, resultIntent);
            finish();  // Torna all'Activity precedente
        });
    }

    private void saveContainer() {
        // Ottieni i dati dai campi
        String shape = shapeSpinner.getSelectedItem().toString();
        Double param1 = null;
        Double param2 = null;
        Double height1 = null;
        Double roofArea1 = null;

        // Parsing dei parametri
        try {
            param1 = Double.parseDouble(parameter1.getText().toString());
        } catch (NumberFormatException e) {
            param1 = null; // Valore non valido
        }

        try {
            param2 = Double.parseDouble(parameter2.getText().toString());
        } catch (NumberFormatException e) {
            param2 = null; // Valore non valido
        }

        try {
            height1 = Double.parseDouble(height.getText().toString());
        } catch (NumberFormatException e) {
            height1 = null; // Valore non valido
        }

        try {
            roofArea1 = Double.parseDouble(roofArea.getText().toString());
        } catch (NumberFormatException e) {
            roofArea1 = null; // Valore non valido
        }

        // Usa il callback per ottenere il nome del contenitore
        Double finalParam1 = param1;
        Double finalParam2 = param2;
        Double finalHeight = height1;
        Double finalRoofArea = roofArea1;
        getContainerName(new ContainerNameCallback() {
            @Override
            public void onNameRetrieved(String containerName) {
                // Crea l'oggetto ContainerData con i dati
                ContainerData data = new ContainerData(containerName, shape, finalParam1, finalParam2, finalHeight, finalRoofArea);

                // Una volta che il nome è stato recuperato, prosegui con il salvataggio
                saveToDatabase(data);
            }
        });
    }

    // Interfaccia per il callback
    interface ContainerNameCallback {
        void onNameRetrieved(String containerName);
    }

    // Funzione che ottiene il nome del contenitore
    private void getContainerName(ContainerNameCallback callback) {
        String enteredName = ((EditText) findViewById(R.id.containerName)).getText().toString();

        if (!enteredName.isEmpty()) {
            callback.onNameRetrieved(enteredName);  // Se il nome è stato inserito, invoca il callback
        } else {
            // Se il nome non è stato inserito, genera un nome incrementale
            getContainerNameIncrementally(callback);
        }
    }

    // Funzione che ottiene il nome incrementale
    private void getContainerNameIncrementally(ContainerNameCallback callback) {
        // Ottieni il nome incrementale in modo asincrono
        db.collection("users")
                .document(userId)
                .collection("containers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = task.getResult().size();
                        String newName = "Serbatoio " + (count + 1);
                        callback.onNameRetrieved(newName);  // Passa il nome generato al callback
                    } else {
                        Toast.makeText(this, "Errore nel recupero dei dati", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Metodo per processare i dati e salvarli nel database
    private void saveToDatabase(ContainerData data) {
        String containerName = data.containerName;
        String shape = data.shape;
        Double param1 = data.param1;
        Double param2 = data.param2;
        Double height1 = data.height1;
        Double roofArea1 = data.roofArea1;

        // Controlla se i campi obbligatori sono vuoti o nulli
        if ((shape.equals("Cerchio") || shape.equals("Quadrato")) &&
                (param1 == null || height1 == null || (!roofAreaCheckbox.isChecked() && roofArea1 == null))) {
            // Se è cerchio o quadrato, controlla se param1 (diametro/lato), height e roofArea (se non selezionato) sono presenti
            Toast.makeText(this, "Compila tutti i campi obbligatori", Toast.LENGTH_SHORT).show();
            return;
        } else if (!(shape.equals("Cerchio") || shape.equals("Quadrato")) &&
                (param1 == null || param2 == null || height1 == null || (!roofAreaCheckbox.isChecked() && roofArea1 == null))) {
            // Se non è cerchio o quadrato, controlla se param1 (semiassi/lunghezza), param2 (semiassi/larghezza), height e roofArea (se non selezionato) sono presenti
            Toast.makeText(this, "Compila tutti i campi obbligatori", Toast.LENGTH_SHORT).show();
            return;
        }

        // Se tutto è valido, continua con il salvataggio o altre operazioni
        double area = 0, volume = 0;

        // Calcolo area e volume
        switch (shape) {
            case "Cerchio":
                double diameter = param1;
                area = Math.PI * Math.pow(diameter / 2, 2);
                volume = area * height1 / 1000;
                break;
            case "Ellisse":
                double semiMajorAxis = param1;
                double semiMinorAxis = param2;
                area = Math.PI * semiMajorAxis * semiMinorAxis;
                volume = area * height1 / 1000;
                break;
            case "Rettangolo":
                double length = param1;
                double width = param2;
                area = length * width;
                volume = area * height1 / 1000;
                break;
            case "Quadrato":
                double side = param1;
                area = Math.pow(side, 2);
                volume = area * height1 / 1000;
                break;
        }

        // Salva nel database
        Map<String, Object> container = new HashMap<>();
        container.put("name", containerName);
        container.put("shape", shape);
        container.put("param1", param1);
        if (!shape.equals("Cerchio") && !shape.equals("Quadrato")) {
            container.put("param2", param2);
        }
        container.put("height", height1);
        container.put("baseArea", area);
        container.put("totalVolume", volume);
        container.put("currentVolume", 0);
        container.put("roofArea", roofAreaCheckbox.isChecked() ? null : roofArea1);

        db.collection("users")
                .document(userId)
                .collection("containers")
                .add(container)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Contenitore aggiunto con successo", Toast.LENGTH_SHORT).show();
                    finish(); // Torna al Fragment precedente
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Errore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}