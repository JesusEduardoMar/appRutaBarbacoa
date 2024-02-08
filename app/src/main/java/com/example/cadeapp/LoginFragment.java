    package com.example.cadeapp;

    import android.content.Intent;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ProgressBar;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.fragment.app.Fragment;

    import com.example.cadeapp.R;
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
    import com.google.firebase.auth.AuthCredential;
    import com.google.firebase.auth.AuthResult;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.auth.GoogleAuthProvider;
    import com.google.firebase.firestore.FirebaseFirestore;

    import java.util.HashMap;
    import java.util.Map;


    public class LoginFragment extends Fragment {

        private static final String TAG = "LoginFragment"; //agrega etiquetas de registro a las declaraciones Log

        EditText log_correo,log_pass;
        TextView recuperarpass,regwhit;

        ProgressBar pbProgressLogin;
        FloatingActionButton gmail;
        Button btnlogin;
        float op = 0;
        String correo,password;
        FirebaseFirestore mFirestore;

        //Uso de la API mAuth de firebase para el inicio de sesion
        FirebaseAuth mAuth;

        //Variables para el funcionamiento de inicio de sesion con google
        private static final int RC_SIGN_IN = 123;
        private GoogleSignInClient mGoogleSignInClient;

        //Variables para el funcionamiento de inicio de sesion con facebook


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment

            View root = inflater.inflate(R.layout.fragment_login, container, false);

            mAuth = FirebaseAuth.getInstance();
            log_correo = root.findViewById(R.id.log_corre);
            log_pass = root.findViewById(R.id.log_pass);
            recuperarpass = root.findViewById(R.id.log_recuperarpass);
            regwhit = root.findViewById(R.id.regwhit);
            btnlogin = root.findViewById(R.id.btn_login);
            gmail = root.findViewById(R.id.login_gmail);
            mFirestore = FirebaseFirestore.getInstance();
            pbProgressLogin = root.findViewById(R.id.progress_login);


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

            mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);


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

            recuperarpass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                    startActivity(new Intent(getContext(),RecuperarActivity.class));
                }
            });
            root.post(() -> setViewAnimations());
            return root;
        }
        //----------Metodos para iniciar sesion con Google------------------

        //Inicio de sesio con Google

        @Override
        public void onStart() {
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
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
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
                        getActivity().finish();
                        startActivity(new Intent(getContext(), MainActivity.class));
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
            Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
        }

        //Fin Mostrar mensajes


        //Animaciones

        private void setViewAnimations() {
            setViewAnimation(log_correo, 300);
            setViewAnimation(log_pass, 400);
            setViewAnimation(recuperarpass, 400);
            setViewAnimation(btnlogin, 500);
            setViewAnimation(regwhit, 500);
            setViewAnimation(gmail, 600);
        }

        //Fin de Animaciones

        private void setViewAnimation(View view, int startDelay) {
            view.setTranslationX(800);
            view.setAlpha(op);
            view.animate()
                    .translationX(0)
                    .alpha(1)
                    .setDuration(800)
                    .setStartDelay(startDelay)
                    .start();
        }
        //Fin de Animaciones

        //redireccionamiento a la activitymain
        private void redireccionarMain(){
            getActivity().finish();
            startActivity(new Intent(getActivity(), MainActivity.class));
        }
    }