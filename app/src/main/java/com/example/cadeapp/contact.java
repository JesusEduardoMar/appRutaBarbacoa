package com.example.cadeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.appbar.CollapsingToolbarLayout;

public class contact extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        // Cambiar el color de la barra de estado
        cambiarColorBarraEstado(getResources().getColor(R.color.black));

    }

    private void cambiarColorBarraEstado(int color) {
        // Comprobar la versión de Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            // Configurar el color de la barra de estado
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(contact.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void call(View view){

    }

}