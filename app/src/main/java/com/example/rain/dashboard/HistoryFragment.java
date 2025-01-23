package com.example.rain.dashboard;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rain.R;
import com.example.rain.databinding.FragmentHistoryBinding;
import com.example.rain.databinding.FragmentWeatherBinding;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class HistoryFragment extends Fragment {

    View view;
    FragmentHistoryBinding binding;
    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("users").document(user.getUid()).collection("collection_history").orderBy("timestamp").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<Entry> entries = new ArrayList<>();
                            List<String> labels = new ArrayList<>();
                            float i = 0;
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                entries.add(new Entry(
                                        i++,
                                        document.getDouble("collectedVolume").floatValue()
                                ));
                                labels.add(document.getString("date"));
                            }

                            LineDataSet dataSet = new LineDataSet(entries, "Acqua Raccolta");

                            dataSet.setDrawValues(false); // Non mostrare i valori sopra i punti
                            dataSet.setColor(Color.GREEN); // Colore della linea
                            dataSet.setCircleColor(Color.BLUE); // Colore dei punti
                            dataSet.setCircleRadius(4f); // Raggio dei punti
                            dataSet.setLineWidth(2f); // Spessore della linea

                            LineData lineData = new LineData(dataSet);
                            binding.collectionChart.setData(lineData);

                            // Configura l'asse X
                            XAxis xAxis = binding.collectionChart.getXAxis();
                            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels)); // Mostra le date
                            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Posiziona l'asse X in basso
                            xAxis.setGranularity(1f); // Mostra un'etichetta per ogni punto
                            xAxis.setGranularityEnabled(true); // Forza la granularità
                            xAxis.setDrawGridLines(false); // Nasconde le linee della griglia sull'asse X
                            xAxis.setTextColor(Color.BLACK); // Colore del testo delle etichette
                            xAxis.setTextSize(12f); // Dimensione del testo delle etichette
                            xAxis.setLabelRotationAngle(40f); // Ruota le etichette di 40 gradi

                            // Configura l'asse Y
                            YAxis leftAxis = binding.collectionChart.getAxisLeft();
                            leftAxis.setDrawGridLines(true); // Mostra le linee della griglia sull'asse Y
                            leftAxis.setTextColor(Color.BLACK); // Colore dei numeri sull'asse Y
                            leftAxis.setTextSize(12f); // Dimensione dei numeri sull'asse Y

                            YAxis rightAxis = binding.collectionChart.getAxisRight();
                            rightAxis.setEnabled(false); // Disabilita l'asse Y a destra

                            // Configura il grafico
                            binding.collectionChart.getDescription().setEnabled(false);
                            binding.collectionChart.getLegend().setEnabled(false); // Rimuovi la legenda
                            binding.collectionChart.setDrawBorders(false); // Non disegnare bordi attorno al grafico
                            binding.collectionChart.setDrawGridBackground(false); // Nessuno sfondo di griglia
                            binding.collectionChart.setExtraOffsets(10, 10, 30, 55); // Margini extra per evitare sovrapposizioni

                            binding.collectionChart.invalidate();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(view, "Errore nel download dei dati", Snackbar.LENGTH_LONG).show();
                    }
                });

        db.collection("users").document(user.getUid()).collection("usage_history").orderBy("timestamp").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<Entry> entries = new ArrayList<>();
                            List<String> labels = new ArrayList<>();
                            float i = 0;
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                entries.add(new Entry(
                                        i++,
                                        document.getDouble("usedVolume").floatValue()
                                ));
                                labels.add(document.getString("date"));
                            }

                            LineDataSet dataSet = new LineDataSet(entries, "Acqua Usata");

                            dataSet.setDrawValues(false); // Non mostrare i valori sopra i punti
                            dataSet.setColor(Color.BLUE); // Colore della linea
                            dataSet.setCircleColor(Color.RED); // Colore dei punti
                            dataSet.setCircleRadius(4f); // Raggio dei punti
                            dataSet.setLineWidth(2f); // Spessore della linea

                            LineData lineData = new LineData(dataSet);
                            binding.usageChart.setData(lineData);

                            // Configura l'asse X
                            XAxis xAxis = binding.usageChart.getXAxis();
                            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels)); // Mostra le date
                            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Posiziona l'asse X in basso
                            xAxis.setGranularity(1f); // Mostra un'etichetta per ogni punto
                            xAxis.setGranularityEnabled(true); // Forza la granularità
                            xAxis.setDrawGridLines(false); // Nasconde le linee della griglia sull'asse X
                            xAxis.setTextColor(Color.BLACK); // Colore del testo delle etichette
                            xAxis.setTextSize(12f); // Dimensione del testo delle etichette
                            xAxis.setLabelRotationAngle(40f); // Ruota le etichette di 40 gradi

                            // Configura l'asse Y
                            YAxis leftAxis = binding.usageChart.getAxisLeft();
                            leftAxis.setDrawGridLines(true); // Mostra le linee della griglia sull'asse Y
                            leftAxis.setTextColor(Color.BLACK); // Colore dei numeri sull'asse Y
                            leftAxis.setTextSize(12f); // Dimensione dei numeri sull'asse Y

                            YAxis rightAxis = binding.usageChart.getAxisRight();
                            rightAxis.setEnabled(false); // Disabilita l'asse Y a destra

                            // Configura il grafico
                            binding.usageChart.getDescription().setEnabled(false);
                            binding.usageChart.getLegend().setEnabled(false); // Rimuovi la legenda
                            binding.usageChart.setDrawBorders(false); // Non disegnare bordi attorno al grafico
                            binding.usageChart.setDrawGridBackground(false); // Nessuno sfondo di griglia
                            binding.usageChart.setExtraOffsets(10, 10, 30, 55); // Margini extra per evitare sovrapposizioni

                            binding.usageChart.invalidate();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(view, "Errore nel download dei dati", Snackbar.LENGTH_LONG).show();
                    }
                });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
