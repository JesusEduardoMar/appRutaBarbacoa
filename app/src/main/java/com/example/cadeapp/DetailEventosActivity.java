package com.example.cadeapp;

import android.content.Intent;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DetailEventosActivity extends AppCompatActivity {

    private TextView titleText, addressText, textDescription, horarioTextView;
    private TextView calificacionScore, calificacionTotal;
    private RatingBar calificacionBar;
    private FirebaseFirestore mFirestore;
    private String idEvento;

    private RecyclerView recyclerViewComentarios;
    private OpinionAdapter comentarioAdapter;
    private List<Opinion> opinionesList;

    // Para cargar las imagenes, este es de Kevan
    private RecyclerView imagesRecycler1;
    private ItemsAdapterHistoria itemsAdapterHistoria;
    private List<String> items;
    private int totalCalificaciones;
    private float promedioCalificaciones;
    //////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailfreixenet);

        // Inicializamos Firestore
        mFirestore = FirebaseFirestore.getInstance();

        //Aquí encontramos las referencias a los elementos de la interfaz de usuario
        titleText = findViewById(R.id.titleText);
        textDescription = findViewById(R.id.textDescription);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textDescription.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }
        addressText = findViewById(R.id.addressText);

        horarioTextView = findViewById(R.id.horarioTextView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textDescription.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }

        recyclerViewComentarios = findViewById(R.id.recyclerViewComentarios);
        recyclerViewComentarios.setLayoutManager(new LinearLayoutManager(this));

        EditText editTextComentario = findViewById(R.id.editTextComentario);
        RatingBar ratingBarOpinion = findViewById(R.id.ratingBarOpinion);
        Button botonEnviarOpinion = findViewById(R.id.botonEnviarOpinion);
        Button botonMostrarComentarios = findViewById(R.id.verMasComentariosButton);


        //Para cargar las imagenes en el recycler view(Kevan)
        imagesRecycler1 = findViewById(R.id.imagesRecycler);
        imagesRecycler1.setHasFixedSize(true);
        imagesRecycler1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        items = new ArrayList<>();
        itemsAdapterHistoria = new ItemsAdapterHistoria(items, this);
        imagesRecycler1.setAdapter(itemsAdapterHistoria);

        // Inicializamos la lista de opiniones y también su adaptador
        opinionesList = new ArrayList<>();
        comentarioAdapter = new OpinionAdapter(opinionesList);
        recyclerViewComentarios.setAdapter(comentarioAdapter);

        // Calificación general
        calificacionScore = findViewById(R.id.calificacionLugarScore);
        calificacionTotal = findViewById(R.id.calificacionLugarTotal);
        calificacionBar = findViewById(R.id.calificacionLugarBar);

        // Obtenemos el ID del evento actual
        idEvento = obtenerIdEvento();

        cargarImagenesDesdeFirestore(idEvento);

        // Obtenemos el ID del usuario ya autenticado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = (user != null) ? user.getUid() : null;

        // Obtenemos y mostramos las opiniones que hay
        obtenerYMostrarOpiniones();

        // Botón de enviar opinión
        botonEnviarOpinion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarOpinion(editTextComentario, ratingBarOpinion, userId, idEvento);
            }
        });
        // Obtenemos el nombre del evento
        obtenerInformacionEvento();

        botonMostrarComentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommentsActivity();
            }
        });

        // Mostrar el botón para regresar y eliminar title
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Cargar las imagenes en el recyclerview desde firestore
    private void cargarImagenesDesdeFirestore(String lugarId) {
        mFirestore.collection("imagesall")
                .whereEqualTo("idEvento", lugarId)
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
                    } else {
                    }
                });
    }

    // Método para obtener y mostrar las opiniones que hay
    private void obtenerYMostrarOpiniones() {
        mFirestore.collection("opiniones")
                .whereEqualTo("idEvento", idEvento)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Reiniciamos la lista de opiniones
                        opinionesList.clear();
                        // Reiniciamos los valores del total y promedio de opiniones
                        totalCalificaciones = 0;
                        promedioCalificaciones = 0;

                        // Iteramos sobre los documentos de la consulta
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Extrae campos del documento
                            String nombreUsuario = document.getString("nombreUsuario");
                            String comentario = document.getString("comentario");
                            float calificacion = document.getDouble("calificacion").floatValue();
                            Date fecha = document.getDate("timestamp");

                            totalCalificaciones += 1;
                            promedioCalificaciones += calificacion;

                            // Condición para solo mostrar primeros 3 comentarios
                            if(totalCalificaciones <= 3) {
                                // Crea un nuevo objeto Opinion con los datos del documento
                                Opinion nuevaOpinion = new Opinion(nombreUsuario, comentario, calificacion, null, idEvento, fecha);

                                // Agrega la nueva opinión a la lista
                                opinionesList.add(nuevaOpinion);

                            }
                        }

                        // Obtener y mostrar promedio general
                        promedioCalificaciones = promedioCalificaciones / totalCalificaciones;
                        String promedio = String.format("%.1f", promedioCalificaciones);
                        calificacionScore.setText(promedio);
                        calificacionBar.setRating(promedioCalificaciones);
                        calificacionTotal.setText(totalCalificaciones + "");

                        // Verificar si la caja de comentarios está vacía
                        verificarComentariosVacios();

                        // Notifica al adaptador que los datos han cambiado
                        comentarioAdapter.notifyDataSetChanged();
                    } else {
                        // Manejo de error en caso de fallo en la consulta
                        Log.e("DetailFreixenetActivity", "Error al obtener las opiniones", task.getException());
                    }
                });
    }

    // Método para enviar una opinión
    private void enviarOpinion(EditText editTextComentario, RatingBar ratingBarOpinion, String userId, String idEvento) {
        try {
            // Obtenemos los datos de la interfaz de usuario
            String comentario = editTextComentario.getText().toString();
            float calificacion = ratingBarOpinion.getRating();

            // Validamos los campos
            if (comentario.isEmpty()) {
                Toast.makeText(DetailEventosActivity.this, "Por favor, ingrese su comentario", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificamos el ID de usuario antes de enviar la opinión
            if (userId != null) {
                // Consultamso en Firestore para obtener el nombre del usuario
                mFirestore.collection("usuarios")
                        .document(userId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // Extraemos el nombre de usuario
                                String nombreUsuario = documentSnapshot.getString("nombre");

                                // Creamos un nuevo objeto Opinion
                                Opinion nuevaOpinion = new Opinion(nombreUsuario, comentario, calificacion, null, idEvento);

                                // Agregamos la nueva opinión a la colección de opiniones en Firestore
                                mFirestore.collection("opiniones")
                                        .add(nuevaOpinion)
                                        .addOnSuccessListener(documentReference -> {
                                            Toast.makeText(DetailEventosActivity.this, "Opinión enviada con éxito", Toast.LENGTH_SHORT).show();

                                            // Limpiamos los campos de la interfaz de usuario después de enviar una nueva opinión
                                            editTextComentario.setText("");
                                            ratingBarOpinion.setRating(0.0f);

                                            // Actualizamos y mostramos las opiniones después de enviar una nueva
                                            obtenerYMostrarOpiniones();
                                        })
                                        .addOnFailureListener(e -> {
                                            // Mensaje de error en caso de fallo al enviar la opinión
                                            Toast.makeText(DetailEventosActivity.this, "Error al enviar la opinión", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Manejo de errores
                            Log.e("DetailEventosActivity", "Error al obtener el documento del usuario", e);
                        });
            }
        } catch (Exception e) {
            // Manejo de errores
            e.printStackTrace();
            Toast.makeText(DetailEventosActivity.this, "Error inesperado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Método para obtener la información del evento
    private void obtenerInformacionEvento() {
        // Obtenemos el nombre del evento desde la intención
        Intent intent = getIntent();
        String name = (intent != null) ? intent.getExtras().getString("titleTxt") : null;

        // Verificamos la existencia del nombre
        if (name != null) {
            // Consultamos en Firestore para obtener información del evento
            mFirestore.collection("eventos").whereEqualTo("nombre_evento", name).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        // Iteramos sobre los resultados de la consulta
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Extraemos los campos del documento
                            String nombre = document.getString("nombre_evento");
                            String info = document.getString("info_evento");
                            String ubicacion = document.getString("ubicacion_evento");
                            String horario = document.getString("horario_evento");
                            String imageUrl = document.getString("url");

                            // Configuramos los elementos de la interfaz de usuario con la información obtenida
                            titleText.setText(nombre);
                            textDescription.setText(info);
                            addressText.setText(ubicacion);
                            horarioTextView.setText(horario);

                        }
                    } else {
                        // Manejo de errores
                        Log.e("DetailFreixenetActivity", "Error al obtener la información del evento", task.getException());
                    }
                }
            });
        } else {
            // Manejo de errores
            Log.e("DetailFreixenetActivity", "El nombre del evento es nulo en la intención.");
        }
    }

    // Método para verificar si la caja de comentarios está vacía
    private void verificarComentariosVacios() {

        // Verificamos si la lista de opiniones está vacía
        if (opinionesList.isEmpty()) {
            // Si la lista está vacía, mostramos el mensaje de ninguna opinión
            findViewById(R.id.noOpinionMessage).setVisibility(View.VISIBLE);

            // Ocultar vistas que muestran puntaje
            findViewById(R.id.calificacionLugarScore).setVisibility(View.GONE);
            findViewById(R.id.calificacionLugarBar).setVisibility(View.GONE);
            findViewById(R.id.calificacionLugarTotal).setVisibility(View.GONE);
            findViewById(R.id.verMasComentariosButton).setVisibility(View.GONE);
        } else {
            // Si hay opiniones, ocultamos el mensaje
            findViewById(R.id.noOpinionMessage).setVisibility(View.GONE);

            // Mostrar Score
            findViewById(R.id.calificacionLugarScore).setVisibility(View.VISIBLE);
            findViewById(R.id.calificacionLugarBar).setVisibility(View.VISIBLE);
            findViewById(R.id.calificacionLugarTotal).setVisibility(View.VISIBLE);
            findViewById(R.id.verMasComentariosButton).setVisibility(View.VISIBLE);
        }
    }

    // Método para obtener el ID del evento actual
    private String obtenerIdEvento() {
        Intent intent = getIntent();
        if (intent != null) {
            // Obtenemos el ID del evento desde la intención
            String idEvento = intent.getStringExtra("idEvento");
            if (idEvento != null && !idEvento.isEmpty()) {
                return idEvento;
            }
        }
        Toast.makeText(this, "Error: ID de evento no válido", Toast.LENGTH_SHORT).show();
        return "";
    }


    private void showCommentsActivity() {

        Intent intent = new Intent(this, Reviews.class);
        intent.putExtra("idEvento", idEvento);
        intent.putExtra("titleTxt", titleText.getText());
        intent.putExtra("totalCalificaciones", totalCalificaciones);
        intent.putExtra("promedioCalificaciones", promedioCalificaciones);
        this.startActivity(intent);
    }

}
