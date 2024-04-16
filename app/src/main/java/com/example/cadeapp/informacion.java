package com.example.cadeapp;

import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;

public class informacion extends ScrollingActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        title = "Condiciones de Uso y Política de Privacidad";
        super.onCreate(savedInstanceState);
        //incrustar activity contact
        NestedScrollView nscrollv;
        nscrollv = findViewById(R.id.nestedScrollView);
        LayoutInflater inflater = getLayoutInflater();
        View myLayout = inflater.inflate(R.layout.activity_informacion, nscrollv, false);
        nscrollv.removeAllViews();
        nscrollv.addView(myLayout);

        TextView privTextView = findViewById(R.id.privTextView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            privTextView.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }

        TextView usoTextView = findViewById(R.id.usoTextView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            usoTextView.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }

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

    @Override
    public void onBackPressed() {
        // Establecer el resultado como RESULT_OK
        setResult(RESULT_OK);
        finish();
    }
}


