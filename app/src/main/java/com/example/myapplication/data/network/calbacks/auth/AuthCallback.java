package com.example.myapplication.data.network.calbacks.auth;

import com.example.myapplication.data.models.auth.LoginResponse;

public interface AuthCallback {
        /**
        This callback to set data based on the response of the API call in Authentication
        */
        void onSuccess(LoginResponse response);
        void onError(Throwable t);
}
