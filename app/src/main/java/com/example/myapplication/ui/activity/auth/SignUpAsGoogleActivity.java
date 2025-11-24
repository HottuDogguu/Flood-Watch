package com.example.myapplication.ui.activity.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.models.auth.SignupPostRequest;
import com.example.myapplication.data.respository.AuthenticationAPIRequestHandler;
import com.example.myapplication.ui.activity.BaseActivity;
import com.example.myapplication.ui.activity.home.HomeActivity;
import com.example.myapplication.utils.GlobalUtility;
import com.example.myapplication.utils.auth.BaseAuthUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SignUpAsGoogleActivity extends BaseActivity {


    private EditText etEmailAddress,etContactNo,etFullName;
    private TextView tvSignIn;
    private Button btnManualSignUp;
    private AuthenticationAPIRequestHandler authenticationAPI;
    private Context context;
    private Activity activity;
    private GlobalUtility globalUtility;
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
            etFullName.setText(email);
            etEmailAddress.setText(fullName);
        }

        btnManualSignUp.setOnClickListener(v -> {
            String contactNo = etContactNo.getText().toString();
            //get the fcm token
            baseAuthUtility.getFCMToken().addOnSuccessListener(token -> {
                        fcmToken = token;
                        var user = new SignupPostRequest.User(email, fullName, status, sign_in_type, fcmToken);
                        var address = new SignupPostRequest.Address(null, null, null, null);
                        var personalInfo = new SignupPostRequest.PersonalInformation(contactNo, null);
                        SignupPostRequest request = new SignupPostRequest(user, address, personalInfo);
                        authenticationAPI.googleSignUp(request, new ResponseCallback<ApiSuccessfulResponse>() {
                            @Override
                            public void onSuccess(ApiSuccessfulResponse response) {
                                String accessToken = globalUtility.getValueInYAML(BuildConfig.ACCESS_TOKEN_KEY, context);
                                //store access token in data store
                                dataStoreManager.putString(accessToken, response.getAccess_token());
                                Intent intent = new Intent(SignUpAsGoogleActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                //Show message
                                Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                                finish();
                            }
                            @Override
                            public void onError(Throwable t) {
                                Toast.makeText(context,  t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FCM_TOKEN_ERROR", Objects.requireNonNull(e.getMessage()));
                    });
        });

        tvSignIn.setOnClickListener(v ->{
            Intent intent = new Intent(SignUpAsGoogleActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

    }


    private void initViews() {
        this.context = this;
        this.activity = this;

        etFullName = (EditText) findViewById(R.id.etFullName);
        etEmailAddress = (EditText) findViewById(R.id.etEmail);
        etContactNo = (EditText) findViewById(R.id.etContactNumber);
        btnManualSignUp = (Button) findViewById(R.id.btnManualSignUp);
        tvSignIn = (TextView) findViewById(R.id.sGtvSignIn);

                authenticationAPI = new AuthenticationAPIRequestHandler(activity, context);
        globalUtility = new GlobalUtility();
        baseAuthUtility = new BaseAuthUtility(context,activity);
    }
}
