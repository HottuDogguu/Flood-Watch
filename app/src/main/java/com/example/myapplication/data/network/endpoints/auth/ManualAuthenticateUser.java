package com.example.myapplication.data.network.endpoints.auth;

import com.example.myapplication.data.models.auth.LoginManualResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ManualAuthenticateUser {

    @FormUrlEncoded
    @Headers({
            "Accept: application/json"
    })
    //This is the endpoint of the manual login
    @POST("api/v1/auth/login")
    Call<LoginManualResponse> authenticateUser(@Field("username") String username,
                                               @Field("password") String password);
}
