package com.example.cadeapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cadeapp.R;

public class DetailCoteActivity extends AppCompatActivity {

    private TextView titleText, addressText, textDescription;
    private ImageView vinedoImg;
    int contador;
    Button boton01, boton02;
    TextView cajaDeTexto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_cote);

        contador = 0;
        boton01 = findViewById(R.id.botonRestar);
        boton01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contador--;
                cajaDeTexto.setText(Integer.toString(contador));
            }
        });
        boton02 = findViewById(R.id.botonSumar);
        boton02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contador++;
                cajaDeTexto.setText(Integer.toString(contador));
            }
        });
        cajaDeTexto = findViewById(R.id.textcont);
        cajaDeTexto.setText(Integer.toString(contador));
    }
}