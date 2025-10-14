package com.example.myapplication.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.data.models.auth.GoogleAuthResponse;
import com.example.myapplication.data.network.APIBuilder;
import com.example.myapplication.calbacks.auth.AuthCallback;
import com.example.myapplication.data.models.auth.LoginManualRequestPost;
import com.example.myapplication.data.models.auth.LoginManualResponse;
import com.example.myapplication.data.respository.auth.AuthenticationAPI;
import com.example.myapplication.security.DataStoreManager;
import com.example.myapplication.utils.GlobalUtility;

public class AuthenticationActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private TextView sample;
    private Button loginBtn;
    private Button googleBtn;
    private Context context;
    private AuthenticationAPI auth;
    private GlobalUtility utility;
    private DataStoreManager dataStoreManager;



    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        context = this;
        APIBuilder apiBuilder = new APIBuilder();
        auth = new AuthenticationAPI(this);
        this.utility = new GlobalUtility();
        dataStoreManager= new DataStoreManager(context);

        email = (EditText) findViewById(R.id.emailInput);
        password = (EditText) findViewById(R.id.passwordInput);
        loginBtn = (Button) findViewById(R.id.loginButton);
        googleBtn = (Button) findViewById(R.id.googleSignInButton);

        loginBtn.setOnClickListener(v -> {

            //call the get response function which is the request from apiBuilder
            auth.manualLoginResponse(new LoginManualRequestPost(email.getText().toString(), password.getText().toString()),
                    new AuthCallback<LoginManualResponse>() {
                        @Override
                        public void onSuccess(LoginManualResponse response) {
                            dataStoreManager.saveDataFromJava("access_token", response.getToken().getAccess_token(), () -> {
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
                    });
        });

        googleBtn.setOnClickListener(v -> {
           auth.googleLoginResponse("442931204719-vcurqg7q42npvonomi9innbmvk2j3bqu.apps.googleusercontent.com", new AuthCallback<GoogleAuthResponse>() {
               @Override
               public void onSuccess(GoogleAuthResponse response) {


                   String userActions = response.getAction();
                   int profile_setup_steps = response.getData().getProfile_setup_steps();
                   String userStatus = response.getData().getStatus();
                    Intent intent;
                   if(userActions.equals("login")){
                       if(userStatus.equals("pending")){
                        Toast.makeText(context,"Please verify your account first." ,Toast.LENGTH_SHORT).show();
                        // redirect to send email verification

                        //Check if the user is deleted via soft delete
                       }else if(userStatus.equals("deleted")){
                           Toast.makeText(context,"No accounts associated anymore." ,Toast.LENGTH_SHORT).show();
                           //then terminate the code here via returning nothing
                            return;
                       }

                       //if no in if and else if, it means the user is verified
                        //So proceed to checking the profile set up where the user stop to filling their profile
                       switch (profile_setup_steps){
                           case 1 :
                               //if one it means it is in the personal information filling form
                               intent = new Intent(AuthenticationActivity.this, DashboardActivity.class);
                           case 2:
                               //this is the address filling
                       }
                       //This is the end of the if user action is logged in
                   }else{
                       //then it means the user is signing up
                        //redirect to the first step in profile setup, which is in the personal information
                   }
                   //When Successfully created account or login, store the access token in data store
                   utility.insertDataToDataStore("access_token",
                           dataStoreManager,
                           response.getToken().getAccess_token(),() -> {
                               utility.getDataFromDataStore("access_token",dataStoreManager, data ->{});}
                   );
               }

               @Override
               public void onError(Throwable t) {
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_SHORT).show();
               }
           });
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