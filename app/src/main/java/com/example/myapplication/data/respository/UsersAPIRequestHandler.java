package com.example.myapplication.data.respository;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.calbacks.ResponseCallback;

import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.models.api_response.NewsAPIResponse;
import com.example.myapplication.data.models.users.UserChangePasswordRequest;
import com.example.myapplication.data.models.users.UserNotificationSettingsRequest;
import com.example.myapplication.data.models.users.UsersUpdateInformationRequest;

import com.example.myapplication.data.network.APIBuilder;
import com.example.myapplication.data.network.endpoints.auth.UploadProfileUser;
import com.example.myapplication.data.network.endpoints.users.UserChangePassword;
import com.example.myapplication.data.network.endpoints.users.UserGetInformation;
import com.example.myapplication.data.network.endpoints.users.UserGetTenNews;
import com.example.myapplication.data.network.endpoints.users.UserLogout;
import com.example.myapplication.data.network.endpoints.users.UserNotificationSettings;
import com.example.myapplication.data.network.endpoints.users.UserUpdateFCMToken;
import com.example.myapplication.data.network.endpoints.users.UserUpdateInformation;

import com.example.myapplication.utils.GlobalUtility;

import okhttp3.Headers;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersAPIRequestHandler extends BaseRepository {

    private Activity activity;
    private APIBuilder api;
    private GlobalUtility globalUtility;
    private Context context;
    private String ACCESS_TOKEN_KEY;

    public UsersAPIRequestHandler(Activity activity, Context context) {
        super(activity, context);
        this.context = context;
        this.activity = activity;
        this.api = new APIBuilder(context);
        this.globalUtility = new GlobalUtility();
        ACCESS_TOKEN_KEY = globalUtility.getValueInYAML(BuildConfig.ACCESS_TOKEN_KEY, context);

    }

    public void updateUserInfo(UsersUpdateInformationRequest request,
                               ResponseCallback<ApiSuccessfulResponse> callback) {



                    UserUpdateInformation updateInformation = api.createService(UserUpdateInformation.class);
                    updateInformation.updateInfo(request.getFullnameBody(),
                                    request.getImagePart(),
                                    request.getEmailBody(), request.getContactNumberBody(),
                                    request.getSecondNumberBody(), request.getStreetBody(),
                                    request.getBarangayBody(),
                                    request.getCityBody())
                            .enqueue(new Callback<ApiSuccessfulResponse>() {
                                @Override
                                public void onResponse(@NonNull Call<ApiSuccessfulResponse> call, @NonNull Response<ApiSuccessfulResponse> response) {

                                    globalUtility.parseAPIResponse(response, callback);
//                                    setRefreshTokenToDataStorage(response);
                                }

                                @Override
                                public void onFailure(@NonNull Call<ApiSuccessfulResponse> call, Throwable t) {
                                    callback.onError(t);
                                }
                            });

    }

    public void getUserInformation( ResponseCallback<ApiSuccessfulResponse> callback) {


        UserGetInformation userGetInformation = api.createService(UserGetInformation.class);

        userGetInformation.getUser().enqueue(new Callback<ApiSuccessfulResponse>() {
            @Override
            public void onResponse(Call<ApiSuccessfulResponse> call, Response<ApiSuccessfulResponse> response) {
                globalUtility.parseAPIResponse(response, callback);
                // handle new access token
//                setRefreshTokenToDataStorage(response);
            }

            @Override
            public void onFailure(Call<ApiSuccessfulResponse> call, Throwable t) {

            }
        });

    }

    public void changeUserPassword( UserChangePasswordRequest request,
                                   ResponseCallback<ApiSuccessfulResponse> callback) {
        UserChangePassword userChangePassword = api.createService(UserChangePassword.class);
        userChangePassword.changePassword(request).enqueue(new Callback<ApiSuccessfulResponse>() {
            @Override
            public void onResponse(Call<ApiSuccessfulResponse> call, Response<ApiSuccessfulResponse> response) {

                globalUtility.parseAPIResponse(response, callback);
                // handle new access token
//                setRefreshTokenToDataStorage(response);
            }

            @Override
            public void onFailure(Call<ApiSuccessfulResponse> call, Throwable t) {

                throw new RuntimeException(t.getMessage());

            }
        });
    }

    public void logOutUser(String token, ResponseCallback<ApiSuccessfulResponse> callback) {
        UserLogout logout = api.createService(UserLogout.class);
        logout.logoutUser(token).enqueue(new Callback<ApiSuccessfulResponse>() {
            @Override
            public void onResponse(Call<ApiSuccessfulResponse> call, Response<ApiSuccessfulResponse> response) {

                globalUtility.parseAPIResponse(response, callback);
//                setRefreshTokenToDataStorage(response);
            }

            @Override
            public void onFailure(Call<ApiSuccessfulResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void uploadProfilePhoto(MultipartBody.Part imageFile,ResponseCallback<ApiSuccessfulResponse> callback) {

        UploadProfileUser uploadProfileUser = api.createService(UploadProfileUser.class);
        uploadProfileUser.uploadPhoto(imageFile).enqueue(new Callback<ApiSuccessfulResponse>() {
            @Override
            public void onResponse(Call<ApiSuccessfulResponse> call, Response<ApiSuccessfulResponse> response) {

                //Handle success and error response
                globalUtility.parseAPIResponse(response, callback);
                //refresh token
//                setRefreshTokenToDataStorage(response);
            }

            @Override
            public void onFailure(Call<ApiSuccessfulResponse> call, Throwable t) {
                callback.onError(t);
            }
        });


    }

    public void UpdateFCMToken(String newFCMToken, String accessToken, ResponseCallback<ApiSuccessfulResponse> callback) {
        UserUpdateFCMToken userUpdateFCMToken = api.createService(UserUpdateFCMToken.class);
        userUpdateFCMToken.updateFCMToken(newFCMToken, "Bearer " + accessToken).enqueue(new Callback<ApiSuccessfulResponse>() {
            @Override
            public void onResponse(Call<ApiSuccessfulResponse> call, Response<ApiSuccessfulResponse> response) {


                //then handle response
                globalUtility.parseAPIResponse(response, callback);

                //check for refresh access token first
//                setRefreshTokenToDataStorage(response);
            }

            @Override
            public void onFailure(Call<ApiSuccessfulResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void updateUserNotificationSettings(UserNotificationSettingsRequest request, ResponseCallback<ApiSuccessfulResponse> callback) {
        UserNotificationSettings updateSettings = api.createService(UserNotificationSettings.class);
        updateSettings.updateNotificationSettings(request).enqueue(new Callback<ApiSuccessfulResponse>() {
            @Override
            public void onResponse(Call<ApiSuccessfulResponse> call, Response<ApiSuccessfulResponse> response) {

                //then handle the api response
                globalUtility.parseAPIResponse(response, callback);
                //get updated access token
//                setRefreshTokenToDataStorage(response);
            }

            @Override
            public void onFailure(Call<ApiSuccessfulResponse> call, Throwable t) {

            }
        });

    }

    public void getTenNews(ResponseCallback<NewsAPIResponse> callback) {
        UserGetTenNews userGetTenNews = api.createService(UserGetTenNews.class);
        userGetTenNews.getTenNewsResponse().enqueue(new Callback<NewsAPIResponse>() {
            @Override
            public void onResponse(Call<NewsAPIResponse> call, Response<NewsAPIResponse> response) {
                globalUtility.parseAPIResponse(response, callback);
            }

            @Override
            public void onFailure(Call<NewsAPIResponse> call, Throwable t) {

            }
        });

    }

}
