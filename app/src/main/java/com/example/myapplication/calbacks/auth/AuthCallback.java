package com.example.myapplication.calbacks.auth;

public interface AuthCallback<T> {
        /**
        This callback to set data based on the response of the API call in Authentication
        */
        void onSuccess(T response);
        void onError(Throwable t);
}
