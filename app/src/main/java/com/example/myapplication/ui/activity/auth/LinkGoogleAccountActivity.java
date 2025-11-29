package com.example.myapplication.ui.activity.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.security.DataSharedPreference;

import com.example.myapplication.ui.activity.BaseActivity;
import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.auth.LinkAccountToMultipleSiginMethodsRequest;
import com.example.myapplication.data.respository.AuthenticationAPIRequestHandler;
import com.example.myapplication.ui.activity.home.HomeActivity;
import com.example.myapplication.utils.GlobalUtility;

import java.util.Objects;


public class LinkGoogleAccountActivity extends BaseActivity {
    private Button btnLinkAccount;
    private TextView tvBackToSignIn;
    private AuthenticationAPIRequestHandler autApi;
    private Activity activity;
    private Context context;
    private final String TAG = "LINK_GOOGLE_ACCOUNT_ACTIVITY";
    private DataSharedPreference dataSharedPreference;
    private GlobalUtility globalUtility;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_google_account);
        initViews();

        btnLinkAccount.setOnClickListener(v -> {
            String userId = getIntent().getStringExtra("UserId");
            String userEmail = getIntent().getStringExtra("UserEmail");
            autApi.linkUserAccountToGoogle(new LinkAccountToMultipleSiginMethodsRequest(userId, userEmail), new ResponseCallback<ApiSuccessfulResponse>() {
                @Override
                public void onSuccess(ApiSuccessfulResponse response) {
                    // set access token
                    //Start the new Activity or show it
                    String accessTokenKey = globalUtility.getValueInYAML(BuildConfig.ACCESS_TOKEN_KEY, context);
                    dataSharedPreference.saveData(accessTokenKey, response.getAccess_token());
                    Intent intent = new Intent(LinkGoogleAccountActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(Throwable t) {
                    //If encountered error, show the message
                    Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, Objects.requireNonNull(t.getMessage()));
                }
            });
        });

        tvBackToSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(LinkGoogleAccountActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //Start the new Activity
            startActivity(intent);
        });
    }


    private void initViews() {
        this.context = this;
        activity = new Activity();
        autApi = new AuthenticationAPIRequestHandler(activity, context);
        dataSharedPreference = DataSharedPreference.getInstance(context);
        globalUtility = new GlobalUtility();


        this.btnLinkAccount = (Button) findViewById(R.id.btnLinkAccount);
        this.tvBackToSignIn = (TextView) findViewById(R.id.tvBackToSignIn);


    }

}
