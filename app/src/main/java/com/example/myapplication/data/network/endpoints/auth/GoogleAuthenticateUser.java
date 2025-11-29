package com.example.myapplication.data.network.endpoints.auth;

import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;

import retrofit2.Call;
import retrofit2.http.Body;

import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GoogleAuthenticateUser {

    //This is the endpoint of the google login
    @Headers({"Content-Type: application/json"})
    @POST("auth/google/callback")
    Call<ApiSuccessfulResponse> authenticateUser(@Body String token);
}
