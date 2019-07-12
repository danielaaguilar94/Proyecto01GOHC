package com.example.proyecto01gohc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etEmail,etPassword;
    private Button btnIniciar;
    private FirebaseAuth mAuth;
    //public static final Pattern EMAIL_ADDRESS = Pattern.compile( "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+" );

    public static final Pattern EMAIL_ADDRESS = Pattern.compile("([a-z0-9]+(\\.?[a-z0-9])*)+@(([a-z]+)\\.([a-z]+))+");
    AlertDialog.Builder dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.campoEmail);
        etPassword = findViewById(R.id.campoPassword);
        btnIniciar = findViewById(R.id.botonIniciarSesion);
        mAuth = FirebaseAuth.getInstance();
        btnIniciar.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
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
                       Toast.makeText(getApplicationContext(), "Bienvenido", Toast.LENGTH_SHORT).show();
                       Intent intent = new Intent(getApplicationContext(), Usuarios.class);
                       startActivity(intent);

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
