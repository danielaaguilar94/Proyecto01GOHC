package com.example.proyecto01gohc.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Company {


    private String name;
    private String catchPhrase;
    private String bs;

    public String getName() {
        return name;
    }

    public String getCatchPhrase() {
        return catchPhrase;
    }

    public String getBs() {
        return bs;
    }
}
