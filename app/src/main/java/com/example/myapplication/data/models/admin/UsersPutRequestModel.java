package com.example.myapplication.data.models.admin;

import com.example.myapplication.data.models.auth.SignupPostRequest;

public class UsersPutRequestModel {
    String full_name, contact_no;
    UserAddress address;

    public UsersPutRequestModel(String full_name, String contact_no, UserAddress address) {
        this.full_name = full_name;
        this.contact_no = contact_no;
        this.address = address;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public UserAddress getAddress() {
        return address;
    }

    public void setAddress(UserAddress address) {
        this.address = address;
    }

    public static class UserAddress{
        public UserAddress(String street, String barangay, String city, String province) {
            this.street = street;
            this.barangay = barangay;
            this.city = city;
            this.province = province;
        }

        String street,barangay, city,province;

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getBarangay() {
            return barangay;
        }

        public void setBarangay(String barangay) {
            this.barangay = barangay;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }
    }

}
