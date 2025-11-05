package com.example.myapplication.data.network.websockets;

import com.example.myapplication.calbacks.WebsocketCallback;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;


public class WebsocketManager {

    private OkHttpClient client;
    private WebSocket webSocket;
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final int RECONNECT_DELAY = 5000;
    private WebsocketCallback callback;
    private WebsocketConnection listener;
    private String WEBSOCKET_URL;
    public WebsocketManager(WebsocketCallback callback) {
        this.callback = callback;
        this.listener = new WebsocketConnection(callback);

    }

    public String getWEBSOCKET_URL() {
        return WEBSOCKET_URL != null ? WEBSOCKET_URL : "";
    }

    public void setWEBSOCKET_URL(String WEBSOCKET_URL) {
        this.WEBSOCKET_URL = WEBSOCKET_URL;
    }


    public void connect(String WEBSOCKET_URL) {

        this.setWEBSOCKET_URL(WEBSOCKET_URL);
        Request request = new Request.Builder().url(WEBSOCKET_URL).build();
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
        webSocket = client.newWebSocket(request, listener);
    }
    public void reconnect(boolean isConnected){
        if (!isConnected) {
            executor.schedule(() -> {
                System.out.println("Reconnecting WebSocket...");
                connect(this.getWEBSOCKET_URL());
            }, RECONNECT_DELAY, TimeUnit.MILLISECONDS);
        }
    }
}
