
package com.example.cadeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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
}

