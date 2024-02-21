package com.example.cadeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class cardPreparacionbarba extends AppCompatActivity {

    ImageView flecha1;

    private RecyclerView imagesRecycler2;
    private ItemsAdapterHistoria itemsAdapterHistoria;
    private List<String> items;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_preparacionbarba);

        flecha1 = findViewById(R.id.flechaatras1);
        flecha1.setColorFilter(null);

        flecha1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cardPreparacionbarba.this, VerTodosLosLugaresActivity.class);
                startActivity(intent);
                finish();
            }
        });

        imagesRecycler2 = findViewById(R.id.imagesRecycler);
        imagesRecycler2.setHasFixedSize(true);
        imagesRecycler2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        mFirestore = FirebaseFirestore.getInstance();
        items = new ArrayList<>();
        itemsAdapterHistoria = new ItemsAdapterHistoria(items, this);
        imagesRecycler2.setAdapter(itemsAdapterHistoria);

        cargarImagenesDesdeFirestore();

    }

    private void cargarImagenesDesdeFirestore() {
        mFirestore.collection("imagesall")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Obtener la URL de la imagen de cada documento y agregarla a la lista
                            String imageUrl = document.getString("url");
                            items.add(imageUrl);
                        }
                        // Notificar al adaptador sobre el cambio en los datos
                        itemsAdapterHistoria.notifyDataSetChanged();
                    }
                    else {
                    }
                });
    }


    public void onBackPressed(){
        super.onBackPressed();

        Intent intent = new Intent(cardPreparacionbarba.this, VerTodosLosLugaresActivity.class);
        startActivity(intent);
        finish();
    }



}