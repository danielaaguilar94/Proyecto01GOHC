package com.example.proyecto01gohc.Model;


public class User {


    private int id;

    private String name;

    private String username;

   private String email;

    private Address address;

    private Geo geo;

    private String phone;

    private String website;

    private Company company;

    public User(int id, String name, String username, String email, Address address, Geo geo, String phone, String website, Company company) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.address = address;
        this.geo = geo;
        this.phone = phone;
        this.website = website;
        this.company = company;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    public Geo getGeo() {
        return geo;
    }

    public String getPhone() {
        return phone;
    }

    public String getWebsite() {
        return website;
    }

    public Company getCompany() {
        return company;
    }


}
