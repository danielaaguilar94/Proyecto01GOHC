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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto01gohc.Interface.JsonPlaceHolderApi;
import com.example.proyecto01gohc.Model.Post;
import com.example.proyecto01gohc.Model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

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
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);


        buscarPost=findViewById(R.id.searchPosts);
        txtPosts=findViewById(R.id.textPosts);
        listaPosts=findViewById(R.id.listaViewPosts);
        imageViewInfoUsuario=findViewById(R.id.imageButtonInfoUsuario);
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


                //intent.putExtras(idUsuarios);
                startActivity(intent);

            }
        });



        getPosts(useriden);

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




    private void getPosts(final int userId) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Post>> call = jsonPlaceHolderApi.getPosts(userId);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                //si la respuesta no fue exitosa
                if (!response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Código: "+response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                final List<Post> listaDePosts = response.body();


                String[] titulosPosts = new String[listaDePosts.size()];
                final int[] idPosts = new int[listaDePosts.size()];

                //looping through all the heroes and inserting the names inside the string array
                for (int i = 0; i < listaDePosts.size(); i++) {
                    titulosPosts[i] = listaDePosts.get(i).getTitle();
                    idPosts[i] = listaDePosts.get(i).getId();


                }

                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, titulosPosts);
                //displaying the string array into listview
                listaPosts.setAdapter(adapter);

                listaPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        int idPost = idPosts[i];
                        Log.d("id de Post", ""+idPost);
                        Intent intent = new Intent(getApplicationContext(), ContenidoPost.class);
                        Bundle miBundle = new Bundle();
                        miBundle.putString("idPost", Integer.toString(idPost));
                        intent.putExtras(miBundle);
                        startActivity(intent);
                    }
                });

                buscarPost.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        adapter.getFilter().filter(s);
                        return false;
                    }
                });


                for (Post u:listaDePosts) {

                    Log.d("idPost", ""+u.getId());
                    Log.d("title", "" +u.getTitle());
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
