package com.example.myapplication;


import android.app.Application;

import android.util.Log;

import androidx.work.WorkManager;

import com.example.myapplication.security.DataStorageManager;


public class App extends Application {
    private static boolean isAppInForeground = false;
    private static int activityCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize DataStoreManager
        DataStorageManager.getInstance(this);
//        cancelAllPendingWork();
    }

    private void cancelAllPendingWork() {
        WorkManager workManager = WorkManager.getInstance(this);

        // Cancel all work by tag
        workManager.cancelAllWorkByTag("SESSION_CLEANUP");

        // Cancel all work by unique name
        workManager.cancelUniqueWork("SESSION_CLEANUP_WORK");

        // Cancel all work (nuclear option)
        workManager.cancelAllWork();

        Log.d("WorkManager", "Cancelled all pending work requests");
    }
}
