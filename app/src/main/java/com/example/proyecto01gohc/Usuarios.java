package com.example.proyecto01gohc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto01gohc.Interface.JsonPlaceHolderApi;
import com.example.proyecto01gohc.Model.User;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
    ArrayAdapter<Integer> adapterId;
    FirebaseAuth auth;
    ProgressBar progreso;
    List<User> listaUsuarios;
    SimpleAdapter simpleAdapter;
    //ArrayAdapter<HashMap<Integer,String>> adapter1;
    //ArrayList<HashMap<Integer,String>> idsAndNames;

    //private final static String TAG = Usuarios.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);
///////////////
        txtUsuarios = findViewById(R.id.textUsuarios);
        buscarUsuario = findViewById(R.id.searchUsuarios);
        listaUsu = findViewById(R.id.listViewUsuarios);
        listaUsu.setTextFilterEnabled(true);
        //listaUsu.setFilterText(.toString());
        progreso = findViewById(R.id.cargando);
        auth = FirebaseAuth.getInstance();

        getUsuarios();
        buscarUsuarios();
    }
/////////////////Se obtiente el inflater para el menú/////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_contextual, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
////////////////Menú contexual donde se muestran las siguientes opciones: usuarios, desarrollador o cerrar sesión////////////////
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemUsuarios:
                Intent intento = new Intent(this, Usuarios.class);
                startActivity(intento);
                break;
            case R.id.itemCerrarSesion:
                FirebaseUser user = auth.getCurrentUser();
                Toast.makeText(getApplicationContext(), "Has cerrado la sesión con: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                LoginManager.getInstance().logOut();
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

///////////// método para obtener a los usuarios de la api con retrofit y usando la interface declarada en la carpeta interface///////
    private void getUsuarios() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        progreso.setVisibility(View.VISIBLE);
        Call<List<User>> call = jsonPlaceHolderApi.getUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, final Response<List<User>> response) {
                //si la respuesta no fue exitosa
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Código: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                //final List<User> listaUsuarios = response.body();
                getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<List<User>>() {
                    @Override
                    public Loader<List<User>> onCreateLoader(int i, Bundle bundle) {

                        return new AsyncTaskLoader<List<User>>(Usuarios.this) {

                            List<User> users;

                            @Override
                            protected void onStartLoading() {
                                super.onStartLoading();
                                if (users == null) {

                                    progreso.setVisibility(View.VISIBLE);
                                    forceLoad();
                                } else {
                                    deliverResult(users);
                                }
                            }

                            @Override
                            public List<User> loadInBackground() {
                                try {
                                    return jsonPlaceHolderApi.getUsers().execute().body();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            }

                            @Override
                            public void deliverResult(List<User> data) {
                                super.deliverResult(data);
                                users = data;
                            }
                        };
                    }

                    @Override
                    public void onLoadFinished(Loader<List<User>> loader, List<User> users) {
                        if (users != null) {

                            listaUsuarios = response.body();

                        }
                        progreso.setVisibility(View.GONE);
                        listarUsuarios();
                        funcionalidadClicItems();

                    }

                    @Override
                    public void onLoaderReset(Loader<List<User>> loader) {
                        //listaUsu.setAdapter(null);
                    }
                });

                /*progreso.setVisibility(View.GONE);
                listaUsuarios = response.body();
                listarUsuarios();
                funcionalidadClicItems();*/
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                progreso.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarInfoUser(List<User> listaUser) {

        if (listaUser!=null){
            ArrayList<Map<String, Object>> itemDataList = new ArrayList<Map<String, Object>>();

            int size= listaUser.size();

            for (int i=0; i<size; i++ ){
                User user = listaUser.get(i);
                HashMap<String,Object> listItemMap = new HashMap<String, Object>();
                listItemMap.put("id",  String.valueOf(user.getId()));
                listItemMap.put("name",  user.getName());
                itemDataList.add(listItemMap);
            }
            simpleAdapter = new SimpleAdapter(this, itemDataList, android.R.layout.simple_list_item_2,
                    new String[]{"name", "id"}, new int[]{android.R.id.text1});
            listaUsu.setAdapter(simpleAdapter);
        }
    }

    //////////////Lista los nombres de los usuarios obtenidos de la respuesta de la API y se muestran en el listview/////////////
    private void listarUsuarios(){
        mostrarInfoUser(listaUsuarios);

        final ArrayList<String> nomUsu = new ArrayList<String>(listaUsuarios.size());
        final ArrayList<Integer> ids = new ArrayList<Integer>(listaUsuarios.size());


        final String[] nombreUsuarios = new String[listaUsuarios.size()];
        final int[] idUsuarios = new int[listaUsuarios.size()];

      //idsAndNames = new ArrayList<>();
        //final Map<ArrayList<Integer>, ArrayList<String>> idsnombres = new HashMap<ArrayList<Integer>, ArrayList<String>>(listaUsuarios.size());

        for (int i = 0; i < listaUsuarios.size(); i++) {
            nomUsu.add(listaUsuarios.get(i).getName());
            ids.add(listaUsuarios.get(i).getId());
            nombreUsuarios[i] = listaUsuarios.get(i).getName();
            idUsuarios[i] = listaUsuarios.get(i).getId();
           // HashMap<Integer,String> hashMap = new HashMap<>();
            //hashMap.put(listaUsuarios.get(i).getId(), listaUsuarios.get(i).getName());
            //idsAndNames.add(hashMap);

        }
        //ArrayAdapter<HashMap<Integer,String>> adapter1 =new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, idsAndNames);

       /* adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, nomUsu);
        //listaUsu.setAdapter(adapter1);
        //adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, idsnombres);
        adapterId = new ArrayAdapter<Integer>(getApplicationContext(),android.R.layout.simple_list_item_1,ids);
        listaUsu.setAdapter(adapterId);
        listaUsu.setAdapter(adapter);
        adapter.setNotifyOnChange(true);
        adapterId.setNotifyOnChange(true);
        //adapter1.setNotifyOnChange(true);*/


        ////////////////////Para ver en consola////////////////////////////////
        for (User u : listaUsuarios) {
            String usuario = "";
            usuario += "name" + u.getName();
            Log.d("idUser", "" + u.getId());
            Log.d("Name", u.getName());
        }

    }
/////////////////////Traida la lista, asignar la acción de los clicks por cada item de la lista///////////////////////////////
    private void funcionalidadClicItems(){
        ///////////implementar el onItemClick para los items de la lista/////////////////////
        listaUsu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, int i, long l) {

                HashMap<String, String> hashMap = (HashMap<String, String>)listaUsu.getItemAtPosition(i);
                final String id = hashMap.get("id");
                final String nombre = hashMap.get("name");

                Log.d("id de Usuario", ""+id);
                Log.d("Nombre de Usuario", nombre);


                ///////////////////Intent para la siguiente actividad de los Posts, en ella se envía el id del usuario///////
                Intent intent = new Intent(getApplicationContext(), PostsActivity.class);
                Bundle miBundle = new Bundle();
                miBundle.putString("userId", String.valueOf(id));
                intent.putExtras(miBundle);
                startActivity(intent);
            }

        });


    }
/////////////////////Pendiente-------> funcionalidad correcta del filtro y actualización de la nueva lista de usuarios en el buscador ///////////
    private void buscarUsuarios(){
        /////Función del searchview//////////////////////////////////////////////////
        buscarUsuario.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (TextUtils.isEmpty(s)) {
                    listaUsu.clearTextFilter();
                } else {
                    listaUsu.setFilterText(s.toString());
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                if (TextUtils.isEmpty(s)) {
                    listaUsu.clearTextFilter();
                } else {
                    listaUsu.setFilterText(s);
                }
                return true;
            }
        });
    }
}

