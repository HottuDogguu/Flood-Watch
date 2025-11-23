package com.example.myapplication.data.network.endpoints.alerts;

import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.models.api_response.FiveWeatherForecast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface WeatherForecastFiveHours {

    @Headers({"Content-Type: application/json"})
    @GET("flood-data/weather-forecast")
    Call<FiveWeatherForecast> getFiveHoursWeatherForecast();
}
