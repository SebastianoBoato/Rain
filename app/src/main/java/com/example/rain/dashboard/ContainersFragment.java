package com.example.rain.dashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rain.R;

/**
 * Fragment che gestisce la schermata dedicata ai contenitori.
 * Viene utilizzato per mostrare o interagire con i serbatoi registrati dall'utente.
 */
public class ContainersFragment extends Fragment {

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
        view = inflater.inflate(R.layout.fragment_containers, container, false);

        // Log per scopi di debug, utile per confermare il caricamento del Fragment.
        Log.d("CONTAINERS FRAGMENT", "ContainersFragment caricato");

        // QUA IN MEZZO VA L'IMPLEMENTAZIONE
        // Se necessario, qui si possono configurare i componenti della vista o inizializzare la logica specifica.

        return view; // Restituisce la vista radice del Fragment.
    }
}
