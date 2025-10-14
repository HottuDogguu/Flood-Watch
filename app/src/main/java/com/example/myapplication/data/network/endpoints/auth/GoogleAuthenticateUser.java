package com.example.myapplication.data.network.endpoints.auth;

import com.example.myapplication.data.models.auth.GoogleAuthResponse;
import com.example.myapplication.data.models.auth.GoogleTokenRequestPost;

import retrofit2.Call;
import retrofit2.http.Body;

import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GoogleAuthenticateUser {

    //This is the endpoint of the google login

    @Headers("Content-Type: application/json")
    @POST("api/v1/auth/google/callback")
    Call<GoogleAuthResponse> authenticateUser(@Body GoogleTokenRequestPost token);
}
