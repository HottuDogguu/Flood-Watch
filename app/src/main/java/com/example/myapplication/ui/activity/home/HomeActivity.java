package com.example.myapplication.ui.activity.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.calbacks.WebsocketCallback;
import com.example.myapplication.Constants;
import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.models.api_response.FiveWeatherForecast;
import com.example.myapplication.data.models.api_response.ListOfNotificationResponse;
import com.example.myapplication.data.models.api_response.NewsAPIResponse;
import com.example.myapplication.data.models.api_response.WebsocketResponseData;
import com.example.myapplication.data.network.websockets.WebsocketManager;
import com.example.myapplication.data.respository.FloodDataAPIHandler;
import com.example.myapplication.data.respository.UsersAPIRequestHandler;
import com.example.myapplication.security.DataSharedPreference;

import com.example.myapplication.ui.activity.notification.LocalNotificationManager;
import com.example.myapplication.ui.adapter.HourlyForecastAdapter;
import com.example.myapplication.ui.adapter.NewsCarouselAdapter;
import com.example.myapplication.ui.adapter.WeatherHourTimelineAdapter;
import com.example.myapplication.utils.GlobalUtility;
import com.example.myapplication.utils.home.BaseHomepageUtility;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;


public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private LocalNotificationManager localNotificationManager;
    private FloatingActionButton fabEmergency;
    private TextView tvWaterLevel;
    private TextView tvStatus;
    private TextView tvRainfall;
    private TextView tvAlerts;
    private TextView tvStation;
    private ImageView btnNotifications;
    private WeatherHourTimelineAdapter fiveHourAdapter;
    private RecyclerView rvHourlyForecast;

    // Sample data

    private GlobalUtility globalUtility;
    private Activity activity;
    private Context context;
    private DataSharedPreference dataSharedPreference;
    private UsersAPIRequestHandler apiRequesthandler;
    private FloodDataAPIHandler floodDataAPIHandler;
    private CompositeDisposable compositeDisposable;
    private String USER_DATA_KEY;
    // News Carousel Views
    private CardView cardNewsCarousel;
    private RecyclerView rvNewsCarousel;
    private NewsCarouselAdapter newsCarouselAdapter;
    private TextView tvSeeAllNews;
    private String ACCESS_TOKEN_KEY;
    private BaseHomepageUtility baseHomepageUtility;
    private List<ListOfNotificationResponse.NotificationData> alertListData;
    private List<FiveWeatherForecast.HourlyWeatherForecast> hourlyData;
    private List<NewsAPIResponse.NewsData> newsDataList;
    // Weather Timeline Views - NEW
    private TextView tvViewFullWeather;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        initViews();

        // Setup toolbar
        setupToolbar();

        // Setup bottom navigation
        setupBottomNavigation();

        // Setup FAB (Emergency button)
        setupEmergencyFab();

        requestNotificationPermission();
        btnNotifications.setOnClickListener(v -> {
            // Navigate to News Activity
            Intent intent = new Intent(HomeActivity.this, NotificationHistoryActivity.class);
            startActivity(intent);
        });
    }

    private void initViews() {
        activity = this;
        context = this;

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        fabEmergency = (FloatingActionButton) findViewById(R.id.fab_emergency);
        tvWaterLevel = (TextView) findViewById(R.id.tv_water_level);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        tvRainfall = (TextView) findViewById(R.id.tv_rainfall);
        tvStation = (TextView) findViewById(R.id.tv_station);
        btnNotifications = (ImageView) findViewById(R.id.btn_notifications);


        // Initialize News Carousel Views
        cardNewsCarousel = findViewById(R.id.card_news_carousel);
        rvNewsCarousel = findViewById(R.id.rv_news_carousel);
        tvSeeAllNews = findViewById(R.id.tv_see_all_news);

        //Init Five Forecast Carousel
        rvHourlyForecast = findViewById(R.id.rv_weather_timeline);

        dataSharedPreference = DataSharedPreference.getInstance(context);
        apiRequesthandler = new UsersAPIRequestHandler(activity, context);
        globalUtility = new GlobalUtility();
        //init homepage utility
        baseHomepageUtility = new BaseHomepageUtility(
                context,
                activity);
        compositeDisposable = new CompositeDisposable();


        floodDataAPIHandler = new FloodDataAPIHandler(activity, context);
        //KEYS
        USER_DATA_KEY = globalUtility.getValueInYAML(BuildConfig.USER_INFORMATION_KEY, context);
        ACCESS_TOKEN_KEY = globalUtility.getValueInYAML(BuildConfig.ACCESS_TOKEN_KEY, context);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupBottomNavigation() {
        // Set Home as selected by default
        bottomNavigation.setSelectedItemId(R.id.nav_home);

        bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    // Already on home, do nothing
                    return true;
                } else if (id == R.id.nav_news) {
                    // Navigate to News Activity
                    Intent intent = new Intent(HomeActivity.this, NewsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0); // No animation
                    return true;
                } else if (id == R.id.nav_emergency) {
                    // Navigate to MDRRMO Contacts (Emergency)
                    Intent intent = new Intent(HomeActivity.this, MDRRMOContactsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_weather) {
                    // Navigate to Weather Monitor Activity
                    Intent intent = new Intent(HomeActivity.this, WeatherMonitorActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_profile) {
                    // Navigate to Profile Activity
                    Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }

                return false;
            }
        });
    }


    private void setupEmergencyFab() {
        fabEmergency.setOnClickListener(v -> {
            // Navigate to MDRRMO Contacts (Emergency)
            Intent intent = new Intent(HomeActivity.this, MDRRMOContactsActivity.class);
            startActivity(intent);
        });
    }

    private void setupWeatherTimeline() {

    }

    public void setDataFromDataStorage() {
                    //Function to get the user information
                    apiRequesthandler.getUserInformation(new ResponseCallback<ApiSuccessfulResponse>() {
                        @Override
                        public void onSuccess(ApiSuccessfulResponse response) {
                            // Save new user data
                            Gson gson = new Gson();
                            String jsonData = gson.toJson(response.getData());
                            dataSharedPreference.saveData(USER_DATA_KEY, jsonData);

                            //connect to websocket
                            connectToWebSocket(response.getData().getId());
                            //get the three recent notification
                            getThreeRecentNotification();

                            //get ten data for news
                            initializedNewsData();

                            //initialized the data for weather forecast
                            initializeWeatherForecastData();
                        }

                        @Override
                        public void onError(Throwable t) {
                            Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                            //navigate to login activity
                            baseHomepageUtility.navigateToLogin();
                        }
                    });

    }

    List<FiveWeatherForecast.HourlyWeatherForecast> newData = new ArrayList<>();

    WebsocketManager manager = new WebsocketManager(new WebsocketCallback() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onMessageReceived(String message) {
            Log.i("Websocket", "Websocket received message");
            Gson gson = new Gson();
            WebsocketResponseData data = gson.<WebsocketResponseData>fromJson(message, WebsocketResponseData.class);
            String content = data.getData().getNotification_text();
            String title = data.getData().getTitle();
            String topic = data.getData().getTopic();
            String severity = data.getData().getSeverity();


            if (data.getData().getTopic().equalsIgnoreCase(Constants.FLOOD_ALERT)) {
                runOnUiThread(() -> updateCurrentWaterLevelData(data.getData().getValue(), data.getData().getSeverity()));

                //check if will notify the users
                if (data.isIs_online_users_will_notify() && data.isIs_flood_alert_on()) {
                    //Then show notif
                    LocalNotificationManager.showNotification(context, title, content, topic, severity);

                    runOnUiThread(() -> {
                        updateCurrentWaterLevelData(data.getData().getValue(), severity);
                        //update the three recent notification
                    });

                }
            }
            if (data.getData().getTopic().equals(Constants.WEATHER_ALERT)) {

                runOnUiThread(() -> {
                    List<Integer> precipitation_probability = data.getData().getPrecipitation_probability();
                    List<String> hourly_time = data.getData().getHourly_time();
                    List<Integer> temperatures = data.getData().getTemperatures();
                    List<Double> precipitation = data.getData().getPrecipitation();
                    List<Double> wind_speed = data.getData().getWind_speed();
                    List<Integer> humidity = data.getData().getHumidity();
                    newData.clear();
                    for (int i = 0; i < precipitation.size(); i++) {
                        newData.add(new FiveWeatherForecast.HourlyWeatherForecast(
                                precipitation_probability.get(i),
                                humidity.get(i),
                                temperatures.get(i),
                                wind_speed.get(i),
                                precipitation.get(i),
                                hourly_time.get(i)));
                    }
                    //then set up the recycle view
                    fiveHourAdapter.updateData(newData);
                });

                if (data.isIs_weather_updates_on()) {
                    LocalNotificationManager.showNotification(context, title, content, topic, severity);
                }
            }
            //get the three latest notif everytime it receive a message
            getThreeRecentNotification();

        }

        @Override
        public void onConnected() {
            Log.i("Websocket", "Websocket is connected");
            //retrieve the current data of Flood data
            floodDataAPIHandler.getCurrentFloodData(new ResponseCallback<ApiSuccessfulResponse>() {
                @SuppressLint({"SetTextI18n", "ResourceType"})
                @Override
                public void onSuccess(ApiSuccessfulResponse response) {
                    Toast.makeText(activity, response.getMessage(), Toast.LENGTH_SHORT).show();
                    runOnUiThread(() -> {
                        String waterStatus =  response.getData().getAlert_level();
                         waterStatus =  waterStatus != null && !waterStatus.isEmpty() ? waterStatus : "No Data";
                        String waterLevel =  String.valueOf(response.getData().getWater_level());
                        waterLevel =  !waterLevel.isEmpty() ? waterLevel : "No Data";
                        updateCurrentWaterLevelData(waterLevel,waterStatus);
                        //get the three recent notification
                        getThreeRecentNotification();
                    });
                }

                @Override
                public void onError(Throwable t) {

                }
            });

        }

        @Override
        public void onDisconnected(boolean isConnected) {
            //try to reconnect in 5 seconds if connection loss.
            manager.reconnect(isConnected);
        }

        @Override
        public void onFailureToConnect(boolean isConnected) {
            manager.reconnect(isConnected);
        }
    });


    private int getSeverityColor(String alertLevel) {
        switch (alertLevel) {
            case "Warning":
                return R.drawable.bg_status_warning;
            case "Critical":
                return R.drawable.bg_status_critical;

            case "Severe":
                return R.drawable.bg_alert_severe;
            default:
                return R.drawable.bg_status_normal;
        }
    }



    private void getThreeRecentNotification() {
        floodDataAPIHandler.getPaginatedNotifications(1, 10, new ResponseCallback<ListOfNotificationResponse>() {
            @Override
            public void onSuccess(ListOfNotificationResponse response) {
                alertListData = new ArrayList<>();
                for (ListOfNotificationResponse.NotificationData data : response.getData()) {
                    if (alertListData.size() == 3) {
                        break;
                    }

                    alertListData.add(data);

                }
                updateRecentNotification(alertListData);
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void updateCurrentWaterLevelData(String waterLevel, String alertLevelStatus) {
        // set the initial data for homepage
        tvWaterLevel.setText(waterLevel + "m");
        int color = getSeverityColor(alertLevelStatus);
        tvStatus.setText(alertLevelStatus);
        tvStatus.setBackgroundResource(color);
    }

    private void initializedNewsData() {
        floodDataAPIHandler.getNewsPaginated(1, 10,"All", new ResponseCallback<NewsAPIResponse>() {
            @Override
            public void onSuccess(NewsAPIResponse response) {
                //then set data to a list
                newsDataList = response.getData();
                //then set up the recycle view
                setUpNewsRecycleView();

                //For logging and it will delete when in production
                Gson gson = new Gson();
                String loggingData = gson.toJson(response.getData());
                Log.i("NEWS_PAGINATED", loggingData);
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("NEWS_PAGINATED", Objects.requireNonNull(t.getMessage()));
            }
        });
    }


    private void initializeWeatherForecastData() {
        floodDataAPIHandler.getFiveHoursWeatherForecast(new ResponseCallback<FiveWeatherForecast>() {
            @Override
            public void onSuccess(FiveWeatherForecast response) {
                //then set data to a list
                hourlyData = response.getData();
                //then set up the recycle view
                setupFiveWeatherRecyclerView();
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("WEATHER_FORECAST_DASHBOARD", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private void setupFiveWeatherRecyclerView() {
        //initialized adapter
        fiveHourAdapter = new WeatherHourTimelineAdapter(hourlyData);
        rvHourlyForecast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvHourlyForecast.setAdapter(fiveHourAdapter);
    }

    private void setUpNewsRecycleView() {
        //initialized adapter for news
        newsCarouselAdapter = new NewsCarouselAdapter(newsDataList, context);
        rvNewsCarousel.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvNewsCarousel.setAdapter(newsCarouselAdapter);
    }


    private void updateRecentNotification(List<ListOfNotificationResponse.NotificationData> alertsList) {
        LinearLayout alertsContainer = findViewById(R.id.alertsContainer);
        CardView cardRecentAlerts = findViewById(R.id.cardRecentAlerts);
        if (!alertsList.isEmpty()) {
            cardRecentAlerts.setVisibility(View.VISIBLE);
            alertsContainer.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(context);
            int counter = 0;
            for (ListOfNotificationResponse.NotificationData alert : alertsList) {
                View alertView = inflater.inflate(R.layout.recent_alerts_item, alertsContainer, false);
                TextView tvTitle = alertView.findViewById(R.id.tvAlertTitle);
                TextView tvDesc = alertView.findViewById(R.id.tvAlertDescription);
                TextView tvTime = alertView.findViewById(R.id.tvAlertTime);
                ImageView imgAlertIcon = alertView.findViewById(R.id.imgAlertIcon);

                //get the past time
                String timeAgo = globalUtility.getTimeAgo(alert.getCreated_at());
                tvTitle.setText(alert.getTitle());
                tvDesc.setText(alert.getNotification_text());
                tvTime.setText(timeAgo);

                // Optionally set background based on severity
                int color = getSeverityColor(alert.getSeverity());


                imgAlertIcon.setColorFilter(color);
                //added the notif in the per card
                alertsContainer.addView(alertView);

                if (counter > 3) {
                    break;
                }

                counter++;
            }
        } else {
            alertsContainer.removeAllViews();
        }
    }

    private void connectToWebSocket(String userId) {
        String WebsocketBase = globalUtility.getValueInYAML(BuildConfig.API_WEBSOCKET_BASE_URL, this);
        String WebsocketUrl = WebsocketBase + "home/user?user_id=" + userId;
        manager.connect(WebsocketUrl);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //get the updated data
        setDataFromDataStorage();
        // Set Home as selected when returning to this activity
        bottomNavigation.setSelectedItemId(R.id.nav_home);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        100
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }

}