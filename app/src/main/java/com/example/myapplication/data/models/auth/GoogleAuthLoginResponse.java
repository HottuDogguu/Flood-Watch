package com.example.myapplication.data.models.auth;

import java.io.Serializable;
import java.util.List;

public class GoogleAuthLoginResponse implements Serializable {
    String status, action;
    TokenData token;
    UserData data;

    public GoogleAuthLoginResponse(String status, String action, TokenData token, UserData data) {
        this.status = status;

        this.action = action;
        this.token = token;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public TokenData getToken() {
        return token;
    }

    public void setToken(TokenData token) {
        this.token = token;
    }

    public UserData getData() {
        return data;
    }

    public void setData(UserData data) {
        this.data = data;
    }

    public class TokenData implements Serializable{
        String access_token, access_type;

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String getAccess_type() {
            return access_type;
        }

        public void setAccess_type(String access_type) {
            this.access_type = access_type;
        }
    }

    public class UserData implements Serializable{
        Boolean is_profile_completed;
        String fullname, status,role,email,id;
        int profile_setup_steps;
        List<String> sign_in_type;

        public Boolean getIs_profile_completed() {
            return is_profile_completed;
        }

        public void setIs_profile_completed(Boolean is_profile_completed) {
            this.is_profile_completed = is_profile_completed;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getFullname() {
            return fullname;
        }

        public void setFullname(String username) {
            this.fullname = username;
        }

        public int getProfile_setup_steps() {
            return profile_setup_steps;
        }

        public void setProfile_setup_steps(int profile_setup_steps) {
            this.profile_setup_steps = profile_setup_steps;
        }
        public List<String> getSign_in_type() {
            return sign_in_type;
        }

        public void setSign_in_type(List<String> sign_in_type) {
            this.sign_in_type = sign_in_type;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
