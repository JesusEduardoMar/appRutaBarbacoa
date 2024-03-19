package com.example.cadeapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.timessquare.CalendarPickerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import java.lang.Exception;

public class MainActivity extends AppCompatActivity {
    // Variables
    private RecyclerView.Adapter adapterEventos,adapterVinedos;
    private RecyclerView recyclerViewEventos, recyclerViewVinedos;
    private MeowBottomNavigation bottomNavigation;
    TextView txt_Nombre,txt_correo,txt_telefono,txt_Nombre2,txt_correo2;
    Button cerrar;
    LinearLayout notificationContainer;


    RelativeLayout  menu, profile, home, notifications, map;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore mFirestore;
    private GoogleSignInClient mGoogleSignInClient;
    ConstraintLayout card3;
    ConstraintLayout card4;
    RecyclerView recyclerView;
    ItemsAdapterVinedos itemsAdapterVinedos;
    ItemsAdapterEventos itemsAdapterEventos;
    ArrayList<ItemsDomainVinedos> items;
    ArrayList<ItemsDomainEventos> items2;
    ProgressBar pbProgressMain;
    private Task<QuerySnapshot> eventosTask;
    RelativeLayout relativeContact1;
    RelativeLayout relativeFAQ1;

    // Método llamado a la hora de crear la actividad
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        setContentView(R.layout.activity_main);
        //notifications = findViewById(R.id.notifications);
        // Dentro de tu método onCreate después de setContentView(R.layout.activity_main);
        LinearLayout notificationContainer = findViewById(R.id.notificationContainerr);

        Button button3 = findViewById(R.id.button3);

            // --> Inicialización de Firebase y otros elementos de la interfaz de usuario
            mFirestore = FirebaseFirestore.getInstance();
            pbProgressMain = findViewById(R.id.progress_main);

            // --> Configuración de RecyclerView para eventos
            recyclerView = findViewById(R.id.viewEventos);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

            items2 = new ArrayList<>();
            itemsAdapterEventos = new ItemsAdapterEventos(items2, this);
            recyclerView.setAdapter(itemsAdapterEventos);

            mFirestore.collection("eventos").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    pbProgressMain.setVisibility(View.VISIBLE);
                    if(error != null){
                        Log.e("Firestore error", error.getMessage());
                        return;
                    }
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            ItemsDomainEventos evento = dc.getDocument().toObject(ItemsDomainEventos.class);
                            items2.add(evento);

                            // Crear una nueva vista de notificación
                            View notificationView = getLayoutInflater().inflate(R.layout.layout_notification, null);
                            TextView notificationMessage = notificationView.findViewById(R.id.notificationMessage);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                notificationMessage.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
                            }
                            String mensaje = "¡Nuevo Evento Disponible! " + evento.getNombre_evento() + ". ¡No te lo pierdas! el "+ evento.getFecha_evento();
                            notificationMessage.setText(mensaje);

                            // Ajustar márgenes para la vista de notificación
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            int marginPixels = (int) (12 * getResources().getDisplayMetrics().density);
                            layoutParams.setMargins(0, 0, 0, marginPixels);

                            // Aplicar los parámetros de diseño a la vista de notificación
                            notificationView.setLayoutParams(layoutParams);

                            // Agregar la nueva notificación al LinearLayout
                            notificationContainer.addView(notificationView);
                        }
                    }
                    itemsAdapterEventos.notifyDataSetChanged();
                    pbProgressMain.setVisibility(View.GONE);
                }
            });

            // --> Configuración de RecyclerView para las barbacoas (sólo 5 lugares)
            recyclerView = findViewById(R.id.viewViñedos);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

            items = new ArrayList<>();
            itemsAdapterVinedos = new ItemsAdapterVinedos(items, this);
            recyclerView.setAdapter(itemsAdapterVinedos);
            mFirestore.collection("barbacoas")
                    .orderBy("nombre_barbacoa") // Filtramos por nombre
                    //.limit(3)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(error != null){
                                Log.e("Firestore error", error.getMessage());
                                return;
                            }
                            //items.clear(); // Limpiar la lista actual de lugares
                            for (DocumentChange dc : value.getDocumentChanges()) {
                                switch (dc.getType()) {
                                    case ADDED:
                                        items.add(dc.getNewIndex(), dc.getDocument().toObject(ItemsDomainVinedos.class));
                                        itemsAdapterVinedos.notifyItemInserted(dc.getNewIndex());// Notificar al adaptador que hemos insertado datos

                                        // Crear una nueva vista de notificación
                                        View notificationView = getLayoutInflater().inflate(R.layout.layout_notification, null);
                                        TextView notificationMessage = notificationView.findViewById(R.id.notificationMessage);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            notificationMessage.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
                                        }
                                        String mensaje = dc.getDocument().getString("nombre_barbacoa")+ " está disponible en Cadereyta, ¡Ven a Conocerlo!";
                                        notificationMessage.setText(mensaje);

                                        // Ajustar márgenes para la vista de notificación
                                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                        );
                                        int marginPixels = (int) (12 * getResources().getDisplayMetrics().density);
                                        layoutParams.setMargins(0, 0, 0, marginPixels);

                                        // Aplicar los parámetros de diseño a la vista de notificación
                                        notificationView.setLayoutParams(layoutParams);

                                        // Agregar la nueva notificación al LinearLayout
                                        notificationContainer.addView(notificationView);

                                        break;
                                    case MODIFIED:
                                        items.set(dc.getOldIndex(), dc.getDocument().toObject(ItemsDomainVinedos.class));
                                        itemsAdapterVinedos.notifyItemChanged(dc.getOldIndex());// Notificar al adaptador que los datos han cambiado
                                        break;
                                    case REMOVED:
                                        items.remove(dc.getOldIndex());
                                        itemsAdapterVinedos.notifyItemRemoved(dc.getOldIndex());// Notificar al adaptador que los datos han sido eliminados

                                        // Crear una nueva vista de notificación y agregarla
                                        View notificationViewRemoved = getLayoutInflater().inflate(R.layout.layout_notification, null);
                                        TextView notificationMessageRemoved = notificationViewRemoved.findViewById(R.id.notificationMessage);
                                        String removedMessage = dc.getDocument().getString("nombre_barbacoa") + " ya no está disponible en Cadereyta :(";
                                        notificationMessageRemoved.setText(removedMessage);
                                        // Ajustar márgenes para la vista de notificación
                                        LinearLayout.LayoutParams layoutParamsRemoved = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                        );
                                        int marginPixelsRemoved = (int) (12 * getResources().getDisplayMetrics().density);
                                        layoutParamsRemoved.setMargins(0, 0, 0, marginPixelsRemoved);
                                        notificationViewRemoved.setLayoutParams(layoutParamsRemoved);
                                        notificationContainer.addView(notificationViewRemoved);

                                        break;
                                }
                            }
                            pbProgressMain.setVisibility(View.GONE);
                        }
                    });

        // --> Configuración de la barra de navegación inferior (MeowBottomNavigation)
        bottomNavigation = findViewById(R.id.bottomNavigation);
        cerrar = findViewById(R.id.cerrar_sesion);
        menu = findViewById(R.id.menu);
        profile = findViewById(R.id.profile);
        home = findViewById(R.id.home);
        notifications = findViewById(R.id.notifications);
        map = findViewById(R.id.map);
        txt_Nombre = findViewById(R.id.Mostrarnombre);
        txt_Nombre2 = findViewById(R.id.nombre2);
        txt_correo2 = findViewById(R.id.correo2);
        txt_correo = findViewById(R.id.Mostrarcorreo);
        txt_telefono = findViewById(R.id.Mostrartelefono);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        bottomNavigation.show(3,true);
        relativeContact1 = findViewById(R.id.relativeContact);
        relativeFAQ1 = findViewById(R.id.relativeFAQ);

        //-------------Servicios Google----------------
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //Configuracion para el uso de inicio de sesion con google

        card3 = findViewById(R.id.cardInicio3);
        card4 = findViewById(R.id.cardInicio4);

        // Añadir los íconos a la barra de navegación inferior
        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.configuracion));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.baseline_person_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.baseline_home_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.campana));
        bottomNavigation.add(new MeowBottomNavigation.Model(5, R.drawable.baseline_place_24));

        // Configuración de los listeners para la barra de navegación inferior
        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                switch (model.getId()){
                    case 1:
                        menu.setVisibility(View.VISIBLE);
                        profile.setVisibility(View.GONE);
                        home.setVisibility(View.GONE);
                        notifications.setVisibility(View.GONE);
                        map.setVisibility(View.GONE);
                        break;
                    case 2:
                        menu.setVisibility(View.GONE);
                        profile.setVisibility(View.VISIBLE);
                        home.setVisibility(View.GONE);
                        notifications.setVisibility(View.GONE);
                        map.setVisibility(View.GONE);
                        break;
                    case 3:
                        menu.setVisibility(View.GONE);
                        profile.setVisibility(View.GONE);
                        home.setVisibility(View.VISIBLE);
                        notifications.setVisibility(View.GONE);
                        map.setVisibility(View.GONE);
                        break;
                    case 4:
                        menu.setVisibility(View.GONE);
                        profile.setVisibility(View.GONE);
                        home.setVisibility(View.GONE);
                        notifications.setVisibility(View.VISIBLE);
                        map.setVisibility(View.GONE);
                        break;
                    case 5:
                        menu.setVisibility(View.GONE);
                        profile.setVisibility(View.GONE);
                        home.setVisibility(View.GONE);
                        notifications.setVisibility(View.GONE);
                        map.setVisibility(View.VISIBLE);
                        break;
                }
                return null;
            }
        });

        // --> Configuración de listeners adicionales para la barra de navegación inferior
        bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                switch (model.getId()){
                    case 1:
                        menu.setVisibility(View.VISIBLE);
                        profile.setVisibility(View.GONE);
                        home.setVisibility(View.GONE);
                        notifications.setVisibility(View.GONE);
                        map.setVisibility(View.GONE);
                        break;
                }
                return null;
            }
        });
        bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                switch (model.getId()){
                    case 2:
                        menu.setVisibility(View.GONE);
                        profile.setVisibility(View.VISIBLE);
                        home.setVisibility(View.GONE);
                        notifications.setVisibility(View.GONE);
                        map.setVisibility(View.GONE);
                        break;
                }
                return null;
            }
        });
        bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                switch (model.getId()){
                    case 3:
                        menu.setVisibility(View.GONE);
                        profile.setVisibility(View.GONE);
                        home.setVisibility(View.VISIBLE);
                        notifications.setVisibility(View.GONE);
                        map.setVisibility(View.GONE);
                        break;
                }
                return null;
            }
        });
        bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                switch (model.getId()){
                    case 4:
                        menu.setVisibility(View.GONE);
                        profile.setVisibility(View.GONE);
                        home.setVisibility(View.GONE);
                        notifications.setVisibility(View.VISIBLE);
                        map.setVisibility(View.GONE);
                        break;
                }
                return null;
            }
        });

        bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                switch (model.getId()){
                    case 5:
                        menu.setVisibility(View.GONE);
                        profile.setVisibility(View.GONE);
                        home.setVisibility(View.GONE);
                        notifications.setVisibility(View.GONE);
                        map.setVisibility(View.VISIBLE);
                        break;
                }
                return null;
            }
        });

                                             ////CALENDARIO//////
// Se inicializa el selector de fechas
        Date today = new Date();
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 30);

        CalendarPickerView datePicker = findViewById(R.id.calendarView);
        datePicker.init(today, nextYear.getTime()).withSelectedDate(today);

// Recuperar eventos de la base de datos
        mFirestore.collection("eventos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Asignar la tarea completada a la variable de instancia
                    eventosTask = task;

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Obtener información del evento
                        String nombreEvento = document.getString("nombre_evento");
                        String fechaEventoString = document.getString("fecha_evento");

                        // Verificar si la fecha del evento es nula
                        if (fechaEventoString != null) {
                            // Convertir la fecha del evento de String a Date
                            try {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("d 'de' MMMM 'de' yyyy, hh:mm:ss a", Locale.getDefault());
                                Date fechaEvento = dateFormat.parse(fechaEventoString);
                                if (fechaEvento != null) {
                                    // Marcar la fecha del evento en el calendario
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(fechaEvento);
                                    datePicker.selectDate(cal.getTime());
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                                // Manejar el error de parseo si es necesario
                            }
                        } else {
                            // Manejar el caso en el que fechaEventoString es null
                        }
                    } 
                } else {
                        Log.d(TAG, "Error al obtener eventos: ", task.getException());
                    }
                }
            });

    // Definir el listener para la selección de fechas
            datePicker.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
                @Override
                public void onDateSelected(Date date) {
                    // Convertir la fecha seleccionada a un formato legible
                    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
                    String fechaSeleccionada = dateFormat.format(date);

                    // Verificar si hay un evento en la fecha seleccionada
                    String informacionEvento = "";
                    if (eventosTask != null && eventosTask.isSuccessful()) {
                        for (QueryDocumentSnapshot document : eventosTask.getResult()) {
                            String nombreEvento = document.getString("nombre_evento");
                            String fechaEventoString = document.getString("fecha_evento");

                            // Convertir la fecha del evento de String a Date
                            Date fechaEvento = null;
                            try {
                                SimpleDateFormat dateFormat2 = new SimpleDateFormat("d 'de' MMMM 'de' yyyy, hh:mm:ss a", Locale.getDefault());
                                fechaEvento = dateFormat2.parse(fechaEventoString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                // Manejar el error de parseo si es necesario
                            }

                            if (fechaEvento != null) {
                                Calendar calEvento = Calendar.getInstance();
                                calEvento.setTime(fechaEvento);
                                Calendar calSeleccionada = Calendar.getInstance();
                                calSeleccionada.setTime(date);

                                if (calEvento.get(Calendar.YEAR) == calSeleccionada.get(Calendar.YEAR) &&
                                        calEvento.get(Calendar.MONTH) == calSeleccionada.get(Calendar.MONTH) &&
                                        calEvento.get(Calendar.DAY_OF_MONTH) == calSeleccionada.get(Calendar.DAY_OF_MONTH)) {
                                    // Se encontró un evento en la fecha seleccionada
                                    informacionEvento = nombreEvento;
                                    break; // No es necesario continuar buscando más eventos
                                }
                            }
                        }
                    }

                    // Mostrar la información del evento en el área designada
                    button3.setText("Nombre del evento para la fecha seleccionada: " + informacionEvento);
                }

                @Override
                public void onDateUnselected(Date date) {
                    // No es necesario implementar este método para tu caso
                }
            });
                                                  ////FIN CALENDARIO//////


            // --> Muestra el fragmento del mapa
            Fragment fragment = new Map_Fragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();

            // --> Configuración de listener para el botón de cerrar sesión
            cerrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logout();
                }
            });


        relativeContact1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, contact.class);
                    startActivity(intent);
                    finish();
                }
            });

        relativeFAQ1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FaqActivity.class);
                startActivity(intent);
                finish();
            }
        });
      
           // --> Configuración de listeners para los botones de tarjetas de información (hasta arriba)

        // Ahora te lleva a Visualizar todos los lugares de Barbacoa que hay
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VerTodosLosLugaresActivity.class);
                startActivity(intent);
                finish();
            }
        });
        // Visualizar todos los lugares de Pulque que hay
        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, cardDemasEntradas.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void mostrarSnackbar(String mensaje) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), mensaje, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    // --> Método llamado al iniciar la actividad
        protected void onStart(){
            super.onStart();
            FirebaseUser user = mAuth.getCurrentUser();
            if(user == null){
                irLogin();
            }else{
                cargardatos();
            }
        }

        // --> Método para cargar los datos del usuario desde Firestore
        private void cargardatos(){
            mFirestore.collection("usuarios").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()){
                            String nombre = document.getString("nombre");
                            String correo = document.getString("correo");
                            String telefono = document.getString("telefono");

                            txt_Nombre.setText(nombre);
                            txt_correo.setText(correo);
                            txt_telefono.setText(telefono);
                            txt_Nombre2.setText(nombre);
                            txt_correo2.setText(correo);
                        }
                    }
                }
            });
        }

        // --> Método para cerrar sesión
        private void logout(){
            mAuth.signOut();

            mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        irLogin();
                    }else {
                        mostrarMensaje("No se logro cerrar sesion");
                    }
                }
            });
        }

        // --> Método para ir a la pantalla de inicio de sesión
        private void irLogin(){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        // --> Método para mostrar mensajes de Toast
        private void mostrarMensaje(String mensaje){
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        }
    }
