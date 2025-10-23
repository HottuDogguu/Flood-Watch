package com.example.myapplication.data.network;

import com.example.myapplication.utils.GlobalUtility;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIBuilder {
    private Retrofit retrofit;
    private GlobalUtility utility;

    public APIBuilder(){
        String manufacturer = android.os.Build.MANUFACTURER;
        String model = android.os.Build.MODEL;
        String androidVersion = android.os.Build.VERSION.RELEASE;
        String deviceName = manufacturer + " " + model;

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException, IOException {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("User-Agent", deviceName + " (Android " + androidVersion + ")")
                                .build();
                        return chain.proceed(request);
                    }
                })
                .build();
        utility = new GlobalUtility();
        this.retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.7.41:9898/api/fms/v1/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public Retrofit getRetrofit() {
        return retrofit;
    }
}
