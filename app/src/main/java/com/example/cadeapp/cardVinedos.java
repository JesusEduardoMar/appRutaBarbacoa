package com.example.cadeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.example.cadeapp.R;

public class cardVinedos extends AppCompatActivity {

    ImageView flecha2;

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_vinedos);

        flecha2 = findViewById(R.id.flechaatras2);

        flecha2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cardVinedos.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void onBackPressed(){
        super.onBackPressed();

        Intent intent = new Intent(cardVinedos.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}