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

import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.example.myapplication.security.DataStorageManager;
import com.example.myapplication.ui.activity.BaseActivity;
import com.example.myapplication.ui.activity.DashboardActivity;
import com.example.myapplication.calbacks.auth.AuthCallback;
import com.example.myapplication.data.models.auth.LinkAccountToMultipleSiginMethodsRequest;
import com.example.myapplication.data.models.auth.ManualLoginResponse;
import com.example.myapplication.data.respository.auth.AuthenticationAPI;
import com.example.myapplication.utils.GlobalUtility;


public class LinkGoogleAccountActivity extends BaseActivity {
    private Button btnLinkAccount;
    private TextView tvBackToSignIn;
    private AuthenticationAPI autApi;
    private Activity activity;
    private Context context;
    private DataStorageManager dataStoreManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_google_account);
        initViews();

        btnLinkAccount.setOnClickListener(v -> {
            String userId = getIntent().getStringExtra("UserId");
            String userEmail = getIntent().getStringExtra("UserEmail");
            autApi.linkUserAccountToGoogle(new LinkAccountToMultipleSiginMethodsRequest(userId, userEmail), new AuthCallback<ManualLoginResponse>() {
                @Override
                public void onSuccess(ManualLoginResponse response) {
                    Intent intent = new Intent(LinkGoogleAccountActivity.this, DashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    // set access token
                    dataStoreManager.putString(BuildConfig.ACCESS_TOKEN_KEY, response.getAccess_token());
                    //Start the new Activity or show it
                    startActivity(intent);
                }

                @Override
                public void onError(Throwable t) {
                    //If encountered error, show the message
                    Toast.makeText(activity, "An error occurred" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void initViews() {
        this.context = this;
        activity = new Activity();
        autApi = new AuthenticationAPI(activity);
        dataStoreManager = DataStorageManager.getInstance(context);

        this.btnLinkAccount = (Button) findViewById(R.id.btnLinkAccount);
        this.tvBackToSignIn = (TextView) findViewById(R.id.tvBackToSignIn);


    }

}
