package com.example.myapplication.data.network.endpoints.flood;

import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.models.api_response.FiveWeatherForecast;
import com.example.myapplication.data.models.api_response.ListOfNotificationResponse;
import com.example.myapplication.data.models.api_response.NewsAPIResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface FloodWeatherNotification {


    //get latest flood data
    @Headers({"Content-Type: application/json"})
    @GET("flood-data/first")
    Call<ApiSuccessfulResponse> getLatestFloodData();


    //Get news
    @Headers({"Content-Type: application/json"})
    @GET("news/")
    Call<NewsAPIResponse> getTenNews(@Query("skip") int skip, @Query("limit") int limit);


    //get notifications
    @Headers({"Content-Type: application/json"})
    @GET("notification/")
    Call<ListOfNotificationResponse> getRecentNotification(@Query("skip")int skip, @Query("limit") int limit);


    //get weaather forecast
    @Headers({"Content-Type: application/json"})
    @GET("flood-data/weather-forecast")
    Call<FiveWeatherForecast> getFiveHoursWeatherForecast();

}
