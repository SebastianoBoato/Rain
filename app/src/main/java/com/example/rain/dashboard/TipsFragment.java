package com.example.rain.dashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rain.R;

/**
 * Fragment che gestisce la schermata dei consigli (Tips).
 * Può essere utilizzato per mostrare suggerimenti sull'uso dell'acqua raccolta
 * o altre pratiche sostenibili.
 */
public class TipsFragment extends Fragment {

    // Variabile per mantenere un riferimento alla vista radice del layout del Fragment.
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
        view = inflater.inflate(R.layout.fragment_tips, container, false);

        // QUA IN MEZZO VA L'IMPLEMENTAZIONE
        // Qui puoi inizializzare e configurare i componenti della vista, ad esempio:
        // - Una lista di suggerimenti dinamici.
        // - Testo informativo o immagini relative ai consigli.
        // - Pulsanti per condividere suggerimenti sui social o salvarli localmente.

        return view; // Restituisce la vista radice del Fragment.
    }
}
