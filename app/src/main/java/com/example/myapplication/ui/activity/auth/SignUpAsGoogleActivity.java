package com.example.myapplication.ui.activity.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.models.auth.SignupPostRequest;
import com.example.myapplication.data.respository.AuthenticationAPIRequestHandler;
import com.example.myapplication.data.validation.DataFieldsValidation;
import com.example.myapplication.ui.activity.BaseActivity;
import com.example.myapplication.ui.activity.home.HomeActivity;
import com.example.myapplication.utils.GlobalUtility;
import com.example.myapplication.utils.auth.BaseAuthUtility;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SignUpAsGoogleActivity extends BaseActivity {


    private TextInputEditText etEmailAddress, etContactNo, etFullName;
    private TextInputLayout tilEmailAddress, tilContactNo, tilFullName;
    private TextView tvSignIn;
    private MaterialButton btnManualSignUp;
    private AuthenticationAPIRequestHandler authenticationAPI;
    private DataFieldsValidation dataFieldsValidation;
    private Context context;
    private Activity activity;
    private GlobalUtility globalUtility;
    private ScrollView scrollView;
    private BaseAuthUtility baseAuthUtility;
    // Init first
    private String email;
    String fullName;
    private List<String> sign_in_type;
    private String status;
    private String fcmToken = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_as_google);
        initViews();

        ApiSuccessfulResponse.UserData userData = (ApiSuccessfulResponse.UserData) getIntent().getSerializableExtra("UserData");
        sign_in_type = new ArrayList<>();
        email = "";
        fullName = "";
        status = "";


        if (userData != null) {
            email = userData.getEmail();
            fullName = userData.getFullname();
            sign_in_type = userData.getSign_in_type();

            status = userData.getStatus();
            etFullName.setText(fullName);
            etEmailAddress.setText(email);
            etEmailAddress.setEnabled(false);
        }


        btnManualSignUp.setOnClickListener(v -> {
            //validate first
            String contactNo = etContactNo.getText().toString();
            String FullName = etFullName.getText().toString();
            //validate first
            boolean isFullNameEmpty = dataFieldsValidation.isEmptyField(FullName);
            boolean isFullNameValid = dataFieldsValidation.isFieldValid(FullName);
            boolean isContactNoEmpty = dataFieldsValidation.isEmptyField(contactNo);

            boolean phoneValidation = dataFieldsValidation.isValidPHMobile(contactNo);

            //validate full name first
            if (isFullNameEmpty) {
                setRequestFocusOnField(scrollView, etFullName, tilFullName, "This field must not be empty.");
                return;
            }

            if (!isFullNameValid) {
                setRequestFocusOnField(scrollView, etFullName, tilFullName, "Invalid Full name, must contains letter and space only.");
                return;
            }

            //validate contact no
            if (isContactNoEmpty) {
                setRequestFocusOnField(scrollView, etContactNo, tilContactNo, "This field must not be empty.");
                return;
            }

            //validate phone and contact no
            if (!phoneValidation) {
                setRequestFocusOnField(scrollView, etContactNo, tilContactNo, "Invalid phone number.");
                return;
            }


            //get the fcm token
            baseAuthUtility.getFCMToken().addOnSuccessListener(token -> {
                        fcmToken = token;
                        var user = new SignupPostRequest.User(email, fullName, status, contactNo, sign_in_type, fcmToken);
                        var address = new SignupPostRequest.Address(null, null, null, null);
                        SignupPostRequest request = new SignupPostRequest(user, address);
                        authenticationAPI.googleSignUp(request, new ResponseCallback<ApiSuccessfulResponse>() {
                            @Override
                            public void onSuccess(ApiSuccessfulResponse response) {
                                String accessToken = globalUtility.getValueInYAML(BuildConfig.ACCESS_TOKEN_KEY, context);
                                //store access token in data store
                                dataSharedPreference.saveData(accessToken, response.getAccess_token());
                                Intent intent = new Intent(SignUpAsGoogleActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                //Show message
                                Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onError(Throwable t) {
                                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FCM_TOKEN_ERROR", Objects.requireNonNull(e.getMessage()));
                    });
        });

        tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpAsGoogleActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        //handle text watcher listener in fields
        handleOnTextListeners();
    }

    private void handleOnTextListeners() {
        etFullName.addTextChangedListener(new TextWatcher() {
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

        etEmailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                tilEmailAddress.setError(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        etContactNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                tilContactNo.setError(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

    }
    private void setRequestFocusOnField(ScrollView scrollView, TextInputEditText editText, TextInputLayout inputLayout, String message) {
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

    private void initViews() {
        this.context = this;
        this.activity = this;

        etFullName = findViewById(R.id.etFullNameSG);
        etEmailAddress =  findViewById(R.id.etEmailSG);
        etContactNo = findViewById(R.id.etContactNumberSG);
        btnManualSignUp = findViewById(R.id.btnManualSignUpSG);
        tvSignIn = findViewById(R.id.tvSignInSG);
        tilFullName = findViewById(R.id.tilFullNameSG);
        tilEmailAddress = findViewById(R.id.tilEmailSG);
        tilContactNo = findViewById(R.id.tilContactNumberSG);
        scrollView = findViewById(R.id.signupScrollViewSG);

        //set enable false the email address
//        etEmailAddress.setEnabled(false);



        authenticationAPI = new AuthenticationAPIRequestHandler(activity, context);
        globalUtility = new GlobalUtility();
        baseAuthUtility = new BaseAuthUtility(context, activity);
        dataFieldsValidation = new DataFieldsValidation();

    }
}
