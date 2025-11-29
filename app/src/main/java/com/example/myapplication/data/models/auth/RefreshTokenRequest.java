package com.example.myapplication.data.models.auth;

import com.google.gson.annotations.SerializedName;

public class RefreshTokenRequest {
    @SerializedName("old_access_token")
    public String old_access_token;

    public RefreshTokenRequest(String old_access_token) {
        this.old_access_token = old_access_token;
    }
}
