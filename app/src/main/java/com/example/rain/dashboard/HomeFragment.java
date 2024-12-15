package com.example.rain.dashboard;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.rain.R;
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
import com.google.firebase.firestore.FirebaseFirestore;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    binding.userName.setText(documentSnapshot.getString("firstName"));
                }
            }
        });

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        db.collection("users").document(userId).collection("containers").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            double sum = 0;
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                sum += document.getDouble("currentVolume");
                            }
                            binding.currentTotalVolume.setText(String.format(Locale.US, "%.1fL", sum));
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
                });

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

        return view; // Restituisce la vista radice del Fragment.
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
