package com.example.cadeapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cadeapp.R;


public class SplashWinestastic extends AppCompatActivity {
    //private int SPLASH_TIME_OUT = 20000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_winestastic);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start your main activity here
                Intent mainIntent = new Intent(SplashWinestastic.this, LoginActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, 3000);
    }
}
