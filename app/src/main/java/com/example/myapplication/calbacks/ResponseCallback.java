package com.example.myapplication.calbacks;

public interface ResponseCallback<T> {
    /**
     * This callback to set data based on the response of the API call
     */
    void onSuccess(T response);

    void onError(Throwable t);
}
