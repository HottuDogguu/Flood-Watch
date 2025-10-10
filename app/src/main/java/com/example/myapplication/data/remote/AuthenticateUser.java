package com.example.myapplication.data.remote;

import com.example.myapplication.data.models.auth.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AuthenticateUser{

    @FormUrlEncoded
    @Headers({
            "Accept: application/json"
    })
    @POST("api/v1/auth/login")
    Call<LoginResponse> authenticateUser( @Field("username") String username,
                                          @Field("password") String password);
}
