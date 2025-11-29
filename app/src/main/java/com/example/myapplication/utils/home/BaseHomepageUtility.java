package com.example.myapplication.utils.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.security.DataSharedPreference;
import com.example.myapplication.security.DataStorageManager;
import com.example.myapplication.ui.activity.auth.LoginActivity;
import com.example.myapplication.utils.GlobalUtility;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class BaseHomepageUtility {
    private Context context;
    private Activity activity;

    public BaseHomepageUtility(Context context,
                               Activity activity
    ) {
        this.context = context;
        this.activity = activity;
    }

    public void navigateToLogin() {
        //navigate to login activity
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        activity.finish();
    }

}
