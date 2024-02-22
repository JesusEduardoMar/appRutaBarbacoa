package com.example.cadeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class cardHistoriabarbacoa extends AppCompatActivity {

    ImageView flecha2;

    private RecyclerView imagesRecycler1;
    private ItemsAdapterHistoria itemsAdapterHistoria;
    private List<String> items;
    private FirebaseFirestore mFirestore;

    ProgressBar pbProgressMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_historiabarbacoa);

        flecha2 = findViewById(R.id.flechaatras2);

        flecha2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cardHistoriabarbacoa.this, VerTodosLosLugaresActivity.class);
                startActivity(intent);
                finish();
            }
        });


        imagesRecycler1 = findViewById(R.id.imagesRecycler);
        imagesRecycler1.setHasFixedSize(true);
        imagesRecycler1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        mFirestore = FirebaseFirestore.getInstance();
        items = new ArrayList<>();
        itemsAdapterHistoria = new ItemsAdapterHistoria(items, this);
        imagesRecycler1.setAdapter(itemsAdapterHistoria);

        cargarImagenesDesdeFirestore("https://www.elfinanciero.com.mx/resizer/VRIHCZLbd3-OHxa9hPDSz1T_AUI=/800x0/filters:format(jpg):quality(70)/cloudfront-us-east-1.images.arcpublishing.com/elfinanciero/NHH3F5SOLZGDJNGBVJQRU7ZRBE.jpeg");
        cargarImagenesDesdeFirestore("https://i.blogs.es/b4889c/1/1366_2000.jpg");

    }

    private void cargarImagenesDesdeFirestore(String url) {
        mFirestore.collection("imagesall")
                .whereEqualTo("url", url)
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

        Intent intent = new Intent(cardHistoriabarbacoa.this, VerTodosLosLugaresActivity.class);
        startActivity(intent);
        finish();
    }

}