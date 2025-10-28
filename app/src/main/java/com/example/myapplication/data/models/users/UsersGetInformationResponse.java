package com.example.myapplication.data.models.users;

public class UsersGetInformationResponse {
    String status, message;
    UserData data;


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

    public UserData getData() {
        return data;
    }

    public void setData(UserData data) {
        this.data = data;
    }


    public static class UserData {

        String id, fullname, email, status, role;
        int profile_setup_steps;
        boolean is_profile_completed;
        UserAddress Address;
        UserPersonalInformation PersonalInformation;
        UserProfileImage ProfileImage;

        public UserAddress getAddress() {
            return Address;
        }

        public void setAddress(UserAddress address) {
            Address = address;
        }

        public UserPersonalInformation getPersonalInformation() {
            return PersonalInformation;
        }

        public void setPersonalInformation(UserPersonalInformation personalInformation) {
            PersonalInformation = personalInformation;
        }

        public UserProfileImage getProfileImage() {
            return ProfileImage;
        }

        public void setProfileImage(UserProfileImage profileImage) {
            ProfileImage = profileImage;
        }


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFullname() {
            return fullname;
        }

        public void setFullname(String fullname) {
            this.fullname = fullname;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getRole() {
            return role;
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

    }


    public static class UserAddress {
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

    public static class UserPersonalInformation {
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

    public static class UserProfileImage {
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
