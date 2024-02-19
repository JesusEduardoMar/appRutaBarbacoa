package com.example.cadeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class VerTodosLosLugaresActivity extends AppCompatActivity {
    // Variables
    private RecyclerView recyclerView;
    private ProgressBar pbProgressMain;
    private FirebaseFirestore mFirestore;
    private ArrayList<ItemsDomainVinedos> items;
    private ItemsAdapterVinedos itemsAdapterVinedos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_ver_lugares);

        // Inicialización de Firebase y otros elementos de la interfaz de usuario
        mFirestore = FirebaseFirestore.getInstance();

        // Inicialización del ProgressBar
        pbProgressMain = findViewById(R.id.progress_main);

        // Configuración de RecyclerView para las barbacoas
        recyclerView = findViewById(R.id.recycler_view_ver_lugares);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        items = new ArrayList<>();
        itemsAdapterVinedos = new ItemsAdapterVinedos(items, this);
        recyclerView.setAdapter(itemsAdapterVinedos);

        mFirestore.collection("barbacoas").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                pbProgressMain.setVisibility(View.VISIBLE);
                if(error != null){
                    Log.e("Firestore error", error.getMessage());
                    return;
                }
                for(DocumentChange dc : value.getDocumentChanges()){
                    if(dc.getType() == DocumentChange.Type.ADDED){
                        items.add(dc.getDocument().toObject(ItemsDomainVinedos.class));
                    }
                    itemsAdapterVinedos.notifyDataSetChanged();
                    pbProgressMain.setVisibility(View.GONE);
                }
            }
        });

    }
}
