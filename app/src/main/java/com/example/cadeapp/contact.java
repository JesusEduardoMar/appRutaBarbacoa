package com.example.cadeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class contact extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
    }

    public void onBackPressed(){
        super.onBackPressed();

        Intent intent = new Intent(contact.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}