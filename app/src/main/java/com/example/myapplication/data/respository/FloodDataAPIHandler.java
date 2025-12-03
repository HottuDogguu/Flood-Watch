package com.example.myapplication.data.respository;

import android.app.Activity;
import android.content.Context;

import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.models.api_response.FiveWeatherForecast;
import com.example.myapplication.data.models.api_response.ListOfNotificationResponse;
import com.example.myapplication.data.models.api_response.NewsAPIResponse;
import com.example.myapplication.data.network.APIBuilder;
import com.example.myapplication.data.network.endpoints.flood.FloodData;
import com.example.myapplication.data.network.endpoints.flood.FloodWeatherNotification;

import com.example.myapplication.utils.GlobalUtility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FloodDataAPIHandler extends BaseRepository{


    private Activity activity;
    private Context context;
    private APIBuilder api;
    private GlobalUtility globalUtility;
    private FloodWeatherNotification floodWeatherNotification;



    public FloodDataAPIHandler(Activity activity, Context context) {
        super(activity,context);
        this.context = context;
        this.activity = activity;
        this.api = new APIBuilder(context);
        this.globalUtility = new GlobalUtility();

    }

    public void getCurrentFloodData(ResponseCallback<ApiSuccessfulResponse> callback){
        FloodData floodData = api.createService(FloodData.class);
        floodData.getLatestFloodData().enqueue(new Callback<ApiSuccessfulResponse>() {
            @Override
            public void onResponse(Call<ApiSuccessfulResponse> call, Response<ApiSuccessfulResponse> response) {

                globalUtility.parseAPIResponse(response,callback);
                //check for refresh token in headers
                setRefreshTokenToDataStorage(response);
            }

            @Override
            public void onFailure(Call<ApiSuccessfulResponse> call, Throwable t) {
            callback.onError(t);
            }
        });
    }
    public void getPaginatedNotifications(int skip, int limit, ResponseCallback<ListOfNotificationResponse> callback){
        floodWeatherNotification = api.createService(FloodWeatherNotification.class);
        floodWeatherNotification.getRecentNotification(skip, limit).enqueue(new Callback<ListOfNotificationResponse>() {
            @Override
            public void onResponse(Call<ListOfNotificationResponse> call, Response<ListOfNotificationResponse> response) {

                globalUtility.parseAPIResponse(response,callback);
                //check for refresh token in headers
                setRefreshTokenToDataStorage(response);
            }

            @Override
            public void onFailure(Call<ListOfNotificationResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
    public void getFiveHoursWeatherForecast(ResponseCallback<FiveWeatherForecast> callback){
        floodWeatherNotification = api.createService(FloodWeatherNotification.class);
        floodWeatherNotification.getFiveHoursWeatherForecast().enqueue(new Callback<FiveWeatherForecast>() {
            @Override
            public void onResponse(Call<FiveWeatherForecast> call, Response<FiveWeatherForecast> response) {

                globalUtility.parseAPIResponse(response,callback);
                //check for refresh token in headers
                setRefreshTokenToDataStorage(response);

            }
            @Override
            public void onFailure(Call<FiveWeatherForecast> call, Throwable t) {
                callback.onError(t);
            }
        });

    }


    public void getInitialWeatherForecastData(){

    }


}
