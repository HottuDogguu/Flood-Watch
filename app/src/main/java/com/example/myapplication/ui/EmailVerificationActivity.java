package com.example.myapplication.ui;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class EmailVerificationActivity extends AppCompatActivity {
    private WebSocket webSocket;
    private String userEmail;// pass your user's email here
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        client = new OkHttpClient();
        userEmail = getIntent().getStringExtra("UserEmail");
        Toast.makeText(this, "Email" + userEmail, Toast.LENGTH_SHORT).show();
        Request request = new Request.Builder().url("ws://192.168.7.41:8000/api/v1/auth/verify/account/activation?email=" + userEmail+"-Android").build();
        WebSocketListener listener = new WebSocketListener() {

            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                runOnUiThread(() ->
                        Toast.makeText(EmailVerificationActivity.this, "Websocket is Closed ", Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                runOnUiThread(() ->
                        Toast.makeText(EmailVerificationActivity.this, "Websocket is closing: " , Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                runOnUiThread(() ->
                        Toast.makeText(EmailVerificationActivity.this,
                                "WebSocket connection failed: " + t.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                runOnUiThread(() -> {
                        Intent intent = new Intent(EmailVerificationActivity.this, DashboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                });
            }

            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                runOnUiThread(() ->
                        Toast.makeText(EmailVerificationActivity.this, "Websocket is open", Toast.LENGTH_SHORT).show()
                );
            }
        };
       client.newWebSocket(request, listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.dispatcher().executorService().shutdown();

    }



}
