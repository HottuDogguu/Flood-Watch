package com.example.myapplication.data.models.auth;

public class ManualSignUpResponse {
    String status, message;


    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class TokenData{
        String access_token, access_type ,status;
    }

}
