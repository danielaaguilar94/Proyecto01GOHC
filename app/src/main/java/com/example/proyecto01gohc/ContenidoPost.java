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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto01gohc.Interface.JsonPlaceHolderApi;
import com.example.proyecto01gohc.Model.Post;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ContenidoPost extends AppCompatActivity {

    TextView tvTitle, tvBody;
    FirebaseAuth auth;
    ProgressBar progreso;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenido_post);

        tvTitle = findViewById(R.id.textoTitle);
        tvBody = findViewById(R.id.textoBody);
        progreso = findViewById(R.id.cargandoContenidoPost);
        auth = FirebaseAuth.getInstance();

        Bundle miBundle = this.getIntent().getExtras();


        String idPost = miBundle.getString("idPost");
        int id = Integer.parseInt(idPost);


        getContPosts(id);
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
                AlertDialog.Builder dialog = new AlertDialog.Builder(ContenidoPost.this);
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

    private void getContPosts(int id) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        progreso.setVisibility(View.VISIBLE);
        Call<Post> call = jsonPlaceHolderApi.getContPost(id);
        call.enqueue(new Callback <Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                //si la respuesta no fue exitosa
                if (!response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Código: "+response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                final Post post = response.body();
                progreso.setVisibility(View.GONE);

                String title = post.getTitle();
                String body = post.getBody();

                tvTitle.setText(title);
                tvBody.setText(body);



                    Log.d("title", "" +title);
                    Log.d("body", "" +body);

                }


            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                progreso.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
