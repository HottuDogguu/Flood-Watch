package com.example.myapplication.data.network.endpoints.users;

import com.example.myapplication.data.models.users.UsersGetInformationResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;

public interface UserGetInformation {

    @GET("user/information")
    Call<UsersGetInformationResponse> getUser(@Header("Authorization") String token);
}
