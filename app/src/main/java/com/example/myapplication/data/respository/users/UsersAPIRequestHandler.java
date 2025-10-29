package com.example.myapplication.data.respository.users;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.calbacks.ResponseCallback;

import com.example.myapplication.data.models.users.UsersGetInformationResponse;
import com.example.myapplication.data.models.users.UsersUpdateInformationRequest;
import com.example.myapplication.data.models.users.UsersUpdateInformationResponse;
import com.example.myapplication.data.network.APIBuilder;
import com.example.myapplication.data.network.endpoints.users.UserGetInformation;
import com.example.myapplication.data.network.endpoints.users.UserUpdateInformation;
import com.example.myapplication.security.DataStorageManager;
import com.example.myapplication.utils.GlobalUtility;

import java.io.File;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Header;

public class UsersAPIRequestHandler {

    private Activity activity;
    private APIBuilder api;
    private GlobalUtility globalUtility;
    private DataStorageManager dataStorageManager;
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
        userGetInformation.getUser(token).enqueue(new Callback<UsersGetInformationResponse>() {
            @Override
            public void onResponse(Call<UsersGetInformationResponse> call, Response<UsersGetInformationResponse> response) {
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
            public void onFailure(Call<UsersGetInformationResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

}
