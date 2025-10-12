package com.example.myapplication.data.network.endpoints.auth;

import com.example.myapplication.data.models.auth.GoogleAuthResponse;
import com.example.myapplication.data.models.auth.LoginManualResponse;

import retrofit2.Call;
import retrofit2.http.GET;

import retrofit2.http.Query;

public interface GoogleAuthenticateUser {

    //This is the endpoint of the google login
    @GET("api/v1/auth/google/callback")
    Call<GoogleAuthResponse> authenticateUser(@Query("token") String token);
}
