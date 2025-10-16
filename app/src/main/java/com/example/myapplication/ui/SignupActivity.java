package com.example.myapplication.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchableInfo;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.calbacks.auth.AuthCallback;
import com.example.myapplication.data.models.auth.ManualSignUpResponse;
import com.example.myapplication.data.models.auth.SignupPostRequest;
import com.example.myapplication.data.models.auth.SignupPostRequest.*;
import com.example.myapplication.data.respository.auth.AuthenticationAPI;
import com.example.myapplication.utils.GlobalUtility;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {


    private Button signupButton;
    private Context context;
    private AuthenticationAPI auth;
    private GlobalUtility utility;
    private TextInputEditText txtFullname;
    private TextInputEditText txtEmail;
    private TextInputEditText txtContactNo;
    private TextInputEditText txtSecondContactNo;
    private TextInputEditText txtStreet;
    private TextInputEditText txtBrgy;
    private TextInputEditText txtCity;
    private TextInputEditText txtProvince;
    private TextInputEditText txtPassword;
    private TextInputEditText txtConfirmPass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);


        //initialize variables
        this.context = this;

        auth = new AuthenticationAPI(this);
        utility = new GlobalUtility();
        signupButton = (Button) findViewById(R.id.btnManualSignUp);

        // initialized view components
        txtBrgy = (TextInputEditText) findViewById(R.id.etBarangay);
        txtCity = (TextInputEditText) findViewById(R.id.etCity);
        txtProvince = (TextInputEditText) findViewById(R.id.etProvince);
        txtStreet = (TextInputEditText) findViewById(R.id.etStreet);
        txtEmail = (TextInputEditText) findViewById(R.id.etEmail);
        txtConfirmPass = (TextInputEditText) findViewById(R.id.etConfirmPassword);
        txtFullname = (TextInputEditText) findViewById(R.id.etFullName);
        txtContactNo = (TextInputEditText) findViewById(R.id.etContactNumber);
        txtSecondContactNo = (TextInputEditText) findViewById(R.id.etSecondaryNumber);
        txtPassword = (TextInputEditText) findViewById(R.id.etPassword);



        //Listeners
        signupButton.setOnClickListener(v->{
            //Set the data to be inserted in database, it will validate soon.


            //user
            String email = Objects.requireNonNull(txtEmail.getText()).toString();
            String password = Objects.requireNonNull(txtPassword.getText()).toString();
            String fullname = Objects.requireNonNull(txtFullname.getText()).toString();
            var user = new User(email,password,fullname);

            //address
            String street = Objects.requireNonNull(txtStreet.getText()).toString();
            String barangay = Objects.requireNonNull(txtBrgy.getText()).toString();
            String city = Objects.requireNonNull(txtCity.getText()).toString();
            String province = Objects.requireNonNull(txtProvince.getText()).toString();
            var address = new Address(street,barangay,city,province);


            //Personal Info
            String contactNo = Objects.requireNonNull(txtContactNo.getText()).toString();
            String secondContactNo = Objects.requireNonNull(txtSecondContactNo.getText()).toString();
            var personalInfo = new PersonalInformation(contactNo, secondContactNo);


            SignupPostRequest postRequest = new SignupPostRequest(user,address,personalInfo);
            this.auth.manualSignUp(postRequest, new AuthCallback<ManualSignUpResponse>() {
                @Override
                public void onSuccess(ManualSignUpResponse response) {

                }

                @Override
                public void onError(Throwable t) {

                }
            });
        });



    }
}
