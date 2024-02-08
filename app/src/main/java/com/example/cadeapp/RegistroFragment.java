package com.example.cadeapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cadeapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class RegistroFragment extends Fragment {

    //Declaramos las variables
    EditText nombre,correo,telefono,password,confirmpass;
    Button btnregistro;
    String nameuser,correouser,telefonouser,passworduser,confirmaruser;

    //Instancias de los servicios firebase
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_registro, container, false);

        //Inicializamos las variables y los servicios de firebase
        nombre = root.findViewById(R.id.reg_nombre);
        correo = root.findViewById(R.id.reg_correo);
        telefono = root.findViewById(R.id.reg_telefono);
        password = root.findViewById(R.id.reg_password);
        confirmpass = root.findViewById(R.id.reg_confirpass);
        btnregistro = root.findViewById(R.id.btn_registro);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        //Boton para reistrar usuarios
        btnregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameuser = nombre.getText().toString().trim();
                correouser = correo.getText().toString().trim();
                telefonouser = telefono.getText().toString().trim();
                passworduser = password.getText().toString().trim();
                confirmaruser = confirmpass.getText().toString().trim();

                if(!nameuser.isEmpty() && !correouser.isEmpty() && !telefonouser.isEmpty() && !passworduser.isEmpty() && !confirmaruser.isEmpty()){
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
                    mostrarMensaje("Los campos no debe de estar vacios");
                }
            }
        });

        return root;
    }


    //Metodo para registrar los usuarios dentro de firebase
    //El metodo debe resibir los parametros nameuser, correouser, telefonouser y passworduser del onclicklistener del boton registrar usuarios
    private void realizarConsulta(String correouser, String telefonouser) {

       //Realizamos una consulta a firebase para saber si el telefono no esta ligado con algun usuario
        mFirestore.collection("usuarios").whereEqualTo("telefono", telefonouser).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().isEmpty()){
                        //Si el numero no esta registrado, creamos el usuario
                        registroUsuarios(nameuser,correouser,telefonouser,passworduser);
                    }else{
                        Toast.makeText(getActivity(), "El número de teléfono ya está en uso", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "Error al hacer la consulta", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registroUsuarios(String nameuser, String correouser, String telefonouser, String passworduser){
        mAuth.createUserWithEmailAndPassword(correouser, passworduser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //Obtenemos el id del usuario
                    String id = mAuth.getCurrentUser().getUid();
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", id);
                    map.put("nombre", nameuser);
                    map.put("correo", correouser);
                    map.put("telefono", telefonouser);

                    //Registramos el usuario en firestore
                    mFirestore.collection("usuarios").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // Redirige solo cuando la creación de la cuenta sea exitosa
                            Toast.makeText(getActivity(), "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                            redireccionarMain();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Error al guardar datos", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Si la creación de la cuenta falla, muestra un mensaje de error
                    Toast.makeText(getActivity(), "Ya se creó una cuenta con este correo", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void mostrarMensaje(String mensaje){
        Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
    }

    private void redireccionarMain(){
        getActivity().finish();
        startActivity(new Intent(getActivity(), MainActivity.class));
    }

}