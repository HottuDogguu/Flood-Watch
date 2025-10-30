package com.example.myapplication.data.models.users;

import android.net.Uri;

import com.example.myapplication.data.models.auth.SignupPostRequest;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UsersUpdateInformationRequest {

    RequestBody requestFile;
    MultipartBody.Part imagePart;
    RequestBody fullnameBody;
    RequestBody emailBody;
    RequestBody contactNumberBody;
    RequestBody secondNumberBody;
    RequestBody streetBody;
    RequestBody barangayBody;
    RequestBody cityBody;
    RequestBody provinceBody;

    public UsersUpdateInformationRequest(
            File imageFile,
            String fullname,
            String email,
            SignupPostRequest.PersonalInformation personalInformation,
            SignupPostRequest.Address address
    ) {
        fullnameBody = RequestBody.create(MediaType.parse("text/plain"), fullname != null ? fullname : "");
        emailBody = RequestBody.create(MediaType.parse("text/plain"), email != null ? email : "");
        contactNumberBody = RequestBody.create(MediaType.parse("text/plain"),
                personalInformation.getContact_number() != null ? personalInformation.getContact_number() : "");
        secondNumberBody = RequestBody.create(MediaType.parse("text/plain"),
                personalInformation.getSecond_number() != null ? personalInformation.getSecond_number() : "");
        streetBody = RequestBody.create(MediaType.parse("text/plain"),
                address.getStreet() != null ? address.getStreet() : "");
        barangayBody = RequestBody.create(MediaType.parse("text/plain"),
                address.getBarangay() != null ? address.getBarangay() : "");
        cityBody = RequestBody.create(MediaType.parse("text/plain"),
                address.getCity() != null ? address.getCity() : "");
        provinceBody = RequestBody.create(MediaType.parse("text/plain"),
                address.getProvince() != null ? address.getProvince() : "");

        // ✅ Handle nullable image safely
        if (imageFile != null) {
            requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
            imagePart = MultipartBody.Part.createFormData("img_file", imageFile.getName(), requestFile);
        } else {
            requestFile = null;
            imagePart = null;
        }
    }

    public RequestBody getFullnameBody() {
        return fullnameBody;
    }

    public void setFullnameBody(RequestBody fullnameBody) {
        this.fullnameBody = fullnameBody;
    }

    public RequestBody getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(RequestBody emailBody) {
        this.emailBody = emailBody;
    }

    public RequestBody getContactNumberBody() {
        return contactNumberBody;
    }

    public void setContactNumberBody(RequestBody contactNumberBody) {
        this.contactNumberBody = contactNumberBody;
    }

    public RequestBody getSecondNumberBody() {
        return secondNumberBody;
    }

    public void setSecondNumberBody(RequestBody secondNumberBody) {
        this.secondNumberBody = secondNumberBody;
    }

    public RequestBody getStreetBody() {
        return streetBody;
    }

    public void setStreetBody(RequestBody streetBody) {
        this.streetBody = streetBody;
    }

    public RequestBody getBarangayBody() {
        return barangayBody;
    }

    public void setBarangayBody(RequestBody barangayBody) {
        this.barangayBody = barangayBody;
    }

    public RequestBody getCityBody() {
        return cityBody;
    }

    public void setCityBody(RequestBody cityBody) {
        this.cityBody = cityBody;
    }

    public RequestBody getProvinceBody() {
        return provinceBody;
    }

    public void setProvinceBody(RequestBody provinceBody) {
        this.provinceBody = provinceBody;
    }

    public RequestBody getRequestFile() {
        return requestFile;
    }

    public void setRequestFile(RequestBody requestFile) {
        this.requestFile = requestFile;
    }

    public MultipartBody.Part getImagePart() {
        return imagePart;
    }

    public void setImagePart(MultipartBody.Part imagePart) {
        this.imagePart = imagePart;
    }
}
