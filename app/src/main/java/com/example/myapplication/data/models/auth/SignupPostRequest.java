package com.example.myapplication.data.models.auth;

import java.util.List;

public class SignupPostRequest {
    User user_;
    Address address_;
    PersonalInformation personal_information_;

    public Address getAddress_() {
        return address_;
    }

    public User getUser_() {
        return user_;
    }

    public void setAddress_(Address address_) {
        this.address_ = address_;
    }

    public void setPersonal_information_(PersonalInformation personal_information_) {
        this.personal_information_ = personal_information_;
    }

    public void setUser_(User user_) {
        this.user_ = user_;
    }

    public PersonalInformation getPersonal_information_() {
        return personal_information_;
    }

    public SignupPostRequest(User user_, Address address_, PersonalInformation personal_information_) {
        this.user_ = user_;
        this.address_ = address_;
        this.personal_information_ = personal_information_;
    }

    public static class User {
        String email;
        String status;
        List<String> sign_in_type;

        public User(String email, String fullname,String status, List<String> sign_in_type) {
            this.email = email;
            this.status = status;
            this.fullname = fullname;
            this.sign_in_type =sign_in_type;


        }
        public User(String email, String password, String fullname) {
            this.email = email;
            this.password = password;

            this.fullname = fullname;

            if (this.status == null) {
                this.status = "pending";
            }
        }

        String password;
        String fullname;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFullname() {
            return fullname;
        }

        public void setFullname(String fullname) {
            this.fullname = fullname;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class Address {
        String street;

        public Address(String street, String barangay, String city, String province) {
            this.street = street;
            this.barangay = barangay;
            this.city = city;
            this.province = province;
        }

        String barangay;
        String city;
        String province;

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getBarangay() {
            return barangay;
        }

        public String getCity() {
            return city;
        }

        public String getProvince() {
            return province;
        }

        public void setBarangay(String barangay) {
            this.barangay = barangay;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public void setProvince(String province) {
            this.province = province;
        }

    }

    public static class PersonalInformation {
        public PersonalInformation(String contact_number, String second_number) {
            this.contact_number = contact_number;
            this.second_number = second_number;
        }

        String contact_number, second_number;

        public String getContact_number() {
            return contact_number;
        }

        public String getSecond_number() {
            return second_number;
        }

        public void setContact_number(String contact_number) {
            this.contact_number = contact_number;
        }

        public void setSecond_number(String second_number) {
            this.second_number = second_number;
        }

    }
}
