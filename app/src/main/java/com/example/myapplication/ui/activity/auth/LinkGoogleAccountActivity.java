package com.example.myapplication.ui.activity.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.ui.activity.DashboardActivity;
import com.example.myapplication.calbacks.auth.AuthCallback;
import com.example.myapplication.data.models.auth.LinkAccountToMultipleSiginMethodsRequest;
import com.example.myapplication.data.models.auth.ManualLoginResponse;
import com.example.myapplication.data.respository.auth.AuthenticationAPI;
import com.example.myapplication.security.DataStoreManager;
import com.example.myapplication.utils.GlobalUtility;


public class LinkGoogleAccountActivity extends AppCompatActivity {
    private Button btnLinkAccount;
    private TextView tvBackToSignIn;
    private AuthenticationAPI autApi;
    private Activity activity;
    private Context context;
    private GlobalUtility globalUtility;
    private DataStoreManager dataStoreManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_google_account);
        initViews();

        btnLinkAccount.setOnClickListener(v -> {
            String userId = getIntent().getStringExtra("UserId");
            String userEmail = getIntent().getStringExtra("UserEmail");
            autApi.linkUserAccountToGoogle(new LinkAccountToMultipleSiginMethodsRequest(userId,userEmail), new AuthCallback<ManualLoginResponse>() {
                @Override
                public void onSuccess(ManualLoginResponse response) {
                    Intent intent = new Intent(LinkGoogleAccountActivity.this, DashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    //Add the access token in data storage
                    globalUtility.insertDataToDataStore("access_token",
                            dataStoreManager,
                            response.getAccess_token(), () -> {
                                globalUtility.getDataFromDataStore("access_token", dataStoreManager, data -> {
                                    Toast.makeText(context, "Successfully Linked Account.", Toast.LENGTH_SHORT).show();
                                    //Remove the extra in intent
                                    getIntent().removeExtra("UserId");
                                    getIntent().removeExtra("UserEmail");
                                });
                            });
                    //Start the new Activity or show it
                    startActivity(intent);
                    //then finish this current views
                    finish();
                }
                @Override
                public void onError(Throwable t) {
                    //If encountered error, show the message
                    Toast.makeText(activity, "An error occurred"+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void initViews() {
        this.context = this;
        activity = new Activity();
        autApi = new AuthenticationAPI(activity);
        dataStoreManager = new DataStoreManager(context);
        globalUtility = new GlobalUtility();

        this.btnLinkAccount = (Button) findViewById(R.id.btnLinkAccount);
        this.tvBackToSignIn = (TextView) findViewById(R.id.tvBackToSignIn);


    }

}
