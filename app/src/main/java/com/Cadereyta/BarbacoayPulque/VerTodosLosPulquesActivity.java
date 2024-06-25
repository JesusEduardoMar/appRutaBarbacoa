package com.Cadereyta.BarbacoayPulque;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.animation.ObjectAnimator;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class VerTodosLosPulquesActivity extends AppCompatActivity {
    // Variables
    private RecyclerView recyclerView;
    private ProgressBar pbProgressMain;
    private FirebaseFirestore mFirestore;
    private ArrayList<ItemsDomainPulques> items;
    private ItemsAdapterPulques itemsAdapterPulques;
    private ConstraintLayout card1;
    private ConstraintLayout card2;
    //private ScrollView scrollView;
    private NestedScrollView scrollView;
    protected Class lastActivity = MainActivity.class;
    private DocumentSnapshot lastVisible; // Último documento visible en la lista

    // Constantes
    private static final int PAGE_SIZE = 5; // Tamaño de la página

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_ver_pulques);
        configSwipe();

        // Inicialización de Firebase y otros elementos de la interfaz de usuario
        mFirestore = FirebaseFirestore.getInstance();

        // Inicialización del ProgressBar
        pbProgressMain = findViewById(R.id.progress_main);

        // Configuración de RecyclerView para las barbacoas
        recyclerView = findViewById(R.id.recycler_view_ver_pulques);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Cargar los primeros elementos
        loadFirstPage();

        items = new ArrayList<>();
        itemsAdapterPulques = new ItemsAdapterPulques(items, this);
        recyclerView.setAdapter(itemsAdapterPulques);

        // Inicializar los ConstraintLayouts y el ScrollView
        card1 = findViewById(R.id.cardInicio1);
        card2 = findViewById(R.id.cardInicio2);
        scrollView = findViewById(R.id.scrollView);
// Configurar un listener de desplazamiento para el ScrollView
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY();

                // Determinar cuánto desplazamiento necesitas para que los ConstraintLayouts desaparezcan completamente
                int fadeOutOffset = 200; // Ajusta este valor según sea necesario

                // Calcular el alpha basado en el desplazamiento
                float alpha = Math.max(0f, 1f - (float) scrollY / fadeOutOffset);

                // Animar el alpha de los ConstraintLayouts
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(card1, "alpha", alpha);
                animator1.setDuration(0); // Duración cero para que la animación sea instantánea
                animator1.start();

                ObjectAnimator animator2 = ObjectAnimator.ofFloat(card2, "alpha", alpha);
                animator2.setDuration(0); // Duración cero para que la animación sea instantánea
                animator2.start();
            }
        });
        // Listener para el scroll del RecyclerView
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                // Si hemos llegado al final de la lista y hay más elementos por cargar
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                    loadMoreItems();
                }
            }
        });


        //SearchView
        SearchView searchView = findViewById(R.id.search_view);

        // Configuramos el listener para el cambio de texto en el SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Este método se llama cuando se presiona "Enter" o se envía una consulta

                // Realizar la búsqueda con el texto ingresado
                performSearch(query);

                // Devolver true para indicar que la búsqueda fue manejada
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Este método se llama cuando cambia el texto en el SearchView

                // Si el texto está vacío, mostrar todos los lugares nuevamente
                if (newText.isEmpty()) {
                    showAllPlaces();
                } else {
                    // Realizar la búsqueda en tiempo real mientras el usuario escribe
                    performSearch(newText);
                }

                // Devolver false para permitir que el SearchView maneje los cambios de texto
                return false;
            }
        });


        // Ahora aquí están los listeners para card1 y card2 (historia de la barbacoa y preparación de barbacoa)
        card1 = findViewById(R.id.cardInicio1);
        card2 = findViewById(R.id.cardInicio2);

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VerTodosLosPulquesActivity.this, historiaPulque.class);
                intent.putExtra("idDesc", "preparacion_pulque");
                intent.putExtra("toolbar_icon", historiaPulque.PREPARACION_PULQUE);
                //con esta linea limpiamos las actividades para que no se muestren mas que una sola en lugar de cada que abramos un lugar
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VerTodosLosPulquesActivity.this, historiaPulque.class);
                intent.putExtra("idDesc", "historia_pulque");
                intent.putExtra("toolbar_icon", historiaPulque.HISTORIA_PULQUE);
                //con esta linea limpiamos las actividades para que no se muestren mas que una sola en lugar de cada que abramos un lugar
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        // Mostrar el botón para regresar y eliminar title
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    // Método para cargar la primera página de elementos
    private void loadFirstPage() {
        pbProgressMain.setVisibility(View.VISIBLE);
        mFirestore.collection("pulques")
                .limit(PAGE_SIZE)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            items.add(document.toObject(ItemsDomainPulques.class));
                        }
                        itemsAdapterPulques.notifyDataSetChanged();
                        // Guardar la referencia al último documento visible
                        lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                    } else {
                        Log.e("Firestore error", "Error getting documents: ", task.getException());
                    }
                    pbProgressMain.setVisibility(View.GONE);
                });
    }

    // Método para cargar más elementos cuando el usuario se desplaza hacia abajo
    private void loadMoreItems() {
        pbProgressMain.setVisibility(View.VISIBLE);
        mFirestore.collection("pulques")
                .startAfter(lastVisible)
                .limit(PAGE_SIZE)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        if (!documents.isEmpty()) {
                            for (DocumentSnapshot document : documents) {
                                items.add(document.toObject(ItemsDomainPulques.class));
                            }
                            itemsAdapterPulques.notifyDataSetChanged();
                            // Actualizar la referencia al último documento visible
                            lastVisible = documents.get(documents.size() - 1);
                        }else {
                            Log.d("Paginación", "Se alcanzó el final de la lista.");
                        }
                    } else {
                        Log.e("Firestore error", "Error getting documents: ", task.getException());
                    }
                    pbProgressMain.setVisibility(View.GONE);
                });
    }
    private void configSwipe() {
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Simula una actualización de 2 segundos
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // Detiene la animación de actualización
                swipeRefreshLayout.setRefreshing(false);

                recreate();
            }, 600);
        });
    }

    // Aquí realixzamos un método para la búsqueda por texto ingresado
    private void performSearch(String query) {
        if(items == null)
        {
            return;
        }
        // Convertir el texto de búsqueda y el nombre de los lugares a minúsculas para una comparación sin distinción de mayúsculas y minúsculas
        String lowerCaseQuery = query.toLowerCase();
        // Filtrar los lugares por nombre basado en el texto de búsqueda
        ArrayList<ItemsDomainPulques> filteredList = new ArrayList<>();
        for (ItemsDomainPulques item : items) {
            if (item.getNombre_pulque() != null && removeAccents(item.getNombre_pulque()).toLowerCase().contains(removeAccents(lowerCaseQuery))) {
                filteredList.add(item);
            }
        }

        // Actualizamos el RecyclerView con los resultados  obtenidos
        itemsAdapterPulques.setFilter(filteredList);
    }
    // Método para eliminar los acentos de una cadena
    private String removeAccents(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    // Aquí mostramos todos los lugares de nuevo
    private void showAllPlaces() {
        itemsAdapterPulques.setFilter(items);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

        Intent intent = new Intent(this, lastActivity);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}

