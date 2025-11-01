package com.example.myapplication.data.network.endpoints.auth;

import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;

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
    @POST("auth/login")
    Call<ApiSuccessfulResponse> authenticateUser(@Field("username") String username,
                                                 @Field("password") String password);
}
