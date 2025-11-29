package com.example.myapplication.data.network.endpoints.users;

import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserLogout {

    @Headers({"Content-Type: application/json"})
    @POST("user/logout")
    Call<ApiSuccessfulResponse> logoutUser(@Body String currentToken);
}
