package com.example.myapplication.data.models.api_response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApiSuccessfulResponse implements Serializable {
    String message, status_message, access_token, action,id;
    int status_code;
    UserData data;

    public String getMessage() {
        return (message == null || message.isEmpty()) ? "" : message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus_message() {
        return (status_message == null || status_message.isEmpty()) ? "" : status_message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }

    public String getAccess_token() {
        return (access_token == null || access_token.isEmpty()) ? "" : access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getAction() {
        return (action == null || action.isEmpty()) ? "" : action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public UserData getData() {
        return data != null ? data : new UserData();
    }

    public void setData(UserData data) {
        this.data = data;
    }

    public static class UserData implements Serializable {
        UserAddress Address;
        double water_level;
        int precipitation_probability;
        UserPersonalInformation PersonalInformation;
        UserProfileImage ProfileImage;

        String id, fullname, email, status, role,fcm_token,alert_level;

        int profile_setup_steps;
        List<String>sign_in_type;

        public int getPrecipitation_probability() {
            return precipitation_probability;
        }

        public void setPrecipitation_probability(int precipitation_probability) {
            this.precipitation_probability = precipitation_probability;
        }

        boolean is_profile_completed, is_deleted,is_flood_alert_on,
                is_emergency_alert_on,
                is_weather_updates_on;

        public double getWater_level() {
            return water_level;
        }

        public void setWater_level(double water_level) {
            this.water_level = water_level;
        }

        public String getAlert_level() {
            return alert_level != null ? alert_level : "";
        }

        public void setAlert_level(String alert_level) {
            this.alert_level = alert_level;
        }

        public boolean isIs_flood_alert_on() {
            return is_flood_alert_on;
        }

        public void setIs_flood_alert_on(boolean is_flood_alert_on) {
            this.is_flood_alert_on = is_flood_alert_on;
        }

        public boolean isIs_emergency_alert_on() {
            return is_emergency_alert_on;
        }

        public void setIs_emergency_alert_on(boolean is_emergency_alert_on) {
            this.is_emergency_alert_on = is_emergency_alert_on;
        }

        public boolean isIs_weather_updates_on() {
            return is_weather_updates_on;
        }

        public void setIs_weather_updates_on(boolean is_weather_updates_on) {
            this.is_weather_updates_on = is_weather_updates_on;
        }

        public String getFcm_token() {
            return fcm_token;
        }

        public void setFcm_token(String fcm_token) {
            this.fcm_token = fcm_token;
        }

        public List<String> getSign_in_type() {
            return sign_in_type != null ? sign_in_type : new ArrayList<String>();
        }

        public void setSign_in_type(List<String> sign_in_type) {
            this.sign_in_type = sign_in_type;
        }

        public String getId() {
            return (id == null || id.isEmpty()) ? "" : id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFullname() {
            return (fullname == null || fullname.isEmpty()) ? "" : fullname;
        }

        public boolean isIs_deleted() {
            return is_deleted;
        }

        public void setIs_deleted(boolean is_deleted) {
            this.is_deleted = is_deleted;
        }

        public void setFullname(String fullname) {
            this.fullname = fullname;
        }

        public String getEmail() {
            return (email == null || email.isEmpty()) ? "" : email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getStatus() {
            return (status == null || status.isEmpty()) ? "" : status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getRole() {
            return (role == null || role.isEmpty()) ? "" : role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public int getProfile_setup_steps() {
            return profile_setup_steps;
        }

        public void setProfile_setup_steps(int profile_setup_steps) {
            this.profile_setup_steps = profile_setup_steps;
        }

        public boolean isIs_profile_completed() {
            return is_profile_completed;
        }

        public void setIs_profile_completed(boolean is_profile_completed) {
            this.is_profile_completed = is_profile_completed;
        }

        public UserAddress getAddress() {
            return Address != null ? Address : new UserAddress();
        }
        public void setAddress(UserAddress address) {
            Address = address;
        }

        public UserPersonalInformation getPersonalInformation() {
            return PersonalInformation != null ? PersonalInformation : new UserPersonalInformation();
        }

        public void setPersonalInformation(UserPersonalInformation personalInformation) {
            PersonalInformation = personalInformation;
        }

        public UserProfileImage getProfileImage() {
            return ProfileImage != null ? ProfileImage : new UserProfileImage();
        }

        public void setProfileImage(UserProfileImage profileImage) {
            ProfileImage = profileImage;
        }



    }
    public static class UserAddress implements Serializable {
        public String getCity() {
            if (this.city == null || this.city.isEmpty()) return "";
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getBarangay() {
            if (this.barangay == null || this.barangay.isEmpty()) return "";
            return barangay;
        }

        public void setBarangay(String barangay) {
            this.barangay = barangay;
        }

        public String getUser_id() {

            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getStreet() {
            if (this.street == null || this.street.isEmpty()) return "";
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getProvince() {
            if (this.province == null || this.province.isEmpty()) return "";
            return province;
        }

        public void setProvince(String province) {

            this.province = province;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        String city, barangay, user_id, street, province;
        int id;
    }

    public static class UserPersonalInformation implements Serializable {
        public String getSecond_number() {
            if (this.second_number == null || this.second_number.isEmpty()) return "";
            return second_number;
        }

        public void setSecond_number(String second_number) {
            this.second_number = second_number;
        }

        public String getContact_number() {
            if (this.contact_number == null || this.contact_number.isEmpty()) return "";
            return contact_number;
        }

        public void setContact_number(String contact_number) {
            this.contact_number = contact_number;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        String second_number, contact_number, user_id;
        int id;
    }

    public static class UserProfileImage implements Serializable {
        int id;
        String public_key, img_url, user_id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPublic_key() {
            if (this.public_key == null || this.public_key.isEmpty()) return "";
            return public_key;
        }

        public void setPublic_key(String public_key) {
            this.public_key = public_key;
        }

        public String getImg_url() {
            if (this.img_url == null || this.img_url.isEmpty()) return "";
            return img_url;
        }

        public void setImg_url(String img_url) {
            this.img_url = img_url;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }
    }
}

