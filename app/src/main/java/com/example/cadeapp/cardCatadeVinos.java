package com.example.cadeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.cadeapp.R;

public class cardCatadeVinos extends AppCompatActivity {

    ImageView flecha1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_catade_vinos);

        flecha1 = findViewById(R.id.flechaatras);
        flecha1.setColorFilter(null);

        flecha1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cardCatadeVinos.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void onBackPressed(){
        super.onBackPressed();

        Intent intent = new Intent(cardCatadeVinos.this, MainActivity.class);
        startActivity(intent);
        finish();
    }



}