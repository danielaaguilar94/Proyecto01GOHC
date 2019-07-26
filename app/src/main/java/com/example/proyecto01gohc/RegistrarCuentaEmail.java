package com.example.proyecto01gohc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.util.regex.Pattern;

public class RegistrarCuentaEmail extends AppCompatActivity {

    //Se declara la instancia de FirebaseAuth
    private FirebaseAuth firebaseAuth;
    //se declara un ProgressDialog para mostrar mientras registra usuario
    private ProgressDialog dialogoProgreso;

    private EditText etIngresarEmail, etIngresarPass, etConfirmarPass;
    private Button botonRegistrarCuenta;
    public static final Pattern EMAIL_ADDRESS = Pattern.compile("^[\\w\\\\\\+]+(\\.[\\w\\\\]+)*@([A-Za-z0-9-]+\\.)+[A-Za-z](2,4)$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_cuenta_email);

        //se inicializa la instancia FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();
        //referenciamos los views EditText
        etIngresarEmail= findViewById(R.id.editIngresarEmail);
        etIngresarPass=findViewById(R.id.editContraseñaRegistro);
        etConfirmarPass=findViewById(R.id.editConfirmContraseñaRegistro);
        botonRegistrarCuenta=findViewById(R.id.btnRegistrarCuenta);

        //Se crea un objeto ProgressDialog para aviso al usuario del progreso de autenticación
        dialogoProgreso = new ProgressDialog(this);
    }

    public void onClick(View view) {
        registrarUsuario();

    }

    public void registrarUsuario(){



            final String correo, contraseña, confirmContraseña;
            correo=etIngresarEmail.getText().toString().trim();
            contraseña=etIngresarPass.getText().toString().trim();
            confirmContraseña=etConfirmarPass.getText().toString().trim();


            //si lo que hay en la caja de Texto asignada a correo está vacía
            if (correo.isEmpty()&&contraseña.isEmpty()&&confirmContraseña.isEmpty()){
                Toast.makeText(getApplicationContext(), "Rellene los datos del formulario para registrarse", Toast.LENGTH_SHORT).show();
                etIngresarEmail.setError("Debe ingresar un correo");
                etIngresarPass.setError("Debe ingresar contraseña de al menos 6 caracteres");
                etConfirmarPass.setError("Debe confirmar la contraseña ingresada");
                return;

            }

            else if(correo.isEmpty())
            {
                Toast.makeText(getApplicationContext(), "Ingrese correo",Toast.LENGTH_SHORT).show();
                etIngresarEmail.setError("Debe ingresar un correo");
                return;

            }
            //si lo que hay en la caja de Texto asignada a contraseña está vacía
            else if(contraseña.isEmpty())
            {
                Toast.makeText(getApplicationContext(),"Falta ingresar contraseña",Toast.LENGTH_SHORT).show();
                etIngresarPass.setError("Debe ingresar una contraseña");
                return;
            }
             else if(confirmContraseña.isEmpty())
            {
                Toast.makeText(getApplicationContext(),"Debes confirmar tu contraseña, ingrésala",Toast.LENGTH_SHORT).show();
                etConfirmarPass.setError("Requerido confirmar contraseña");
                return;
            }

             else if (!contraseña.equals(confirmContraseña))
             {
                 Toast.makeText(getApplicationContext(),"Las contraseñas no coinciden",Toast.LENGTH_SHORT).show();

                 etConfirmarPass.setError("Confirme correctamente, no coincide con la anterior");
                 etIngresarPass.setError("La contraseña no coincide con la de confirmación");
                 return;
             }

            else  {
                if(!correo.isEmpty())
                {
                    if (!validarEmailDeRegistro(correo)){
                        etIngresarEmail.setError("Formato inválido");
                    }
                    else {
                        Toast.makeText(this, "Formato de Email válido", Toast.LENGTH_SHORT).show();
                    }

                }
                dialogoProgreso.setMessage("Registrando cuenta, espere....");
                //se muestra en pantalla
                dialogoProgreso.show();
                //se asigna el metodo createUserWithEmailAndPassword a la la instancia de FirebaseAuth
                firebaseAuth.createUserWithEmailAndPassword(correo, contraseña)//se reciben como argumentos los Strings de correo y contraseña
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //si el proceso de Autenticación se realiza con éxito
                                    // Sign in success, update UI with the signed-in user's information
                                    //Se muestra un mensaje al usuario mediante un Toast
                                    Toast.makeText(getApplicationContext(), "Usuario registrado con éxito:Ya puedes Iniciar sesión  " + etIngresarEmail.getText().toString(), Toast.LENGTH_SHORT).show();
                                    irAPantallaLogin();
                                } else {
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        Toast.makeText(getApplicationContext(), "El usuario ya existe", Toast.LENGTH_SHORT).show();
                                    } else
                                    //si no se cumple, se muestra un mensaje en el Toast seguido de la excepción que se produjo en proceso
                                    {
                                        Toast.makeText(getApplicationContext(), "No se pudo registrar: registrate nuevamente" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        //updateUI(null);
                                    }
                                }
                                dialogoProgreso.dismiss();
                            }
                        });
                return;
            }


    }
    private void irAPantallaLogin()
    {
        Intent intent = new Intent(RegistrarCuentaEmail.this,MainActivity.class);
        startActivity(intent);
    }

    private boolean validarEmailDeRegistro(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();

    }
}
