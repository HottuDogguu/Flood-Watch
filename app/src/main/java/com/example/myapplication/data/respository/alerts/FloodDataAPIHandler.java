package com.example.myapplication.data.respository.alerts;

import android.app.Activity;
import android.content.Context;

import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.models.api_response.ListOfNotificationResponse;
import com.example.myapplication.data.network.APIBuilder;
import com.example.myapplication.data.network.endpoints.alerts.FloodData;
import com.example.myapplication.data.network.endpoints.alerts.Notifications;
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
    public void getThreeRecentNotifications(ResponseCallback<ListOfNotificationResponse> callback){
        Notifications notifications = api.getRetrofit().create(Notifications.class);
        notifications.getRecentNotification().enqueue(new Callback<ListOfNotificationResponse>() {
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
}
