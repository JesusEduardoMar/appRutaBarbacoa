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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
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
    private String idReferencia, tipoReferencia;

    // Nombre de campo adecuado para cada item o referencia
    static String BARBACOA = "idBarbacoa";
    static String PULQUE = "idPulque";
    static String EVENTO = "idEvento";


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
        idReferencia = obtenerIdReferencia();

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

    // Método para obtener la información de la barbacoa ?
    private void obtenerInformacionBarbacoa() throws Exception {
        Intent intent = getIntent();

        if (intent != null) {
            // Obtenemos el ID de la barbacoa desde la intención
            idReferencia = intent.getStringExtra(tipoReferencia);
            if (idReferencia == null || idReferencia.isEmpty()) {
                Toast.makeText(this, "Error: ID de barbacoa no válida", Toast.LENGTH_SHORT).show();
                throw new Exception("Id de barbacoa inválido");
            }
        }

        // Obtenemos el nombre de la barbacoa desde la intención
        String name = (intent != null) ? intent.getExtras().getString("titleTxt") : null;
        // Obtenemos el nombre de la barbacoa desde la intención
        totalCalificaciones = (intent != null) ? intent.getExtras().getInt("totalCalificaciones") : null;
        // Obtenemos el nombre de la barbacoa desde la intención
        promedioCalificaciones = (intent != null) ? intent.getExtras().getFloat("promedioCalificaciones") : null;

        // Verificamos la existencia del nombre
        if (name != null) {
            // Configuramos los elementos de la interfaz de usuario con la información obtenida
            titleText.setText(name);

            String promedio = String.format("%.1f", promedioCalificaciones);
            calificacionScore.setText(promedio);
            calificacionBar.setRating(promedioCalificaciones);
            calificacionTotal.setText(totalCalificaciones + "");

            // Mostramos los comentarios de la barbacoa en la que estamos comentando
            mostrarComentarios();

        } else {
            // Manejo de errores
            Log.e("DetailFreixenetActivity", "El nombre de la barbacoa es nulo en la intención.");
        }
    }


    // Método para mostrar los comentarios de la respectiva barbacoa
    private void mostrarComentarios() {

        // Consulta en Firestore para obtener comentarios relacionados con la referencia actual
        mFirestore.collection("opiniones")
                .whereEqualTo(tipoReferencia, idReferencia)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Limpiamos la lista antes de añadir las nuevas opiniones
                        opinionesList.clear();

                        // Iteramos sobre los documentos obtenidos en la consulta
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Extraemos los campos del documento
                            String comentario = document.getString("comentario");
                            String idUsuario = document.getString("idUsuario");
                            float calificacion = document.getDouble("calificacion").floatValue();
                            Date fecha = document.getDate("timestamp");

                            // Creamos un nuevo objeto Opinion para la referencia adecuada
                            Opinion nuevaOpinion = null;
                            if(tipoReferencia.equals(BARBACOA)) {
                                nuevaOpinion = new Opinion(idUsuario, comentario, calificacion, idReferencia, null, null, fecha);
                            }
                            else if(tipoReferencia.equals(EVENTO)) {
                                nuevaOpinion = new Opinion(idUsuario, comentario, calificacion, null, idReferencia, null, fecha);
                            }
                            else if(tipoReferencia.equals(PULQUE)) {
                                nuevaOpinion = new Opinion(idUsuario, comentario, calificacion, null, null, idReferencia, fecha);
                            }
                            else{
                                // Manejo de errores
                                Log.e( "Comentario -> " + tipoReferencia, "Error al obtener comentarios", task.getException());
                            }
                            // Agregamos la nueva opinión a la lista
                            opinionesList.add(nuevaOpinion);
                        }

                        // Notificamos al adaptador sobre los cambios en la lista
                        comentarioAdapter.notifyDataSetChanged();
                    } else {
                        // Manejo de errores
                        Log.e("DetailFreixenetActivity", "Error al obtener comentarios", task.getException());
                    }
                });

    }

    // Método para obtener el ID de la barbacoa actual
    private String obtenerIdReferencia() {
        Intent intent = getIntent();
        if (intent != null) {
            // Obtenemos que la referencia del comentario (Barbacoa, pulque o evento)
            tipoReferencia = intent.getStringExtra("tipoReferencia");
            if(tipoReferencia != null){
                // Obtenemos el ID de la barbacoa desde la intención
                String idReferencia = intent.getStringExtra(tipoReferencia);
                if (idReferencia != null && !idReferencia.isEmpty()) {
                    return idReferencia;
                }

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