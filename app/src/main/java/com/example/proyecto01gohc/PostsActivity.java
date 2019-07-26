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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto01gohc.Interface.JsonPlaceHolderApi;
import com.example.proyecto01gohc.Model.Post;
import com.example.proyecto01gohc.Model.User;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostsActivity extends AppCompatActivity {

    SearchView buscarPost;
    ListView listaPosts;
    TextView txtPosts;
    ImageView imageViewInfoUsuario;
    ArrayAdapter<String> adapter;
    SimpleAdapter simpleAdapter;
    FirebaseAuth auth;
    ProgressBar progreso;
    List<Post> listaDePosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);


        buscarPost=findViewById(R.id.searchPosts);
        txtPosts=findViewById(R.id.textPosts);
        listaPosts=findViewById(R.id.listaViewPosts);
        listaPosts.setTextFilterEnabled(true);
        imageViewInfoUsuario=findViewById(R.id.imageButtonInfoUsuario);
        progreso = findViewById(R.id.cargandoPosts);
        auth = FirebaseAuth.getInstance();

        Bundle miBundle = this.getIntent().getExtras();


            String usuarioId = miBundle.getString("userId");
           final int useriden = Integer.parseInt(usuarioId);

        imageViewInfoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DetalleUsuario.class);
                Bundle miBundle = new Bundle();

                miBundle.putString("userId",""+ useriden);
                intent.putExtras(miBundle);
                startActivity(intent);

            }
        });

        getPosts(useriden);
        buscarPost();

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
                FirebaseUser user = auth.getCurrentUser();
                Toast.makeText(getApplicationContext(), "Has cerrado la sesión con: "+user.getEmail(), Toast.LENGTH_SHORT).show();
                LoginManager.getInstance().logOut();
                auth.getInstance().signOut();
                startActivity(new Intent(getBaseContext(), MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finishAndRemoveTask();
                break;
            case R.id.itemDesarrollador:
                AlertDialog.Builder dialog = new AlertDialog.Builder(PostsActivity.this);
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
    ////////////petición a la api con retrofit////////////////
    private void getPosts(final int userId) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        progreso.setVisibility(View.VISIBLE);
        Call<List<Post>> call = jsonPlaceHolderApi.getPosts(userId);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                //si la respuesta no fue exitosa
                if (!response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Código: "+response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                listaDePosts = response.body();
                progreso.setVisibility(View.GONE);

                listarPosts();
                funcionalidadClickPosts();
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                progreso.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listarPosts(){
        mostrarInfoPost(listaDePosts);

        for (Post u : listaDePosts) {

            Log.d("idPost", ""+u.getId());
            Log.d("title", "" +u.getTitle());
        }

    }

    private  void funcionalidadClickPosts(){
        listaPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                HashMap<String, String> hashMap = (HashMap<String, String>)listaPosts.getItemAtPosition(i);
                final String id = hashMap.get("idPost");
                final String titulo = hashMap.get("namePost");

                Log.d("id de Post", ""+id);
                Log.d("Título de Post", titulo);


                ///////////////////Intent para la siguiente actividad del ContenidoPost, en ella se envía el id del post///////
                Intent intent = new Intent(getApplicationContext(), ContenidoPost.class);
                Bundle miBundle = new Bundle();
                miBundle.putString("idPost", String.valueOf(id));
                intent.putExtras(miBundle);
                startActivity(intent);
            }
        });

    }

    private void buscarPost(){
        buscarPost.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (TextUtils.isEmpty(s)) {
                    listaPosts.clearTextFilter();
                } else {
                    listaPosts.setFilterText(s);
                }
                return true;

            }
        });
    }
    ///////////método que recibe una lista del tipo User y almacena datos del objeto en un HashMap///////////
    private void mostrarInfoPost(List<Post> listPost) {

        if (listPost!=null){
            ArrayList<Map<String, Object>> itemDataList = new ArrayList<Map<String, Object>>();

            int size= listPost.size();

            for (int i=0; i<size; i++ ){
                Post post = listPost.get(i);
                HashMap<String,Object> listItemMap = new HashMap<String, Object>();
                listItemMap.put("idPost",  String.valueOf(post.getId()));
                listItemMap.put("namePost",  post.getTitle());
                itemDataList.add(listItemMap);
            }
            simpleAdapter = new SimpleAdapter(this, itemDataList, android.R.layout.simple_list_item_1,
                    new String[]{"namePost", "idPost"}, new int[]{android.R.id.text1});
            listaPosts.setAdapter(simpleAdapter);
        }
    }
}
