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
import com.example.myapplication.data.network.endpoints.flood.NewsPaginated;
import com.example.myapplication.data.network.endpoints.flood.Notifications;
import com.example.myapplication.data.network.endpoints.flood.WeatherForecastFiveHours;
import com.example.myapplication.utils.GlobalUtility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FloodDataAPIHandler {


    private Activity activity;
    private Context context;
    private APIBuilder api;
    private GlobalUtility globalUtility;

    public FloodDataAPIHandler(Activity activity, Context context) {
        this.context = context;
        this.activity = activity;
        this.api = new APIBuilder(context);
        this.globalUtility = new GlobalUtility();

    }

    public void getCurrentFloodData(ResponseCallback<ApiSuccessfulResponse> callback){
        FloodData floodData = api.getRetrofit().create(FloodData.class);
        floodData.getLatestFloodData().enqueue(new Callback<ApiSuccessfulResponse>() {
            @Override
            public void onResponse(Call<ApiSuccessfulResponse> call, Response<ApiSuccessfulResponse> response) {
                globalUtility.parseAPIResponse(response,callback);
            }

            @Override
            public void onFailure(Call<ApiSuccessfulResponse> call, Throwable t) {
            callback.onError(t);
            }
        });
    }
    public void getPaginatedNotifications(int skip, int limit, ResponseCallback<ListOfNotificationResponse> callback){
        Notifications notifications = api.getRetrofit().create(Notifications.class);
        notifications.getRecentNotification(skip, limit).enqueue(new Callback<ListOfNotificationResponse>() {
            @Override
            public void onResponse(Call<ListOfNotificationResponse> call, Response<ListOfNotificationResponse> response) {
                globalUtility.parseAPIResponse(response,callback);
            }

            @Override
            public void onFailure(Call<ListOfNotificationResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
    public void getFiveHoursWeatherForecast(ResponseCallback<FiveWeatherForecast> callback){
        WeatherForecastFiveHours weatherForecastFiveHours = api.getRetrofit().create(WeatherForecastFiveHours.class);
        weatherForecastFiveHours.getFiveHoursWeatherForecast().enqueue(new Callback<FiveWeatherForecast>() {
            @Override
            public void onResponse(Call<FiveWeatherForecast> call, Response<FiveWeatherForecast> response) {
                globalUtility.parseAPIResponse(response,callback);

            }
            @Override
            public void onFailure(Call<FiveWeatherForecast> call, Throwable t) {
                callback.onError(t);
            }
        });

    }
    public void getNewsPaginated(int skip, int limit, ResponseCallback<NewsAPIResponse> callback){
        NewsPaginated newsPaginated = api.getRetrofit().create(NewsPaginated.class);
        newsPaginated.getTenNews(skip,limit).enqueue(new Callback<NewsAPIResponse>() {
            @Override
            public void onResponse(Call<NewsAPIResponse> call, Response<NewsAPIResponse> response) {
                globalUtility.parseAPIResponse(response,callback);

            }
            @Override
            public void onFailure(Call<NewsAPIResponse> call, Throwable t) {
                callback.onError(t);
            }
        });

    }

}
