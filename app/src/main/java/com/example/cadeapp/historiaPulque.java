package com.example.cadeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class historiaPulque extends ScrollingActivity {

    private TextView textDescription;
    private RecyclerView imagesRecycler1;
    private ImageView toolbar_icon;
    private List<String> items;
    private FirebaseFirestore mFirestore;

    private ItemsAdapterHistoria itemsAdapterHistoria;
    private String idDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        title = "Preparación del Pulque";
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_historia_pulque);


        textDescription = findViewById(R.id.textDescription);
        obtenerInformacionDesc();

        // Change Icon of top_background
        toolbar_icon = findViewById(R.id.toolbar_icon);
        toolbar_icon.setImageResource(R.drawable.preparapulque);
        toolbar_icon.setScaleType(ImageView.ScaleType.CENTER_CROP);

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

        // Obtenemos el ID del pulque actual
        idDesc = obtenerdesc();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Volvemos a la MainActivity
        Intent intent = new Intent(historiaPulque.this, VerTodosLosPulquesActivity.class);
        //con esta linea limpiamos las actividades para que no se muestren mas que una sola en lugar de cada que abramos un lugar
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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


    // Método para obtener la información de la descripcion
    private void obtenerInformacionDesc() {
        // Verificamos la existencia del id
        if (idDesc != null) {
            // Consultamos en Firestore para obtener información de la descripcion
            mFirestore.collection("infocardviews").document(idDesc).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        // Extraemos los campos del documento
                        String info = document.getString("id_descripcion");

                        // Configuramos los elementos de la interfaz de usuario con la información obtenida
                        textDescription.setText(info);

                    } else {
                        // Manejo de errores
                        Log.e("historiaPulque", "Error al obtener la información de la descripcion", task.getException());
                    }
                }
            });
        } else {
            // Manejo de errores
            Log.e("historiaPulque", "El nombre de la descripcion es nulo en la intención.");
        }
    }

    // Método para obtener el ID de la descripcion actual
    private String obtenerdesc() {
        Intent intent = getIntent();
        if (intent != null) {
            // Obtenemos el ID de la descripcion desde la intención
            String idDesc = intent.getStringExtra("idDesc");
            if (idDesc != null && !idDesc.isEmpty()) {
                return idDesc;
            }
        }
        Toast.makeText(this, "Error: ID de descripcion no válido", Toast.LENGTH_SHORT).show();
        return "";
    }

}