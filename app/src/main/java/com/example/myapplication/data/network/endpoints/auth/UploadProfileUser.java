package com.example.myapplication.data.network.endpoints.auth;

import com.example.myapplication.data.models.auth.UploadPhotoResponse;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface UploadProfileUser {

    @Multipart
    @PUT("user/profile-image")
    Call<UploadPhotoResponse> uploadPhoto( @Part MultipartBody.Part image,
                                            @Header("Authorization") String token);
}
