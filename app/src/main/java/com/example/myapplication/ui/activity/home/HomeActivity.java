package com.example.myapplication.ui.activity.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.calbacks.WebsocketCallback;
import com.example.myapplication.Constants;
import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.models.api_response.ListOfNotificationResponse;
import com.example.myapplication.data.models.api_response.WebsocketResponseData;
import com.example.myapplication.data.network.websockets.WebsocketManager;
import com.example.myapplication.data.respository.alerts.FloodDataAPIHandler;
import com.example.myapplication.data.respository.users.UsersAPIRequestHandler;
import com.example.myapplication.security.DataStorageManager;
import com.example.myapplication.ui.activity.notification.LocalNotificationManager;
import com.example.myapplication.utils.GlobalUtility;
import com.example.myapplication.utils.home.BaseHomepageUtility;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    // Sample data
    private double currentWaterLevel = 2.3;
    private int rainfall = 68;
    private int activeAlerts = 2;
    private GlobalUtility globalUtility;
    private Activity activity;
    private Context context;
    private DataStorageManager dataStorageManager;
    private UsersAPIRequestHandler apiRequesthandler;
    private FloodDataAPIHandler floodDataAPIHandler;
    private CompositeDisposable compositeDisposable;
    private String USER_DATA_KEY;
    private String ACCESS_TOKEN_KEY;
    private BaseHomepageUtility baseHomepageUtility;
    private ExecutorService executor = Executors.newFixedThreadPool(3);
    private List<ListOfNotificationResponse.NotificationData> alertListData;


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

        // Setup listeners
        setupListeners();

        // Update UI with initial data
        updateUI();
        //connect to websocket on create of the app
        connectToWebSocket();
        requestNotificationPermission();

        //update three recent notification

    }

    private void initViews() {
        activity = this;
        context = this;

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        fabEmergency = (FloatingActionButton) findViewById(R.id.fab_emergency);
        tvWaterLevel = (TextView) findViewById(R.id.tv_water_level);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        tvRainfall = (TextView) findViewById(R.id.tv_rainfall);
        tvAlerts = (TextView) findViewById(R.id.tv_alerts);
        tvStation = (TextView) findViewById(R.id.tv_station);
        btnNotifications = (ImageView) findViewById(R.id.btn_notifications);

        dataStorageManager = DataStorageManager.getInstance(context);
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
                } else if (id == R.id.nav_history) {
                    // Navigate to Flood History Activity
                    Intent intent = new Intent(HomeActivity.this, FloodHistoryActivity.class);
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

    private void setupListeners() {
        btnNotifications.setOnClickListener(v -> {
            Toast.makeText(this, "Opening Notifications...", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Notifications Activity
        });

        // View Map button
        findViewById(R.id.btn_view_map).setOnClickListener(v -> {
            Toast.makeText(this, "Opening Map...", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Map Activity
        });
    }

    private void updateUI() {
        // Update water level
        tvWaterLevel.setText(String.format("%.1fm", currentWaterLevel));

        // Update status based on water level
        String status;
        if (currentWaterLevel >= 4) {
            status = "CRITICAL";
            tvStatus.setBackgroundResource(R.drawable.bg_status_critical);
        } else if (currentWaterLevel >= 3) {
            status = "WARNING";
            tvStatus.setBackgroundResource(R.drawable.bg_status_warning);
        } else {
            status = "NORMAL";
            tvStatus.setBackgroundResource(R.drawable.bg_status_normal);
        }
        tvStatus.setText(status);

        // Update other stats
        tvRainfall.setText(rainfall + "%");
        tvAlerts.setText(String.valueOf(activeAlerts));
        tvStation.setText("Calauan River Station");
    }


    public void setDataFromDataStorage() {
        Disposable flowable = dataStorageManager.getString(ACCESS_TOKEN_KEY)
                .firstElement()
                .subscribe(token -> {
                    //Function to get the user information
                    apiRequesthandler.getUserInformation(token, new ResponseCallback<ApiSuccessfulResponse>() {
                        @Override
                        public void onSuccess(ApiSuccessfulResponse response) {
                            // Save new user data
                            Gson gson = new Gson();
                            String jsonData = gson.toJson(response.getData());
                            dataStorageManager.putString(USER_DATA_KEY, jsonData);
                        }

                        @Override
                        public void onError(Throwable t) {
                            Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                            //navigate to login activity
                            baseHomepageUtility.navigateToLogin();
                        }
                    });
                });
        compositeDisposable.add(flowable);
    }


    @Override
    public void onBackPressed() {

    }

    // Method to simulate water level updates (you can call this from your data source)
    public void updateWaterLevel(double newLevel) {
        currentWaterLevel = newLevel;
        updateUI();
    }

    // Method to update rainfall data
    public void updateRainfall(int newRainfall) {
        rainfall = newRainfall;
        updateUI();
    }

    // Method to update alerts count
    public void updateAlerts(int newAlerts) {
        activeAlerts = newAlerts;
        updateUI();
    }

    private boolean isConnected = false;

    WebsocketManager manager = new WebsocketManager(new WebsocketCallback() {
        @Override
        public void onMessageReceived(String message) {
            Gson gson = new Gson();
            WebsocketResponseData data = gson.<WebsocketResponseData>fromJson(message, WebsocketResponseData.class);
            Log.i("Websocket", "Websocket received message");
            String content = data.getData().getNotification_text();
            String title = data.getData().getTitle();
            String topic = data.getData().getTopic();
            String severity = data.getData().getSeverity();
            //set the new value for water level if the topic is flood alers

            if (data.getData().getTopic().equalsIgnoreCase(Constants.FLOOD_ALERT)) {
                runOnUiThread(() -> updateCurrentWaterLevelData(data.getData().getValue(), data.getData().getSeverity()));
                //Then show notif
                //check if will notify the users

                String createdAt = data.getData().getNotification_created_at();
                if (data.isIs_online_users_will_notify()) {

                    LocalNotificationManager.showNotification(context, title, content, topic, severity);
                    ListOfNotificationResponse.NotificationData new_data = new ListOfNotificationResponse.NotificationData(
                            severity,
                            topic,
                            title,
                            content,
                            createdAt);

                    if (alertListData.size() == 3) {
                        alertListData.remove(-1);
                    }
                    alertListData.add(new_data);// after create, then update the recent notif
                    runOnUiThread(() -> {
                        updateCurrentWaterLevelData(data.getData().getValue(), severity);
                        //update the three recent notification


                    });

                }
            }
            if (data.getData().getTopic().equals(Constants.WEATHER_ALERT)) {
                runOnUiThread(() -> {
                    tvRainfall.setText(data.getData().getValue() + "%");
                    LocalNotificationManager.showNotification(context, title, content, topic, severity);
                });

            }

            if (data.getData().getTopic().equals(Constants.EMERGENCY_ALERT)) {
                //TODO malalaman pa
            }


            if (data.getData().getTopic().equals(Constants.WEATHER_ALERT)) {
                //TODO
            }
            getThreeRecentNotification();

        }

        @Override
        public void onConnected() {
            Log.i("Websocket", "Websocket is connected");
            isConnected = true;
            //retrieve the current data of Flood data
            floodDataAPIHandler.getCurrentFloodData(new ResponseCallback<ApiSuccessfulResponse>() {
                @SuppressLint({"SetTextI18n", "ResourceType"})
                @Override
                public void onSuccess(ApiSuccessfulResponse response) {
                    Toast.makeText(activity, response.getMessage(), Toast.LENGTH_SHORT).show();
                    runOnUiThread(() -> {
                        updateCurrentWaterLevelData(String.valueOf(response.getData().getWater_level()),
                                response.getData().getAlert_level());
                        //get the three recent notification
                        getThreeRecentNotification();
                        //update the rainfall percent
                        updateWaterRainfall();
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


    private void updateWaterRainfall(){
        floodDataAPIHandler.getFiveHoursWeatherForecast(new ResponseCallback<ApiSuccessfulResponse>() {
            @Override
            public void onSuccess(ApiSuccessfulResponse response) {
                @SuppressLint("DefaultLocale") String waterRainfallValue = String.valueOf(response.getData().getPrecipitation_probability() +"%");
                tvRainfall.setText(waterRainfallValue);

            }

            @Override
            public void onError(Throwable t) {
                Log.e("WATERRAINFALL",t.getMessage());

            }
        });
    }

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
        floodDataAPIHandler.getThreeRecentNotifications(new ResponseCallback<ListOfNotificationResponse>() {
            @Override
            public void onSuccess(ListOfNotificationResponse response) {
                alertListData = response.getData();
                updateRecentNotification(alertListData);
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }


    public void updateCurrentWaterLevelData(String waterLevel, String alertLevelStatus) {
        // set the initial data for homepage
        tvWaterLevel.setText(waterLevel + "m");
        int color = getSeverityColor(alertLevelStatus);
        tvStatus.setText(alertLevelStatus);
        tvStatus.setBackgroundResource(color);
    }


    private void updateRecentNotification(List<ListOfNotificationResponse.NotificationData> alertsList) {
        LinearLayout alertsContainer = findViewById(R.id.alertsContainer);
        CardView cardRecentAlerts = findViewById(R.id.cardRecentAlerts);
        if (!alertsList.isEmpty()) {
            cardRecentAlerts.setVisibility(View.VISIBLE);
            alertsContainer.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(context);
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
                alertView.setBackgroundResource(color);

                imgAlertIcon.setColorFilter(color);
                //added the notif in the per card
                alertsContainer.addView(alertView);
            }
        }
    }

    private void connectToWebSocket() {

        //connect to websocket
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(fcmToken -> {
            String WebsocketBase = globalUtility.getValueInYAML(BuildConfig.API_WEBSOCKET_BASE_URL, this);
            String WebsocketUrl = WebsocketBase + "home/user?fcm_token=" + fcmToken;
            manager.connect(WebsocketUrl);
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        //get the updated data
        setDataFromDataStorage();
        // Set Home as selected when returning to this activity
        bottomNavigation.setSelectedItemId(R.id.nav_home);
        //get the three recent notification
        getThreeRecentNotification();
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