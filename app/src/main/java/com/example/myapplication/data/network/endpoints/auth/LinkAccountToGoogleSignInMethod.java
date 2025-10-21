package com.example.myapplication.data.network.endpoints.auth;

import com.example.myapplication.data.models.auth.GoogleAuthLoginResponse;
import com.example.myapplication.data.models.auth.LinkAccountToMultipleSiginMethodsRequest;
import com.example.myapplication.data.models.auth.LoginManualResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface LinkAccountToGoogleSignInMethod {

    @Headers({"Content-Type: application/json"})
    @POST("auth/linked/account")
    Call<LoginManualResponse> authenticateUser(@Body LinkAccountToMultipleSiginMethodsRequest request);
}
