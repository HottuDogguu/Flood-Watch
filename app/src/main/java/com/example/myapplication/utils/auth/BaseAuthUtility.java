package com.example.myapplication.utils.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.respository.AuthenticationAPIRequestHandler;
import com.example.myapplication.data.respository.UsersAPIRequestHandler;
import com.example.myapplication.security.DataSharedPreference;

import com.example.myapplication.ui.activity.auth.EmailVerificationActivity;
import com.example.myapplication.ui.activity.home.HomeActivity;
import com.example.myapplication.ui.activity.auth.LinkGoogleAccountActivity;
import com.example.myapplication.ui.activity.auth.SignUpAsGoogleActivity;
import com.example.myapplication.utils.GlobalUtility;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;
import java.util.Objects;

public class BaseAuthUtility {

    private Context context;
    private Activity activity;
    private GlobalUtility globalUtility;
    private DataSharedPreference dataSharedPreference;
    private UsersAPIRequestHandler usersAPIRequestHandler;
    private final String TAG = "BASE_ACTIVITY";

    public BaseAuthUtility(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        globalUtility = new GlobalUtility();
        usersAPIRequestHandler = new UsersAPIRequestHandler(activity,context);
        dataSharedPreference = DataSharedPreference.getInstance(context);

    }

    public void setRequestFocusOnField(EditText editText, TextInputLayout inputLayout, String message) {
        //if empty, show a message
        inputLayout.setError(message);
        //request focus and show keyboard
        editText.requestFocus();
        editText.setFocusableInTouchMode(true);
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public void triggerGoogleButton(AuthenticationAPIRequestHandler authenticationAPI) {
        String webClient = globalUtility.getValueInYAML(BuildConfig.WEB_CLIENT_ID, context);
        authenticationAPI.googleLoginResponse(webClient, new ResponseCallback<ApiSuccessfulResponse>() {
            @Override
            public void onSuccess(ApiSuccessfulResponse response) {
                String userActions = response.getAction();
                List<String> sign_in_type = response.getData().getSign_in_type();
                if (userActions.equals("login")) {
                    //check the sign in type if contains google
                    if (!sign_in_type.contains("google")) {
                        //check if the account is pending or not
                        if(response.getData().getStatus().equalsIgnoreCase("pending")){
                            Intent intent = new Intent(context, EmailVerificationActivity.class);
                            context.startActivity(intent);
                            return;
                        }
                        //if it is linked in password only, then go to link account page
                        Intent intent = new Intent(context, LinkGoogleAccountActivity.class);
                        //add the data in intent, then it will retrieve in the linked page
                        intent.putExtra("UserEmail", response.getData().getEmail());
                        intent.putExtra("UserId", response.getData().getId());
                        context.startActivity(intent);
                    } else {

                        getFCMToken().addOnSuccessListener(fcmToken -> {
                            usersAPIRequestHandler.UpdateFCMToken(fcmToken, response.getAccess_token(), new ResponseCallback<ApiSuccessfulResponse>() {
                                @Override
                                public void onSuccess(ApiSuccessfulResponse response1) {
                                    String ACCESS_TOKEN_KEY = globalUtility.getValueInYAML(BuildConfig.ACCESS_TOKEN_KEY, context);
                                    // if user has a google as sign in type, then auto login
                                    Intent intent = new Intent(context, HomeActivity.class);
                                    //store the access token in data storage
                                    dataSharedPreference.saveData(ACCESS_TOKEN_KEY, response.getAccess_token());
                                    context.startActivity(intent);
                                    //log only the message
                                    Log.i("FCM_TOKEN", response1.getMessage());
                                }

                                @Override
                                public void onError(Throwable t) {
                                    Log.e("FCM_TOKEN", Objects.requireNonNull(t.getMessage()));

                                }
                            });
                        });

                    }
                    //otherwise the user has no account yet. Then redirect to sign Up as Google Activity
                } else {
                    Intent intent = new Intent(context, SignUpAsGoogleActivity.class);
                    intent.putExtra("UserData", response.getData());
                    context.startActivity(intent);
                }
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, Objects.requireNonNull(t.getMessage()));

            }
        });

    }

    public Task<String> getFCMToken() {
        return FirebaseMessaging.getInstance().getToken();
    }
}


