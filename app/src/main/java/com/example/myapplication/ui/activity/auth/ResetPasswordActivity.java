package com.example.myapplication.ui.activity.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.models.users.ResetPasswordPutRequest;
import com.example.myapplication.data.respository.UsersAPIRequestHandler;
import com.example.myapplication.data.validation.DataFieldsValidation;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResetPasswordActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(ResetPasswordActivity.class);
    private TextInputEditText etEmailReset, etCurrentPassword, etNewPassword, etConfirmPassword;
    private TextInputLayout tilEmailReset, tilCurrentPassword, tilNewPassword, tilConfirmPassword;
    private DataFieldsValidation dataFieldsValidation;
    private UsersAPIRequestHandler usersAPIRequestHandler;
    private Button btnReset;
    private Activity activity;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //init views
        initViews();

        //button to reset password
        handleResetPassword();

        //text watcher
        handleTextWatcher();


    }

    private void handleTextWatcher() {

        //for new password
        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etNewPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //for confirm password
        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //reset the error message indicator
                tilConfirmPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //for confirm password
        etCurrentPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //reset the error message indicator
                etCurrentPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void handleResetPassword() {

        btnReset.setOnClickListener(v -> {

            String oldPassword = etCurrentPassword.getText().toString();
            String newPassword = etNewPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();
            String userEmail = etEmailReset.getText().toString();

            String errorMessage = dataFieldsValidation.validatePassword(newPassword);

            if(userEmail.isEmpty()){
                etEmailReset.setError("Email address must not be empty.");
                etEmailReset.requestFocus();
                return;
            }

            if (!errorMessage.isEmpty()) {
                etNewPassword.setError(errorMessage);
                etNewPassword.requestFocus();
                return;
            }

            if (!dataFieldsValidation.isPasswordMatch(newPassword, confirmPassword)) {
                etNewPassword.setError("Password and confirm password must match.");
                etNewPassword.requestFocus();
                return;
            }
            ResetPasswordPutRequest request = new ResetPasswordPutRequest(userEmail, oldPassword, newPassword);
            usersAPIRequestHandler.resetUserPassword(request, new ResponseCallback<ApiSuccessfulResponse>() {
                @Override
                public void onSuccess(ApiSuccessfulResponse response) {
                    Toast.makeText(activity, response.getMessage(), Toast.LENGTH_SHORT).show();

                    //navigate to login
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(Throwable t) {
                    Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });


        });
    }

    private void initViews() {

        activity = this;
        context = this;

        etEmailReset = findViewById(R.id.etEmailReset);
        etCurrentPassword = findViewById(R.id.etOldPasswordReset);
        etNewPassword = findViewById(R.id.etNewPasswordReset);
        etConfirmPassword = findViewById(R.id.etConfirmPasswordReset);
        tilEmailReset = findViewById(R.id.tilEmailReset);
        tilCurrentPassword = findViewById(R.id.tilOldPasswordReset);
        tilNewPassword = findViewById(R.id.tilNewPasswordReset);
        tilConfirmPassword = findViewById(R.id.tilConfirmPasswordReset);
        btnReset = findViewById(R.id.btnResetPassword);

        dataFieldsValidation = new DataFieldsValidation();
        usersAPIRequestHandler = new UsersAPIRequestHandler(activity, context);

    }
}
