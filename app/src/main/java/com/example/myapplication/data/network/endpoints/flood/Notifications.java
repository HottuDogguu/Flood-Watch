package com.example.myapplication.data.network.endpoints.flood;

import com.example.myapplication.data.models.api_response.ListOfNotificationResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface Notifications {

    @Headers({"Content-Type: application/json"})
    @GET("notification/")
    Call<ListOfNotificationResponse> getRecentNotification(@Query("skip")int skip, @Query("limit") int limit);
}
