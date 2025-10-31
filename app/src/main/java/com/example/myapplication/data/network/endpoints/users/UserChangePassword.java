package com.example.myapplication.data.network.endpoints.users;

import com.example.myapplication.data.models.users.UserChangePasswordRequest;
import com.example.myapplication.data.models.users.UserChangePasswordResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PUT;

public interface UserChangePassword {
    @Headers({"Content-Type: application/json"})
    @PUT("user/change-password")
    Call<UserChangePasswordResponse> changePassword(@Body UserChangePasswordRequest request,
                                                    @Header("Authorization") String token);

}
