package com.Cadereyta.BarbacoayPulque;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarActivity extends AppCompatActivity {


    TextInputEditText recup_correo, recup_pass, recup_confirmar;

    TextInputLayout avisoCorreo, avisopass, avisoconfirmar;
    Button btn_recup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar);

        recup_correo = findViewById(R.id.correo_recup);
        btn_recup = findViewById(R.id.recup_password);

        avisoCorreo = findViewById(R.id.txtlayoutCorreo);

        btn_recup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validarUsuario();
            }
        });

    }

    public void onBackPressed(){
        super.onBackPressed();

        Intent intent = new Intent(RecuperarActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void validarUsuario(){
        String correo = recup_correo.getText().toString().trim();
        if(correo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            mostrarAlerta("Correo invalido");
            return;
        }
        enviarPassword(correo);
    }

    private void enviarPassword(String correo){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(correo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mostrarAlerta("Revisa tu correo, por favor");
                        }
                        else{
                            mostrarAlerta("correo invalido");
                        }
                    }
                });

    }


    private void mostrarAlerta(String mensajealert){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recuperar Contraseña");
        builder.setMessage(mensajealert);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(RecuperarActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.show();
    }

}