package com.example.myapplication.data.models.auth;

public class GoogleTokenRequestPost {
    String token;
    public GoogleTokenRequestPost(String token){
        this.token = token;

    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
