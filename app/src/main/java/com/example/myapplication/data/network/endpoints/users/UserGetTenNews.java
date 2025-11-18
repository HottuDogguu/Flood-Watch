package com.example.myapplication.data.network.endpoints.users;

import com.example.myapplication.data.models.api_response.NewsAPIResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface UserGetTenNews {
    @Headers({"Content-Type: application/json"})
    @GET("news")
    Call<NewsAPIResponse> getTenNewsResponse();

}
