package com.example.myapplication.data.network;

import android.content.Context;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.utils.GlobalUtility;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIBuilder {
    private Retrofit retrofit;
    private GlobalUtility utility;
    private Context context;

    public APIBuilder(Context context) {
        this.context = context;
        String manufacturer = android.os.Build.MANUFACTURER;
        String model = android.os.Build.MODEL;
        String androidVersion = android.os.Build.VERSION.RELEASE;
        String deviceName = manufacturer + " " + model;

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("User-Agent", deviceName + " (Android " + androidVersion + ")")
                                .build();
                        return chain.proceed(request);
                    }
                })
                .build();
        utility = new GlobalUtility();
        String BASE_HTTP_URL = utility.getValueInYAML(
                BuildConfig.API_HTTP_BASE_URL, context);
        this.retrofit = new Retrofit.Builder()
                .baseUrl(BASE_HTTP_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
