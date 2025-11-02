package com.example.myapplication.utils.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.respository.users.UsersAPIRequestHandler;
import com.example.myapplication.security.DataStorageManager;
import com.example.myapplication.ui.activity.HomeActivity;
import com.example.myapplication.ui.activity.auth.EmailVerificationActivity;
import com.example.myapplication.utils.GlobalUtility;
import com.google.android.material.textfield.TextInputLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class LoginActivityUtility extends BaseAuthUtility {

    private static final Logger log = LoggerFactory.getLogger(LoginActivityUtility.class);
    private Context context;
    private Activity activity;
    private DataStorageManager dataStorageManager;
    private GlobalUtility globalUtility;
    private UsersAPIRequestHandler usersAPIRequestHandler;
    private EditText email, password;

    TextInputLayout loginEmailTextInput, loginPasswordTextInput;


    public LoginActivityUtility(Context context,
                                Activity activity,
                                DataStorageManager dataStorageManager,
                                EditText email,
                                EditText password,
                                TextInputLayout loginEmailTextInput,
                                TextInputLayout loginPasswordTextInput
    ) {

        super(context,activity);
        this.context = context;
        this.activity = activity;
        this.dataStorageManager = dataStorageManager;
        this.globalUtility = new GlobalUtility();
        this.email = email;
        this.password = password;
        this.loginEmailTextInput = loginEmailTextInput;
        this.loginPasswordTextInput = loginPasswordTextInput;
        usersAPIRequestHandler = new UsersAPIRequestHandler(activity,context);
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
        String accessTokenKey = globalUtility.getValueInYAML(BuildConfig.ACCESS_TOKEN_KEY, context);
        dataStorageManager.putString(accessTokenKey, response.getAccess_token());

        this.getFCMToken().addOnSuccessListener(fcmToken -> {
            usersAPIRequestHandler.UpdateFCMToken(fcmToken, response.getAccess_token(), new ResponseCallback<ApiSuccessfulResponse>() {
                @Override
                public void onSuccess(ApiSuccessfulResponse response) {
                    //log only the message
                    Log.i("FCM_TOKEN", response.getMessage());
                }

                @Override
                public void onError(Throwable t) {
                    Log.e("FCM_TOKEN", Objects.requireNonNull(t.getMessage()));

                }
            });
        });
        Toast.makeText(context, response.getMessage(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this.context, HomeActivity.class);
        context.startActivity(intent);
        //then check if the fcm token is change



    }


    // handling Google login


}
