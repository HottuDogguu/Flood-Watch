//package com.example.myapplication.data.network.endpoints.users;
//
//import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
//
//import okhttp3.MultipartBody;
//import okhttp3.RequestBody;
//import retrofit2.Call;
//import retrofit2.http.Header;
//import retrofit2.http.Multipart;
//import retrofit2.http.PUT;
//import retrofit2.http.Part;
//
//public interface UserUpdateInformation {
//    @Multipart
//    @PUT("user/information")
//    Call<ApiSuccessfulResponse> updateInfo(@Part("fullname") RequestBody fullname,
//                                           @Part MultipartBody.Part img_file,
//                                           @Part("email") RequestBody email,
//                                           @Part("contact_number") RequestBody contact_number,
//                                           @Part("street") RequestBody street,
//                                           @Part("barangay") RequestBody barangay,
//                                           @Part("city") RequestBody city);
//}
