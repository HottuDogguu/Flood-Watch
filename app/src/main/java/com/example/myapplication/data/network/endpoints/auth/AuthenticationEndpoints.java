package com.example.myapplication.data.network.endpoints.auth;

import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.models.auth.LinkAccountToMultipleSiginMethodsRequest;
import com.example.myapplication.data.models.auth.SignupPostRequest;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface AuthenticationEndpoints {



    //This is the endpoint of the google login
    @Headers({"Content-Type: application/json"})
    @POST("auth/google/callback")
    Call<ApiSuccessfulResponse> authenticateUser(@Body String token);


    @Headers({"Content-Type: application/json"})
    @POST("auth/link/account")
    Call<ApiSuccessfulResponse> authenticateUser(@Body LinkAccountToMultipleSiginMethodsRequest request);
    //for manual login
    @FormUrlEncoded
    @Headers({
            "Accept: application/json"})
    //This is the endpoint of the manual login
    @POST("auth/login")
    Call<ApiSuccessfulResponse> authenticateUser(@Field("username") String username,
                                                 @Field("password") String password);


    @Headers({"Content-Type: application/json"})
    @POST("auth/create-account")
    Call<ApiSuccessfulResponse> authenticateUser(@Body SignupPostRequest signupPostRequest);


    //to update user profile
    @Multipart
    @PUT("user/profile-image")
    Call<ApiSuccessfulResponse> uploadPhoto(@Part MultipartBody.Part image);
    //To get new or refresh access token
    @Headers({"Content-Type: application/json"})
    @POST("auth/refresh-token")
    Call<ApiSuccessfulResponse> refreshToken(@Body String oldToken);
}
