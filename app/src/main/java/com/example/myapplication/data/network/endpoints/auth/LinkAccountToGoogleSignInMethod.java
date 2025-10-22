package com.example.myapplication.data.network.endpoints.auth;

import com.example.myapplication.data.models.auth.LinkAccountToMultipleSiginMethodsRequest;
import com.example.myapplication.data.models.auth.ManualLoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface LinkAccountToGoogleSignInMethod {

    @Headers({"Content-Type: application/json"})
    @POST("auth/linked/account")
    Call<ManualLoginResponse> authenticateUser(@Body LinkAccountToMultipleSiginMethodsRequest request);
}
