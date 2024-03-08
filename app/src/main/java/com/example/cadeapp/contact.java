package com.example.cadeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.appbar.CollapsingToolbarLayout;

public class contact extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

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