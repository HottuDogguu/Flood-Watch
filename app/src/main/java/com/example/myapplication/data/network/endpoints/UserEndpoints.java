package com.example.myapplication.data.network.endpoints;

import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.models.api_response.NewsAPIResponse;
import com.example.myapplication.data.models.users.ResetPasswordPutRequest;
import com.example.myapplication.data.models.users.UserChangePasswordRequest;
import com.example.myapplication.data.models.users.UserNotificationSettingsRequest;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface UserEndpoints {

    //to update password
    @Headers({"Content-Type: application/json"})
    @PUT("user/change-password")
    Call<ApiSuccessfulResponse> changePassword(@Body UserChangePasswordRequest request);

    //to get user information
    @GET("user/information")
    Call<ApiSuccessfulResponse> getUser();


    //to get 10 news
    @Headers({"Content-Type: application/json"})
    @GET("news/")
    Call<NewsAPIResponse> getTenNews(@Query("skip") int skip, @Query("limit") int limit, @Query("tags") String tags);


    //to logout users
    @Headers({"Content-Type: application/json"})
    @POST("user/logout")
    Call<ApiSuccessfulResponse> logoutUser(@Body String currentToken);

    // to update user notification settings
    @Headers({"Content-Type: application/json"})
    @PUT("user/settings/notification")
    Call<ApiSuccessfulResponse> updateNotificationSettings(@Body UserNotificationSettingsRequest userNotificationSettings);

    //to update user fcm token every login or signup
    @Headers({"Content-Type: application/json"})
    @PUT("user/fcm-token")
    Call<ApiSuccessfulResponse> updateFCMToken(@Body String newFcmToken, @Header("Authorization") String accessToken);


    //to update user information
    @Multipart
    @PUT("user/information")
    Call<ApiSuccessfulResponse> updateInfo(@Part("fullname") RequestBody fullname,
                                           @Part MultipartBody.Part img_file,
                                           @Part("email") RequestBody email,
                                           @Part("contact_number") RequestBody contact_number,
                                           @Part("street") RequestBody street,
                                           @Part("barangay") RequestBody barangay,
                                           @Part("city") RequestBody city);

    @Headers({"Content-Type: application/json"})
    @POST("auth/email/re-send")
    Call<ApiSuccessfulResponse> reSendEmailVerification(@Query("email")String email);

    @Headers({"Content-Type: application/json"})
    @PUT("user/reset-password")
    Call<ApiSuccessfulResponse> resetUserPassword(@Body ResetPasswordPutRequest request);
}
