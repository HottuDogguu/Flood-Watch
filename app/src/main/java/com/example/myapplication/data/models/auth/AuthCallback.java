package com.example.myapplication.data.models.auth;

public interface AuthCallback {
        void onSuccess(LoginResponse response);
        void onError(Throwable t);
}
