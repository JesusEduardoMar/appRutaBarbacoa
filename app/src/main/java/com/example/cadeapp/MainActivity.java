package com.example.cadeapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.text.LineBreaker;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarCellView;
import com.squareup.timessquare.CalendarPickerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import java.lang.Exception;

public class MainActivity extends AppCompatActivity {
    // Variables
    private RecyclerView.Adapter adapterEventos,adapterVinedos,adapterPulques;
    private RecyclerView recyclerViewEventos, recyclerViewVinedos, recyclerViewPulques;
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
    ItemsAdapterPulques itemsAdapterPulques;
    ArrayList<ItemsDomainVinedos> items;
    ArrayList<ItemsDomainEventos> items2;
    ArrayList<ItemsDomainPulques> items3;
    ProgressBar pbProgressMain;
    private Task<QuerySnapshot> eventosTask;
    RelativeLayout relativeContact1;
    RelativeLayout relativeFAQ1;
    RelativeLayout relativeInfo1;
    RelativeLayout relativePrivacity1;
    private static final int REQUEST_CODE_CONTACT = 101; //Constante utilizada para regresar a menu desde contact

    private final Date today = new Date(); //fecha actual

    private final Calendar nextYear = Calendar.getInstance();

    // Método llamado a la hora de crear la actividad
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        setContentView(R.layout.activity_main);
        //notifications = findViewById(R.id.notifications);
        LinearLayout notificationContainer = findViewById(R.id.notificationContainerr);
        LinearLayout notificationContainerNuevas = findViewById(R.id.notificationContainerNuevas);
        LinearLayout notificationContainerUltimos7Dias = findViewById(R.id.notificationContainerUltimos7Dias);
        LinearLayout notificationContainerUltimos30Dias = findViewById(R.id.notificationContainerUltimos30Dias);

        Date currentDate = new Date();

        Calendar calendarToday = Calendar.getInstance();
        calendarToday.setTime(currentDate);
        calendarToday.set(Calendar.HOUR_OF_DAY, 0); // Hora del día a las 00:00
        calendarToday.set(Calendar.MINUTE, 0); // Mminutos a 0
        calendarToday.set(Calendar.SECOND, 0); // Segundos a 0
        calendarToday.set(Calendar.MILLISECOND, 0); // Milisegundos a 0

        // Guardamos la fecha de inicio del día actual
        Date todayStartTime = calendarToday.getTime();

        // Obtenemos el tiempo en milisegundos por si las dudas
        long todayStartTimeInMillis = calendarToday.getTimeInMillis();

        // Obtenemos la fecha de ayer
        Calendar calendarYesterday = Calendar.getInstance();
        calendarYesterday.setTime(currentDate);
        calendarYesterday.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = calendarYesterday.getTime();

        // Obtenemos la fecha de hace 7 días
        Calendar calendar7DaysAgo = Calendar.getInstance();
        calendar7DaysAgo.setTime(currentDate);
        calendar7DaysAgo.add(Calendar.DAY_OF_YEAR, -7);
        Date date7DaysAgo = calendar7DaysAgo.getTime();

        // Obtenemos la fecha de hace 30 días
        Calendar calendar30DaysAgo = Calendar.getInstance();
        calendar30DaysAgo.setTime(currentDate);
        calendar30DaysAgo.add(Calendar.DAY_OF_YEAR, -30);
        Date date30DaysAgo = calendar30DaysAgo.getTime();


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
                            Date notificationDate = dc.getDocument().getTimestamp("fecha").toDate();

                            ItemsDomainEventos evento = dc.getDocument().toObject(ItemsDomainEventos.class);
                            items2.add(evento);

                            // Comparamos la fecha de la notificación con la fecha actual y las fechas de hace 7 y 30 días
                            if (notificationDate.after(todayStartTime)) {
                                addNotification("¡Nuevo Evento Disponible! " + evento.getNombre_evento() + ". ¡No te lo pierdas! el "+ evento.getFecha_evento(), notificationContainerNuevas, R.layout.layout_notificatione);
                            } else if (notificationDate.after(date7DaysAgo)) {
                                addNotification("¡Nuevo Evento Disponible! " + evento.getNombre_evento() + ". ¡No te lo pierdas! el "+ evento.getFecha_evento(), notificationContainerUltimos7Dias, R.layout.layout_notificatione);
                            } else if (notificationDate.after(date30DaysAgo)) {
                                addNotification("¡Nuevo Evento Disponible! " + evento.getNombre_evento() + ". ¡No te lo pierdas! el "+ evento.getFecha_evento(), notificationContainerUltimos30Dias, R.layout.layout_notificatione);
                            }
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
                                        // Agregar el elemento al RecyclerView
                                        items.add(dc.getNewIndex(), dc.getDocument().toObject(ItemsDomainVinedos.class));
                                        itemsAdapterVinedos.notifyItemInserted(dc.getNewIndex());// Notificar al adaptador que hemos insertado datos

                                        // Procesamos las notificaciones
                                        Date notificationDate = dc.getDocument().getTimestamp("fecha").toDate();
                                        ItemsDomainVinedos evento = dc.getDocument().toObject(ItemsDomainVinedos.class);
                                        //items.add(evento);

                                        // Comparamos la fecha de la notificación con la fecha actual y las fechas de hace 7 y 30 días
                                        if (notificationDate.after(todayStartTime)) {
                                            addNotification(dc.getDocument().getString("nombre_barbacoa")+ " está disponible en Cadereyta, ¡Ven a Conocerlo!", notificationContainerNuevas, R.layout.layout_notification);
                                        } else if (notificationDate.after(date7DaysAgo)) {
                                            addNotification(dc.getDocument().getString("nombre_barbacoa")+ " está disponible en Cadereyta, ¡Ven a Conocerlo!", notificationContainerUltimos7Dias, R.layout.layout_notification);
                                        } else if (notificationDate.after(date30DaysAgo)) {
                                            addNotification(dc.getDocument().getString("nombre_barbacoa")+ " está disponible en Cadereyta, ¡Ven a Conocerlo!", notificationContainerUltimos30Dias, R.layout.layout_notification);
                                        }
                                        break;

                                    case MODIFIED:
                                        items.set(dc.getOldIndex(), dc.getDocument().toObject(ItemsDomainVinedos.class));
                                        itemsAdapterVinedos.notifyItemChanged(dc.getOldIndex());// Notificar al adaptador que los datos han cambiado
                                        break;
                                    case REMOVED:
                                        items.remove(dc.getOldIndex());
                                        itemsAdapterVinedos.notifyItemRemoved(dc.getOldIndex());// Notificar al adaptador que los datos han sido eliminados
                                        // Procesar notificaciones
                                        notificationDate = dc.getDocument().getTimestamp("fecha").toDate();

                                        // Comparar la fecha de la notificación con la fecha actual y las fechas de hace 7 y 30 días
                                        if (notificationDate.after(todayStartTime)) {
                                            addNotification(dc.getDocument().getString("nombre_barbacoa")+ " ya no está disponible en Cadereyta :(", notificationContainerNuevas, R.layout.layout_notification);
                                        } else if (notificationDate.after(date7DaysAgo)) {
                                            addNotification(dc.getDocument().getString("nombre_barbacoa")+ " ya no está disponible en Cadereyta :(", notificationContainerUltimos7Dias, R.layout.layout_notification);
                                        } else if (notificationDate.after(date30DaysAgo)) {
                                            addNotification(dc.getDocument().getString("nombre_barbacoa")+ " ya no está disponible en Cadereyta :(", notificationContainerUltimos30Dias, R.layout.layout_notification);
                                        }
                                        break;
                                }
                            }
                            pbProgressMain.setVisibility(View.GONE);
                        }
                    });

        // --> Configuración de RecyclerView para los Pulques
        recyclerView = findViewById(R.id.viewPulques);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        items3 = new ArrayList<>();
        itemsAdapterPulques = new ItemsAdapterPulques(items3, this);
        recyclerView.setAdapter(itemsAdapterPulques);
        mFirestore.collection("pulques")
                .orderBy("nombre_pulque") // Filtramos por nombre
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
                                    // Agregamos el elemento al RecyclerView
                                    items3.add(dc.getNewIndex(), dc.getDocument().toObject(ItemsDomainPulques.class));
                                    itemsAdapterPulques.notifyItemInserted(dc.getNewIndex());// Notificar al adaptador que hemos insertado datos

                                    // Procesamos las notificaciones
                                    Date notificationDate = dc.getDocument().getTimestamp("fecha").toDate();
                                    ItemsDomainPulques pulque = dc.getDocument().toObject(ItemsDomainPulques.class);
                                    //items3.add(pulque);

                                    // Comparamos la fecha de la notificación con la fecha actual y las fechas de hace 7 y 30 días
                                    if (notificationDate.after(todayStartTime)) {
                                        addNotification(dc.getDocument().getString("nombre_pulque")+ " está disponible en Cadereyta, ¡Ven a Conocerlo!", notificationContainerNuevas, R.layout.layout_notificationp);
                                    } else if (notificationDate.after(date7DaysAgo)) {
                                        addNotification(dc.getDocument().getString("nombre_pulque")+ " está disponible en Cadereyta, ¡Ven a Conocerlo!", notificationContainerUltimos7Dias, R.layout.layout_notificationp);
                                    } else if (notificationDate.after(date30DaysAgo)) {
                                        addNotification(dc.getDocument().getString("nombre_pulque")+ " está disponible en Cadereyta, ¡Ven a Conocerlo!", notificationContainerUltimos30Dias, R.layout.layout_notificationp);
                                    }
                                    break;

                                case MODIFIED:
                                    items3.set(dc.getOldIndex(), dc.getDocument().toObject(ItemsDomainPulques.class));
                                    itemsAdapterPulques.notifyItemChanged(dc.getOldIndex());// Notificar al adaptador que los datos han cambiado
                                    break;
                                case REMOVED:
                                    items3.remove(dc.getOldIndex());
                                    itemsAdapterPulques.notifyItemRemoved(dc.getOldIndex());// Notificar al adaptador que los datos han sido eliminados

                                    // Procesar notificaciones
                                    notificationDate = dc.getDocument().getTimestamp("fecha").toDate();

                                    // Comparar la fecha de la notificación con la fecha actual y las fechas de hace 7 y 30 días
                                    if (notificationDate.after(todayStartTime)) {
                                        addNotification(dc.getDocument().getString("nombre_pulque")+ " ya no está disponible en Cadereyta :(", notificationContainerNuevas, R.layout.layout_notificationp);
                                    } else if (notificationDate.after(date7DaysAgo)) {
                                        addNotification(dc.getDocument().getString("nombre_pulque")+ " ya no está disponible en Cadereyta :(", notificationContainerUltimos7Dias, R.layout.layout_notificationp);
                                    } else if (notificationDate.after(date30DaysAgo)) {
                                        addNotification(dc.getDocument().getString("nombre_pulque")+ " ya no está disponible en Cadereyta :(", notificationContainerUltimos30Dias, R.layout.layout_notificationp);
                                    }
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
        relativeInfo1= findViewById(R.id.relativeInfo);
        relativePrivacity1 = findViewById(R.id.relativePrivacity);

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

        // Inicializamos el selector de fechas
        final Date today = new Date(); //fecha actual
        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 5);

        CalendarPickerView datePicker = findViewById(R.id.calendarView);
        datePicker.init(today, nextYear.getTime()).withSelectedDate(today);

        // Recuperación de eventos de la base de datos
        mFirestore.collection("eventos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Asignamos la tarea completada a la variable de instancia
                    eventosTask = task;
                    // Iteramos sobre la colección Eventos
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Obtenemos la información del evento (nombre y fecha)
                        String nombreEvento = document.getString("nombre_evento");
                        Date fecha = document.getDate("fecha_eventoo");

                        // Verificamos si la fecha del evento es nula

                        if (fecha != null) {
                            //Estamos verificando si la fecha del evento está en el día actual o en el futuro
                            if (!fecha.before(today)) {
                                // Marcamos la fecha del evento en el calendario
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(fecha);
                                datePicker.selectDate(cal.getTime());
                            }
                        }
                    }
                    datePicker.selectDate(today);
                } else {
                    Log.d(TAG, "Error al obtener eventos: ", task.getException());
                }
            }
        });
        // Definimos el listener para la Selección de fechas, busca si hay un evento en la fecha y mostramos su info
        datePicker.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {

                // Verificamos si hay un evento en la fecha seleccionada
                String informacionEvento = " ";
                if (eventosTask != null && eventosTask.isSuccessful()) {
                    for (QueryDocumentSnapshot document : eventosTask.getResult()) {
                        String nombreEvento = document.getString("nombre_evento");
                        Date fecha = document.getDate("fecha_eventoo");

                        // Convertimos la fecha del evento de un tipo String a Date
                        Date fechaEvento = null;

                        if (fecha != null) {
                            //Estamos verificando si la fecha del evento está en el día actual o en el futuro
                            if (!fecha.before(today)) {
                                // Convertimos la fecha del evento a un objeto Calendar
                                Calendar calEvento = Calendar.getInstance();
                                calEvento.setTime(fecha);
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
                }
                // Mostrar la información del evento en el área designada
                button3.setText("Nombre del evento para la fecha seleccionada: " + informacionEvento);
            }
            @Override
            public void onDateUnselected(Date date) {
            }
        });

        // Decorador para cambiar el color de fondo de las celdas con eventos (EventDecorator)
        datePicker.setDecorators(Collections.singletonList(new EventDecorator()));

        ////FIN CALENDARIO//////



        //EXTRAS EN INTENTS PARA CALENDARIO---------------------------------------------------------------------------------------------

        // Lee el extra del Intent para ver si se debe mostrar un ID específico
        int selectedItemId = getIntent().getIntExtra("selectedItemId", -1);
        String markerTitle = null;
        if (selectedItemId != -1) {
            // Extraer datos del markerTitle
            markerTitle = getIntent().getStringExtra("markerTitle");

            // Se cambió el estado debido al extra del Intent
            bottomNavigation.show(selectedItemId, true);

            // Limpiar el extra del Intent
            getIntent().removeExtra("selectedItemId");
            getIntent().removeExtra("markerTitle");
        } else {
            // Estado predeterminado
            bottomNavigation.show(3, true);
        }

            // --> Muestra el fragmento del mapa
            Fragment fragment = new Map_Fragment();

            // Bundle para mandar argumentos al Map_Fragment
            Bundle bundle = new Bundle();
            bundle.putString("markerTitle", markerTitle);
            fragment.setArguments(bundle);

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
                    // Iniciar la actividad "contact" con startActivityForResult
                    Intent intent = new Intent(MainActivity.this, contact.class);
                    startActivityForResult(intent, REQUEST_CODE_CONTACT);
                }
            });

        relativeFAQ1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la actividad "FAQ" con startActivityForResult
                Intent intent = new Intent(MainActivity.this, FaqActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CONTACT);
            }
        });

        relativeInfo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la actividad "FAQ" con startActivityForResult
                Intent intent = new Intent(MainActivity.this, informacion.class);
                startActivityForResult(intent, REQUEST_CODE_CONTACT);
            }
        });

        relativePrivacity1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la actividad "FAQ" con startActivityForResult
                Intent intent = new Intent(MainActivity.this, privacidad.class);
                startActivityForResult(intent, REQUEST_CODE_CONTACT);
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
                Intent intent = new Intent(MainActivity.this, VerTodosLosPulquesActivity.class);
                startActivity(intent);
                finish();
            }
        });

        getLocalizacionn();

    }
// METODO PARA PEDIR UBICACION AL USUARIO --------------------------------------
    private void getLocalizacionn(){
        int permiso = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if(permiso == PackageManager.PERMISSION_DENIED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            }else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }
    }

    // --> EventDecorator: Aquí cambiamos el color del una fecha del CALENDARIO para ver si hay un evento
    private class EventDecorator implements CalendarCellDecorator {
        @Override
        public void decorate(CalendarCellView cellView, Date date) {
            // Verificamos si la fecha tiene un evento asociado
            boolean tieneEvento = tieneEventoEnFecha(date);
            if (tieneEvento) {
                // Cambiamos el color de fondo de la celda si tiene un evento
                cellView.setBackgroundColor(Color.rgb(255, 165, 0)); // Ponemos de color la celda
            } else if (isToday(date)){
                cellView.setBackgroundColor(Color.rgb(178, 218, 250)); // Ponemos de color la celda
            } else {
                cellView.setBackgroundColor(getResources().getColor(R.color.white));
            }
        }
        // Checamos si hay un evento asociado a una fecha
        private boolean tieneEventoEnFecha(Date date) {
            // Creamos un objeto Calendar y establecemos su tiempo para que coincida con la fecha dada
            Calendar cal1 = Calendar.getInstance();//cal1 representa la fecha asociada a la celda del calendario actual que se está decorando
            cal1.setTime(date);

            if (eventosTask != null && eventosTask.isSuccessful()) {
                // Iteramoa sobre los resultados de Firestore
                for (QueryDocumentSnapshot document : eventosTask.getResult()) {
                    Date fecha = document.getDate("fecha_eventoo");
                    if (fecha != null) {
                            //Estamos verificando si la fecha del evento está en el día actual o en el futuro
                            if (!fecha.before(today) ) {
                                // Convertimos la fecha del evento a un objeto Calendar
                                Calendar cal2 = Calendar.getInstance();
                                cal2.setTime(fecha);

                                // Comparamos los campos de año, mes y día de cal1 (fecha de la celda del calendario) con los campos correspondientes de cal2 (fecha del evento)
                                // Si son iguales, significa que hay un evento asociado a la fecha dada
                                if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                                        cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                                        cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)) {
                                    // Si hay un evento en la fecha, devuelve verdadero
                                    return true;
                                }
                            }
                    }
                }
            }
            // Si no hay eventos en la fecha dada, devuelve false
            return false;
        }

        // Verifica si es la fecha de hoy
        private boolean isToday(Date date){
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(date);
            cal2.setTime(today);
            return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);

        }
    }

    // --> addNotification: Aquí configuramos las NOTIFICACIONES
    private void addNotification(String mensaje, LinearLayout notificationContainer, int layoutResId) {
        View notificationView = getLayoutInflater().inflate(layoutResId, null);
        TextView notificationMessage = notificationView.findViewById(R.id.notificationMessage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationMessage.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }
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

        // Agregar la nueva notificación al contenedor especificado
        notificationContainer.addView(notificationView);
    }

    private void mostrarSnackbar(String mensaje) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), mensaje, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

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

        //Manejar el resultado de la actividad (contact y FAQ)
        //Se utiliza en Android para recibir resultados de actividades secundarias que han sido iniciadas mediante startActivityForResult()
        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_CODE_CONTACT && resultCode == RESULT_OK) {
                // Establecer el ícono del menú como activo en el Meow Bottom Navigation
                bottomNavigation.show(1, true);
                // Mostrar la vista del menú
                menu.setVisibility(View.VISIBLE);
                profile.setVisibility(View.GONE);
                home.setVisibility(View.GONE);
                notifications.setVisibility(View.GONE);
                map.setVisibility(View.GONE);
            }
        }

/*
        @Override
        public void onBackPressed(){
        new AlertDialog.Builder(this)
                .setMessage("Pulsa de nuevo para salir")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("NO", null)
                .show();
        }*/


    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Pulse de nuevo para salir", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}
