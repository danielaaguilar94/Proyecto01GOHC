package com.example.proyecto01gohc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etEmail,etPassword;
    private Button btnIniciar;
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static final String TAG ="FACELOG";
    private ProgressDialog dialogoProgreso;
    private TextView tvRegistrarCuenta;


    //public static final Pattern EMAIL_ADDRESS = Pattern.compile( "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+" );

    //public static final Pattern EMAIL_ADDRESS = Pattern.compile("([a-z0-9]+(\\.?[a-z0-9])*)+@(([a-z]+)\\.([a-z]+))+");
    public static final Pattern EMAIL_ADDRESS = Pattern.compile("^[\\w\\\\\\+]+(\\.[\\w\\\\]+)*@([A-Za-z0-9-]+\\.)+[A-Za-z](2,4)$");
    //public  static final Pattern EMAIL_ADDRESS = Pattern.compile( "/^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/");
    AlertDialog.Builder dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.campoEmail);
        etPassword = findViewById(R.id.campoPassword);
        btnIniciar = findViewById(R.id.botonIniciarSesion);
        tvRegistrarCuenta= findViewById(R.id.textRegistrarCuenta);
        tvRegistrarCuenta.setPaintFlags(tvRegistrarCuenta.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvRegistrarCuenta.setText("¿No estás registrado? Registrate aquí");

        mAuth = FirebaseAuth.getInstance();
        dialogoProgreso = new ProgressDialog(this);
        btnIniciar.setOnClickListener(this);
        tvRegistrarCuenta.setOnClickListener(this);
        //Inicializar el boton de login a Facebook
        callbackManager = CallbackManager.Factory.create();
        LoginButton btnloginFacebook = findViewById(R.id.boton_login_facebook);
        btnloginFacebook.setReadPermissions("email", "public_profile");
        btnloginFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
               // Log.d(TAG, "facebook:login exitoso:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:Se canceló la operación");
                Toast.makeText(getApplicationContext(), "Se canceló la operación", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:Ocurrió un error al ingresar", error);
                Toast.makeText(getApplicationContext(),"Ocurrió un error al ingresar", Toast.LENGTH_SHORT).show();
            }
        });
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser!=null)
                {
                    Toast.makeText(getApplicationContext(), "Bienvenido: "+firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                    irPantallaPrincipal();
                }
            }
        };
    }

    private void irPantallaPrincipal()
    {
        //FirebaseUser user = mAuth.getCurrentUser();
        Intent intent = new Intent(getApplicationContext(), Usuarios.class);
        startActivity(intent);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
       mAuth.addAuthStateListener(authStateListener);
    }
    public void onStop() {
        super.onStop();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.removeAuthStateListener(authStateListener);
    }

    /*public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null) {
            updateUI();
        }
    }

    private void updateUI() {
        Toast.makeText(getApplicationContext(), "Bienvenido", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), Usuarios.class);
        startActivity(intent);

    }*/

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful())
                        {
                            Toast.makeText(getApplicationContext(), "Ha fallado la autenticación",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.botonIniciarSesion:
                dialog = new AlertDialog.Builder(MainActivity.this);
                String email = etEmail.getText().toString().trim();
                String password=etPassword.getText().toString().trim();
                if (email.equals("")||password.equals("")){
                    validacionCampos();
                }

                else {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                irPantallaPrincipal();

                            } else {
                                dialog.setTitle("Usuario inexistente/válido");
                                dialog.setMessage("No se pudo loguear usuario");
                                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();

                                    }
                                });
                                AlertDialog alertDialog = dialog.create();
                                alertDialog.show();

                            }
                        }
                    });
                }
                break;

            case R.id.textRegistrarCuenta:
                irARegistraCuentaEmail();
               break;


        }

    }

    private void irARegistraCuentaEmail(){
        Intent intento = new Intent(MainActivity.this, RegistrarCuentaEmail.class);
        startActivity(intento);

    }

    private void validacionCampos() {
        String email = etEmail.getText().toString().trim();
        String password=etPassword.getText().toString().trim();

        if (email.equals(""))
        {
            etEmail.setError("Campo Requerido");
            dialog.setTitle("Campo requerido").setMessage("Ingrese email").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();

                }
            });
            AlertDialog alertDialog = dialog.create();
            alertDialog.show();

        }
        else if (password.equals(""))
        {
            etPassword.setError("Campo requerido");
            dialog.setTitle("Campo requerido").setMessage("Ingrese contraseña").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog alertDialog = dialog.create();
            alertDialog.show();

        }

        if (!validarEmail(email)){
            etEmail.setError("Formato inválido");
        }
        else{
            Toast.makeText(this, "Formato de Email válido", Toast.LENGTH_SHORT).show();

        }

    }


    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();

    }


}
