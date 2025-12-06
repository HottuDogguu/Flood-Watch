package com.example.myapplication.data.respository;

import android.app.Activity;
import android.content.Context;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.admin.NewsPostRequestModel;
import com.example.myapplication.data.models.admin.UsersPutRequestModel;
import com.example.myapplication.data.models.api_response.AdminDashboardApiResponse;
import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.models.api_response.ApiUsesInformationResponse;
import com.example.myapplication.data.models.api_response.NewsAPIResponse;
import com.example.myapplication.data.network.APIBuilder;
import com.example.myapplication.data.network.endpoints.AdminEndpoints;
import com.example.myapplication.data.network.endpoints.AuthenticationEndpoints;
import com.example.myapplication.data.network.endpoints.FloodWeatherNotificationEndpoints;
import com.example.myapplication.utils.GlobalUtility;
import com.example.myapplication.utils.MultiPartUtility;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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


    public void getAdminDashboardData(ResponseCallback<AdminDashboardApiResponse> callback) {
        adminEndpoints = api.createService(AdminEndpoints.class);
        adminEndpoints.getAdminDashboard().enqueue(new Callback<AdminDashboardApiResponse>() {
            @Override
            public void onResponse(Call<AdminDashboardApiResponse> call, Response<AdminDashboardApiResponse> response) {
                globalUtility.parseAPIResponse(response, callback);
            }

            @Override
            public void onFailure(Call<AdminDashboardApiResponse> call, Throwable t) {

            }
        });
    }

    /**
     * To get the users information for admin side
     *
     * @param skip     to get the data from specific offset
     * @param limit    to limit the result of the data
     * @param callback to handle the response when it's error or success
     */
    public void getUsersInformation(int skip, int limit, ResponseCallback<ApiUsesInformationResponse> callback) {
        adminEndpoints = api.createService(AdminEndpoints.class);
        adminEndpoints.getUserInformation(skip, limit).enqueue(new Callback<ApiUsesInformationResponse>() {
            @Override
            public void onResponse(Call<ApiUsesInformationResponse> call, Response<ApiUsesInformationResponse> response) {
                globalUtility.parseAPIResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiUsesInformationResponse> call, Throwable t) {

            }
        });
    }


    public void createNews(NewsPostRequestModel request, ResponseCallback<NewsAPIResponse> callback) {
        adminEndpoints = api.createService(AdminEndpoints.class);
        List<MultipartBody.Part> imageParts =
                MultiPartUtility.prepareImageList(request.images);

        List<RequestBody> tagParts =
                MultiPartUtility.prepareTags(request.tags);

        if (imageParts.isEmpty()) {
            imageParts = null;
        }
        adminEndpoints.createNews(MultiPartUtility.toRequestBody(request.title),
                MultiPartUtility.toRequestBody(request.content),
                MultiPartUtility.toRequestBody(request.status),
                imageParts,
                tagParts,
                MultiPartUtility.toRequestBody(request.source)).enqueue(new Callback<NewsAPIResponse>() {
            @Override
            public void onResponse(Call<NewsAPIResponse> call, Response<NewsAPIResponse> response) {
                globalUtility.parseAPIResponse(response, callback);
            }

            @Override
            public void onFailure(Call<NewsAPIResponse> call, Throwable t) {

            }
        });

    }


    /**
     * This is a function to handle the api call, which is the update news.
     *
     * @param newsId   is the unique identifier of news. It serves as the basis to update the news.
     * @param request  it is serves as the request body.
     * @param callback it will handle the successful and error response.
     */
    public void updateNews(String newsId, NewsPostRequestModel request, ResponseCallback<NewsAPIResponse> callback) {
        adminEndpoints = api.createService(AdminEndpoints.class);

        List<MultipartBody.Part> imageParts =
                MultiPartUtility.prepareImageList(request.images);

        List<RequestBody> tagParts =
                MultiPartUtility.prepareTags(request.tags);

        if (imageParts.isEmpty()) {
            imageParts = null;
        }
        adminEndpoints.updateNews(newsId,
                        MultiPartUtility.toRequestBody(request.title),
                        MultiPartUtility.toRequestBody(request.content),
                        MultiPartUtility.toRequestBody(request.status),
                        imageParts,
                        tagParts,
                        MultiPartUtility.toRequestBody(request.source))
                .enqueue(new Callback<NewsAPIResponse>() {
                    @Override
                    public void onResponse(Call<NewsAPIResponse> call, Response<NewsAPIResponse> response) {
                        globalUtility.parseAPIResponse(response, callback);
                    }

                    @Override
                    public void onFailure(Call<NewsAPIResponse> call, Throwable t) {

                    }
                });
    }

    public void deleteNews(String newsId, ResponseCallback<NewsAPIResponse> callback){
        adminEndpoints = api.createService(AdminEndpoints.class);
        adminEndpoints.deleteNews(newsId).enqueue(new Callback<NewsAPIResponse>() {
            @Override
            public void onResponse(Call<NewsAPIResponse> call, Response<NewsAPIResponse> response) {
                globalUtility.parseAPIResponse(response,callback);
            }

            @Override
            public void onFailure(Call<NewsAPIResponse> call, Throwable t) {

            }
        });
    }
    public void updateUserInformation(String newsId, UsersPutRequestModel request, ResponseCallback<ApiSuccessfulResponse> callback){
        adminEndpoints = api.createService(AdminEndpoints.class);
        adminEndpoints.updateUserInformation(newsId,request).enqueue(new Callback<ApiSuccessfulResponse>() {
            @Override
            public void onResponse(Call<ApiSuccessfulResponse> call, Response<ApiSuccessfulResponse> response) {
                globalUtility.parseAPIResponse(response,callback);
            }

            @Override
            public void onFailure(Call<ApiSuccessfulResponse> call, Throwable t) {

            }
        });
    }

    public void deleteUserInformation(String newsId, ResponseCallback<ApiSuccessfulResponse> callback){
        adminEndpoints = api.createService(AdminEndpoints.class);
        adminEndpoints.deleteUserInformation(newsId).enqueue(new Callback<ApiSuccessfulResponse>() {
            @Override
            public void onResponse(Call<ApiSuccessfulResponse> call, Response<ApiSuccessfulResponse> response) {
                globalUtility.parseAPIResponse(response,callback);
            }
            @Override
            public void onFailure(Call<ApiSuccessfulResponse> call, Throwable t) {

            }
        });
    }




}
