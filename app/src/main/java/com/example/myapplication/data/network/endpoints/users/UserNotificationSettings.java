package com.example.myapplication.data.network.endpoints.users;

import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.models.users.UserNotificationSettingsRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PUT;

public interface UserNotificationSettings {
    @Headers({"Content-Type: application/json"})
    @PUT("user/settings/notification")
    Call<ApiSuccessfulResponse> updateNotificationSettings(@Body UserNotificationSettingsRequest userNotificationSettings);
}
