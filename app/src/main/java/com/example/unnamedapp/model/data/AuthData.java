package com.example.unnamedapp.model.data;

public class AuthData {
    public String token;
    public String email;
    public String name;

    public AuthData(String token, String email, String name) {
        this.token = token;
        this.email = email;
        this.name = name;
    }
}
