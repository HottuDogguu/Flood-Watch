package com.example.myapplication.ui.activity.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.auth.GoogleAuthLoginResponse;
import com.example.myapplication.data.models.auth.ManualSignUpResponse;
import com.example.myapplication.data.models.auth.SignupPostRequest;
import com.example.myapplication.data.respository.auth.AuthenticationAPIRequestHandler;
import com.example.myapplication.ui.activity.BaseActivity;
import com.example.myapplication.utils.GlobalUtility;

import java.util.ArrayList;
import java.util.List;

public class SignUpAsGoogleActivity extends BaseActivity {

    private EditText etFullName;
    private EditText etEmailAddress;
    private EditText etContactNo;
    private Button btnManualSignUp;
    private AuthenticationAPIRequestHandler authenticationAPI;
    private Context context;
    private Activity activity;
    private GlobalUtility globalUtility;
    // Init first
    private String email;
    String fullName;
    private List<String> sign_in_type;
    String status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_as_google);
        initViews();
        GoogleAuthLoginResponse.UserData userData = (GoogleAuthLoginResponse.UserData) getIntent().getSerializableExtra("UserData");
        sign_in_type = new ArrayList<>();
        email = "";
        fullName = "";
        status = "";


        if (userData != null) {
            email = userData.getEmail();
            fullName = userData.getFullname();
            sign_in_type = userData.getSign_in_type();
            for (var val :sign_in_type) {
                Toast.makeText(context, "Value"+val, Toast.LENGTH_SHORT).show();
            }

            status = userData.getStatus();
            etFullName.setText(email);
            etEmailAddress.setText(fullName);
        }

        btnManualSignUp.setOnClickListener(v -> {
            String contactNo = etContactNo.getText().toString();

            var user = new SignupPostRequest.User(email, fullName, status, sign_in_type);
            var address = new SignupPostRequest.Address(null, null, null, null);
            var personalInfo = new SignupPostRequest.PersonalInformation(contactNo, null);
            SignupPostRequest request = new SignupPostRequest(user, address, personalInfo);

            authenticationAPI.googleSignUp(request, new ResponseCallback<ManualSignUpResponse>() {
                @Override
                public void onSuccess(ManualSignUpResponse response) {
                    Intent intent = new Intent(SignUpAsGoogleActivity.this, UploadProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                }
                @Override
                public void onError(Throwable t) {
                    Toast.makeText(context, "An error occurred " + t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

        });

    }

    private void initViews() {
        this.context = this;
        this.activity = this;


        authenticationAPI = new AuthenticationAPIRequestHandler(activity,context);
        globalUtility = new GlobalUtility();

        etFullName = (EditText) findViewById(R.id.etFullName);
        etEmailAddress = (EditText) findViewById(R.id.etEmail);
        etContactNo = (EditText) findViewById(R.id.etContactNumber);
        btnManualSignUp = (Button) findViewById(R.id.btnManualSignUp);
    }
}
