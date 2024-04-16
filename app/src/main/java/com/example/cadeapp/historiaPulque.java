package com.example.cadeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class historiaPulque extends ScrollingActivity {

    private RecyclerView imagesRecycler1;
    private ImageView toolbar_icon;
    private List<String> items;
    private FirebaseFirestore mFirestore;

    private ItemsAdapterHistoria itemsAdapterHistoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        title = "Preparación del Pulque";
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_historia_pulque);

        //incrustar activity contact
        NestedScrollView nscrollv;
        nscrollv = findViewById(R.id.nestedScrollView);
        LayoutInflater inflater = getLayoutInflater();
        View myLayout = inflater.inflate(R.layout.activity_historia_pulque, nscrollv, false);
        nscrollv.removeAllViews();
        nscrollv.addView(myLayout);

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Volvemos a la MainActivity
        Intent intent = new Intent(historiaPulque.this, VerTodosLosPulquesActivity.class);
        startActivity(intent);
        finish();
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
}