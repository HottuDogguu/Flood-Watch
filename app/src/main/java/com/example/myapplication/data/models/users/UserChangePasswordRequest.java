package com.example.myapplication.data.models.users;

public class UserChangePasswordRequest {
    String old_password;
    String new_password;

    public UserChangePasswordRequest(String old_password, String new_password) {
        this.old_password = old_password;
        this.new_password = new_password;
    }

    public String getOldPassword() {
        return old_password;
    }

    public void setOldPassword(String old_password) {
        this.old_password = old_password;
    }


    public String getNewPassword() {
        return new_password;
    }


    public void setNewPassword(String new_password) {
        this.new_password = new_password;
    }
}
