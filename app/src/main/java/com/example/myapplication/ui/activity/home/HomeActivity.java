package com.example.myapplication.ui.activity.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.respository.users.UsersAPIRequestHandler;
import com.example.myapplication.security.DataStorageManager;
import com.example.myapplication.utils.GlobalUtility;
import com.example.myapplication.utils.home.BaseHomepageUtility;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
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
    private CompositeDisposable compositeDisposable;
    private String USER_DATA_KEY;
    private String ACCESS_TOKEN_KEY;
    private BaseHomepageUtility baseHomepageUtility;

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

    }

    private void initViews() {
        activity = this;
        context = this;
        bottomNavigation = findViewById(R.id.bottom_navigation);
        fabEmergency = findViewById(R.id.fab_emergency);
        tvWaterLevel = findViewById(R.id.tv_water_level);
        tvStatus = findViewById(R.id.tv_status);
        tvRainfall = findViewById(R.id.tv_rainfall);
        tvAlerts = findViewById(R.id.tv_alerts);
        tvStation = findViewById(R.id.tv_station);
        btnNotifications = findViewById(R.id.btn_notifications);

        dataStorageManager = DataStorageManager.getInstance(context);
        apiRequesthandler = new UsersAPIRequestHandler(activity, context);        globalUtility = new GlobalUtility();
        //init homepage utility
        baseHomepageUtility = new BaseHomepageUtility(
                context,
                activity);
        compositeDisposable = new CompositeDisposable();

        //KEYS
        USER_DATA_KEY = globalUtility.getValueInYAML(BuildConfig.USER_INFORMATION_KEY, context);
        ACCESS_TOKEN_KEY = globalUtility.getValueInYAML(BuildConfig.ACCESS_TOKEN_KEY, context);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
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
    protected void onResume() {
        super.onResume();
        //get the updated data
        setDataFromDataStorage();
        // Set Home as selected when returning to this activity
        bottomNavigation.setSelectedItemId(R.id.nav_home);

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
}