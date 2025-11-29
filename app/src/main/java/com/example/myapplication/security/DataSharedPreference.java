package com.example.myapplication.security;

import android.content.Context;
import android.content.SharedPreferences;

public class DataSharedPreference {

    private final String SHARED_PREFERENCE_KEY = "a7783946-6d0a-47db-952d-06db0fde0e9a54840048-7516-45af-96c6-a82824268745-a82824268745";
    private static DataSharedPreference instance;
    private SharedPreferences prefs;


    // Private constructor
    private DataSharedPreference(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(SHARED_PREFERENCE_KEY, Context.MODE_PRIVATE);

    }

    public static DataSharedPreference getInstance(Context context) {
        if (instance == null) {
            instance = new DataSharedPreference(context);
        }
        return instance;
    }

    public void saveData(String key, String data) {
        prefs.edit().putString(key,data).apply();
    }

    public String getData(String key) {
        return prefs.getString(key, "");
    }

    public void clearPreference(){
        prefs.edit().clear().apply();
    }
}
