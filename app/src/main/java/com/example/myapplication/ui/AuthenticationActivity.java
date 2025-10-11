package com.example.myapplication.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.data.network.APIBuilder;
import com.example.myapplication.data.network.calbacks.auth.AuthCallback;
import com.example.myapplication.data.models.auth.LoginRequestPost;
import com.example.myapplication.data.models.auth.LoginResponse;
import com.example.myapplication.data.respository.auth.AuthenticationAPI;
import com.example.myapplication.security.DataStoreManager;

import kotlin.Unit;

public class AuthenticationActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private TextView sample;
    private Button loginBtn;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        context = this;
        APIBuilder api = new APIBuilder();


        email = (EditText) findViewById(R.id.emailInput);
        password = (EditText) findViewById(R.id.passwordInput);
        loginBtn = (Button) findViewById(R.id.loginButton);


        loginBtn.setOnClickListener(v -> {
            AuthenticationAPI auth = new AuthenticationAPI();
            DataStoreManager dataStoreManager = new DataStoreManager(this);

            //call the get response function which is the request from api
            auth.getResponse(new LoginRequestPost(email.getText().toString(), password.getText().toString()),
                    new AuthCallback() {
                        @Override
                        public void onSuccess(LoginResponse response) {

                            dataStoreManager.saveDataFromJava("access_token", response.getAccess_token(), () -> {
                                dataStoreManager.getStringFromJava("access_token", s -> {
                                    Intent intent = new Intent(AuthenticationActivity.this,DashboardActivity.class);
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
                    }, api);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        DataStoreManager dataStoreManager = DataStoreManager.Companion.getInstance(getApplicationContext());
        dataStoreManager.deleteKeyFromJava("access_token", () ->{
            Toast.makeText(dataStoreManager.getContext(),"Successfully Deleted", Toast.LENGTH_LONG).show();
            return null;
        });
    }
}