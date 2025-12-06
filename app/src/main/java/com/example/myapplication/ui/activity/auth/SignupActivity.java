package com.example.myapplication.ui.activity.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
            tilStreet, tilBarangay,
            tilCity,
            tilSecondaryNumber,
            tilPassword,
            tilConfirmPassword;
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

        //handle the text watcher listeners
        handleOnTextListeners();


        //Listeners
        signupButton.setOnClickListener(v -> {
            //initialization
            String userEmail = Objects.requireNonNull(txtEmail.getText()).toString();
            String userFullName = Objects.requireNonNull(txtFullname.getText()).toString();
            String userContactNo = Objects.requireNonNull(txtContactNo.getText()).toString();
            String userPass = Objects.requireNonNull(txtPassword.getText()).toString();
            String userConfirmPass = Objects.requireNonNull(txtConfirmPass.getText()).toString();
            String street = Objects.requireNonNull(txtStreet.getText()).toString();
            String barangay = Objects.requireNonNull(txtBrgy.getText()).toString();
            String city = Objects.requireNonNull(txtCity.getText()).toString();

            //validate first
            boolean isEmailEmpty = dataFieldsValidation.isEmptyField(userEmail);
            boolean isFullNameEmpty = dataFieldsValidation.isEmptyField(userFullName);
            boolean isFullNameValid = dataFieldsValidation.isFieldValid(userFullName);
            boolean isContactNoEmpty = dataFieldsValidation.isEmptyField(userContactNo);
            boolean isUserPasswordEmpty = dataFieldsValidation.isEmptyField(userPass);
            boolean isBarangayValidated = dataFieldsValidation.isFieldValid(barangay);
            boolean isCityValidated = dataFieldsValidation.isFieldValid(city);
            boolean isStreetValidated = dataFieldsValidation.isFieldValid(street);

            boolean emailValidation = dataFieldsValidation.isValidEmail(userEmail);
            boolean phoneValidation = dataFieldsValidation.isValidPHMobile(userContactNo);

            String passwordValidateMessage = dataFieldsValidation.validatePassword(userPass);
            boolean isPasswordMatch = dataFieldsValidation.isPasswordMatch(userPass, userConfirmPass);

            //validation for empty fields

            //validate full name first
            if (isFullNameEmpty) {
                setRequestFocusOnField(scrollView, txtFullname, tilFullName, "This field must not be empty.");
                return;
            }
            if (!isFullNameValid) {
                setRequestFocusOnField(scrollView, txtFullname, tilFullName, "Invalid Full name, must contains letter and space only.");
                return;
            }
            //validate email
            if (isEmailEmpty) {
                setRequestFocusOnField(scrollView, txtEmail, tilEmail, "This field must not be empty.");
                return;
            }

            //validate email and contact no
            if (!emailValidation) {
                setRequestFocusOnField(scrollView, txtEmail, tilEmail, "Invalid inputted email address.");
                return;
            }

            //validate contact no
            if (isContactNoEmpty) {
                setRequestFocusOnField(scrollView, txtContactNo, tilContactNumber, "This field must not be empty.");
                return;
            }

            //validate phone and contact no
            if (!phoneValidation) {
                setRequestFocusOnField(scrollView, txtContactNo, tilContactNumber, "Invalid phone number.");
                return;
            }

            //validate address
            if (!isStreetValidated) {
                setRequestFocusOnField(scrollView, txtStreet, tilStreet, "Invalid Street, must contain letters and spaces.");
                return;
            }
            if (!isBarangayValidated) {
                setRequestFocusOnField(scrollView, txtBrgy, tilBarangay, "Invalid Baranagay, must contain letters and spaces.");
                return;
            }
            if (!isCityValidated) {
                setRequestFocusOnField(scrollView, txtCity, tilCity, "Invalid City, must contain letters and spaces.");
                return;
            }


            //validate password
            if (isUserPasswordEmpty) {
                setRequestFocusOnField(scrollView, txtPassword, tilPassword, "This field must not be empty.");
                return;
            }


            //validate password
            if (!passwordValidateMessage.isEmpty()) {
                setRequestFocusOnField(scrollView, txtPassword, tilPassword, passwordValidateMessage);
                return;
            }

            //validate confirm password
            if (!isPasswordMatch) {
                setRequestFocusOnField(scrollView, txtConfirmPass, tilConfirmPassword, "Confirm password must match to password.");
                return;
            }

//            //call the API
            CheckBox cbAgreement = findViewById(R.id.cbAgreeTerms);

            if (cbAgreement.isChecked()) {
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
            } else {
                Toast.makeText(context, "You must read and agree with our terms and conditions.", Toast.LENGTH_SHORT).show();
                cbAgreement.requestFocus();
            }


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


    /**
     * To back to normal the fields, once get errors..
     */
    private void handleOnTextListeners() {
        txtFullname.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                tilFullName.setError(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                tilEmail.setError(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        txtContactNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                tilContactNumber.setError(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });


        txtStreet.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                tilStreet.setError(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        txtBrgy.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                tilBarangay.setError(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        txtCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                tilCity.setError(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                tilPassword.setError(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        txtConfirmPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                tilConfirmPassword.setError(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
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

        tilStreet = findViewById(R.id.tilStreet);
        tilBarangay = findViewById(R.id.tilBarangay);
        tilCity = findViewById(R.id.tilCity);
        tilEmail = (TextInputLayout) findViewById(R.id.tilEmail);
        tilFullName = (TextInputLayout) findViewById(R.id.tilFullName);
        tilContactNumber = (TextInputLayout) findViewById(R.id.tilContactNumber);
        tilPassword = (TextInputLayout) findViewById(R.id.tilPassword);
        tilConfirmPassword = (TextInputLayout) findViewById(R.id.tilConfirmPassword);
        baseAuthUtility = new BaseAuthUtility(context, activity);
        txtCity.setText("Santa Cruz");

    }


    //FCM Token


    private User buildUser() {
        //user
        String email = Objects.requireNonNull(txtEmail.getText()).toString();
        String password = Objects.requireNonNull(txtPassword.getText()).toString();
        String fullname = Objects.requireNonNull(txtFullname.getText()).toString();
        String contactNo = Objects.requireNonNull(txtContactNo.getText()).toString();

        return new User(email, password, fullname, contactNo, null);
    }

    private Address buildAddress() {
        //address
        String street = Objects.requireNonNull(txtStreet.getText()).toString();
        String barangay = Objects.requireNonNull(txtBrgy.getText()).toString();
        String city = Objects.requireNonNull(txtCity.getText()).toString();
        return new Address(street, barangay, city);
    }

    private void setRequestFocusOnField(ScrollView scrollView, EditText editText, TextInputLayout inputLayout, String message) {
        //if empty, show a message

        inputLayout.setError(message);
        scrollView.post(() -> {
            scrollView.smoothScrollTo((int) editText.getX(), editText.getBottom());
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }
}
