package com.example.myapplication.data.network.endpoints.auth;

import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface UploadProfileUser {

    @Multipart
    @PUT("user/profile-image")
    Call<ApiSuccessfulResponse> uploadPhoto(@Part MultipartBody.Part image,
                                            @Header("Authorization") String token);
}
