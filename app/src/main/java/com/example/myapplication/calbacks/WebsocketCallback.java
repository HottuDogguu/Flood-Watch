package com.example.myapplication.calbacks;

public interface WebsocketCallback {
    void onMessageReceived(String message);
    void onConnected();
    void onDisconnected();
    void onFailureToConnect();
}
