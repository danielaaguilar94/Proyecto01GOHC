package com.example.proyecto01gohc;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto01gohc.Interface.JsonPlaceHolderApi;
import com.example.proyecto01gohc.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Usuarios extends AppCompatActivity {

    SearchView buscarUsuario;
    ListView listaUsu;
    TextView txtUsuarios;

    ArrayAdapter<String> adapter;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);

        txtUsuarios=findViewById(R.id.textUsuarios);
       buscarUsuario=findViewById(R.id.searchUsuarios);
        listaUsu=findViewById(R.id.listViewUsuarios);
        auth = FirebaseAuth.getInstance();

        getUsuarios();
       // listaUsu.setTextFilterEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_contextual,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    //para el menú contexual pongo las siguientes opciones, ya sea usuarios, desarrollador o cerrar sesión
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
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
                AlertDialog.Builder dialog = new AlertDialog.Builder(Usuarios.this);
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
// método para obtener a los usuarios de la api con retrofit y usando la interface declarada en la carpeta interface
    private void getUsuarios() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<List<User>> call = jsonPlaceHolderApi.getUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, final Response<List<User>> response) {
                //si la respuesta no fue exitosa
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Código: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                final List<User> listaUsuarios = response.body();


                final String[] nombreUsuarios = new String[listaUsuarios.size()];
                final int [] idUsuarios = new int[listaUsuarios.size()];


                for (int i = 0; i < listaUsuarios.size(); i++) {
                    nombreUsuarios[i] = listaUsuarios.get(i).getName();
                   idUsuarios[i] = listaUsuarios.get(i).getId();
                }
                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, nombreUsuarios);
                listaUsu.setAdapter(adapter);
                adapter.setNotifyOnChange(true);
                listaUsu.setTextFilterEnabled(true);
                buscarUsuario.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        adapter.getFilter().filter(s.toLowerCase().toString());
                        adapter.notifyDataSetChanged();

                        return true;
                    }
                });

               listaUsu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(final AdapterView<?> adapterView, View view, int i, long l) {
                        //
                        /*Aquí mandaba a traer al idUsuarios[i] del for anterior para obtener los id's de los usuarios y lo almacenada a un int
                        pero con lo del filtro lo cambié tomando en cuenta el id del item del adapterview, en la siguiente clase de posts no
                        cambié, puesto que no me ha funcionado aquí, funcionando en esta clase, espero implementarla en la otra.*/
                        //int idUsu = idUsuarios[i];
                        int idUsu = (int) adapterView.getItemIdAtPosition(i+1);
                        String name = adapterView.getItemAtPosition(i).toString();
                        adapter.getItem(i);

                        Log.d("Nombre de usuario", ""+name);
                        Log.d("idUser", ""+idUsu);
                        Intent intent = new Intent(getApplicationContext(), PostsActivity.class);
                        Bundle miBundle = new Bundle();
                        miBundle.putString("userId", String.valueOf(idUsu));
                        intent.putExtras(miBundle);
                        startActivity(intent);
                    }

                });

                for (User u : listaUsuarios) {
                    String usuario = "";
                    usuario += "name" + u.getName();
                    Log.d("idUser", "" + u.getId());
                    Log.d("Name", u.getName());
                }
               /* buscarUsuario.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        adapter.getFilter().filter(s);
                        return false;
                    }


                    public boolean onQueryTextChange(String s) {
                        //String text = s;
                        adapter.getFilter().filter(s);;
                        return false;

                    }
                });*/
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }









}

