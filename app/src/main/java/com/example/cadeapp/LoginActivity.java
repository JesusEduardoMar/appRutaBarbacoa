package com.example.cadeapp;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity"; //agrega etiquetas de registro a las declaraciones Log

    TextInputEditText log_correo,log_pass;
    TextView recuperarpass;

    ProgressBar pbProgressLogin;

    FloatingActionButton gmail;
    Button btnlogin, btnRegistro;
    float op = 0;
    String correo,password;
    FirebaseFirestore mFirestore;

    //Uso de la API mAuth de firebase para el inicio de sesion
    FirebaseAuth mAuth;

    //Variables para el funcionamiento de inicio de sesion con google
    private static final int RC_SIGN_IN = 123;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        log_correo = findViewById(R.id.lblcorreo);
        log_pass = findViewById(R.id.lblpassword);
        recuperarpass = findViewById(R.id.txtrecuperar);
        btnlogin = findViewById(R.id.btniniciar);
        btnRegistro = findViewById(R.id.btn_registro);
        gmail = findViewById(R.id.login_gmail);
        pbProgressLogin = findViewById(R.id.progress_login);
        mFirestore = FirebaseFirestore.getInstance();


        //-------------Verificar si la colección "usuarios" existe---------------
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().isEmpty()) {
                    // La colección no existe, créala
                    db.collection("usuarios").document("dummyDocument").set(new HashMap<>())
                            .addOnSuccessListener(documentReference -> {
                                // Éxito al crear la colección
                                Log.d(TAG, "Colección 'usuarios' creada exitosamente");
                            })
                            .addOnFailureListener(e -> {
                                // Error al crear la colección
                                Log.e(TAG, "Error al crear la colección 'usuarios'", e);
                            });
                }
            } else {
                // Error al intentar acceder a la colección
                Log.e(TAG, "Error al verificar la existencia de la colección 'usuarios'", task.getException());
            }
        });
        //-------------Fin de la verificación si la colección "usuarios" existe---------------


        //-------------Servicios Google----------------
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        //Configuracion para el uso de inicio de sesion con google


        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        //-------------fin de Servicios Google----------------

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                correo = log_correo.getText().toString().trim();
                password = log_pass.getText().toString().trim();

                if(!correo.isEmpty() && !password.isEmpty()){
                    loginuser(correo,password);
                }else{
                    mostrarMensaje("Los campos no deben de estar vacios");
                }
            }
        });

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(LoginActivity.this, RegistrarActivity.class);
                startActivity(intent);
            }
        });
        recuperarpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(LoginActivity.this, RecuperarActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI(FirebaseUser user) {
        user = mAuth.getCurrentUser();
        if(user != null){
            redireccionarMain();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken){
        pbProgressLogin.setVisibility(View.VISIBLE);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            pbProgressLogin.setVisibility(View.GONE);
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            if(task.getResult().getAdditionalUserInfo().isNewUser()) {
                                String uid = user.getUid();
                                String correo = user.getEmail();
                                String nombre = user.getDisplayName();

                                Map<Object, String> map = new HashMap<>();
                                map.put("id", uid);
                                map.put("nombre", nombre);
                                map.put("correo", correo);
                                map.put("telefono", "");

                                mFirestore.collection("usuarios").document(uid).set(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                // Redirige solo cuando la creación de la cuenta sea exitosa
                                                mostrarMensaje("Usuario registrado con éxito");
                                                redireccionarMain();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                mostrarMensaje("Error al guardar datos");
                                            }
                                        });

                            }
                        }else{
                            updateUI(null);
                        }


                    }
                });
    }

    //----------Fin de Metodos para iniciar sesion con Google------------------

    //----------Metodos para iniciar sesion con email y contraseña------------------

    //Inicio de sesion con email y contraseña

    private void loginuser(String correo, String password){
        pbProgressLogin.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(correo,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            pbProgressLogin.setVisibility(View.GONE);
                            finish();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            mostrarMensaje("Bienvenido");
                        }else{
                            pbProgressLogin.setVisibility(View.GONE);
                            mostrarMensaje("Error");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pbProgressLogin.setVisibility(View.GONE);
                        mostrarMensaje("El correo o contraseña son incorrectos");
                    }
                });

    }

    //Fin de Inicio de sesion con email y contraseña

    //----------Fin de Metodos para iniciar sesion con email y contraseña------------------

    //Mostrar mensajes

    private void mostrarMensaje(String mensaje){
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    //Fin Mostrar mensajes

    //redireccionamiento a la activitymain
    private void redireccionarMain(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Pulse de nuevo para salir", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


}