package com.example.myapplication.ui.activity.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.security.DataSharedPreference;

import com.example.myapplication.ui.activity.BaseActivity;
import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.auth.ManualLoginRequest;
import com.example.myapplication.data.respository.AuthenticationAPIRequestHandler;
import com.example.myapplication.data.validation.DataFieldsValidation;

import com.example.myapplication.utils.auth.LoginActivityUtility;
import com.google.android.material.textfield.TextInputLayout;


import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class LoginActivity extends BaseActivity {
    private EditText email, password;

    private Button loginBtn, googleBtn;

    private TextView signupButton;
    private TextInputLayout loginEmailTextInput, loginPasswordTextInput;
    private Context context;
    private Activity activity;
    private AuthenticationAPIRequestHandler auth;

    private DataSharedPreference dataSharedPreference;
    private DataFieldsValidation dataValidation;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private LoginActivityUtility loginActivityUtility;


    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        initViews();
        //Clear shared preference before log in
        dataSharedPreference.clearPreference();
        String ACCESS_TOKEN_KEY = globalUtility.getValueInYAML(BuildConfig.ACCESS_TOKEN_KEY, context);
        ACCESS_TOKEN_KEY = ACCESS_TOKEN_KEY != null ? ACCESS_TOKEN_KEY : "Hey";
        Log.i("PREFERENCE", dataSharedPreference.getData(ACCESS_TOKEN_KEY));

        //OnClickListener
        //listener for email when typing
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String loginEmail = email.getText().toString();
                if (dataValidation.isEmptyField(loginEmail)) {
                    //if empty, show a message
                    loginEmailTextInput.setError("This field must not be empty!");
                } else {
                    loginEmailTextInput.setError(null);
                }
            }
        });
        //listener for email when typing
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginPasswordTextInput.setError(null);
            }
        });

        loginBtn.setOnClickListener(v -> {
            String loginEmail = email.getText().toString();
            String loginPassword = password.getText().toString();

            //set the error text to null
            if (dataValidation.isEmptyField(loginEmail)) {
                loginActivityUtility.setRequestFocusOnField(email,
                        loginEmailTextInput,
                        "This field must not be empty!");
                return;
            }
            //if no empty fields, then proceed to calling api.
            //call the get response function which is the request from apiBuilder
            getIntent().putExtra("UserEmail", loginEmail); //send email into intent
            auth.manualLoginResponse(new ManualLoginRequest(loginEmail, loginPassword),
                    new ResponseCallback<ApiSuccessfulResponse>() {
                        @Override
                        public void onSuccess(ApiSuccessfulResponse response) {
                            loginActivityUtility.handleOnSuccess(response);
                        }

                        @Override
                        public void onError(Throwable t) {
                            loginActivityUtility.handleOnError(t, loginEmail);
                        }
                    });
        });

        //When the user click Signin as Google or continue as google
        googleBtn.setOnClickListener(v -> {
            loginActivityUtility.triggerGoogleButton(auth);
        });
        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }


    public void initViews() {

        // initialized variables
        context = this; // Set context
        activity = this; // Set context
        auth = new AuthenticationAPIRequestHandler(this, context);

        dataValidation = new DataFieldsValidation();

        email = (EditText) findViewById(R.id.emailInput);
        password = (EditText) findViewById(R.id.loginPasswordInputField);
        loginBtn = (Button) findViewById(R.id.loginButton);
        googleBtn = (Button) findViewById(R.id.googleSignInButton);
        signupButton = (TextView) findViewById(R.id.signupButton);
        loginEmailTextInput = (TextInputLayout) findViewById(R.id.tilLoginEmail);
        loginPasswordTextInput = (TextInputLayout) findViewById(R.id.tilLoginPassword);
        dataSharedPreference = DataSharedPreference.getInstance(context);
        //initialized login utility
        this.loginActivityUtility = new LoginActivityUtility(
                context, activity, dataSharedPreference, email, password,
                loginEmailTextInput, loginPasswordTextInput);
    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}