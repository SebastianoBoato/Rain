package com.example.rain.dashboard;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.rain.R;
import com.example.rain.dashboard.container.AddContainerActivity;
import com.example.rain.dashboard.container.SelectContainerActivity;
import com.example.rain.dashboard.profile.ProfileActivity;
import com.example.rain.databinding.FragmentHomeBinding;
import com.example.rain.databinding.FragmentWeatherBinding;
import com.example.rain.items.DailyWeatherItem;
import com.example.rain.items.HourlyWeatherItem;
import com.example.rain.utils.OneElementCallback;
import com.example.rain.utils.WeatherCallback;
import com.example.rain.utils.WeatherUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    // Variabile per mantenere il riferimento alla vista radice del layout del Fragment.
    View view;
    private FragmentHomeBinding binding;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private Button selectContainerButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        // Carica il nome dell'utente
        db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    binding.userName.setText(documentSnapshot.getString("firstName"));
                }
            }
        });

        //accedere al profilo utente
        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Aggiungi listener per la modifica in tempo reale dei contenitori
        db.collection("users").document(userId).collection("containers")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Snackbar.make(view, "Errore: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                            return;
                        }

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            double sumCurrentVolume = 0;
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                if(document.getDouble("currentVolume") != null){
                                    sumCurrentVolume += document.getDouble("currentVolume");
                                }
                            }
                            binding.currentTotalVolume.setText(String.format(Locale.US, "%.2f", sumCurrentVolume) + " L"); // Converti in Litri
                        } else {
                            Snackbar.make(view, "Nessun contenitore trovato", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });

        // Aggiungi listener per la modifica in tempo reale dell'acqua raccolta
        db.collection("users").document(userId).collection("collection_history")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Snackbar.make(view, "Errore: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                            return;
                        }

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            double sumCollectedWater = 0;
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                if(document.getDouble("collectedVolume") != null){
                                    sumCollectedWater += document.getDouble("collectedVolume");
                                }
                            }
                            binding.lifetimeCollectedVolume.setText(String.format(Locale.US, "%.2f", sumCollectedWater) + " L"); // Converti in Litri
                        } else {
                            Snackbar.make(view, "Non trovata l'acqua utilizzata", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });

        // Aggiungi listener per la modifica in tempo reale dell'acqua usata
        db.collection("users").document(userId).collection("usage_history")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Snackbar.make(view, "Errore: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                            return;
                        }

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            double sumUsedWater = 0;
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                if(document.getDouble("usedVolume") != null){
                                    sumUsedWater += document.getDouble("usedVolume");
                                }
                            }
                            binding.lifetimeUsedVolume.setText(String.format(Locale.US, "%.2f", sumUsedWater) + " L"); // Converti in Litri
                        } else {
                            Snackbar.make(view, "Non trovata l'acqua utilizzata", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });

        /*db.collection("users").document(userId).collection("containers").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            double sum = 0;
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                sum += document.getDouble("currentVolume");
                            }
                            binding.currentTotalVolume.setText(String.format(Locale.US, "%.2fL", sum/1000));
                        }
                        else {
                            Snackbar.make(view, "Nessun contenitore trovato", Snackbar.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(view, "Errore: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });*/

        WeatherUtils.getLocation(db, user, new OneElementCallback<String>() {
            @Override
            public void onSuccess(String location) {

                WeatherUtils.getWeatherDetails(location, new WeatherCallback() {
                    @Override
                    public void onSuccess(List<HourlyWeatherItem> hourlyWeatherItemsToday,
                                          List<HourlyWeatherItem> hourlyWeatherItemsTomorrow,
                                          List<HourlyWeatherItem> hourlyWeatherItemsAfterTomorrow,
                                          DailyWeatherItem todayWeather,
                                          DailyWeatherItem tomorrowWeather,
                                          DailyWeatherItem afterTomorrowWeather) {

                        Glide.with(HomeFragment.this).load("https:" + todayWeather.getIconUrl()).into(binding.todayWeatherIcon);
                        Glide.with(HomeFragment.this).load("https:" + tomorrowWeather.getIconUrl()).into(binding.tomorrowWeatherIcon);
                        Glide.with(HomeFragment.this).load("https:" + afterTomorrowWeather.getIconUrl()).into(binding.afterTomorrowWeatherIcon);
                    }

                    @Override
                    public void onError(String error) {
                        Snackbar.make(view, error, Snackbar.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                Snackbar.make(view, error, Snackbar.LENGTH_LONG).show();
            }
        });

        //bottone per modificare quantità acqua
        selectContainerButton = view.findViewById(R.id.selectContainerButton);
        selectContainerButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SelectContainerActivity.class);
            startActivity(intent);
        });

        return view; // Restituisce la vista radice del Fragment.
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
