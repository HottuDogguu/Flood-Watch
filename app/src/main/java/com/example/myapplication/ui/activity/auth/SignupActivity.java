package com.example.myapplication.ui.activity.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.myapplication.R;

import com.example.myapplication.data.models.auth.SignupPostRequest.*;
import com.example.myapplication.data.respository.AuthenticationAPIRequestHandler;
import com.example.myapplication.data.validation.DataFieldsValidation;
import com.example.myapplication.security.DataSharedPreference;
import com.example.myapplication.ui.activity.BaseActivity;
import com.example.myapplication.utils.GlobalUtility;
import com.example.myapplication.utils.auth.BaseAuthUtility;
import com.example.myapplication.utils.auth.SignUpActivityUtility;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


import java.util.Objects;

public class SignupActivity extends BaseActivity {

    private Button signupButton, btnGoogleSignup;
    private Context context;
    private AuthenticationAPIRequestHandler auth;
    private GlobalUtility utility;
    private ScrollView scrollView;
    private TextView tvSignIn;
    private BaseAuthUtility baseAuthUtility;
    private DataSharedPreference dataSharedPreference;

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
    boolean isValidated = false;
    private DataFieldsValidation dataFieldsValidation;

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_password);
        initViews();// initialized variables
        dataFieldsValidation = new DataFieldsValidation();
        SignUpActivityUtility signUpActivityUtility = new SignUpActivityUtility(
                this,
                this,
                auth,
                globalUtility,
                dataSharedPreference);

        //Listeners
        signupButton.setOnClickListener(v -> {
            //initialization
            String userEmail = Objects.requireNonNull(txtEmail.getText()).toString();
            String userFullName = Objects.requireNonNull(txtFullname.getText()).toString();
            String userContactNo = Objects.requireNonNull(txtContactNo.getText()).toString();
            String userPass = Objects.requireNonNull(txtPassword.getText()).toString();
            String userConfirmPass = Objects.requireNonNull(txtPassword.getText()).toString();

            //validate first
            boolean isEmailEmpty = dataFieldsValidation.isEmptyField(userEmail);
            boolean isFullNameEmpty = dataFieldsValidation.isEmptyField(userFullName);
            boolean isContactNoEmpty = dataFieldsValidation.isEmptyField(userContactNo);
            boolean isUserPasswordEmpty = dataFieldsValidation.isEmptyField(userPass);

            String emailValidateMessage = dataFieldsValidation.validateEmail(userEmail);
            String phoneValidateMessage = dataFieldsValidation.validatePhoneNumber(userContactNo);

            String passwordValidateMessage = dataFieldsValidation.validatePassword(userPass);
            boolean isPasswordMatch = dataFieldsValidation.isPasswordMatch(userPass, userConfirmPass);
            //validation for empty fields
            if (isFullNameEmpty) {
                isValidated = false;
                setRequestFocusOnField(scrollView, txtFullname, tilFullName, "This field must not be empty!", false);
                return;
            } else {
                isValidated = true;
                setRequestFocusOnField(scrollView, txtFullname, tilFullName, "", isValidated);

            }
            if (isEmailEmpty) {
                isValidated = false;
                setRequestFocusOnField(scrollView, txtEmail, tilEmail, "This field must not be empty!", false);
             return;

            } else {
                isValidated = true;
                setRequestFocusOnField(scrollView, txtEmail, tilEmail, "", isValidated);

            }
            if (isContactNoEmpty) {
                isValidated = false;
                setRequestFocusOnField(scrollView, txtContactNo, tilContactNumber, "This field must not be empty!", false);
                return;
            } else {
                isValidated = true;
                setRequestFocusOnField(scrollView, txtContactNo, tilContactNumber, "", isValidated);

            }
            if (isUserPasswordEmpty) {
                isValidated = false;
                setRequestFocusOnField(scrollView, txtPassword, tilPassword, "This field must not be empty!", false);
                return;
            } else {
                isValidated = true;
                setRequestFocusOnField(scrollView, txtPassword, tilPassword, "", isValidated);

            }
            //Validation for email, Contact no, Second Phone Number, Password and confirm password
            if (emailValidateMessage.isEmpty()) {
                isValidated = true;
                setRequestFocusOnField(scrollView, txtEmail, tilEmail, "", isValidated);
            } else {
                isValidated = false;
                setRequestFocusOnField(scrollView, txtEmail, tilEmail, emailValidateMessage, isValidated);
            }

            if (phoneValidateMessage.isEmpty()) {
                isValidated = true;
                setRequestFocusOnField(scrollView, txtContactNo, tilContactNumber, "", isValidated);
            } else {
                isValidated = false;
                setRequestFocusOnField(scrollView, txtContactNo, tilContactNumber, phoneValidateMessage, isValidated);
            }

            if (passwordValidateMessage.isEmpty()) {
                setRequestFocusOnField(scrollView, txtPassword, tilPassword, "", true);
            } else {
                setRequestFocusOnField(scrollView, txtPassword, tilPassword, passwordValidateMessage, false);
            }
            if (isPasswordMatch) {
                setRequestFocusOnField(scrollView, txtConfirmPass, tilConfirmPassword, "", true);
            } else {
                setRequestFocusOnField(scrollView, txtConfirmPass, tilConfirmPassword, "Password not match!", false);
            }

            //call the API
            baseAuthUtility.getFCMToken().addOnSuccessListener(token -> {
                        this.buildUser().setFcm_token(token);
                        signUpActivityUtility.signUpUser(
                                this.buildUser(),
                                this.buildAddress());
                        //Set the data to be inserted in database, it will validate soon.
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FCM_TOKEN_ERROR", Objects.requireNonNull(e.getMessage()));
                    });

        });

        btnGoogleSignup.setOnClickListener(v -> {
            signUpActivityUtility.triggerGoogleButton(auth);
        });
        //When press back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                utility.showExitDialog(context);
            }
        });

        //back to signIn page
        tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

    }

    private void initViews() {
        //initialize variables
        this.context = this;
        auth = new AuthenticationAPIRequestHandler(this, context);
        utility = new GlobalUtility();
        signupButton = (Button) findViewById(R.id.btnManualSignUp);

        // initialized view components
        txtBrgy = (TextInputEditText) findViewById(R.id.etBarangay);
        txtCity = (TextInputEditText) findViewById(R.id.etCity);
//        txtProvince = (TextInputEditText) findViewById(R.id.etProvince);
        txtStreet = (TextInputEditText) findViewById(R.id.etStreet);
        txtEmail = (TextInputEditText) findViewById(R.id.etEmail);
        txtConfirmPass = (TextInputEditText) findViewById(R.id.etConfirmPassword);
        txtFullname = (TextInputEditText) findViewById(R.id.etFullName);
        txtContactNo = (TextInputEditText) findViewById(R.id.etContactNumber);
        txtPassword = (TextInputEditText) findViewById(R.id.etPassword);
        scrollView = findViewById(R.id.signupScrollView);
        tvSignIn = (TextView) findViewById(R.id.tvSignIn);
        btnGoogleSignup = (Button) findViewById(R.id.btnGoogleSignUp);
        dataSharedPreference = DataSharedPreference.getInstance(context);
        tilEmail = (TextInputLayout) findViewById(R.id.tilEmail);
        tilFullName = (TextInputLayout) findViewById(R.id.tilFullName);
        tilContactNumber = (TextInputLayout) findViewById(R.id.tilContactNumber);
        tilPassword = (TextInputLayout) findViewById(R.id.tilPassword);
        tilConfirmPassword = (TextInputLayout) findViewById(R.id.tilConfirmPassword);
        baseAuthUtility = new BaseAuthUtility(context,activity);
        txtCity.setText("Santa Cruz");

    }


    //FCM Token


    private User buildUser() {
        //user
        String email = Objects.requireNonNull(txtEmail.getText()).toString();
        String password = Objects.requireNonNull(txtPassword.getText()).toString();
        String fullname = Objects.requireNonNull(txtFullname.getText()).toString();
        String contactNo = Objects.requireNonNull(txtContactNo.getText()).toString();

        return new User(email, password, fullname,contactNo, null);
    }

    private Address buildAddress() {
        //address
        String street = Objects.requireNonNull(txtStreet.getText()).toString();
        String barangay = Objects.requireNonNull(txtBrgy.getText()).toString();
        String city = Objects.requireNonNull(txtCity.getText()).toString();
        return new Address(street, barangay,city);
    }

    private void setRequestFocusOnField(ScrollView scrollView, EditText editText, TextInputLayout inputLayout, String message, boolean isValidated) {
        //if empty, show a message
        if (!isValidated) {
            inputLayout.setError(message);
            scrollView.post(() -> {
                scrollView.smoothScrollTo((int) editText.getX(), editText.getBottom());
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
