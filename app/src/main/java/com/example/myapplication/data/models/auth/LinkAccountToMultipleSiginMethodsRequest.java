package com.example.myapplication.data.models.auth;

public class LinkAccountToMultipleSiginMethodsRequest {

    String user_id;
    String email;
    String new_type;

    public LinkAccountToMultipleSiginMethodsRequest(String user_id, String email) {
        this.user_id = user_id;
        this.email = email;
        this.new_type = "google"; // Default value for type
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getNew_type() {
        return new_type;
    }

    public void setNew_type(String new_type) {
        this.new_type = new_type;
    }



}
