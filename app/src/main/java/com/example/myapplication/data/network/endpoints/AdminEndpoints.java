package com.example.myapplication.data.network.endpoints;

import com.example.myapplication.data.models.api_response.AdminDashboardApiResponse;
import com.example.myapplication.data.models.api_response.ApiUsesInformationResponse;
import com.example.myapplication.data.models.api_response.NewsAPIResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AdminEndpoints {
    @Headers({"Content-Type: application/json"})
    @GET("admin/dashboard")
    Call<AdminDashboardApiResponse> getAdminDashboard();

    @Headers({"Content-Type: application/json"})
    @GET("admin/users/information")
    Call<ApiUsesInformationResponse> getUserInformation(@Query("skip") int skip, @Query("limit") int limit);

    @Multipart
    @POST("admin/news")
    Call<NewsAPIResponse> createNews(
            @Part("title") RequestBody title,
            @Part("content") RequestBody content,
            @Part("status") RequestBody status,
            @Part List<MultipartBody.Part> images,
            @Part("tags") List<RequestBody> tags,
            @Part("source") RequestBody source
    );

    @Multipart
    @PUT("admin/news/{news_id}")
    Call<NewsAPIResponse> updateNews(@Path("news_id") String newsId,
                                     @Part("title") RequestBody title,
                                     @Part("content") RequestBody content,
                                     @Part("status") RequestBody status,
                                     @Part List<MultipartBody.Part> images,
                                     @Part("tags") List<RequestBody> tags,
                                     @Part("source") RequestBody source);

}
