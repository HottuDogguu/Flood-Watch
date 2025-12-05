package com.example.myapplication.data.network;

import android.content.Context;


import com.example.myapplication.BuildConfig;
import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.network.endpoints.AuthenticationEndpoints;
import com.example.myapplication.security.DataSharedPreference;
import com.example.myapplication.utils.GlobalUtility;

import java.io.IOException;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RefreshTokenInterceptor implements Interceptor {

    private final DataSharedPreference storageManager;
    private final AuthenticationEndpoints authService;
    private final String accessTokenKey;

    private final Context context;
    private final GlobalUtility globalUtility;
    private final Map<String, Object> routeLocks = new ConcurrentHashMap<>();
    private Object refreshLock = new Object();


    public RefreshTokenInterceptor(Context context, DataSharedPreference storageManager, AuthenticationEndpoints authService) {
        this.context = context;
        this.storageManager = storageManager;
        this.authService = authService;

        globalUtility = new GlobalUtility();
        this.accessTokenKey = globalUtility.getValueInYAML(BuildConfig.ACCESS_TOKEN_KEY, context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {


        //synchronized it, so that it will handle route one at a time
        synchronized (routeLocks) {
            //get the current request
            Request originalRequest = chain.request();

            //get the access token
            String accessToken = storageManager.getData(accessTokenKey);
            //check if the token is not null or not empty
            if (accessToken != null && !accessToken.isEmpty()) {
                //then set authorization header to the original request
                originalRequest = originalRequest.newBuilder()
                        .removeHeader("Authorization")
                        .header("Authorization", "Bearer " + accessToken)
                        .build();
            }

            //then get the response of the original request
            Response response = chain.proceed(originalRequest);

            // check if the token is expired
            if (response.code() == 401) {

                //then get the current access token
                String refreshToken = storageManager.getData(accessTokenKey);
                //check if not null or not empty
                if (refreshToken != null && !refreshToken.isEmpty()) {
                    try {
                        //check if the refresh token is not equal to previous access token
                        if (!refreshToken.equals(accessToken)) {
                            // Already refreshed
                            Request retryRequest = originalRequest.newBuilder()
                                    .removeHeader("Authorization")
                                    .header("Authorization", "Bearer " + refreshToken)
                                    .build();

                            return chain.proceed(retryRequest);
                        }

                        //call the refresh token route to get the new access token
                        retrofit2.Response<ApiSuccessfulResponse> refreshResponse =
                                authService.refreshToken(refreshToken).execute();

                        //check if the response is successful
                        if (refreshResponse.isSuccessful() && refreshResponse.body() != null) {
                            //then store the new access token in preference
                            String newAccessToken = refreshResponse.body().getAccess_token();
                            storageManager.saveData(accessTokenKey, newAccessToken);

                            // Retry original request with new token
                            Request retryRequest = originalRequest.newBuilder()
                                    .removeHeader("Authorization")
                                    .header("Authorization", "Bearer " + newAccessToken)
                                    .build();
                            //close the original response
                            response.close();
                            //then return the original request with a fresh access token
                            return chain.proceed(retryRequest);
                        } else {
                            //if encounter error, return the original response
                            return response;
                        }
                    } catch (Exception e) {
                        //if encounter error, return the original response
                        e.printStackTrace();
                        return response;
                    }
                } else {
                    //if not 401, then return the response
                    return response;
                }
            }
            //if not 401, then return the response
            return response;
        }
    }


}
