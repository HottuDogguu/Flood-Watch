package com.example.myapplication.data.network.endpoints.flood;

import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface FloodData {

    @Headers({"Content-Type: application/json"})
    @GET("flood-data/first")
    Call<ApiSuccessfulResponse> getLatestFloodData();

}
