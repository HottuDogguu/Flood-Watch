package com.example.myapplication.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.data.network.APIBuilder;
import com.example.myapplication.data.network.calbacks.auth.AuthCallback;
import com.example.myapplication.data.models.auth.LoginManualRequestPost;
import com.example.myapplication.data.models.auth.LoginManualResponse;
import com.example.myapplication.data.respository.auth.AuthenticationAPI;
import com.example.myapplication.security.DataStoreManager;

public class AuthenticationActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private TextView sample;
    private Button loginBtn;
    private Button googleBtn;
    private Context context;
    private AuthenticationAPI auth;


    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        context = this;
        APIBuilder apiBuilder = new APIBuilder();
        auth = new AuthenticationAPI(this);


        email = (EditText) findViewById(R.id.emailInput);
        password = (EditText) findViewById(R.id.passwordInput);
        loginBtn = (Button) findViewById(R.id.loginButton);
        googleBtn = (Button) findViewById(R.id.googleSignInButton);


        loginBtn.setOnClickListener(v -> {
            DataStoreManager dataStoreManager = new DataStoreManager(this);

            //call the get response function which is the request from apiBuilder
            auth.manualLoginResponse(new LoginManualRequestPost(email.getText().toString(), password.getText().toString()),
                    new AuthCallback<LoginManualResponse>() {
                        @Override
                        public void onSuccess(LoginManualResponse response) {

                            dataStoreManager.saveDataFromJava("access_token", response.getAccess_token(), () -> {
                                dataStoreManager.getStringFromJava("access_token", s -> {
                                    Intent intent = new Intent(AuthenticationActivity.this, DashboardActivity.class);
                                    //Loading first
                                    Toast.makeText(context, "Successfully Login", Toast.LENGTH_LONG).show();
                                    startActivity(intent);
                                    return null;
                                });
                                return null;
                            });
                        }

                        @Override
                        public void onError(Throwable t) {
                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }, apiBuilder);
        });

        googleBtn.setOnClickListener(v -> {
                auth.googleLoginResponse("442931204719-vcurqg7q42npvonomi9innbmvk2j3bqu.apps.googleusercontent.com");
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
}