package com.example.cadeapp;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class cardPreparacionbarba extends ScrollingActivity {

    ImageView flecha1;

    private RecyclerView imagesRecycler2;
    private RecyclerView imagesRecycler1;
    private ImageView toolbar_icon;
    private ItemsAdapterHistoria itemsAdapterHistoria;
    private List<String> items;
    private FirebaseFirestore mFirestore;

    protected RecyclerView recyclerView;
    protected NestedScrollView nscrollv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        title = "Preparación de la barbacoa";
        lastActivity = VerTodosLosLugaresActivity.class;
        super.onCreate(savedInstanceState);

        toolbar_icon = findViewById(R.id.toolbar_icon);
        toolbar_icon.setImageResource(R.drawable.preparacionbarba);
        toolbar_icon.setScaleType(ImageView.ScaleType.CENTER_CROP);

        recyclerView = findViewById(R.id.recyclerView);
        nscrollv = findViewById(R.id.nestedScrollView);
        LayoutInflater inflater = getLayoutInflater();
        View myLayout = inflater.inflate(R.layout.activity_card_preparacionbarba, nscrollv, false);
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
        cargarImagenesDesdeFirestore("https://www.gastrolabweb.com/u/fotografias/m/2021/8/8/f1280x720-17288_148963_5050.jpg");
        cargarImagenesDesdeFirestore("https://media.timeout.com/images/105476091/1024/576/image.webp");

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

        Intent intent = new Intent(cardPreparacionbarba.this, VerTodosLosLugaresActivity.class);
        startActivity(intent);
        finish();
    }



}