package com.example.myapplication.data.network.endpoints.flood;

import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.models.api_response.ApiMeteoResponse;
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

    //get notifications
    @Headers({"Content-Type: application/json"})
    @GET("notification/")
    Call<ListOfNotificationResponse> getRecentNotification(@Query("skip")int skip, @Query("limit") int limit);


    //get weather forecast
    @Headers({"Content-Type: application/json"})
    @GET("flood-data/weather-forecast")
    Call<FiveWeatherForecast> getFiveHoursWeatherForecast();

    //get initial weather forecast
    @Headers({"Content-Type: application/json"})
    @GET("flood-data/meteo/weather-forecast")
    Call<ApiMeteoResponse> getMeteoWeatherForecast();



}
