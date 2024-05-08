package com.Cadereyta.BarbacoayPulque;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class Boletos extends AppCompatActivity {

    EditText txtinfo;
    Button generar;
    ImageView codigoqr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boletos);


        txtinfo = findViewById(R.id.convertir);
        generar = findViewById(R.id.btn_qr);
        codigoqr = findViewById(R.id.codigoqr);

        generar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.encodeBitmap(txtinfo.getText().toString(),
                            BarcodeFormat.QR_CODE, 750, 750);

                    codigoqr.setImageBitmap(bitmap);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }
}