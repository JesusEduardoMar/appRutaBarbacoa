package com.example.cadeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;

public class RegistrarActivity extends AppCompatActivity {

    //Declaramos las variables
    TextInputEditText nombre,correo,telefono,password,confirmpass;

    TextInputLayout avisopass,avisocorreo;
    Button btnregistro;
    String nameuser,correouser,telefonouser,passworduser,confirmaruser;
    String regexPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_-])(?=\\S+$).{4,}$";


    //Instancias de los servicios firebase
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        //Inicializamos las variables y los servicios de firebase

        nombre = findViewById(R.id.reg_nombre);
        correo = findViewById(R.id.reg_correo);
        avisocorreo = findViewById(R.id.txtlayoutCorreo);
        telefono = findViewById(R.id.reg_telefono);
        password = findViewById(R.id.reg_password);
        avisopass = findViewById(R.id.txtlayoutPass);
        confirmpass = findViewById(R.id.reg_confirpass);
        btnregistro = findViewById(R.id.btn_registro);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        btnregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameuser = nombre.getText().toString().trim();
                correouser = correo.getText().toString().trim();
                telefonouser = telefono.getText().toString().trim();
                passworduser = password.getText().toString().trim();
                confirmaruser = confirmpass.getText().toString().trim();

                if(!nameuser.isEmpty() && !correouser.isEmpty() && !telefonouser.isEmpty() && !passworduser.isEmpty() && !confirmaruser.isEmpty()){
                    if(passworduser.matches(regexPassword)){
                        if(passworduser.equals(confirmaruser)){
                            if(Patterns.PHONE.matcher(telefonouser).matches()){
                                if(Patterns.EMAIL_ADDRESS.matcher(correouser).matches()){
                                    realizarConsulta(correouser,telefonouser);
                                }else{
                                    mostrarMensaje("El correo es invalido");

                                }
                            }else {
                                mostrarMensaje("El numero de telefono debe contar con 10 digitos");
                            }
                        }else{
                            mostrarMensaje("Las contraseñas deben coincidir");
                        }
                    }else{
                        mostrarMensaje("Contraseña invalida");
                    }
                }else{
                    mostrarMensaje("Los campos no debe de estar vacios");
                }
            }
        });


        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validarPassword();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void validarPassword(){
        String validarpass = password.getText().toString().trim();

        if(!validarpass.matches(regexPassword)){
            avisopass.setHelperText("La contraseña requiere 8 caracteres (mayúsculas, minúsculas y numeros)");
        }
        else{
            avisopass.setHelperText("Contraseña fuerte");
        }
    }



    //Metodo para registrar los usuarios dentro de firebase
    //El metodo debe resibir los parametros nameuser, correouser, telefonouser y passworduser del onclicklistener del boton registrar usuarios
    private void realizarConsulta(String correouser, String telefonouser) {

        //Realizamos una consulta a firebase para saber si el telefono no esta ligado con algun usuario
        mFirestore.collection("usuarios").whereEqualTo("telefono", telefonouser).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().isEmpty()){
                        //Si el numero no esta registrado, creamos el usuario
                        registroUsuarios(nameuser,correouser,telefonouser,passworduser);
                    }else{
                        mostrarMensaje("El número de teléfono ya está en uso");
                    }
                }else{
                    mostrarMensaje("Error al hacer la consulta");
                }
            }
        });
    }

    // Registrar usuarios en Firebase y Firestore
    private void registroUsuarios(String nameuser, String correouser, String telefonouser, String passworduser){
        mAuth.createUserWithEmailAndPassword(correouser, passworduser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    //Obtenemos el id del usuario
                    String id = mAuth.getCurrentUser().getUid();
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", id);
                    map.put("nombre", nameuser);
                    map.put("correo", correouser);
                    map.put("telefono", telefonouser);

                    //Registramos el usuario en Firestore
                    mFirestore.collection("usuarios").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        public void onSuccess(Void unused) {
                            // Redirige solo cuando la creación de la cuenta sea exitosa
                            mostrarMensaje("Usuario registrado con éxito");
                            redireccionarMain(); // Mover aquí la redirección
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mostrarMensaje("Error al guardar datos");
                        }
                    });
                } else {
                    // Si la creación de la cuenta falla, muestra un mensaje de error
                    mostrarMensaje("Ya se creó una cuenta con este correo");
                }
            }
        });
    }

    private void mostrarMensaje(String mensaje){
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    private void redireccionarMain(){
        finish(); // Finaliza la actividad actual
        Intent intent = new Intent(RegistrarActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(RegistrarActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}