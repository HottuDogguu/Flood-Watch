package com.example.myapplication.data.network.endpoints.alerts;

import com.example.myapplication.data.models.api_response.ListOfNotificationResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface Notifications {

    @Headers({"Content-Type: application/json"})
    @GET("notification/three-recent")
    Call<ListOfNotificationResponse> getRecentNotification();
}
