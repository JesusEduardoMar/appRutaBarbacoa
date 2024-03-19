package com.example.cadeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class contact extends AppCompatActivity {

    protected TextView phone_text, email_text, social_m_legend;
    protected ImageView facebook_im, x_im, instagram_im;
    protected LinearLayout social_media_list;
    protected int social_media_weight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        // Cambiar el color de la barra de estado
        cambiarColorBarraEstado(getResources().getColor(R.color.black));

        phone_text = findViewById(R.id.phone_number);
        email_text = findViewById(R.id.email);
        facebook_im = findViewById(R.id.facebook_ic);
        x_im = findViewById(R.id.x_ic);
        instagram_im = findViewById(R.id.instagram_ic);
        social_m_legend = findViewById(R.id.social_media_legend);
        social_media_list = findViewById(R.id.social_media_list);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("soporte").document("contacto");

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Extract support contact
                        String phone = document.getString("telefono");
                        String email = document.getString("email");
                        String facebook = document.getString("facebook");
                        String x_twitter = document.getString("x");
                        String instagram = document.getString("instagram");

                        // Show on layout
                        phone_text.setText(phone);
                        email_text.setText(email);

                        // Social media
                        social_media_weight = 0;
                        if (facebook != null && !facebook.isEmpty()) {
                            social_media_weight++;
                            facebook_im.setVisibility(View.VISIBLE);
                        }
                        if (x_twitter != null && !x_twitter.isEmpty()) {
                            social_media_weight++;
                            x_im.setVisibility(View.VISIBLE);
                        }
                        if (instagram != null && !instagram.isEmpty()) {
                            social_media_weight++;
                            instagram_im.setVisibility(View.VISIBLE);
                        }
                        if(social_media_weight > 0)
                            social_m_legend.setVisibility(View.VISIBLE);
                        social_media_list.setWeightSum(social_media_weight);

                    } else {
                        Log.d("Firestore", "No se han podido cargar los datos de contacto");
                    }
                } else {
                    Log.d("Firestore", "Error al obtener los datos de contacto", task.getException());
                }
            }
        });

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