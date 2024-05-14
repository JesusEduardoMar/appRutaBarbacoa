package com.Cadereyta.BarbacoayPulque;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;


public class ErrorActivity extends AppCompatActivity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        button = findViewById(R.id.returnHome);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnHome(view);
            }
        });
    }

    public void returnHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }

    public void onBackPressed(View view) {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }
}