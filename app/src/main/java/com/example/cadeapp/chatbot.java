package com.example.cadeapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cadeapp.R;

import java.util.ArrayList;
import java.util.List;

public class chatbot extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Versions> versionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        recyclerView = findViewById(R.id.recyclerView);

        initData();
        setRecyclerView();
    }

    public void onBackPressed(){
        super.onBackPressed();

        Intent intent = new Intent(chatbot.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setRecyclerView() {
        VersionsAdapter versionsAdapter = new VersionsAdapter(versionsList);
        recyclerView.setAdapter(versionsAdapter);
        recyclerView.setHasFixedSize(true);
    }

    private void initData() {

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