package com.example.myapplication.data.network.websockets;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.calbacks.WebsocketCallback;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebsocketConnection extends WebSocketListener {
    private WebsocketCallback callback;
    private boolean isConnected ;



    public WebsocketConnection(WebsocketCallback callback){
        this.callback = callback;
    }


    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        super.onClosed(webSocket, code, reason);

        if (callback != null) callback.onDisconnected();

    }

    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        super.onOpen(webSocket, response);

        if(callback != null) callback.onConnected();

    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        super.onMessage(webSocket, text);
        if(callback != null){
            callback.onMessageReceived(text);
        }
    }

    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
        if(callback != null) callback.onFailureToConnect();
    }

    @Override
    public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        super.onClosing(webSocket, code, reason);
    }


}
