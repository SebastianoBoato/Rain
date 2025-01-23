package com.example.rain.dashboard;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
                                        if (binding != null) {
                                            binding.currentTotalVolume.setText(String.format(Locale.US, "Ehi %s! Il tuo volume di acqua è di %.2f", userName, sumCurrentVolume) + " L\uD83D\uDCA7");

                                            // Clear previous tips
                                            binding.tipsContainer.removeAllViews();

                                            if (sumCurrentVolume >= 0 && sumCurrentVolume < 50) {
                                                addTipCard(binding.tipsContainer, "🚿 Inizio Raccolta",
                                                        "Hai iniziato a raccogliere acqua piovana! Continua così. Ogni goccia conta.");
                                                addTipCard(binding.tipsContainer, "\uD83C\uDF3F Irrigazione di Piccole Piante e Vasi",
                                                        "Usa l'acqua piovana per annaffiare piante in vaso, fiori o piccole piante aromatiche. Questo ti permette di risparmiare acqua potabile per altre necessità.");
                                                addTipCard(binding.tipsContainer, "\uD83D\uDE97 Pulizia di Superfici Esterne",
                                                        "Utilizza l'acqua per lavare terrazzi, balconi e altre superfici esterne. È un'ottima soluzione per mantenere pulito l'esterno senza spreco di acqua potabile.");
                                                addTipCard(binding.tipsContainer, "🔧 Manutenzione",
                                                        "Controlla periodicamente i tuoi serbatoi per prevenire accumuli di sedimenti o alghe.");
                                            }
                                            else if (sumCurrentVolume >= 50 && sumCurrentVolume < 100) {
                                                addTipCard(binding.tipsContainer, "🌿 Irrigazione Efficiente",
                                                        "Utilizza l'acqua piovana per innaffiare giardini, ortaggi e piante ornamentali.");
                                                addTipCard(binding.tipsContainer, "🚰 Usi Domestici",
                                                        "Puoi utilizzare quest'acqua per pulizie esterne o per sciacquare i marciapiedi.");
                                                addTipCard(binding.tipsContainer, "\uD83D\uDE97 Lavaggio della Macchina",
                                                        "Se la tua auto ha bisogno di una pulita veloce, l’acqua piovana è perfetta per lavarla. Non solo risparmierai acqua potabile, ma riuscirai anche a mantenere la tua auto splendente.");
                                                addTipCard(binding.tipsContainer, "🔧 Manutenzione",
                                                        "Controlla periodicamente i tuoi serbatoi per prevenire accumuli di sedimenti o alghe.");
                                            }
                                            else if (sumCurrentVolume >= 100 && sumCurrentVolume < 250) {
                                                addTipCard(binding.tipsContainer, "🌳 Risparmio Significativo",
                                                        "Ottimo lavoro! Stai facendo una differenza significativa nel risparmio idrico.");
                                                addTipCard(binding.tipsContainer, "🚿 Usi Multipli",
                                                        "Considera l'uso dell'acqua piovana per sciacquoni del bagno o lavatrici con opportuni filtri.");
                                                addTipCard(binding.tipsContainer, "\uD83C\uDF33 Innaffiare Arbusti e Alberi da Frutto",
                                                        "Con una quantità maggiore di acqua, puoi irrigare arbusti più grandi o alberi da frutto, aiutandoli a crescere e a prosperare senza utilizzare acqua potabile.");
                                            }
                                            else {
                                                addTipCard(binding.tipsContainer, "🏆 Campione della Raccolta",
                                                        "Sei un vero esperto nella raccolta di acqua piovana! Complimenti!");
                                                addTipCard(binding.tipsContainer, "🔄 Gestione Avanzata",
                                                        "Con così tanta acqua, considera sistemi di filtraggio e distribuzione più complessi.");
                                                addTipCard(binding.tipsContainer, "💡 Condividi",
                                                        "Potresti ispirare altri a iniziare la raccolta di acqua piovana.");
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
    private void addTipCard(ViewGroup container, String title, String description) {
        View tipCard = getLayoutInflater().inflate(R.layout.tip_card, container, false);

        TextView titleView = tipCard.findViewById(R.id.tipTitle);
        TextView descriptionView = tipCard.findViewById(R.id.tipDescription);

        titleView.setText(title);
        descriptionView.setText(description);

        container.addView(tipCard);
    }
}