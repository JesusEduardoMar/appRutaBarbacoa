package com.example.cadeapp;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cadeapp.databinding.ActivityScrollingBinding;

public class ScrollingActivity extends AppCompatActivity {

    protected ActivityScrollingBinding binding;
    protected String title = "";
    protected Class lastActivity = MainActivity.class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    public void onBackPressed(){
        super.onBackPressed();

        Intent intent = new Intent(this, lastActivity);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void setupActionBar(){

        binding = ActivityScrollingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(title);

        //toolBarLayout.setCollapsedTitleTextColor("");
        //toolBarLayout.setCollapsedTitleTextColor(R.color.flexible_text_color);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }
}