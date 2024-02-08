package com.example.cadeapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cadeapp.R;

public class cardVinedos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_vinedos);
    }

    public void onBackPressed(){
        super.onBackPressed();

        Intent intent = new Intent(cardVinedos.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}