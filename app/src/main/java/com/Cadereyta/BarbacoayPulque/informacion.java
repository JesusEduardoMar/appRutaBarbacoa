package com.Cadereyta.BarbacoayPulque;

import android.content.ComponentCallbacks2;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;

public class informacion extends ScrollingActivity{
    private TextView privTextView;
    private TextView priv1TextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        title = "Términos y Condiciones";
        super.onCreate(savedInstanceState);

        //incrustar activity contact
        NestedScrollView nscrollv;
        nscrollv = findViewById(R.id.nestedScrollView);
        LayoutInflater inflater = getLayoutInflater();
        View myLayout = inflater.inflate(R.layout.activity_informacion, nscrollv, false);
        nscrollv.removeAllViews();
        nscrollv.addView(myLayout);

        TextView usoTextView = findViewById(R.id.usoTextView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            usoTextView.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }
        // Obtener el texto de los recursos y mostrarlo en el TextView con formato HTML
        String privacidadText = getString(R.string.terminos_y_condiciones);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            usoTextView.setText(Html.fromHtml(privacidadText, Html.FROM_HTML_MODE_COMPACT));
        } else {
            usoTextView.setText(Html.fromHtml(privacidadText));
        }

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


