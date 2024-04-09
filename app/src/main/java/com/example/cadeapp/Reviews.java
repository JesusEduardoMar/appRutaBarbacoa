package com.example.cadeapp;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cadeapp.databinding.ActivityScrollingBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Reviews extends AppCompatActivity {

    protected ActivityScrollingBinding binding;

    private TextView titleText;
    private OpinionAdapter comentarioAdapter;
    private List<Opinion> opinionesList;
    private TextView calificacionScore, calificacionTotal;
    private RatingBar calificacionBar;
    private int totalCalificaciones;
    private float promedioCalificaciones;
    private RecyclerView recyclerViewComentarios;
    private FirebaseFirestore mFirestore;
    private String idBarbacoa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        // Inicializamos Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Referencia a objetos de interfaz
        titleText = findViewById(R.id.titleText);

        recyclerViewComentarios = findViewById(R.id.recyclerViewComentarios);
        // Calificación general
        calificacionScore = findViewById(R.id.calificacionLugarScore);
        calificacionTotal = findViewById(R.id.calificacionLugarTotal);
        calificacionBar = findViewById(R.id.calificacionLugarBar);
        recyclerViewComentarios.setLayoutManager(new LinearLayoutManager(this));


        // Inicializamos la lista de opiniones y también su adaptador
        opinionesList = new ArrayList<>();
        comentarioAdapter = new OpinionAdapter(opinionesList);
        recyclerViewComentarios.setAdapter(comentarioAdapter);

        // Obtenemos el ID del evento actual
        idBarbacoa = obtenerIdBarbacoa();

        // Obtenemos y mostramos las opiniones que hay
        obtenerYMostrarOpiniones();

        // Obtenemos la información del evento
        try {
            obtenerInformacionBarbacoa();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Método para obtener y mostrar las opiniones que hay
    private void obtenerYMostrarOpiniones() {
        mFirestore.collection("opiniones")
                .whereEqualTo("idBarbacoa", idBarbacoa)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Usamos una lista para almacenar objetos Opinion
                        List<Opinion> listaOpiniones = new ArrayList<>();

                        // Iteramos sobre los documentos de la consulta
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Extrae campos del documento
                            String nombreUsuario = document.getString("nombreUsuario");
                            String comentario = document.getString("comentario");
                            float calificacion = document.getDouble("calificacion").floatValue();

                            totalCalificaciones += 1;
                            promedioCalificaciones += calificacion;

                            // Crea un nuevo objeto Opinion con los datos del documento
                            Opinion nuevaOpinion = new Opinion(nombreUsuario, comentario, calificacion, idBarbacoa, null);

                            // Agrega la nueva opinión a la lista
                            listaOpiniones.add(nuevaOpinion);
                        }

                        // Obtener y mostrar promedio general
                        promedioCalificaciones = promedioCalificaciones / totalCalificaciones;
                        String promedio = String.format("%.1f", promedioCalificaciones);
                        calificacionScore.setText(promedio);
                        calificacionBar.setRating(promedioCalificaciones);
                        calificacionTotal.setText(totalCalificaciones + " opiniones");

                        // Notifica al adaptador que los datos han cambiado
                        comentarioAdapter.notifyDataSetChanged();
                    } else {
                        // Manejo de error en caso de fallo en la consulta
                        Log.e("DetailFreixenetActivity", "Error al obtener las opiniones", task.getException());
                    }
                });
    }


    // Método para obtener la información de la barbacoa ?
    private void obtenerInformacionBarbacoa() throws Exception {
        Intent intent = getIntent();

        if (intent != null) {
            // Obtenemos el ID de la barbacoa desde la intención
            idBarbacoa = intent.getStringExtra("idBarbacoa");
            if (idBarbacoa == null || idBarbacoa.isEmpty()) {
                Toast.makeText(this, "Error: ID de barbacoa no válida", Toast.LENGTH_SHORT).show();
                throw new Exception("Id de barbacoa inválido");
            }
        }

        // Obtenemos el nombre de la barbacoa desde la intención
        String name = (intent != null) ? intent.getExtras().getString("titleTxt") : null;
        // Obtenemos el nombre de la barbacoa desde la intención
        totalCalificaciones = (intent != null) ? intent.getExtras().getInt("promedioCalificaciones") : null;
        // Obtenemos el nombre de la barbacoa desde la intención
        promedioCalificaciones = (intent != null) ? intent.getExtras().getFloat("promedioCalificaciones") : null;

        // Verificamos la existencia del nombre
        if (name != null) {
            // Consultamos en Firestore para obtener información de la barbacoa
            mFirestore.collection("barbacoas").whereEqualTo("nombre_barbacoa", name).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        // Iteramos sobre los resultados de la consulta
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Configuramos los elementos de la interfaz de usuario con la información obtenida
                            titleText.setText(name);

                            String promedio = String.format("%.1f", promedioCalificaciones);
                            calificacionScore.setText(promedio);
                            calificacionBar.setRating(promedioCalificaciones);
                            calificacionTotal.setText(totalCalificaciones + " opiniones");

                            // Mostramos los comentarios de la barbacoa en la que estamos comentando
                            mostrarComentariosBarbacoa();
                        }
                    } else {
                        // Manejo de errores
                        Log.e("DetailFreixenetActivity", "Error al obtener la información de la barbacoa", task.getException());
                    }
                }
            });
        } else {
            // Manejo de errores
            Log.e("DetailFreixenetActivity", "El nombre de la barbacoa es nulo en la intención.");
        }
    }


    // Método para mostrar los comentarios de la respectiva barbacoa
    private void mostrarComentariosBarbacoa() {

        // Consulta en Firestore para obtener comentarios relacionados con la barbacoa actual
        mFirestore.collection("opiniones")
                .whereEqualTo("idBarbacoa", idBarbacoa)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Limpiamos la lista antes de añadir las nuevas opiniones
                        opinionesList.clear();
                        // Iteramos sobre los documentos obtenidos en la consulta
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Extraemos los campos del documento
                            String comentario = document.getString("comentario");
                            String nombreUsuario = document.getString("nombreUsuario");
                            float calificacion = document.getDouble("calificacion").floatValue();
                            totalCalificaciones += 1;
                            promedioCalificaciones += calificacion;

                            // Creamos un nuevo objeto Opinion
                            Opinion nuevaOpinion = new Opinion(nombreUsuario, comentario, calificacion, idBarbacoa, null);

                            // Agregamos la nueva opinión a la lista
                            opinionesList.add(nuevaOpinion);
                        }
                        promedioCalificaciones = promedioCalificaciones / totalCalificaciones;

                        // Notificamos al adaptador sobre los cambios en la lista
                        comentarioAdapter.notifyDataSetChanged();
                    } else {
                        // Manejo de errores
                        Log.e("DetailFreixenetActivity", "Error al obtener comentarios", task.getException());
                    }
                });

    }

    // Método para obtener el ID de la barbacoa actual
    private String obtenerIdBarbacoa() {
        Intent intent = getIntent();
        if (intent != null) {
            // Obtenemos el ID de la barbacoa desde la intención
            String idBarbacoa = intent.getStringExtra("idBarbacoa");
            if (idBarbacoa != null && !idBarbacoa.isEmpty()) {
                return idBarbacoa;
            }
        }
        Toast.makeText(this, "Error: ID de barbacoa no válida", Toast.LENGTH_SHORT).show();
        return "";
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}