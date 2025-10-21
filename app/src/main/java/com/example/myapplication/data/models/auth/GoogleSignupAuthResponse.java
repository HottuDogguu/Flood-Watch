package com.example.myapplication.data.models.auth;

import java.util.List;

public class GoogleSignupAuthResponse {
    String status, action;

    UserData data;

    public GoogleSignupAuthResponse(String status, String action, UserData data) {
        this.status = status;

        this.action = action;
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


    public UserData getData() {
        return data;
    }

    public void setData(UserData data) {
        this.data = data;
    }

    public class UserData {
        String id;
        Boolean is_profile_completed;
        String fullname;
        String status;
        int profile_setup_steps;
        String role;
        List<String> sign_in_type;
        String created_at;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getFullname() {
            return fullname;
        }

        public void setFullname(String fullname) {
            this.fullname = fullname;
        }

        public Boolean getIs_profile_completed() {
            return is_profile_completed;
        }

        public List<String> getSign_in_type() {
            return sign_in_type;
        }

        public void setSign_in_type(List<String> sign_in_type) {
            this.sign_in_type = sign_in_type;
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
        public int getProfile_setup_steps() {
            return profile_setup_steps;
        }
        public void setProfile_setup_steps(int profile_setup_steps) {
            this.profile_setup_steps = profile_setup_steps;
        }
    }
}
