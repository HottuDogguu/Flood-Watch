package com.example.myapplication.data.respository;

import android.app.Activity;
import android.content.Context;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.api_response.AdminDashboardApiResponse;
import com.example.myapplication.data.models.api_response.ApiUsesInformationResponse;
import com.example.myapplication.data.network.APIBuilder;
import com.example.myapplication.data.network.endpoints.AdminEndpoints;
import com.example.myapplication.data.network.endpoints.AuthenticationEndpoints;
import com.example.myapplication.data.network.endpoints.FloodWeatherNotificationEndpoints;
import com.example.myapplication.utils.GlobalUtility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAPIRequestHandler extends BaseRepository {

    private Activity activity;
    private APIBuilder api;
    private GlobalUtility globalUtility;
    private AuthenticationEndpoints authenticationEndpoint;
    private AdminEndpoints adminEndpoints;
    private FloodWeatherNotificationEndpoints weatherNotification;
    private Context context;
    private String ACCESS_TOKEN_KEY;

    public AdminAPIRequestHandler(Activity activity, Context context) {
        super(activity, context);
        this.context = context;
        this.activity = activity;
        this.api = new APIBuilder(context);
        this.globalUtility = new GlobalUtility();
        ACCESS_TOKEN_KEY = globalUtility.getValueInYAML(BuildConfig.ACCESS_TOKEN_KEY, context);

    }


    public void getAdminDashboardData(ResponseCallback<AdminDashboardApiResponse> callback){
        adminEndpoints = api.createService(AdminEndpoints.class);
        adminEndpoints.getAdminDashboard().enqueue(new Callback<AdminDashboardApiResponse>() {
            @Override
            public void onResponse(Call<AdminDashboardApiResponse> call, Response<AdminDashboardApiResponse> response) {
                globalUtility.parseAPIResponse(response,callback);
            }

            @Override
            public void onFailure(Call<AdminDashboardApiResponse> call, Throwable t) {

            }
        });
    }

    /**
     * To get the users information for admin side
     * @param skip to get the data from specific offset
     * @param limit to limit the result of the data
     * @param callback to handle the response when it's error or success
     */
    public void getUsersInformation(int skip, int limit,ResponseCallback<ApiUsesInformationResponse> callback){
        adminEndpoints = api.createService(AdminEndpoints.class);
        adminEndpoints.getUserInformation(skip, limit).enqueue(new Callback<ApiUsesInformationResponse>() {
            @Override
            public void onResponse(Call<ApiUsesInformationResponse> call, Response<ApiUsesInformationResponse> response) {
                globalUtility.parseAPIResponse(response,callback);
            }

            @Override
            public void onFailure(Call<ApiUsesInformationResponse> call, Throwable t) {

            }
        });
    }


}
