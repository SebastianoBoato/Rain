package com.example.rain.dashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rain.R;

public class ContainersFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_containers, container, false);

        Log.d("CONTAINERS FRAGMENT", "ContainersFragment caricato");

        // QUA IN MEZZO VA L'IMPLEMENTAZIONE

        return view;
    }
}