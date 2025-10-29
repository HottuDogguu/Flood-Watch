package com.example.myapplication.data.respository.auth;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.CancellationSignal;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import androidx.credentials.CredentialManager;

import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;


import com.example.myapplication.data.models.auth.GoogleAuthLoginResponse;
import com.example.myapplication.data.models.auth.LinkAccountToMultipleSiginMethodsRequest;
import com.example.myapplication.data.models.auth.ManualSignUpResponse;
import com.example.myapplication.data.models.auth.SignupPostRequest;
import com.example.myapplication.data.models.auth.UploadPhotoResponse;
import com.example.myapplication.data.network.APIBuilder;
import com.example.myapplication.calbacks.ResponseCallback;
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

import java.util.concurrent.Executor;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticationAPIRequestHandler {

    private Activity activity;
    private Context context;
    private APIBuilder api;
    private GlobalUtility globalUtility;

    public AuthenticationAPIRequestHandler(Activity activity, Context context) {
        this.context = context;
        this.activity = activity;
        this.api = new APIBuilder(context);
        this.globalUtility = new GlobalUtility();

    }

    public void manualLoginResponse(ManualLoginRequest requestPost,
                                    ResponseCallback<ManualLoginResponse> callback) {
        ManualAuthenticateUser auth = api.getRetrofit().create(ManualAuthenticateUser.class);
        auth.authenticateUser(requestPost.getEmail(), requestPost.getPassword()).enqueue(new Callback<ManualLoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<ManualLoginResponse> call, @NonNull Response<ManualLoginResponse> response) {
                globalUtility.parseAPIResponse(response,callback);
            }
            @Override
            public void onFailure(@NonNull Call<ManualLoginResponse> call, @NonNull Throwable t) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private void googleGetToken(String webClientId, ResponseCallback<String> callback) {

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

    private void signIn(GetCredentialRequest request, ResponseCallback<String> callback) {
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
                                    ResponseCallback<GoogleAuthLoginResponse> callback) {
        this.googleGetToken(webClientId, new ResponseCallback<String>() {

            @Override
            public void onSuccess(String request) {
                GoogleAuthenticateUser googleAuthenticateUser = api.getRetrofit().create(GoogleAuthenticateUser.class);
                //Call the api
                googleAuthenticateUser.authenticateUser(request).enqueue(new Callback<GoogleAuthLoginResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GoogleAuthLoginResponse> call, @NonNull Response<GoogleAuthLoginResponse> response) {

                        //Handle success and error response
                        globalUtility.parseAPIResponse(response,callback);
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
                             ResponseCallback<ManualSignUpResponse> callback) {
        SignUpUser signUpUser = api.getRetrofit().create(SignUpUser.class);
        signUpUser.authenticateUser(request).enqueue(new Callback<ManualSignUpResponse>() {
            @Override
            public void onResponse(@NonNull Call<ManualSignUpResponse> call, @NonNull Response<ManualSignUpResponse> response) {
                //Handle success and error response
                globalUtility.parseAPIResponse(response,callback);
            }

            @Override
            public void onFailure(@NonNull Call<ManualSignUpResponse> call, @NonNull Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void googleSignUp(SignupPostRequest request,
                             ResponseCallback<ManualSignUpResponse> callback) {
        SignUpUser signUpUser = api.getRetrofit().create(SignUpUser.class);
        signUpUser.authenticateUser(request).enqueue(new Callback<ManualSignUpResponse>() {
            @Override
            public void onResponse(@NonNull Call<ManualSignUpResponse> call, @NonNull Response<ManualSignUpResponse> response) {
                //Handle success and error response
                globalUtility.parseAPIResponse(response,callback);
            }

            @Override
            public void onFailure(@NonNull Call<ManualSignUpResponse> call, @NonNull Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void linkUserAccountToGoogle(LinkAccountToMultipleSiginMethodsRequest request, ResponseCallback<ManualLoginResponse> callback) {
        LinkAccountToGoogleSignInMethod linkAccountToGoogleSignInMethod = api.getRetrofit().create(LinkAccountToGoogleSignInMethod.class);
        linkAccountToGoogleSignInMethod.authenticateUser(request).enqueue(new Callback<ManualLoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<ManualLoginResponse> call, @NonNull Response<ManualLoginResponse> response) {
                //Handle success and error response
                globalUtility.parseAPIResponse(response,callback);
            }

            @Override
            public void onFailure(@NonNull Call<ManualLoginResponse> call, @NonNull Throwable t) {
                callback.onError(t);
            }
        });

    }

    public void uploadProfilePhoto(MultipartBody.Part imageFile, String access_token, ResponseCallback<UploadPhotoResponse> callback) {

        UploadProfileUser uploadProfileUser = api.getRetrofit().create(UploadProfileUser.class);
        uploadProfileUser.uploadPhoto(imageFile, access_token).enqueue(new Callback<UploadPhotoResponse>() {
            @Override
            public void onResponse(Call<UploadPhotoResponse> call, Response<UploadPhotoResponse> response) {
                //Handle success and error response
                globalUtility.parseAPIResponse(response,callback);
            }

            @Override
            public void onFailure(Call<UploadPhotoResponse> call, Throwable t) {
                callback.onError(t);
            }
        });


    }
}

