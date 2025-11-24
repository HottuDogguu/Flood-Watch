package com.example.myapplication.data.network.endpoints.flood;

import com.example.myapplication.data.models.api_response.ListOfNotificationResponse;
import com.example.myapplication.data.models.api_response.NewsAPIResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface NewsPaginated {
    @Headers({"Content-Type: application/json"})
    @GET("news/")
    Call<NewsAPIResponse> getTenNews(@Query("skip") int skip, @Query("limit") int limit);
}
