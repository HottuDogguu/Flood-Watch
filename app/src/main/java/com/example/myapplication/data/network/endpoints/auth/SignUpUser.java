package com.example.myapplication.data.network.endpoints.auth;

import com.example.myapplication.data.models.auth.LoginManualResponse;
import com.example.myapplication.data.models.auth.ManualSignUpResponse;
import com.example.myapplication.data.models.auth.SignupPostRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SignUpUser {
    @Headers({
            "Content-Type: application/json"
    })
    @POST("api/v1/auth/create-account")
    Call<ManualSignUpResponse> authenticateUser(@Body SignupPostRequest signupPostRequest);

}
