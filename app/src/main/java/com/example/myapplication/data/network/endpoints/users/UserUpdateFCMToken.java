package com.example.myapplication.data.network.endpoints.users;

import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PUT;

public interface UserUpdateFCMToken {

    @Headers({"Content-Type: application/json"})
    @PUT("user/fcm-token")
    Call<ApiSuccessfulResponse> updateFCMToken(@Body String newFcmToken, @Header("Authorization") String accessToken);
}
