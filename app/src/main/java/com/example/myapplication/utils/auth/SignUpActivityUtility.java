package com.example.myapplication.utils.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.security.DataSharedPreference;
import com.example.myapplication.ui.activity.auth.EmailVerificationActivity;
import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.auth.SignupPostRequest;
import com.example.myapplication.data.respository.AuthenticationAPIRequestHandler;
import com.example.myapplication.utils.GlobalUtility;

public class SignUpActivityUtility extends BaseAuthUtility {
    private AuthenticationAPIRequestHandler auth;
    private GlobalUtility globalUtility;
    private DataSharedPreference dataSharedPreference;
    private Context context;

    public SignUpActivityUtility(Context context,
                                 Activity activity,
                                 AuthenticationAPIRequestHandler auth,
                                 GlobalUtility globalUtility,
                                 DataSharedPreference dataSharedPreference) {
        super(context,activity);
        this.auth = auth;
        this.context = context;
        this.globalUtility = globalUtility;
        this.dataSharedPreference = dataSharedPreference;
    }

    public void signUpUser(SignupPostRequest.User user,
                           SignupPostRequest.Address address) {
        SignupPostRequest postRequest = new SignupPostRequest(user, address);
        this.auth.manualSignUp(postRequest, new ResponseCallback<ApiSuccessfulResponse>() {
            @Override
            public void onSuccess(ApiSuccessfulResponse response) {
                String accessTokenKey = globalUtility.getValueInYAML(BuildConfig.ACCESS_TOKEN_KEY, context);
                dataSharedPreference.saveData(accessTokenKey, response.getAccess_token());
                Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, EmailVerificationActivity.class);
                intent.putExtra("UserEmail", user.getEmail());
                context.startActivity(intent);
            }
            @Override
            public void onError(Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
