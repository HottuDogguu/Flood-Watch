package com.example.myapplication.data.network.endpoints.users;

import com.example.myapplication.data.models.users.UserLogoutResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface UserLogout {

    @Headers({"Content-Type: application/json"})
    @POST("user/logout")
    Call<UserLogoutResponse> logoutUser(@Body String token, @Header("Authorization") String accessToken);
}
