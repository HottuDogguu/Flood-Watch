package com.example.myapplication.data.respository.auth;

import android.app.Activity;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import androidx.credentials.CredentialManager;

import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialCancellationException;
import androidx.credentials.exceptions.GetCredentialException;


import com.example.myapplication.data.models.auth.GoogleAuthResponse;
import com.example.myapplication.data.models.auth.GoogleTokenRequestPost;
import com.example.myapplication.data.network.APIBuilder;
import com.example.myapplication.calbacks.auth.AuthCallback;
import com.example.myapplication.data.models.auth.LoginManualRequestPost;
import com.example.myapplication.data.models.auth.LoginManualResponse;
import com.example.myapplication.data.network.endpoints.auth.GoogleAuthenticateUser;
import com.example.myapplication.data.network.endpoints.auth.ManualAuthenticateUser;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticationAPI {

    private Activity activity;
    private APIBuilder api;

    public AuthenticationAPI(Activity activity) {
        this.activity = activity;
        this.api = new APIBuilder();

    }

    public void manualLoginResponse(LoginManualRequestPost requestPost,
                                    AuthCallback<LoginManualResponse> callback) {
        ManualAuthenticateUser auth = api.getRetrofit().create(ManualAuthenticateUser.class);
        auth.authenticateUser(requestPost.getEmail(), requestPost.getPassword()).enqueue(new Callback<LoginManualResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginManualResponse> call, @NonNull Response<LoginManualResponse> response) {

                // Check if no error
                if (response.isSuccessful() && response.body() != null) {
                    //then set data on callback on success
                    callback.onSuccess(response.body());
                } else {
                    //otherwise set error on callback
                    callback.onError(new Exception("Login failed: " + response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginManualResponse> call, @NonNull Throwable t) {
                callback.onError(t);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private void googleGetToken(String webClientId, AuthCallback<GoogleTokenRequestPost> callback) {

        //Setup the google auth
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .setNonce("nonce_" + System.currentTimeMillis())
                .build();

        //Get the credentials of the user
        GetCredentialRequest getCredentialRequest = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        //Call the signIn function below to get the credentials of the user after signing
        signIn(getCredentialRequest, callback);
    }

    private void signIn(GetCredentialRequest request, AuthCallback<GoogleTokenRequestPost> callback) {
        CredentialManager credentialManager = CredentialManager.create(activity);
        CancellationSignal cancellationSignal = new CancellationSignal();
        Executor executor = activity.getMainExecutor();

        credentialManager.getCredentialAsync(
                activity,
                request,
                cancellationSignal,
                executor,
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(@NonNull GetCredentialResponse result) {

                        GoogleIdTokenCredential googleIdCredential =
                                GoogleIdTokenCredential.createFrom(result.getCredential().getData());
                        String token = googleIdCredential.getIdToken();
                        callback.onSuccess(new GoogleTokenRequestPost(token));

                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {

                    }
                }
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public void googleLoginResponse(String webClientId,
                                    AuthCallback<GoogleAuthResponse> callback) {
        this.googleGetToken(webClientId, new AuthCallback<GoogleTokenRequestPost>() {
            @Override
            public void onSuccess(GoogleTokenRequestPost response) {
                GoogleAuthenticateUser googleAuthenticateUser = api.getRetrofit().create(GoogleAuthenticateUser.class);
                //Call the api
                googleAuthenticateUser.authenticateUser(response).enqueue(new Callback<GoogleAuthResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GoogleAuthResponse> call, @NonNull Response<GoogleAuthResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            //then set data on callback on success
                            callback.onSuccess(response.body());
                        } else {
                            //otherwise set error on callback
                            try {
                                JSONObject errorMessage = new JSONObject(response.errorBody().string());
                                JSONObject detailObj = errorMessage.getJSONObject("detail");
                                String message = detailObj.getString("message");
                                callback.onError(new Exception(message+ " :"+ response.code()));
                            } catch (IOException | JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GoogleAuthResponse> call, @NonNull Throwable t) {
                        callback.onError(t);
                    }
                });
            }

            @Override
            public void onError(Throwable t) {
                // when Error occurs
            }
        });

    }


}