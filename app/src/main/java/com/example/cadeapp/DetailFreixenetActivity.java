package com.example.cadeapp;

import static com.google.firebase.firestore.AggregateField.average;
import static com.google.firebase.firestore.AggregateField.count;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DetailFreixenetActivity extends AppCompatActivity {
    private TextView titleText, addressText, textDescription, horarioTextView, comentariosText;
    private TextView calificacionScore, calificacionTotal;
    private RatingBar calificacionBar;
    private int contador;
    private Button boton01, boton02;
    private TextView cajaDeTexto;
    private FirebaseFirestore mFirestore;
    private String idBarbacoa;

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

    LinearLayout ubicacionD2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailfreixenet);

        ubicacionD2 = findViewById(R.id.ubicacionD);

        // Inicializamos Firestore
        mFirestore = FirebaseFirestore.getInstance();

        //Aquí encontramos las referencias a los elementos de la interfaz de usuario
        titleText = findViewById(R.id.titleText);
        textDescription = findViewById(R.id.textDescription);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textDescription.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }
        addressText = findViewById(R.id.addressText);
        //boton01 = findViewById(R.id.botonRestar);
        //boton02 = findViewById(R.id.botonSumar);
        //cajaDeTexto = findViewById(R.id.textcont);
        comentariosText = findViewById(R.id.comentariosText);
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

        // Obtenemos el ID de la barbacoa actual
        idBarbacoa = obtenerIdBarbacoa();

        cargarImagenesDesdeFirestore(idBarbacoa);

        // Obtenemos el ID del usuario ya autenticado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = (user != null) ? user.getUid() : null;

        // Obtenemos y mostramos las opiniones que hay
        obtenerYMostrarOpiniones();

        // Botón de enviar opinión
        botonEnviarOpinion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarOpinion(editTextComentario, ratingBarOpinion, userId, idBarbacoa);
            }
        });
        // Obtenemos el nombre de la barbacoa
        obtenerInformacionBarbacoa();

        // Configuramos los listeners para los botones de incremento y decremento
        // configurarListenersBotones();

        botonMostrarComentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommentsDialog();
            }
        });

        ubicacionD2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailFreixenetActivity.this, MainActivity.class);
                intent.putExtra("selectedItemId", 5); // Selecciona el ítem con el ID 5
                //con esta linea limpiamos las actividades para que no se muestren mas que una sola en lugar de cada que abramos un lugar
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }


    // Cargar las imagenes en el recyclerview desde firestore
    private void cargarImagenesDesdeFirestore(String lugarId) {
        mFirestore.collection("imagesall")
                .whereEqualTo("idBarbacoa", lugarId)
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
    // Método para enviar una opinión
    private void enviarOpinion(EditText editTextComentario, RatingBar ratingBarOpinion, String userId, String idBarbacoa) {
        try {
            // Obtenemos los datos de la interfaz de usuario
            String comentario = editTextComentario.getText().toString();
            float calificacion = ratingBarOpinion.getRating();

            // Validamos los campos
            if (comentario.isEmpty()) {
                Toast.makeText(DetailFreixenetActivity.this, "Por favor, ingrese su comentario", Toast.LENGTH_SHORT).show();
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
                                Opinion nuevaOpinion = new Opinion(nombreUsuario, comentario, calificacion, idBarbacoa, null);

                                // Agregamos la nueva opinión a la colección de opiniones en Firestore
                                mFirestore.collection("opiniones")
                                        .add(nuevaOpinion)
                                        .addOnSuccessListener(documentReference -> {
                                            Toast.makeText(DetailFreixenetActivity.this, "Opinión enviada con éxito", Toast.LENGTH_SHORT).show();

                                            // Limpiamos los campos de la interfaz de usuario después de enviar una nueva opinión
                                            editTextComentario.setText("");
                                            ratingBarOpinion.setRating(0.0f);

                                            // Actualizamos y mostramos las opiniones después de enviar una nueva
                                            obtenerYMostrarOpiniones();
                                        })
                                        .addOnFailureListener(e -> {
                                            // Mensaje de error en caso de fallo al enviar la opinión
                                            Toast.makeText(DetailFreixenetActivity.this, "Error al enviar la opinión", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Manejo de errores
                            Log.e("DetailFreixenetActivity", "Error al obtener el documento del usuario", e);
                        });
            }
        } catch (Exception e) {
            // Manejo de errores
            e.printStackTrace();
            Toast.makeText(DetailFreixenetActivity.this, "Error inesperado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    // Método para obtener la información de la barbacoa ?
    private void obtenerInformacionBarbacoa() {
        // Obtenemos el nombre de la barbacoa desde la intención
        Intent intent = getIntent();
        String name = (intent != null) ? intent.getExtras().getString("titleTxt") : null;

        // Verificamos la existencia del nombre
        if (name != null) {
            // Consultamos en Firestore para obtener información de la barbacoa
            mFirestore.collection("barbacoas").whereEqualTo("nombre_barbacoa", name).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        // Iteramos sobre los resultados de la consulta
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Extraemos los campos del documento
                            String nombre = document.getString("nombre_barbacoa");
                            String info = document.getString("info_barbacoa");
                            String ubicacion = document.getString("ubicacion_barbacoa");
                            String horario = document.getString("horario_barbacoa");
                            String imageUrl = document.getString("url");

                            // Configuramos los elementos de la interfaz de usuario con la información obtenida
                            titleText.setText(nombre);
                            textDescription.setText(info);
                            addressText.setText(ubicacion);
                            horarioTextView.setText(horario);
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
/*
    // Método para configurar los listeners de los botones de incrementar y decrementar
    private void configurarListenersBotones() {
        // Listener para el botón de restar
        boton01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contador--;
                cajaDeTexto.setText(Integer.toString(contador));
            }
        });

        // Listener para el botón de sumar
        boton02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contador++;
                cajaDeTexto.setText(Integer.toString(contador));
            }
        });

        // Muestra el valor inicial en el TextView
        cajaDeTexto.setText(Integer.toString(contador));
    }*/

    // Método para mostrar los comentarios de la respectiva barbacoa
    private void mostrarComentariosBarbacoa() {

        // Consulta en Firestore para obtener comentarios relacionados con la barbacoa actual
        mFirestore.collection("opiniones")
                .whereEqualTo("idBarbacoa", idBarbacoa).limit(3)
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

    // Mostrar todos los comentarios
    private void showCommentsDialog(){
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


}


