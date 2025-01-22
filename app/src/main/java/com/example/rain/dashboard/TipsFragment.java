package com.example.rain.dashboard;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.rain.R;
import com.example.rain.databinding.FragmentTipsBinding;
import com.example.rain.databinding.FragmentWeatherBinding;
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

import java.util.Locale;

public class TipsFragment extends Fragment {
    View view;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private FragmentTipsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTipsBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String userName;
                if (binding != null && documentSnapshot.exists()) {
                    userName = documentSnapshot.getString("firstName");

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
                                        // QUI ABBIAMO IL VOLUME TOTALE DI ACQUA TRA TUTTI I CONTENITORI
                                        // I DATI CHE ABBIAMO SONO:
                                        // - userName : nome dell'utente
                                        // - sumCurrentVolume : volume totale di acqua tra tutti i contenitori
                                        if (binding != null){
                                            binding.currentTotalVolume.setText(String.format(Locale.US, "Ehi %s! Il tuo volume di acqua è di %.2f", userName, sumCurrentVolume) + " L"); // Converti in Litri
                                            // QUA SOTTO CI VA LA COSA DEL TIPO:
                                            // IF QUANTITA == X ALLORA PUOI USARLA PER QUESTO
                                            // IF QUANTITA == Y ALLORA PUOI USARLA PER QUEST'ALTRO
                                            // E COSI VIA
                                            if (sumCurrentVolume >= 0  &&  sumCurrentVolume < 50){

                                            }
                                            else if (sumCurrentVolume >= 50  &&  sumCurrentVolume < 100){

                                            }
                                            else if (sumCurrentVolume >= 100  &&  sumCurrentVolume < 250){

                                            } else {

                                            }
                                        }
                                    } else {
                                        Snackbar.make(view, "Nessun contenitore trovato", Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
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
