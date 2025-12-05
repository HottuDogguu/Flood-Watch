package com.example.myapplication.data.network.endpoints;

import com.example.myapplication.data.models.api_response.AdminDashboardApiResponse;
import com.example.myapplication.data.models.api_response.ApiUsesInformationResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface AdminEndpoints {
    @Headers({"Content-Type: application/json"})
    @GET("admin/dashboard")
    Call<AdminDashboardApiResponse> getAdminDashboard();

    @Headers({"Content-Type: application/json"})
    @GET("admin/users/information")
    Call<ApiUsesInformationResponse> getUserInformation(@Query("skip") int skip,@Query("limit") int limit);

}
