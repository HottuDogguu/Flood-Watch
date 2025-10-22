package com.example.myapplication.activities.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.activities.DashboardActivity;
import com.example.myapplication.data.models.auth.GoogleAuthLoginResponse;
import com.example.myapplication.calbacks.auth.AuthCallback;
import com.example.myapplication.data.models.auth.ManualLoginRequest;
import com.example.myapplication.data.models.auth.ManualLoginResponse;
import com.example.myapplication.data.respository.auth.AuthenticationAPI;
import com.example.myapplication.data.validation.DataFieldsValidation;
import com.example.myapplication.security.DataStoreManager;
import com.example.myapplication.utils.GlobalUtility;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;

    private Button loginBtn, googleBtn;

    private TextView signupButton;
    private TextInputLayout loginEmailTextInput, loginPasswordTextInput;
    private Context context;
    private AuthenticationAPI auth;
    private GlobalUtility globalUtility;
    private DataStoreManager dataStoreManager;
    private DataFieldsValidation dataValidation;


    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        // initialized variables
        context = this; // Set context
        auth = new AuthenticationAPI(this);
        this.globalUtility = new GlobalUtility();
        dataStoreManager = new DataStoreManager(context);
        dataValidation = new DataFieldsValidation();

        email = (EditText) findViewById(R.id.emailInput);
        password = (EditText) findViewById(R.id.loginPasswordInputField);
        loginBtn = (Button) findViewById(R.id.loginButton);
        googleBtn = (Button) findViewById(R.id.googleSignInButton);
        signupButton = (TextView) findViewById(R.id.signupButton);
        loginEmailTextInput = (TextInputLayout) findViewById(R.id.tilLoginEmail);
        loginPasswordTextInput = (TextInputLayout) findViewById(R.id.tilLoginPassword);


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
                //if empty, show a message
                loginEmailTextInput.setError("This field must not be empty!");
                //request focus and show keyboard
                email.requestFocus();
                email.setFocusableInTouchMode(true);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(email, InputMethodManager.SHOW_IMPLICIT);
                return;
            }
            if (dataValidation.isEmptyField(loginPassword)) {
                //if empty, show a message
                loginPasswordTextInput.setError("This field must not be empty!");
                password.requestFocus();
                password.setFocusableInTouchMode(true);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(password, InputMethodManager.SHOW_IMPLICIT);
                return;
            }

            //if no empty fields, then proceed to calling api.

            //call the get response function which is the request from apiBuilder
            auth.manualLoginResponse(new ManualLoginRequest(loginEmail, loginPassword),
                    new AuthCallback<ManualLoginResponse>() {
                        @Override
                        public void onSuccess(ManualLoginResponse response) {
                            if (response.getStatus().equals("pending")) {
                                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                //Loading first
                                Toast.makeText(context, "Please verify your account first", Toast.LENGTH_LONG).show();
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                                return;

                            }
                            globalUtility.insertDataToDataStore("access_token", dataStoreManager,
                                    response.getAccess_token(),
                                    () -> {
                                        globalUtility.getDataFromDataStore("access_token", dataStoreManager, data -> {
                                            Toast.makeText(context, "Successfully Login", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                            //Loading first

                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        });
                                    });
                        }

                        @Override
                        public void onError(Throwable t) {
                            String statusCode = t.getMessage();
                            if (statusCode.equals("404") && statusCode != null) {
                                setRequestFocusOnField(email, loginEmailTextInput, "Username not found!");

                            } else if (statusCode.equals("400") && statusCode != null) {
                                setRequestFocusOnField(password, loginPasswordTextInput, "Incorrect Password!");
                            } else if (statusCode.equals("403")) {
                                //If the user is exist but not verified
                                Intent intent = new Intent(LoginActivity.this, EmailVerificationActivity.class);
                                intent.putExtra("UserEmail", email.getText().toString());
                                startActivity(intent);
                            }
                        }
                    });
        });

        //When the user click Signin as Google or continue as google
        googleBtn.setOnClickListener(v -> {
            auth.googleLoginResponse("442931204719-vcurqg7q42npvonomi9innbmvk2j3bqu.apps.googleusercontent.com", new AuthCallback<GoogleAuthLoginResponse>() {
                @Override
                public void onSuccess(GoogleAuthLoginResponse response) {
                    String userActions = response.getAction();
                    int profile_setup_steps = response.getData().getProfile_setup_steps();
                    List<String> sign_in_type = response.getData().getSign_in_type();

                    if (userActions.equals("login")) {
                        //if the account does not linked in google, then it will go to the link page
                        if (!sign_in_type.contains("google")) {
                            //if it is linked in password only, then go to link account page
                            Intent intent = new Intent(LoginActivity.this, LinkGoogleAccountActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            //add the data in intent, then it will retrieve in the linked page
                            intent.putExtra("UserEmail", response.getData().getEmail());
                            intent.putExtra("UserId", response.getData().getId());
                            startActivity(intent);
                            finish();
                            //return it to stop here the logic
                            return;
                        } else {
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            //Add the access token in data storage
                            globalUtility.insertDataToDataStore("access_token",
                                    dataStoreManager,
                                    response.getToken().getAccess_token(), () -> {
                                        globalUtility.getDataFromDataStore("access_token", dataStoreManager, data -> {
                                        });
                                    });
                            startActivity(intent);
                            finish();
                        }

                        //if no in if and else if, it means the user is verified

                        //This is the end of the if user action is logged in
                    } else {
                        Intent intent = new Intent(LoginActivity.this, SignUpAsGoogleActivity.class);
                        intent.putExtra("UserData", response.getData());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                }

                @Override
                public void onError(Throwable t) {
                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        DataStoreManager dataStoreManager = DataStoreManager.Companion.getInstance(getApplicationContext());
        dataStoreManager.deleteKeyFromJava("access_token", () -> {
            return null;
        });
    }

    public void setRequestFocusOnField(EditText editText, TextInputLayout inputLayout, String message) {
        //if empty, show a message
        inputLayout.setError(message);
        //request focus and show keyboard
        editText.requestFocus();
        editText.setFocusableInTouchMode(true);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }
}