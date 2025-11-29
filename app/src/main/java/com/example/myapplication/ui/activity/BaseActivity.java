package com.example.myapplication.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.security.DataSharedPreference;

import com.example.myapplication.utils.GlobalUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class BaseActivity extends AppCompatActivity {
    protected DataSharedPreference dataSharedPreference;
    protected GlobalUtility globalUtility;
    protected Context context;
    protected Activity activity;
    protected CompositeDisposable compositeDisposable;

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        activity = this;
        dataSharedPreference = DataSharedPreference.getInstance(context);
        globalUtility = new GlobalUtility();
        compositeDisposable = new CompositeDisposable();
//        initializeFirebaseAndGetToken();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        globalUtility.hideSystemUI(activity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            globalUtility.hideSystemUI(activity);
        }
    }

    private void initializeFirebaseAndGetToken() {
        try {
            // Check if Firebase app is initialized
            if (FirebaseApp.getApps(this).isEmpty()) {
                Log.d("FCM_NOTIF", "Firebase not initialized yet, delaying token retrieval");
                // Retry after short delay
                getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getFCMToken();
                    }
                }, 1000);
            } else {
                getFCMToken();
            }
        } catch (Exception e) {
            Log.e("FCM_NOTIF", "Firebase initialization error: " + e.getMessage());
        }
    }

    private void getFCMToken() {
        try {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w("FCM_NOTIF", "Fetching FCM registration token failed", task.getException());
                            }
                            // Get new FCM registration token
                            String token = task.getResult();
                            Log.d("FCM_NOTIF", "FCM Token: " + token);
                        }
                    });
        } catch (Exception e) {
            Log.e("FCM_NOTIF", "Error in getFCMToken: " + e.getMessage());
        }
    }

}


