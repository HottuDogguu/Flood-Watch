package com.example.myapplication.activities.auth;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.calbacks.auth.AuthCallback;
import com.example.myapplication.data.models.auth.GoogleAuthLoginResponse;
import com.example.myapplication.data.models.auth.ManualSignUpResponse;
import com.example.myapplication.data.models.auth.SignupPostRequest;
import com.example.myapplication.data.respository.auth.AuthenticationAPI;
import com.example.myapplication.security.DataStoreManager;
import com.example.myapplication.utils.GlobalUtility;

import java.util.List;

public class SignUpAsGoogleActivity extends AppCompatActivity {

    private EditText etFullName;
    private EditText etEmailAddress;
    private EditText etContactNo;
    private Button btnManualSignUp;
    private AuthenticationAPI authenticationAPI;
    private Context context;
    private Activity activity;
    private GlobalUtility globalUtility;
    private DataStoreManager dataStoreManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_as_google);
        initViews();

        btnManualSignUp.setOnClickListener(v -> {
            GoogleAuthLoginResponse.UserData userData = (GoogleAuthLoginResponse.UserData) getIntent().getSerializableExtra("UserData");

            if (userData != null) {
                String email = userData.getEmail();
                String fullName = userData.getFullname();
                List<String> sign_in_type = userData.getSign_in_type();
                String status = userData.getStatus();

                String contactNo = etContactNo.getText().toString();

                etFullName.setText(email);
                etEmailAddress.setText(fullName);
                var user = new SignupPostRequest.User(email, fullName, status, sign_in_type);
                var address = new SignupPostRequest.Address(null, null, null, null);
                var personalInfo = new SignupPostRequest.PersonalInformation(contactNo, null);
                SignupPostRequest request = new SignupPostRequest(user, address, personalInfo);
                authenticationAPI.googleSignUp(request, new AuthCallback<ManualSignUpResponse>() {
                    @Override
                    public void onSuccess(ManualSignUpResponse response) {
                        //Add the access token in data storage
                        globalUtility.insertDataToDataStore("access_token",
                                dataStoreManager,
                                response.getAccess_token(), () -> {
                                    globalUtility.getDataFromDataStore("access_token", dataStoreManager, data -> {
                                    });
                                });
                    }

                    @Override
                    public void onError(Throwable t) {

                    }
                });


            }
        });

    }

    private void initViews() {
        this.context = this;
        this.activity = this;


        authenticationAPI = new AuthenticationAPI(activity);
        globalUtility = new GlobalUtility();
        dataStoreManager = new DataStoreManager(context);

        etFullName = (EditText) findViewById(R.id.etFullName);
        etEmailAddress = (EditText) findViewById(R.id.etEmail);
        etContactNo = (EditText) findViewById(R.id.etContactNumber);
        btnManualSignUp = (Button) findViewById(R.id.btnManualSignUp);
    }
}
