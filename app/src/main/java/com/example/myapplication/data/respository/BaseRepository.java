package com.example.myapplication.data.respository;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.security.DataSharedPreference;

import com.example.myapplication.utils.GlobalUtility;

import java.util.Objects;

import okhttp3.Headers;
import retrofit2.Response;

public class BaseRepository {

    private DataSharedPreference dataSharedPreference;
    private GlobalUtility globalUtility;
    private Activity activity;
    private Context context;


    public BaseRepository(Activity activity, Context context){
        this.context = context;
        this.activity = activity;
        this.globalUtility = new GlobalUtility();
        this.dataSharedPreference = DataSharedPreference.getInstance(context);
    }


    public <T> void setRefreshTokenToDataStorage(Response<T> response) {
        // handle new access token
        String ACCESS_TOKEN_KEY = globalUtility.getValueInYAML(BuildConfig.ACCESS_TOKEN_KEY, context);
        Headers header = response.headers();
        Log.i("Headers",header.toString());
        String headerName = "X-New-Access-Token".toLowerCase();
        if (header.get(headerName) != null && !Objects.requireNonNull(header.get(headerName)).isEmpty()) {
            String newToken = header.get(headerName);
            dataSharedPreference.saveData(ACCESS_TOKEN_KEY, newToken);
        }
    }
}
