package com.example.myapplication.data.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIBuilder {
    private Retrofit retrofit;

    public APIBuilder(){
        this.retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.7.41:9898/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public Retrofit getRetrofit() {
        return retrofit;
    }
}
