package com.example.myapplication.data.models.auth;

public class GoogleTokenRequest {
    String token;
    public GoogleTokenRequest(String token){
        this.token = token;

    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
