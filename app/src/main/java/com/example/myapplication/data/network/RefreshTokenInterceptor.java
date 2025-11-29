package com.example.myapplication.data.network;

import android.content.Context;
import android.util.Log;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.network.endpoints.auth.AuthenticationEndpoint;
import com.example.myapplication.security.DataSharedPreference;
import com.example.myapplication.utils.GlobalUtility;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;

public class RefreshTokenInterceptor implements Interceptor {

    private final DataSharedPreference storageManager;
    private final AuthenticationEndpoint authService;
    private final String accessTokenKey;

    private final Context context;
    private final GlobalUtility globalUtility;
    private final Map<String, Object> routeLocks = new ConcurrentHashMap<>();


    public RefreshTokenInterceptor(Context context, DataSharedPreference storageManager, AuthenticationEndpoint authService) {
        this.context = context;
        this.storageManager = storageManager;
        this.authService = authService;

        globalUtility = new GlobalUtility();
        this.accessTokenKey = globalUtility.getValueInYAML(BuildConfig.ACCESS_TOKEN_KEY, context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request originalRequest = chain.request();

        String routeKey = originalRequest.url().encodedPath(); // lock per route

        // Create lock object for this route if not exist
        routeLocks.putIfAbsent(routeKey, new Object());
        Object lock = routeLocks.get(routeKey);

        synchronized (Objects.requireNonNull(lock)) {
            // Add access token
            String token = storageManager.getData(accessTokenKey);
            if (token != null && !token.isEmpty()) {
                originalRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + token)
                        .build();
            }

            Response response = chain.proceed(originalRequest);

            // Handle 401
            if (response.code() == 401) {
                Response original401Response = response;

                String refreshToken = storageManager.getData(accessTokenKey);
                if (refreshToken != null && !refreshToken.isEmpty()) {
                    try {
                        retrofit2.Response<ApiSuccessfulResponse> refreshResponse =
                                authService.refreshToken(refreshToken).execute();

                        if (refreshResponse.isSuccessful() && refreshResponse.body() != null) {
                            String newAccessToken = refreshResponse.body().getAccess_token();
                            storageManager.saveData(accessTokenKey, newAccessToken);

                            // Retry original request with new token
                            Request retryRequest = originalRequest.newBuilder()
                                    .removeHeader("Authorization")
                                    .header("Authorization", "Bearer " + newAccessToken)
                                    .build();

                            original401Response.close();
                            return chain.proceed(retryRequest);
                        } else {
                            return original401Response;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return original401Response;
                    }
                } else {
                    return original401Response;
                }
            }

            return response;
        }
    }
}
