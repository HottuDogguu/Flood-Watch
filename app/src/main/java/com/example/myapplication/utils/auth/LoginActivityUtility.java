package com.example.myapplication.utils.auth;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.security.DataStorageManager;
import com.example.myapplication.ui.activity.HomeActivity;
import com.example.myapplication.ui.activity.auth.EmailVerificationActivity;
import com.example.myapplication.utils.GlobalUtility;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivityUtility extends BaseAuthUtility{

    private Context context;
    private DataStorageManager dataStorageManager;
    private GlobalUtility globalUtility;
    private EditText email, password;

    TextInputLayout loginEmailTextInput, loginPasswordTextInput;


    public LoginActivityUtility(Context context,
                                DataStorageManager dataStorageManager,
                                EditText email,
                                EditText password,
                                TextInputLayout loginEmailTextInput,
                                TextInputLayout loginPasswordTextInput
    ) {

        super(context);

        this.context = context;
        this.dataStorageManager = dataStorageManager;
        this.globalUtility = new GlobalUtility();
        this.email = email;
        this.password = password;
        this.loginEmailTextInput = loginEmailTextInput;
        this.loginPasswordTextInput = loginPasswordTextInput;
    }

    public void handleOnSuccess(ApiSuccessfulResponse response) {
        //save token and navigate to the homepage
        saveTokenAndNavigateToHomaPage(response);

    }
    public void handleOnError(Throwable throwable) {
        String errorMessage = throwable.getMessage();
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
        if (errorMessage.contains("404") && errorMessage != null && errorMessage.toLowerCase().contains("not found")) {
            setRequestFocusOnField(email, loginEmailTextInput, "Username not found!");
        } else if (errorMessage.contains("400") && errorMessage != null && errorMessage.toLowerCase().contains("password")) {
            setRequestFocusOnField(password, loginPasswordTextInput, "Incorrect Password!");
        } else if (errorMessage.contains("403") && errorMessage.toLowerCase().contains("Verify")) {
            this.navigateToEmailVerification(throwable);
        }
    }

    private void navigateToEmailVerification(Throwable throwable) {
        Intent intent = new Intent(context, EmailVerificationActivity.class);
        //Loading first
        Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_LONG).show();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);


    }

    private void saveTokenAndNavigateToHomaPage(ApiSuccessfulResponse response) {
        //Set to data store
        String accessTokenKey = globalUtility.getValueInYAML(BuildConfig.ACCESS_TOKEN_KEY,context);
        dataStorageManager.putString(accessTokenKey, response.getAccess_token());
        Toast.makeText(context, response.getMessage(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this.context, HomeActivity.class);
        context.startActivity(intent);
    }






    // handling Google login


}
