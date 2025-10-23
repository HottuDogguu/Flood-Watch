package com.example.myapplication.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.myapplication.ui.activity.auth.EmailVerificationActivity;
import com.example.myapplication.calbacks.auth.AuthCallback;
import com.example.myapplication.data.models.auth.ManualSignUpResponse;
import com.example.myapplication.data.models.auth.SignupPostRequest;
import com.example.myapplication.data.respository.auth.AuthenticationAPI;

public class SignUpActivityUtility {
    private AuthenticationAPI auth;
    private Context context;
    public SignUpActivityUtility(Context context, AuthenticationAPI auth){
        this.auth = auth;
        this.context = context;
    }

    public void signUpUser(SignupPostRequest.User user,
                           SignupPostRequest.Address address,
                           SignupPostRequest.PersonalInformation personalInformation){

        SignupPostRequest postRequest = new SignupPostRequest(user,address,personalInformation);
        this.auth.manualSignUp(postRequest, new AuthCallback<ManualSignUpResponse>() {
            @Override
            public void onSuccess(ManualSignUpResponse response) {
                Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, EmailVerificationActivity.class);
                //this will retrieve in EmailVerificationActivity.
                //usage String email = getIntent.getStringExtra("UserEmail");
                intent.putExtra("UserEmail", user.getEmail());
                context.startActivity(intent);
            }
            @Override
            public void onError(Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
