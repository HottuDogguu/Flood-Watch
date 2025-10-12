package com.example.myapplication.data.models.auth;

public class LoginManualResponse {
    TokenData token;

    public TokenData getToken() {
        return token;
    }

    public void setToken(TokenData token) {
        this.token = token;
    }

    public class TokenData{
        String access_token, access_type,refresh_access_token;

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

        public String getRefresh_access_token() {
            return refresh_access_token;
        }

        public void setRefresh_access_token(String refresh_access_token) {
            this.refresh_access_token = refresh_access_token;
        }
    }


}
