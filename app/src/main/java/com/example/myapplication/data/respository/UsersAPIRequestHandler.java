package com.example.myapplication.data.respository;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.calbacks.ResponseCallback;

import com.example.myapplication.data.models.api_response.ApiMeteoResponse;
import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.models.api_response.NewsAPIResponse;
import com.example.myapplication.data.models.users.UserChangePasswordRequest;
import com.example.myapplication.data.models.users.UserNotificationSettingsRequest;
import com.example.myapplication.data.models.users.UsersUpdateInformationRequest;

import com.example.myapplication.data.network.APIBuilder;
import com.example.myapplication.data.network.endpoints.AuthenticationEndpoints;

import com.example.myapplication.data.network.endpoints.FloodWeatherNotificationEndpoints;
import com.example.myapplication.data.network.endpoints.UserEndpoints;


import com.example.myapplication.utils.GlobalUtility;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersAPIRequestHandler extends BaseRepository {

    private Activity activity;
    private APIBuilder api;
    private GlobalUtility globalUtility;
    private AuthenticationEndpoints authenticationEndpoint;
    private UserEndpoints userEndpoints;
    private FloodWeatherNotificationEndpoints weatherNotification;
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


        userEndpoints = api.createService(UserEndpoints.class);
        userEndpoints.updateInfo(request.getFullnameBody(),
                        request.getImagePart(),
                        request.getEmailBody(),
                        request.getContactNumberBody(),
                        request.getStreetBody(),
                        request.getBarangayBody(),
                        request.getCityBody())
                .enqueue(new Callback<ApiSuccessfulResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiSuccessfulResponse> call, @NonNull Response<ApiSuccessfulResponse> response) {

                        globalUtility.parseAPIResponse(response, callback);

                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiSuccessfulResponse> call, Throwable t) {
                        callback.onError(t);
                    }
                });

    }

    public void getUserInformation(ResponseCallback<ApiSuccessfulResponse> callback) {


        userEndpoints= api.createService(UserEndpoints.class);

        userEndpoints.getUser().enqueue(new Callback<ApiSuccessfulResponse>() {
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

    public void changeUserPassword(UserChangePasswordRequest request,
                                   ResponseCallback<ApiSuccessfulResponse> callback) {
        userEndpoints= api.createService(UserEndpoints.class);
        userEndpoints.changePassword(request).enqueue(new Callback<ApiSuccessfulResponse>() {
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
        userEndpoints = api.createService(UserEndpoints.class);
        userEndpoints.logoutUser(token).enqueue(new Callback<ApiSuccessfulResponse>() {
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

    public void uploadProfilePhoto(MultipartBody.Part imageFile, ResponseCallback<ApiSuccessfulResponse> callback) {

        authenticationEndpoint = api.createService(AuthenticationEndpoints.class);
        authenticationEndpoint.uploadPhoto(imageFile).enqueue(new Callback<ApiSuccessfulResponse>() {
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
        userEndpoints = api.createService(UserEndpoints.class);
        userEndpoints.updateFCMToken(newFCMToken, "Bearer " + accessToken).enqueue(new Callback<ApiSuccessfulResponse>() {
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
        userEndpoints= api.createService(UserEndpoints.class);
        userEndpoints.updateNotificationSettings(request).enqueue(new Callback<ApiSuccessfulResponse>() {
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

    public void getNewsPaginated(int skip, int limit, String tags, ResponseCallback<NewsAPIResponse> callback){
        userEndpoints = api.createService(UserEndpoints.class);
        userEndpoints.getTenNews(skip,limit,tags).enqueue(new Callback<NewsAPIResponse>() {
            @Override
            public void onResponse(Call<NewsAPIResponse> call, Response<NewsAPIResponse> response) {
                globalUtility.parseAPIResponse(response,callback);

            }
            @Override
            public void onFailure(Call<NewsAPIResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void getInitialForecastData(ResponseCallback<ApiMeteoResponse> callback){
        weatherNotification = api.createService(FloodWeatherNotificationEndpoints.class);
        weatherNotification.getMeteoWeatherForecast().enqueue(new Callback<ApiMeteoResponse>() {
            @Override
            public void onResponse(Call<ApiMeteoResponse> call, Response<ApiMeteoResponse> response) {
                globalUtility.parseAPIResponse(response,callback);

            }

            @Override
            public void onFailure(Call<ApiMeteoResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
    public void reSendEmailVerification(String email, ResponseCallback<ApiSuccessfulResponse> callback){
        userEndpoints = api.createService(UserEndpoints.class);
        userEndpoints.reSendEmailVerification(email).enqueue(new Callback<ApiSuccessfulResponse>() {
            @Override
            public void onResponse(Call<ApiSuccessfulResponse> call, Response<ApiSuccessfulResponse> response) {
                globalUtility.parseAPIResponse(response,callback);
            }

            @Override
            public void onFailure(Call<ApiSuccessfulResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }



}
