package com.example.myapplication.ui.activity.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.example.myapplication.security.DataStorageManager;
import com.example.myapplication.ui.activity.BaseActivity;
import com.example.myapplication.ui.activity.HomeActivity;
import com.example.myapplication.utils.GlobalUtility;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class EmailVerificationActivity extends BaseActivity {
    private WebSocket webSocket;
    private String userEmail;// pass your user's email here
    private OkHttpClient client;
    private GlobalUtility globalUtility;
    private final String TAG = "EMAIL_VERIFICATION_ACCOUNT";
    private Context context;
    private DataStorageManager dataStorageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);
        context = this;
        globalUtility = new GlobalUtility();


        dataStorageManager = DataStorageManager.getInstance(this);

        String manufacturer = android.os.Build.MANUFACTURER;
        String model = android.os.Build.MODEL;
        String androidVersion = android.os.Build.VERSION.RELEASE;
        String deviceName = manufacturer + " " + model;

        client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request requestWithHeaders = original.newBuilder()
                            .header("User-Agent", deviceName + " (Android " + androidVersion + ")")
                            .build();
                    return chain.proceed(requestWithHeaders);
                })
                .build();

        userEmail = getIntent().getStringExtra("UserEmail");
        String WEBSOCKET_URL = globalUtility.getValueInYAML(BuildConfig.API_WEBSOCKET_BASE_URL, this);

        Request request = new Request.Builder().url(WEBSOCKET_URL + "auth/verify/account/activation?email=" + userEmail + "-Android").build();
        WebSocketListener listener = new WebSocketListener() {

            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                runOnUiThread(() ->
                        Log.i("WEBSOCKET", "Websocket is closed")
                );
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                runOnUiThread(() ->
                        Log.i("WEBSOCKET", "Websocket is closing")
                );
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                runOnUiThread(() ->
                        Log.i("WEBSOCKET", "Websocket Connection failed" + t.getMessage())
                );
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                runOnUiThread(() -> {
                    try {
                        JSONObject object = new JSONObject(text);
                        boolean isActivated = object.getBoolean("is_verified");
                        String access_token = object.getString("access_token");
                        if (isActivated) {
                            // if is successful, then go to specific page
                            Intent intent = new Intent(EmailVerificationActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            //set access token
                            String accessTokenKey = globalUtility.getValueInYAML(BuildConfig.ACCESS_TOKEN_KEY, context);
                            dataStorageManager.putString(accessTokenKey, access_token);

                            startActivity(intent);

                            //Remove the extra before proceeding in email
                            getIntent().removeExtra("UserEmail");
                            //Close the websocket after successful activation
                            client.dispatcher().executorService().shutdown();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                    }

                });
            }

            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                runOnUiThread(() ->
                        Log.i("WEBSOCKET", "Websocket is open.")
                );
            }
        };
        client.newWebSocket(request, listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (client != null) {
            client.dispatcher().executorService().shutdown();
            client.connectionPool().evictAll();
        }

    }


}
