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
        fullnameBody = RequestBody.create(fullname, MediaType.parse("text/plain"));
        emailBody = RequestBody.create(email, MediaType.parse("text/plain"));
        contactNumberBody = RequestBody.create(personalInformation.getContact_number(), MediaType.parse("text/plain"));
        secondNumberBody = RequestBody.create(personalInformation.getSecond_number(), MediaType.parse("text/plain"));
        streetBody = RequestBody.create(address.getStreet(), MediaType.parse("text/plain"));
        barangayBody = RequestBody.create(address.getBarangay(), MediaType.parse("text/plain"));
        cityBody = RequestBody.create(address.getCity(), MediaType.parse("text/plain"));
        provinceBody = RequestBody.create(address.getProvince(), MediaType.parse("text/plain"));
        requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        imagePart = MultipartBody.Part.createFormData("image_file", imageFile.getName(), requestFile);
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
