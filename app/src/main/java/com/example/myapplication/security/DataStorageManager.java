package com.example.myapplication.security;

import android.content.Context;
import android.util.Log;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import com.example.myapplication.BuildConfig;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DataStorageManager {
    private static DataStorageManager instance;
    private final RxDataStore<Preferences> dataStore;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    public static final String DATASTORE_NAME = "DataStorage";
    public static final Preferences.Key<Long> KEY_LAST_ACTIVITY_TIME = PreferencesKeys.longKey("last_activity_time");
    public static final Preferences.Key<Boolean> KEY_SESSION_TIMER_ENABLED = PreferencesKeys.booleanKey("session_timer_enabled");
    public static final Preferences.Key<Boolean> KEY_IS_LOGGED_IN = PreferencesKeys.booleanKey("is_logged_in");
    private Context context;

    // Private constructor to prevent direct instantiation
    private DataStorageManager(Context context) {
        this.context = context.getApplicationContext();
        dataStore = new RxPreferenceDataStoreBuilder(context, DATASTORE_NAME).build();
    }

    /**
     * This is a Singleton Design to ensure that only one instance of object only.
     * @param context of a specific Activity

     */
    public static synchronized DataStorageManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataStorageManager(context.getApplicationContext());
        }
        return instance;
    }

    // Update last activity time
//    public void updateLastActivityTime() {
//        long currentTime = System.currentTimeMillis();
//        putLong(KEY_LAST_ACTIVITY_TIME, currentTime);
//
//        // Enable session timer if not already enabled
//        enableSessionTimer();
//    }

    // Enable session timer
//    public void enableSessionTimer() {
//        putBoolean(KEY_SESSION_TIMER_ENABLED, true);
//
//        // Schedule the cleanup worker
//        SessionCleanupWorker.scheduleCleanupWorker(context);
//    }

    // Disable session timer (call this when user manually logs out)
//    public void disableSessionTimer() {
//        putBoolean(KEY_SESSION_TIMER_ENABLED, false);
//
//        // Cancel any pending workers
//        SessionCleanupWorker.cancelCleanupWorker(context);
//    }

    // Check if session is expired
    public Flowable<Boolean> isSessionExpired() {
        return getLong(KEY_LAST_ACTIVITY_TIME, 0L)
                .map(lastActivityTime -> {
                    if (lastActivityTime == 0L) return true; // No activity recorded

                    long currentTime = System.currentTimeMillis();
                    long twoHoursInMillis = 2 * 60 * 60 * 1000L; // 2 hours in milliseconds

                    return (currentTime - lastActivityTime) > twoHoursInMillis;
                });
    }

    // String operations
    public void putString(String key, String value) {
        Preferences.Key<String> KEY = PreferencesKeys.stringKey(key);
        compositeDisposable.add(
                dataStore.updateDataAsync(preferences -> {
                            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
                            mutablePreferences.set(KEY, value);
                            return Single.just(mutablePreferences);
                        })
                        .subscribeOn(Schedulers.io())
                        .subscribe()
        );
    }

    public Flowable<String> getString(String key) {
        Preferences.Key<String> KEY = PreferencesKeys.stringKey(key);
        return dataStore.data()
                .map(preferences -> preferences.get(KEY) != null ? preferences.get(KEY) : "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()); // Observe on main thread;
    }

    public void putLong(Preferences.Key<Long> key, long value) {
        compositeDisposable.add(
                dataStore.updateDataAsync(preferences -> {
                            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
                            mutablePreferences.set(key, value);
                            return Single.just(mutablePreferences);
                        })
                        .subscribeOn(Schedulers.io())
                        .subscribe()
        );
    }

    public Flowable<Long> getLong(Preferences.Key<Long> key, long defaultValue) {
        return dataStore.data()
                .map(preferences -> preferences.get(key) != null ? preferences.get(key) : defaultValue)
                .subscribeOn(Schedulers.io());
    }

    public void putBoolean(Preferences.Key<Boolean> key, boolean value) {
        compositeDisposable.add(
                dataStore.updateDataAsync(preferences -> {
                            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
                            mutablePreferences.set(key, value);
                            return Single.just(mutablePreferences);
                        })
                        .subscribeOn(Schedulers.io())
                        .subscribe()
        );
    }

    public Flowable<Boolean> getBoolean(Preferences.Key<Boolean> key, boolean defaultValue) {
        return dataStore.data()
                .map(preferences -> preferences.get(key) != null ? preferences.get(key) : defaultValue)
                .subscribeOn(Schedulers.io());
    }

    // Remove key
    public void remove(String key) {
        Preferences.Key<String> KEY = PreferencesKeys.stringKey(key);

        compositeDisposable.add(
                dataStore.updateDataAsync(preferences -> {
                            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
                            mutablePreferences.remove(KEY);
                            return Single.just(mutablePreferences);
                        })
                        .subscribeOn(Schedulers.io())
                        .subscribe()
        );
    }

    // Clear all data
    public void clearAll() {
        compositeDisposable.add(
                dataStore.updateDataAsync(preferences -> {
                            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
                            mutablePreferences.clear();
                            return Single.just(mutablePreferences);
                        })
                        .subscribeOn(Schedulers.io())
                        .subscribe()
        );
    }

    // Check if key exists
    public Flowable<Boolean> contains(String key) {
        Preferences.Key<String> KEY = PreferencesKeys.stringKey(key);

        return dataStore.data()
                .map(preferences -> preferences.get(KEY) != null)
                .subscribeOn(Schedulers.io());
    }

    public void dispose() {
        compositeDisposable.clear();
    }

}
