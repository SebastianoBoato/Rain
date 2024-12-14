package com.example.rain.dashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rain.R;

/**
 * Fragment che gestisce la schermata della cronologia.
 * Utilizzato per mostrare i dati storici relativi alla raccolta dell'acqua
 * o altre statistiche salvate dall'app.
 */
public class HistoryFragment extends Fragment {

    // Variabile per mantenere il riferimento alla vista radice del layout del Fragment.
    View view;

    /**
     * Metodo chiamato per creare e restituire la vista associata al Fragment.
     * @param inflater oggetto per gonfiare (inflate) il layout del Fragment.
     * @param container il contenitore che ospita la vista del Fragment.
     * @param savedInstanceState dati salvati dallo stato precedente, se disponibili.
     * @return la vista creata per il Fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Gonfia il layout XML associato al Fragment e assegna la vista radice a 'view'.
        view = inflater.inflate(R.layout.fragment_history, container, false);

        // QUA IN MEZZO VA L'IMPLEMENTAZIONE
        // Qui si possono configurare gli elementi della vista, come grafici, liste, o altre UI per mostrare i dati storici.

        return view; // Restituisce la vista radice del Fragment.
    }
}
