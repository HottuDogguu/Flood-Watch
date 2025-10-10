package com.example.myapplication.ui;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.data.models.auth.AuthCallback;
import com.example.myapplication.data.models.auth.LoginRequestPost;
import com.example.myapplication.data.models.auth.LoginResponse;
import com.example.myapplication.data.respository.AuthenticationAPI;

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


        email = (EditText) findViewById(R.id.emailInput);
        password = (EditText) findViewById(R.id.passwordInput);
        loginBtn = (Button) findViewById(R.id.loginButton);


        loginBtn.setOnClickListener(v ->{
            AuthenticationAPI auth = new AuthenticationAPI();
            Toast.makeText(context, "Hey", Toast.LENGTH_SHORT).show();

            auth.getResponse(new LoginRequestPost(email.getText().toString(), password.getText().toString()),
                    new AuthCallback() {
                        @Override
                        public void onSuccess(LoginResponse response) {
                            Toast.makeText(context, response.getAccess_token(), Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onError(Throwable t) {
                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        });

    }
}