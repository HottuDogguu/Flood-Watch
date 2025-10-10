package com.example.myapplication.data.respository;


import androidx.annotation.NonNull;

import com.example.myapplication.data.models.auth.AuthCallback;
import com.example.myapplication.data.models.auth.LoginRequestPost;
import com.example.myapplication.data.models.auth.LoginResponse;
import com.example.myapplication.data.remote.AuthenticateUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthenticationAPI {
    Retrofit retrofit;

    public AuthenticationAPI() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.7.41:9898/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public void getResponse(LoginRequestPost requestPost, AuthCallback callback) {
        AuthenticateUser auth = this.retrofit.create(AuthenticateUser.class);

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