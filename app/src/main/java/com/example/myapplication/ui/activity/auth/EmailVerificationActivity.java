package com.example.myapplication.ui.activity.auth;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.example.myapplication.ui.activity.BaseActivity;
import com.example.myapplication.ui.activity.DashboardActivity;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class EmailVerificationActivity extends BaseActivity {
    private WebSocket webSocket;
    private String userEmail;// pass your user's email here
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

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
        Toast.makeText(this, "Email" + userEmail, Toast.LENGTH_SHORT).show();
        Request request = new Request.Builder().url(BuildConfig.API_WEBSOCKET_BASE_URL +"auth/verify/account/activation?email=" + userEmail + "-Android").build();
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
                        if (isActivated) {
                            // if is successful, then go to specific page
                            Intent intent = new Intent(EmailVerificationActivity.this, DashboardActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            //Remove the extra before proceeding in email
                            getIntent().removeExtra("UserEmail");
                            finish();
                            //Close the websocket after successful activation
                            client.dispatcher().executorService().shutdown();
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
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
