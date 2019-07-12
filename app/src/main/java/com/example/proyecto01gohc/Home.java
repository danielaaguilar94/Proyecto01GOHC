package com.example.proyecto01gohc;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends Application {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    @Override
    public void onCreate() {
        super.onCreate();

        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        if (firebaseUser!=null)
        {

            Intent intent=new Intent(Home.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }


    }
}
