package com.example.myapplication.data.models.auth;

public class GoogleAuthResponse {
    String status, message, action;
    TokenData token;
    UserData data;

    public GoogleAuthResponse(String status, String message, String action, TokenData token, UserData data) {
        this.status = status;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public class TokenData {
        String access_token;

        public String getRefresh_access_token() {
            return refresh_access_token;
        }

        public void setRefresh_access_token(String refresh_access_token) {
            this.refresh_access_token = refresh_access_token;
        }

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

        String refresh_access_token;
        String access_type;
    }

    public class UserData {
        Boolean is_profile_completed;
        String username, status;
        int profile_setup_steps;

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

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getProfile_setup_steps() {
            return profile_setup_steps;
        }

        public void setProfile_setup_steps(int profile_setup_steps) {
            this.profile_setup_steps = profile_setup_steps;
        }
    }
}
