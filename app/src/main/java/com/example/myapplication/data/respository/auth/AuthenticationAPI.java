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


import com.example.myapplication.data.network.APIBuilder;
import com.example.myapplication.data.network.calbacks.auth.AuthCallback;
import com.example.myapplication.data.models.auth.LoginManualRequestPost;
import com.example.myapplication.data.models.auth.LoginManualResponse;
import com.example.myapplication.data.network.endpoints.auth.ManualAuthenticateUser;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticationAPI {

    private Activity activity;

    public AuthenticationAPI(Activity activity) {
        this.activity = activity;

    }

    public void manualLoginResponse(LoginManualRequestPost requestPost, AuthCallback callback, APIBuilder api) {
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
    public void googleLoginResponse(String webClientId) {

        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .setNonce("nonce_" + System.currentTimeMillis())
                .build();

        GetCredentialRequest getCredentialRequest = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        signIn(getCredentialRequest);
    }

    private void signIn(GetCredentialRequest request) {
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
                        new Handler(Looper.getMainLooper()).post(() -> {
                            Toast.makeText(activity, "✅ Sign in successful!", Toast.LENGTH_SHORT).show();
                            GoogleIdTokenCredential googleIdCredential =
                                    GoogleIdTokenCredential.createFrom(result.getCredential().getData());
                            // Extract the ID Token and other fields
                            String idToken = googleIdCredential.getIdToken();
                            String email = googleIdCredential.getId();
                            String displayName = googleIdCredential.getDisplayName();
                        });
                    }
                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            if (e instanceof GetCredentialCancellationException) {
                                Toast.makeText(activity, "Sign in cancelled or no credentials available.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(activity, "Sign in failed: " + e.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
                            }
                            Log.e("CredentialManager", "❌ Sign in failed: ", e);
                        });
                    }
                }
        );
    }

}