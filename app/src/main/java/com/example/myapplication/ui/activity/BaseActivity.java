package com.example.myapplication.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.security.DataStorageManager;

public class BaseActivity extends AppCompatActivity {
    protected DataStorageManager dataStoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataStoreManager = DataStorageManager.getInstance(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
