package com.example.myapplication.data.respository.auth;

import android.app.Activity;
import android.os.Build;
import android.os.CancellationSignal;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import androidx.credentials.CredentialManager;

import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;


import com.example.myapplication.calbacks.auth.APIResponseHandler;
import com.example.myapplication.data.models.auth.GoogleAuthLoginResponse;
import com.example.myapplication.data.models.auth.GoogleTokenRequest;
import com.example.myapplication.data.models.auth.LinkAccountToMultipleSiginMethodsRequest;
import com.example.myapplication.data.models.auth.ManualSignUpResponse;
import com.example.myapplication.data.models.auth.SignupPostRequest;
import com.example.myapplication.data.models.auth.UploadPhotoResponse;
import com.example.myapplication.data.models.errors.ApiErrorResponse;
import com.example.myapplication.data.network.APIBuilder;
import com.example.myapplication.calbacks.auth.AuthCallback;
import com.example.myapplication.data.models.auth.ManualLoginRequest;
import com.example.myapplication.data.models.auth.ManualLoginResponse;
import com.example.myapplication.data.network.endpoints.auth.GoogleAuthenticateUser;
import com.example.myapplication.data.network.endpoints.auth.LinkAccountToGoogleSignInMethod;
import com.example.myapplication.data.network.endpoints.auth.ManualAuthenticateUser;

import com.example.myapplication.data.network.endpoints.auth.SignUpUser;
import com.example.myapplication.data.network.endpoints.auth.UploadProfileUser;
import com.example.myapplication.utils.GlobalUtility;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.concurrent.Executor;

import kotlin.jvm.Throws;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AuthenticationAPI {

    private Activity activity;
    private APIBuilder api;
    private GlobalUtility globalUtility;

    public AuthenticationAPI(Activity activity) {
        this.activity = activity;
        this.api = new APIBuilder();
        this.globalUtility = new GlobalUtility();

    }

    public void manualLoginResponse(ManualLoginRequest requestPost,
                                    AuthCallback<ManualLoginResponse> callback) {
        ManualAuthenticateUser auth = api.getRetrofit().create(ManualAuthenticateUser.class);
        auth.authenticateUser(requestPost.getEmail(), requestPost.getPassword()).enqueue(new Callback<ManualLoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<ManualLoginResponse> call, @NonNull Response<ManualLoginResponse> response) {
                globalUtility.parseError(response,callback);
            }
            @Override
            public void onFailure(@NonNull Call<ManualLoginResponse> call, @NonNull Throwable t) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private void googleGetToken(String webClientId, AuthCallback<String> callback) {

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

    private void signIn(GetCredentialRequest request, AuthCallback<String> callback) {
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
                        callback.onSuccess(token);
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {

                    }
                }
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public void googleLoginResponse(String webClientId,
                                    AuthCallback<GoogleAuthLoginResponse> callback) {
        this.googleGetToken(webClientId, new AuthCallback<String>() {


            @Override
            public void onSuccess(String request) {

                GoogleAuthenticateUser googleAuthenticateUser = api.getRetrofit().create(GoogleAuthenticateUser.class);
                //Call the api
                googleAuthenticateUser.authenticateUser(request).enqueue(new Callback<GoogleAuthLoginResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GoogleAuthLoginResponse> call, @NonNull Response<GoogleAuthLoginResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            //then set data on callback on success
                            callback.onSuccess(response.body());
                        } else {
                            //otherwise set error on callback
                            try {
                                JSONObject errorMessage = new JSONObject(response.errorBody().toString()
                                );
                                JSONObject detailObj = errorMessage.getJSONObject("detail");
                                String message = detailObj.getString("message");
                                callback.onError(new Exception(message + ": " + response.code()));
                            } catch (JSONException e) {
                                e.fillInStackTrace();
                                Log.i("Debug", Objects.requireNonNull(e.getMessage()));
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GoogleAuthLoginResponse> call, @NonNull Throwable t) {
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

    public void manualSignUp(SignupPostRequest request,
                             AuthCallback<ManualSignUpResponse> callback) {
        SignUpUser signUpUser = api.getRetrofit().create(SignUpUser.class);
        signUpUser.authenticateUser(request).enqueue(new Callback<ManualSignUpResponse>() {
            @Override
            public void onResponse(@NonNull Call<ManualSignUpResponse> call, @NonNull Response<ManualSignUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {

                    callback.onError(new Exception("Sign Up failed: " + response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ManualSignUpResponse> call, @NonNull Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void googleSignUp(SignupPostRequest request,
                             AuthCallback<ManualSignUpResponse> callback) {
        SignUpUser signUpUser = api.getRetrofit().create(SignUpUser.class);
        signUpUser.authenticateUser(request).enqueue(new Callback<ManualSignUpResponse>() {
            @Override
            public void onResponse(@NonNull Call<ManualSignUpResponse> call, @NonNull Response<ManualSignUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    if (response.errorBody() != null) {
                        String errorBody = response.errorBody().toString();
                        Log.e("API_RESPONSE", "Error body: " + errorBody);
                        callback.onError(new Exception("Unknown error with code: " + response.code()));
                        // Parse the error response using Gson
                        try {
                            Gson gson = new Gson();
                            ApiErrorResponse errorResponse = gson.fromJson(errorBody, ApiErrorResponse.class);
                            callback.onError(new Exception("Error :" + errorResponse.getMessage() + " with status code of " + errorResponse.getStatus_code()));

                        } catch (JsonSyntaxException e) {
                            // If parsing fails, fall back to raw error
                            Log.e("API_RESPONSE", "Failed to parse error response", e);
                            callback.onError(new Exception(errorBody));
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ManualSignUpResponse> call, @NonNull Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void linkUserAccountToGoogle(LinkAccountToMultipleSiginMethodsRequest request, AuthCallback<ManualLoginResponse> callback) {
        LinkAccountToGoogleSignInMethod linkAccountToGoogleSignInMethod = api.getRetrofit().create(LinkAccountToGoogleSignInMethod.class);
        linkAccountToGoogleSignInMethod.authenticateUser(request).enqueue(new Callback<ManualLoginResponse>() {
            @Override
            public void onResponse(Call<ManualLoginResponse> call, Response<ManualLoginResponse> response) {
                //Handle success and error response
                globalUtility.parseError(response,callback);
            }

            @Override
            public void onFailure(Call<ManualLoginResponse> call, Throwable t) {
                callback.onError(t);
            }
        });

    }

    public void uploadProfilePhoto(MultipartBody.Part imageFile, String access_token, AuthCallback<UploadPhotoResponse> callback) {
        UploadProfileUser uploadProfileUser = api.getRetrofit().create(UploadProfileUser.class);
        uploadProfileUser.uploadPhoto(imageFile, access_token).enqueue(new Callback<UploadPhotoResponse>() {
            @Override
            public void onResponse(Call<UploadPhotoResponse> call, Response<UploadPhotoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    if (response.errorBody() != null) {
                        String errorBody = response.errorBody().toString();
                        Log.e("API_RESPONSE", "Error body: " + errorBody);
                        callback.onError(new Exception("Unknown error with code: " + response.code()));
                        // Parse the error response using Gson
                        try {
                            Gson gson = new Gson();
                            ApiErrorResponse errorResponse = gson.fromJson(errorBody, ApiErrorResponse.class);
                            callback.onError(new Exception("Error :" + errorResponse.getMessage() + " with status code of " + errorResponse.getStatus_code()));

                        } catch (JsonSyntaxException e) {
                            // If parsing fails, fall back to raw error
                            Log.e("API_RESPONSE", "Failed to parse error response", e);
                            callback.onError(new Exception(errorBody));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UploadPhotoResponse> call, Throwable t) {
                callback.onError(t);
            }
        });


    }
}

