package com.example.proyecto01gohc.Interface;

import com.example.proyecto01gohc.Model.Post;
import com.example.proyecto01gohc.Model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {
    //Anotación para traer información de la pagina
    @GET("users")
    Call<List<User>> getUsers();

    @GET("posts?userId=")
    Call<List<Post>> getPosts(@Query("userId") int userId);

    @GET("posts/{id}")
    Call <Post> getContPost(@Path("id") int id);

    @GET("users/{id}")
    Call <User> getDetallesUsuario(@Path("id") int id);



}
