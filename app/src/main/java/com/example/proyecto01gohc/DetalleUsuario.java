package com.example.proyecto01gohc;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto01gohc.Interface.JsonPlaceHolderApi;
import com.example.proyecto01gohc.Model.Address;
import com.example.proyecto01gohc.Model.Company;
import com.example.proyecto01gohc.Model.Post;
import com.example.proyecto01gohc.Model.User;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetalleUsuario extends AppCompatActivity {
    TextView tvNombreUsuario, tvEmail, tvDireccion, tvTelefono, tvEmpresa;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_usuario);

        tvNombreUsuario= findViewById(R.id.textoNombreUsuario);
        tvEmail = findViewById(R.id.informacionEmail);
        tvDireccion=findViewById(R.id.informacionDireccion);
        tvTelefono=findViewById(R.id.informacionTelefono);
        tvEmpresa=findViewById(R.id.informacionEmpresa);
        auth = FirebaseAuth.getInstance();

        Bundle miBundle = this.getIntent().getExtras();


        String idUser = miBundle.getString("userId");
        int id = Integer.parseInt(idUser);


        getDetallesUsuario(id);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_contextual,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemUsuarios:
                Intent intento = new Intent(this, Usuarios.class);
                startActivity(intento);
                break;
            case R.id.itemCerrarSesion:
                auth.getInstance().signOut();
                startActivity(new Intent(getBaseContext(), MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finishAndRemoveTask();
                break;
            case R.id.itemDesarrollador:
                AlertDialog.Builder dialog = new AlertDialog.Builder(DetalleUsuario.this);
                dialog.setTitle("Información de desarrollador");
                dialog.setMessage("Daniela del Carmen Aguilar Jiménez" + "\n" + "24 años" + "\n" + "Hobbies: Escuchar música, ir al gym, tocar guitarra, cantar");
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getDetallesUsuario(int id) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<User> call = jsonPlaceHolderApi.getDetallesUsuario(id);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                //si la respuesta no fue exitosa
                if (!response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Código: "+response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                final User user = response.body();

                String nombre = user.getName();
                String email = user.getEmail();
                Address direccion = user.getAddress();
                String telefono =user.getPhone();
                Company empresa = user.getCompany();


                tvNombreUsuario.setText(nombre);
                tvEmail.setText(email);
                tvDireccion.setText(direccion.getStreet()+", "+direccion.getSuite()+", "+direccion.getCity());
                tvTelefono.setText(telefono);
                tvEmpresa.setText(empresa.getName());



                Log.d("nombre", "" +nombre);
                Log.d("email", "" +email);
                Log.d("direccion", "" +direccion);
                Log.d("telefono", "" +telefono);
                Log.d("email", "" +empresa);

            }





            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}