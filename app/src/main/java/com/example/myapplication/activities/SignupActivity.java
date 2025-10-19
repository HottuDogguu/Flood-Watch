package com.example.myapplication.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.example.myapplication.R;
import com.example.myapplication.calbacks.auth.AuthCallback;
import com.example.myapplication.data.models.auth.ManualSignUpResponse;
import com.example.myapplication.data.models.auth.SignupPostRequest;
import com.example.myapplication.data.models.auth.SignupPostRequest.*;
import com.example.myapplication.data.respository.auth.AuthenticationAPI;
import com.example.myapplication.data.validation.DataFieldsValidation;
import com.example.myapplication.utils.GlobalUtility;
import com.example.myapplication.utils.SignUpActivityUtility;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {


    private Button signupButton;
    private Context context;
    private AuthenticationAPI auth;
    private GlobalUtility utility;
    ScrollView scrollView;

    //EditText
    private TextInputEditText
            txtFullname,
            txtEmail,
            txtContactNo,
            txtSecondContactNo,
            txtStreet,
            txtBrgy,
            txtCity,
            txtProvince,
            txtPassword,
            txtConfirmPass;
    //TextInputLayout
    private TextInputLayout
            tilEmail,
            tilFullName,
            tilContactNumber,
            tilSecondaryNumber,
            tilPassword,
            tilConfirmPassword;
    private DataFieldsValidation dataFieldsValidation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        initViews();// initialized variables
        dataFieldsValidation = new DataFieldsValidation();
        SignUpActivityUtility signUpActivityUtility = new SignUpActivityUtility(this, auth);

        //Listeners
        signupButton.setOnClickListener(v -> {
            String userEmail = Objects.requireNonNull(txtEmail.getText()).toString();
            String userFullName = Objects.requireNonNull(txtFullname.getText()).toString();
            String userContactNo = Objects.requireNonNull(txtContactNo.getText()).toString();
            String userSecondContactNo = Objects.requireNonNull(txtSecondContactNo.getText()).toString();
            String userPass = Objects.requireNonNull(txtPassword.getText()).toString();
            String userConfirmPass = Objects.requireNonNull(txtPassword.getText()).toString();

            //validate first
            boolean isEmailEmpty = dataFieldsValidation.isEmptyField(userEmail);
            boolean isFullNameEmpty = dataFieldsValidation.isEmptyField(userFullName);
            boolean isContactNoEmpty = dataFieldsValidation.isEmptyField(userContactNo);
            boolean isUserPasswordEmpty = dataFieldsValidation.isEmptyField(userPass);

            String emailValidateMessage = dataFieldsValidation.validateEmail(userEmail);
            String phoneValidateMessage = dataFieldsValidation.validatePhoneNumber(userContactNo);
            String secondPhoneValidateMessage = dataFieldsValidation.validatePhoneNumber(userSecondContactNo);

            String passwordValidateMessage = dataFieldsValidation.validatePassword(userPass);
            boolean isPasswordMatch = dataFieldsValidation.isPasswordMatch(userPass, userConfirmPass);


            if (isFullNameEmpty) {
                setRequestFocusOnField(scrollView, txtFullname, tilFullName, "This field must not be empty!", false);
                return;
            } else {
                setRequestFocusOnField(scrollView, txtFullname, tilFullName, "", true);

            }
            if (isEmailEmpty) {
                setRequestFocusOnField(scrollView, txtEmail, tilEmail, "This field must not be empty!", false);
                return;
            } else {
                setRequestFocusOnField(scrollView, txtEmail, tilEmail, "", true);

            }
            if (isContactNoEmpty) {
                setRequestFocusOnField(scrollView, txtContactNo, tilContactNumber, "This field must not be empty!", false);
                return;
            } else {
                setRequestFocusOnField(scrollView, txtContactNo, tilContactNumber, "", true);

            }
            if (isUserPasswordEmpty) {
                setRequestFocusOnField(scrollView, txtPassword, tilPassword, "This field must not be empty!", false);
                return;
            } else {
                setRequestFocusOnField(scrollView, txtPassword, tilPassword, "", true);

            }


            signUpActivityUtility.signUpUser(
                    this.buildUser(),
                    this.buildAddress(),
                    this.buildPersonalInformation());
            //Set the data to be inserted in database, it will validate soon.
        });

        //When press back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                utility.showExitDialog(context);
            }
        });
    }

    private void initViews() {
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
        scrollView = findViewById(R.id.signupScrollView);

        tilEmail = (TextInputLayout) findViewById(R.id.tilEmail);
        tilFullName = (TextInputLayout) findViewById(R.id.tilFullName);
        tilContactNumber = (TextInputLayout) findViewById(R.id.tilContactNumber);
        tilSecondaryNumber = (TextInputLayout) findViewById(R.id.tilSecondaryNumber);
        tilPassword = (TextInputLayout) findViewById(R.id.tilPassword);
        tilConfirmPassword = (TextInputLayout) findViewById(R.id.tilConfirmPassword);
    }

    private User buildUser() {
        //user
        String email = Objects.requireNonNull(txtEmail.getText()).toString();
        String password = Objects.requireNonNull(txtPassword.getText()).toString();
        String fullname = Objects.requireNonNull(txtFullname.getText()).toString();

        return new User(email, password, fullname);
    }

    private Address buildAddress() {
        //address
        String street = Objects.requireNonNull(txtStreet.getText()).toString();
        String barangay = Objects.requireNonNull(txtBrgy.getText()).toString();
        String city = Objects.requireNonNull(txtCity.getText()).toString();
        String province = Objects.requireNonNull(txtProvince.getText()).toString();

        return new Address(street, barangay, city, province);
    }

    private PersonalInformation buildPersonalInformation() {
        //Personal Info
        String contactNo = Objects.requireNonNull(txtContactNo.getText()).toString();
        String secondContactNo = Objects.requireNonNull(txtSecondContactNo.getText()).toString();
        return new PersonalInformation(contactNo, secondContactNo);
    }

    public void setRequestFocusOnField(ScrollView scrollView, EditText editText, TextInputLayout inputLayout, String message, boolean isValidated) {
        //if empty, show a message
        if (!isValidated) {
            inputLayout.setError(message);
            scrollView.post(() -> {
                scrollView.smoothScrollTo((int)editText.getX(),editText.getBottom());
                editText.requestFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                }
            });

        } else {
            inputLayout.setError(null);
        }
    }
}
