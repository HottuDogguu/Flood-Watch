package com.example.myapplication.data.respository.users;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.calbacks.ResponseCallback;

import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.models.users.UserChangePasswordRequest;
import com.example.myapplication.data.models.users.UserNotificationSettingsRequest;
import com.example.myapplication.data.models.users.UsersUpdateInformationRequest;

import com.example.myapplication.data.network.APIBuilder;
import com.example.myapplication.data.network.endpoints.auth.UploadProfileUser;
import com.example.myapplication.data.network.endpoints.users.UserChangePassword;
import com.example.myapplication.data.network.endpoints.users.UserGetInformation;
import com.example.myapplication.data.network.endpoints.users.UserLogout;
import com.example.myapplication.data.network.endpoints.users.UserNotificationSettings;
import com.example.myapplication.data.network.endpoints.users.UserUpdateFCMToken;
import com.example.myapplication.data.network.endpoints.users.UserUpdateInformation;
import com.example.myapplication.security.DataStorageManager;
import com.example.myapplication.utils.GlobalUtility;

import java.util.Objects;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersAPIRequestHandler {

    private Activity activity;
    private APIBuilder api;
    private GlobalUtility globalUtility;
    private DataStorageManager dataStorageManager;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Context context;

    public UsersAPIRequestHandler(Activity activity, Context context) {
        this.context = context;
        this.activity = activity;
        this.api = new APIBuilder(context);
        this.globalUtility = new GlobalUtility();
        dataStorageManager = DataStorageManager.getInstance(context);
    }

    public void updateUserInfo(UsersUpdateInformationRequest request,
                               String token,
                               ResponseCallback<ApiSuccessfulResponse> callback) {
        try {

            String tokenHeader = "Bearer " + token;
            UserUpdateInformation updateInformation = api.getRetrofit().create(UserUpdateInformation.class);
            updateInformation.updateInfo(request.getFullnameBody(),
                            request.getImagePart(),
                            request.getEmailBody(), request.getContactNumberBody(),
                            request.getSecondNumberBody(), request.getStreetBody(),
                            request.getBarangayBody(),
                            request.getCityBody(), tokenHeader)
                    .enqueue(new Callback<ApiSuccessfulResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<ApiSuccessfulResponse> call, @NonNull Response<ApiSuccessfulResponse> response) {
                            setRefreshTokenToDataStorage(response);
                            globalUtility.parseAPIResponse(response, callback);
                        }

                        @Override
                        public void onFailure(@NonNull Call<ApiSuccessfulResponse> call, Throwable t) {
                            callback.onError(t);
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void getUserInformation(String token, ResponseCallback<ApiSuccessfulResponse> callback) {
        UserGetInformation userGetInformation = api.getRetrofit().create(UserGetInformation.class);
        token = "Bearer " + token;
        userGetInformation.getUser(token).enqueue(new Callback<ApiSuccessfulResponse>() {
            @Override
            public void onResponse(Call<ApiSuccessfulResponse> call, Response<ApiSuccessfulResponse> response) {
                // handle new access token
                setRefreshTokenToDataStorage(response);
                globalUtility.parseAPIResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiSuccessfulResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void changeUserPassword(String token, UserChangePasswordRequest request,
                                   ResponseCallback<ApiSuccessfulResponse> callback) {
        token = "Bearer " + token;
        UserChangePassword userChangePassword = api.getRetrofit().create(UserChangePassword.class);
        userChangePassword.changePassword(request, token).enqueue(new Callback<ApiSuccessfulResponse>() {
            @Override
            public void onResponse(Call<ApiSuccessfulResponse> call, Response<ApiSuccessfulResponse> response) {
                // handle new access token
                setRefreshTokenToDataStorage(response);
                globalUtility.parseAPIResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiSuccessfulResponse> call, Throwable t) {

                throw new RuntimeException(t.getMessage());

            }
        });
    }

    public void logOutUser(String token, ResponseCallback<ApiSuccessfulResponse> callback) {
        UserLogout logout = api.getRetrofit().create(UserLogout.class);
        String accessToken = "Bearer " + token;
        logout.logoutUser(token, accessToken).enqueue(new Callback<ApiSuccessfulResponse>() {
            @Override
            public void onResponse(Call<ApiSuccessfulResponse> call, Response<ApiSuccessfulResponse> response) {
                setRefreshTokenToDataStorage(response);
                globalUtility.parseAPIResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiSuccessfulResponse> call, Throwable t) {
                callback.onError(t);
            }
        });


    }
    public void uploadProfilePhoto(MultipartBody.Part imageFile, String access_token, ResponseCallback<ApiSuccessfulResponse> callback) {

        UploadProfileUser uploadProfileUser = api.getRetrofit().create(UploadProfileUser.class);
        uploadProfileUser.uploadPhoto(imageFile, access_token).enqueue(new Callback<ApiSuccessfulResponse>() {
            @Override
            public void onResponse(Call<ApiSuccessfulResponse> call, Response<ApiSuccessfulResponse> response) {
                //refresh token
                setRefreshTokenToDataStorage(response);
                //Handle success and error response
                globalUtility.parseAPIResponse(response,callback);
            }

            @Override
            public void onFailure(Call<ApiSuccessfulResponse> call, Throwable t) {
                callback.onError(t);
            }
        });


    }

    public void UpdateFCMToken(String newFCMToken,String  accessToken, ResponseCallback<ApiSuccessfulResponse> callback){
        UserUpdateFCMToken userUpdateFCMToken = api.getRetrofit().create(UserUpdateFCMToken.class);
        userUpdateFCMToken.updateFCMToken(newFCMToken, "Bearer "+accessToken).enqueue(new Callback<ApiSuccessfulResponse>() {
            @Override
            public void onResponse(Call<ApiSuccessfulResponse> call, Response<ApiSuccessfulResponse> response) {
                //check for refresh access token first
                setRefreshTokenToDataStorage(response);

                //then handle response
                globalUtility.parseAPIResponse(response,callback);
            }

            @Override
            public void onFailure(Call<ApiSuccessfulResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public  void updateUserNotificationSettings(UserNotificationSettingsRequest request, String accessToken, ResponseCallback<ApiSuccessfulResponse> callback){
        UserNotificationSettings updateSettings = api.getRetrofit().create(UserNotificationSettings.class);
        updateSettings.updateNotificationSettings(request,"Bearer "+accessToken).enqueue(new Callback<ApiSuccessfulResponse>() {
            @Override
            public void onResponse(Call<ApiSuccessfulResponse> call, Response<ApiSuccessfulResponse> response) {
                //get updated access token
                setRefreshTokenToDataStorage(response);
                //then handle the api response
                globalUtility.parseAPIResponse(response, callback);
            }
            @Override
            public void onFailure(Call<ApiSuccessfulResponse> call, Throwable t) {

            }
        });

    }

    private <T> void setRefreshTokenToDataStorage(Response<T> response) {
        // handle new access token
        String ACCESS_TOKEN_KEY = globalUtility.getValueInYAML(BuildConfig.ACCESS_TOKEN_KEY, context);
        Headers header = response.headers();
        Log.i("Headers",header.toString());
        String headerName = "X-New-Access-Token".toLowerCase();
        if (header.get(headerName) != null && !Objects.requireNonNull(header.get(headerName)).isEmpty()) {
            String newToken = header.get(headerName);
            dataStorageManager.putString(ACCESS_TOKEN_KEY, newToken);
        }
    }

}
