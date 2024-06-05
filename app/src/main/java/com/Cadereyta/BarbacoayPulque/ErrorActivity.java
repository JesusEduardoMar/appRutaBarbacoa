package com.Cadereyta.BarbacoayPulque;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class ErrorActivity extends AppCompatActivity {
    private TextView errorDescription;

    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

       errorDescription = findViewById(R.id.errorDescription);
        String errorDescriptionText = getString(R.string.error_description);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            errorDescription.setText(Html.fromHtml(errorDescriptionText, Html.FROM_HTML_MODE_COMPACT));
        } else {
            errorDescription.setText(Html.fromHtml(errorDescriptionText));
        }
        errorDescription.setMovementMethod(LinkMovementMethod.getInstance());

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

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}