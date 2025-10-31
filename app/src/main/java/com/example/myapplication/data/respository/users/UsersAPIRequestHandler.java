package com.example.myapplication.data.respository.users;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.calbacks.ResponseCallback;

import com.example.myapplication.data.models.users.UserChangePasswordRequest;
import com.example.myapplication.data.models.users.UserChangePasswordResponse;
import com.example.myapplication.data.models.users.UserLogoutResponse;
import com.example.myapplication.data.models.users.UsersGetInformationResponse;
import com.example.myapplication.data.models.users.UsersUpdateInformationRequest;
import com.example.myapplication.data.models.users.UsersUpdateInformationResponse;
import com.example.myapplication.data.network.APIBuilder;
import com.example.myapplication.data.network.endpoints.users.UserChangePassword;
import com.example.myapplication.data.network.endpoints.users.UserGetInformation;
import com.example.myapplication.data.network.endpoints.users.UserLogout;
import com.example.myapplication.data.network.endpoints.users.UserUpdateInformation;
import com.example.myapplication.security.DataStorageManager;
import com.example.myapplication.utils.GlobalUtility;

import java.io.File;
import java.util.Objects;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Header;
import retrofit2.http.Part;

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
                               ResponseCallback<UsersUpdateInformationResponse> callback) {
        try {

            String tokenHeader = "Bearer " + token;
            UserUpdateInformation updateInformation = api.getRetrofit().create(UserUpdateInformation.class);
            updateInformation.updateInfo(request.getFullnameBody(),
                            request.getImagePart(),
                            request.getEmailBody(), request.getContactNumberBody(),
                            request.getSecondNumberBody(), request.getStreetBody(),
                            request.getBarangayBody(),
                            request.getCityBody(), tokenHeader)
                    .enqueue(new Callback<UsersUpdateInformationResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<UsersUpdateInformationResponse> call, @NonNull Response<UsersUpdateInformationResponse> response) {
                            // handle new access token
                            String accessTokenKey = globalUtility.getValueInYAML(BuildConfig.ACCESS_TOKEN_KEY, context);
                            Headers header = response.headers();
                            String headerName = "X-New-Access-Token";
                            if (header.get(headerName) != null) {
                                String newToken = header.get(headerName);
                                dataStorageManager.putString(accessTokenKey, newToken);
                            }
                            globalUtility.parseAPIResponse(response, callback);
                        }

                        @Override
                        public void onFailure(@NonNull Call<UsersUpdateInformationResponse> call, Throwable t) {
                            callback.onError(t);
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void getUserInformation(String token, ResponseCallback<UsersGetInformationResponse> callback) {
        UserGetInformation userGetInformation = api.getRetrofit().create(UserGetInformation.class);
        token = "Bearer " + token;
        userGetInformation.getUser(token).enqueue(new Callback<UsersGetInformationResponse>() {
            @Override
            public void onResponse(Call<UsersGetInformationResponse> call, Response<UsersGetInformationResponse> response) {
                // handle new access token
                setRefreshTokenToDataStorage(response);
                globalUtility.parseAPIResponse(response, callback);
            }

            @Override
            public void onFailure(Call<UsersGetInformationResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void changeUserPassword(String token, UserChangePasswordRequest request,
                                   ResponseCallback<UserChangePasswordResponse> callback) {
        token = "Bearer " + token;
        UserChangePassword userChangePassword = api.getRetrofit().create(UserChangePassword.class);
        userChangePassword.changePassword(request, token).enqueue(new Callback<UserChangePasswordResponse>() {
            @Override
            public void onResponse(Call<UserChangePasswordResponse> call, Response<UserChangePasswordResponse> response) {
                // handle new access token
                setRefreshTokenToDataStorage(response);
                globalUtility.parseAPIResponse(response, callback);
            }

            @Override
            public void onFailure(Call<UserChangePasswordResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void logOutUser(String token, ResponseCallback<UserLogoutResponse> callback) {
        UserLogout logout = api.getRetrofit().create(UserLogout.class);
        String accessToken = "Bearer " + token;
        logout.logoutUser(token, accessToken).enqueue(new Callback<UserLogoutResponse>() {
            @Override
            public void onResponse(Call<UserLogoutResponse> call, Response<UserLogoutResponse> response) {
                setRefreshTokenToDataStorage(response);
                globalUtility.parseAPIResponse(response, callback);

            }

            @Override
            public void onFailure(Call<UserLogoutResponse> call, Throwable t) {
                callback.onError(t);
            }
        });


    }


    private <T> void setRefreshTokenToDataStorage(Response<T> response) {
        // handle new access token
        String ACCESS_TOKEN_KEY = globalUtility.getValueInYAML(BuildConfig.ACCESS_TOKEN_KEY, context);

        Headers header = response.headers();
        String headerName = "X-New-Access-Token".toLowerCase();
        if (header.get(headerName) != null && !Objects.requireNonNull(header.get(headerName)).isEmpty()) {
            String newToken = header.get(headerName);
            dataStorageManager.putString(ACCESS_TOKEN_KEY, newToken);
        }
    }

}
