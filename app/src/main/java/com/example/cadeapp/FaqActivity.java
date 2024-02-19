package com.example.cadeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

//import com.example.cadeapp.databinding.ActivityScrollingBinding;

import java.util.ArrayList;
import java.util.List;

public class FaqActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Versions> versionsList;


    //private ActivityScrollingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        /*


        binding = ActivityScrollingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = binding.fab;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
         */

        recyclerView = findViewById(R.id.recyclerView);

        initData();
        setRecyclerView();


        //setSupportActionBar(findViewById(R.id.toolbar));
        //supportActionBar?.setDisplayHomeAsUpEnabled(true);
        //supportActionBar?.setDisplayShowHomeEnabled(true);
        //toolbar_layout.title = "Preguntas frecuentes";
    }

    public void onBackPressed(){
        super.onBackPressed();

        Intent intent = new Intent(FaqActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setRecyclerView() {
        VersionsAdapter versionsAdapter = new VersionsAdapter(versionsList);
        recyclerView.setAdapter(versionsAdapter);
    }

    private void initData(){

        versionsList = new ArrayList<>();

        versionsList.add(new Versions("¿Cómo puedo encontrar eventos en mi zona?", "Para encontrar eventos en tu zona, puedes utilizar la función de búsqueda de la app. \n" +
                "También  puedes utilizar el mapa de la app para ver dónde se encuentran las catas que están cerca de ti. \n" +
                "O si lo prefieres, puedes utilizar los filtros de la app para buscar eventos específicos."));
        versionsList.add(new Versions("¿Cómo puedo reservar un lugar en un evento de cata?", "Para reservar un lugar en un evento de cata, simplemente haz clic en el evento que te interesa y luego haz clic en el botón \"Reservar\". La app te llevará a una página donde podrás introducir tus datos personales y completar el pago."));
        versionsList.add(new Versions("¿Cuánto cuestan las catas?", "El precio de las catas varía según el tipo de cata, la ubicación y el lugar que organice el evento."));
        versionsList.add(new Versions("¿Cómo puedo pagar mi reservación?", "Puedes pagar tu reservación con tarjeta de crédito, débito o PayPal. El pago se realiza a través de la app de forma segura y sencilla."));
        versionsList.add(new Versions("¿Cómo puedo cancelar o modificar mi reserva?", "Si necesitas cancelar o modificar tu reserva, puedes hacerlo a través de la app. Simplemente haz clic en el botón \"Cancelar\" o \"Modificar\" que aparece junto a la reserva que deseas modificar."));

    }

}