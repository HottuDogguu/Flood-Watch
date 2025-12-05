package com.example.myapplication.data.network;

import android.content.Context;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.security.DataSharedPreference;
import com.example.myapplication.utils.GlobalUtility;
import com.example.myapplication.data.network.endpoints.AuthenticationEndpoints;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIBuilder {

    private static Retrofit retrofit;

    public APIBuilder(Context context) {
        DataSharedPreference prefs = DataSharedPreference.getInstance(context);
        GlobalUtility utility = new GlobalUtility();

        String BASE_URL = utility.getValueInYAML(BuildConfig.API_HTTP_BASE_URL, context);

        if (retrofit == null) {

            // Service used for calling refresh-token inside interceptor
            AuthenticationEndpoints authService =
                    new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                            .create(AuthenticationEndpoints.class);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(userAgentInterceptor())
                    .addInterceptor(new RefreshTokenInterceptor(context, prefs, authService))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }

    private Interceptor userAgentInterceptor() {
        return chain -> chain.proceed(
                chain.request().newBuilder()
                        .header("User-Agent", android.os.Build.MANUFACTURER + " " +
                                android.os.Build.MODEL + " (Android " + android.os.Build.VERSION.RELEASE + ")")
                        .build()
        );
    }

    public <T> T createService(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
