package com.example.myapplication.ui.activity.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.example.myapplication.security.DataStorageManager;
import com.example.myapplication.ui.activity.BaseActivity;
import com.example.myapplication.ui.activity.DashboardActivity;
import com.example.myapplication.data.models.auth.GoogleAuthLoginResponse;
import com.example.myapplication.calbacks.auth.AuthCallback;
import com.example.myapplication.data.models.auth.ManualLoginRequest;
import com.example.myapplication.data.models.auth.ManualLoginResponse;
import com.example.myapplication.data.respository.auth.AuthenticationAPI;
import com.example.myapplication.data.validation.DataFieldsValidation;

import com.example.myapplication.utils.auth.LoginActivityUtility;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class LoginActivity extends BaseActivity {
    private EditText email, password;

    private Button loginBtn, googleBtn;

    private TextView signupButton;
    private TextInputLayout loginEmailTextInput, loginPasswordTextInput;
    private Context context;
    private AuthenticationAPI auth;

    private DataStorageManager dataStoreManager;
    private DataFieldsValidation dataValidation;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private LoginActivityUtility loginActivityUtility;


    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        initViews();



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
                String loginEmail = password.getText().toString();
                if (dataValidation.isEmptyField(loginEmail)) {
                    //if empty, show a message
                    loginPasswordTextInput.setError("This field must not be empty!");
                } else {
                    loginPasswordTextInput.setError(null);
                }
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

            if (dataValidation.isEmptyField(loginPassword)) {
                loginActivityUtility.setRequestFocusOnField(password,
                        loginPasswordTextInput,
                        "This field must not be empty!");
                return;
            }
            //if no empty fields, then proceed to calling api.
            //call the get response function which is the request from apiBuilder
            auth.manualLoginResponse(new ManualLoginRequest(loginEmail, loginPassword),
                    new AuthCallback<ManualLoginResponse>() {
                        @Override
                        public void onSuccess(ManualLoginResponse response) {
                            loginActivityUtility.handleOnSuccess(response);
                        }

                        @Override
                        public void onError(Throwable t) {
                            loginActivityUtility.handleOnError(t);
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
        auth = new AuthenticationAPI(this);
        dataStoreManager = DataStorageManager.getInstance(context);

        dataValidation = new DataFieldsValidation();

        email = (EditText) findViewById(R.id.emailInput);
        password = (EditText) findViewById(R.id.loginPasswordInputField);
        loginBtn = (Button) findViewById(R.id.loginButton);
        googleBtn = (Button) findViewById(R.id.googleSignInButton);
        signupButton = (TextView) findViewById(R.id.signupButton);
        loginEmailTextInput = (TextInputLayout) findViewById(R.id.tilLoginEmail);
        loginPasswordTextInput = (TextInputLayout) findViewById(R.id.tilLoginPassword);

        //initialized login utility
        this.loginActivityUtility = new LoginActivityUtility(
                context, dataStoreManager, email, password,
                loginEmailTextInput, loginPasswordTextInput);


    }

    @Override
    protected void onPause() {
        super.onPause();
        compositeDisposable.dispose();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();

    }

}