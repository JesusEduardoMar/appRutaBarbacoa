package com.Cadereyta.BarbacoayPulque;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class contact extends ScrollingActivity {

    protected TextView phone_text, email_text, social_m_legend;
    protected ImageView facebook_im, x_im, instagram_im;
    protected LinearLayout social_media_list;
    protected int social_media_weight;
    protected String facebook_link, x_twitter_link, instagram_link;
    private ImageView toolbar_icon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        title = "Contáctanos";
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_contact);

        // Change Icon of top_background
        toolbar_icon = findViewById(R.id.toolbar_icon);
        toolbar_icon.setImageResource(R.drawable.top_background2);
        toolbar_icon.setScaleType(ImageView.ScaleType.FIT_XY);

        //incrustar activity contact
        NestedScrollView nscrollv;
        nscrollv = findViewById(R.id.nestedScrollView);
        LayoutInflater inflater = getLayoutInflater();
        View myLayout = inflater.inflate(R.layout.activity_contact, nscrollv, false);
        nscrollv.removeAllViews();
        nscrollv.addView(myLayout);

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
                        facebook_link = document.getString("facebook");
                        x_twitter_link = document.getString("x");
                        instagram_link = document.getString("instagram");

                        // Show on layout
                        phone_text.setText(phone);
                        email_text.setText(email);


                        // OnClickListener for facebook button
                        facebook_im.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(facebook_link));
                                startActivity(intent);
                            }
                        });
                        // OnClickListener for x button
                        x_im.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(x_twitter_link));
                                startActivity(intent);
                            }
                        });
                        // OnClickListener for instagram button
                        instagram_im.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(instagram_link));
                                startActivity(intent);
                            }
                        });

                        // Social media
                        social_media_weight = 0;
                        if (facebook_link != null && !facebook_link.isEmpty()) {
                            social_media_weight++;
                            facebook_im.setVisibility(View.VISIBLE);
                        }
                        if (x_twitter_link != null && !x_twitter_link.isEmpty()) {
                            social_media_weight++;
                            x_im.setVisibility(View.VISIBLE);
                        }
                        if (instagram_link != null && !instagram_link.isEmpty()) {
                            social_media_weight++;
                            instagram_im.setVisibility(View.VISIBLE);
                        }
                        if(social_media_weight > 0)
                            social_m_legend.setVisibility(View.VISIBLE);

                        // Set Weight of each element on social media
                        // social_media_list.setWeightSum(social_media_weight);

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
        // Establecer el resultado como RESULT_OK
        setResult(RESULT_OK);
        finish();
    }

    public void call(View view){

    }
}