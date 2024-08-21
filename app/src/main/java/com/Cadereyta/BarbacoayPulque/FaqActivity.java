package com.Cadereyta.BarbacoayPulque;

import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentCallbacks2;
import android.os.Bundle;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FaqActivity extends ScrollingActivity {
    List<Versions> versionsList;


    protected RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        title = "Preguntas frecuentes";
        super.onCreate(savedInstanceState);

        recyclerView = findViewById(R.id.recyclerView);
// inicializamos initData
        initData();
    }

    public void onBackPressed(){
        // Establecer el resultado como RESULT_OK
        setResult(RESULT_OK);
        finish();
    }

    private void setRecyclerView() {
        VersionsAdapter versionsAdapter = new VersionsAdapter(versionsList);
        recyclerView.setAdapter(versionsAdapter);
    }

    private void initData(){

        versionsList = new ArrayList<>();


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("faq").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(QueryDocumentSnapshot document: task.getResult()){
                    String pregunta = document.getString("pregunta");
                    String respuesta = document.getString("respuesta");
                    versionsList.add(new Versions(pregunta, respuesta));
                }
                setRecyclerView();
            }
            else{
                Toast.makeText(this, "Para obtener direcciones, necesitamos tu ubicación para mejorar la precisión de los resultados.", Toast.LENGTH_SHORT).show();
            }
        }
        );
    }

}