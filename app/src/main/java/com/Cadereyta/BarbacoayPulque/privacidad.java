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

public class privacidad extends ScrollingActivity {
    private TextView privTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        title = "Política de Privacidad";
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_privacidad);

        //incrustar activity contact
        NestedScrollView nscrollv;
        nscrollv = findViewById(R.id.nestedScrollView);
        LayoutInflater inflater = getLayoutInflater();
        View myLayout = inflater.inflate(R.layout.activity_privacidad, nscrollv, false);
        nscrollv.removeAllViews();
        nscrollv.addView(myLayout);

        TextView priv1TextView = findViewById(R.id.priv1TextView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            priv1TextView.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }
        // Obtener el texto de privacidad del archivo de recursos y aplicar formato HTML
        String privacidadText = getString(R.string.texto_de_privacidad);
        priv1TextView.setText(Html.fromHtml(privacidadText));

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
