package com.example.cadeapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SearchView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class VerTodosLosLugaresActivity extends AppCompatActivity {
    // Variables
    private RecyclerView recyclerView;
    private ProgressBar pbProgressMain;
    private FirebaseFirestore mFirestore;
    private ArrayList<ItemsDomainVinedos> items;
    private ItemsAdapterVinedos itemsAdapterVinedos;
    private ConstraintLayout card1;
    private ConstraintLayout card2;
    //private ScrollView scrollView;
    private NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_ver_lugares);

        // Inicialización de Firebase y otros elementos de la interfaz de usuario
        mFirestore = FirebaseFirestore.getInstance();

        // Inicialización del ProgressBar
        pbProgressMain = findViewById(R.id.progress_main);

        // Configuración de RecyclerView para las barbacoas
        recyclerView = findViewById(R.id.recycler_view_ver_lugares);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        items = new ArrayList<>();
        itemsAdapterVinedos = new ItemsAdapterVinedos(items, this);
        recyclerView.setAdapter(itemsAdapterVinedos);

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
                Intent intent = new Intent(VerTodosLosLugaresActivity.this, cardPreparacionbarba.class);
                startActivity(intent);
                finish();
            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VerTodosLosLugaresActivity.this, cardHistoriabarbacoa.class);
                startActivity(intent);
                finish();
            }
        });

        mFirestore.collection("barbacoas").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                pbProgressMain.setVisibility(View.VISIBLE);
                if(error != null){
                    Log.e("Firestore error", error.getMessage());
                    return;
                }
                for(DocumentChange dc : value.getDocumentChanges()){
                    if(dc.getType() == DocumentChange.Type.ADDED){
                        items.add(dc.getDocument().toObject(ItemsDomainVinedos.class));
                    }
                    itemsAdapterVinedos.notifyDataSetChanged();
                    pbProgressMain.setVisibility(View.GONE);
                }
            }
        });
    }

    // Aquí realixzamos un método para la búsqueda por texto ingresado
    private void performSearch(String query) {
        if(items == null)
        {
            return;
        }
        // Filtrar los lugares por nombre basado en el texto de búsqueda
        ArrayList<ItemsDomainVinedos> filteredList = new ArrayList<>();
        for (ItemsDomainVinedos item : items) {
            if (item.getNombre_barbacoa() != null &&item.getNombre_barbacoa().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }

        // Actualizamos el RecyclerView con los resultados  obtenidos
        itemsAdapterVinedos.setFilter(filteredList);
    }

    // Aquí mostramos todos los lugares de nuevo
    private void showAllPlaces() {
        itemsAdapterVinedos.setFilter(items);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Volvemos a la MainActivity
        Intent intent = new Intent(VerTodosLosLugaresActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

