package com.example.myapplication.data.respository.auth;


import androidx.annotation.NonNull;

import com.example.myapplication.data.network.APIBuilder;
import com.example.myapplication.data.network.calbacks.auth.AuthCallback;
import com.example.myapplication.data.models.auth.LoginRequestPost;
import com.example.myapplication.data.models.auth.LoginResponse;
import com.example.myapplication.data.network.endpoints.auth.AuthenticateUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticationAPI {
    public AuthenticationAPI() {

    }
    public void getResponse(LoginRequestPost requestPost, AuthCallback callback, APIBuilder api) {
        AuthenticateUser auth = api.getRetrofit().create(AuthenticateUser.class);

        auth.authenticateUser(requestPost.getEmail(),requestPost.getPassword()).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {

                // Check if no error
                if (response.isSuccessful() && response.body() != null) {
                    //then set data on callback on success
                    callback.onSuccess(response.body());
                } else {
                    //otherwise set error on callback
                    callback.onError(new Exception("Login failed: " + response.code()));
                }
            }
            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                callback.onError(t);
            }
        });
    }
}