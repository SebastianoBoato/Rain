package com.example.rain.dashboard;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.rain.R;

public class TipsFragment extends Fragment {
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Gonfia il layout XML associato al Fragment
        view = inflater.inflate(R.layout.fragment_tips, container, false);

        // Qui puoi aggiungere eventuali inizializzazioni aggiuntive se necessario
        // Ad esempio, gestione di bottoni, liste dinamiche, ecc.

        return view;
    }
}
