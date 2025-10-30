package com.example.myapplication.utils.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.auth.GoogleAuthLoginResponse;
import com.example.myapplication.data.respository.auth.AuthenticationAPIRequestHandler;
import com.example.myapplication.ui.activity.DashboardActivity;
import com.example.myapplication.ui.activity.auth.LinkGoogleAccountActivity;
import com.example.myapplication.ui.activity.auth.SignUpAsGoogleActivity;
import com.example.myapplication.utils.GlobalUtility;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class BaseAuthUtility {

    private Context context;
    private GlobalUtility globalUtility;

    public BaseAuthUtility(Context context){
        this.context = context;
        globalUtility = new GlobalUtility();
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
    public void triggerGoogleButton(AuthenticationAPIRequestHandler authenticationAPI){
            String webClient = globalUtility.getValueInYAML(BuildConfig.WEB_CLIENT_ID,context);
            authenticationAPI.googleLoginResponse(webClient, new ResponseCallback<GoogleAuthLoginResponse>() {
                @Override
                public void onSuccess(GoogleAuthLoginResponse response) {
                    String userActions = response.getAction();
                    int profile_setup_steps = response.getData().getProfile_setup_steps();
                    List<String> sign_in_type = response.getData().getSign_in_type();
                    if (userActions.equals("login")) {
                        //if the account does not linked in google, then it will go to the link page
                        if (!sign_in_type.contains("google")) {
                            //if it is linked in password only, then go to link account page
                            Intent intent = new Intent(context, LinkGoogleAccountActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            //add the data in intent, then it will retrieve in the linked page
                            intent.putExtra("UserEmail", response.getData().getEmail());
                            intent.putExtra("UserId", response.getData().getId());
                            context.startActivity(intent);
                        } else {
                            Intent intent = new Intent(context, DashboardActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        }

                        //if no in if and else if, it means the user is verified

                        //This is the end of the user if action is logged in
                    } else {
                        Intent intent = new Intent(context, SignUpAsGoogleActivity.class);
                        intent.putExtra("UserData", response.getData());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    }

                }

                @Override
                public void onError(Throwable t) {
                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

    }
    }


