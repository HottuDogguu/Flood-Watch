package com.example.myapplication.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.users.UsersGetInformationResponse;
import com.example.myapplication.data.respository.users.UsersAPIRequestHandler;
import com.example.myapplication.security.DataStorageManager;
import com.example.myapplication.ui.activity.auth.EmailVerificationActivity;
import com.example.myapplication.ui.activity.users.MDRRMOContactsActivity;
import com.example.myapplication.ui.activity.users.ProfileActivity;
import com.example.myapplication.utils.GlobalUtility;
import com.example.myapplication.utils.home.BaseHomepageUtility;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;


import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private TextView tvWaterLevel;
    private TextView tvStatus;
    private TextView tvRainfall;
    private TextView tvAlerts;
    private TextView tvStation;
    private Button btnViewMap;
    private Button btnEmergency;
    private ImageView btnNotifications;
    private ImageView navHeaderImage;
    // Navigation header views
    private TextView navHeaderName, navHeaderLocation;

    private NavigationView navigationView;

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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Initialize views
        initViews();
        //set user data to the data storage
        setDataFromDataStorage();

        // Setup toolbar
        setupToolbar();

        // Setup navigation drawer
        setupNavigationDrawer();

        // Setup back press handler (NEW APPROACH)
        setupBackPressHandler();

        // Setup listeners
        setupListeners();

        // Update UI with initial data
        updateUI();

    }

    private void initViews() {
        activity = this;
        context = this;
        drawerLayout = findViewById(R.id.drawer_layout);
        tvWaterLevel = findViewById(R.id.tv_water_level);
        tvStatus = findViewById(R.id.tv_status);
        tvRainfall = findViewById(R.id.tv_rainfall);
        tvAlerts = findViewById(R.id.tv_alerts);
        tvStation = findViewById(R.id.tv_station);
        btnViewMap = findViewById(R.id.btn_view_map);
        btnEmergency = findViewById(R.id.btn_emergency);
        btnNotifications = findViewById(R.id.btn_notifications);


        navigationView = findViewById(R.id.nav_view);

        // Initialize navigation header views
        View headerView = navigationView.getHeaderView(0);
        navHeaderName = headerView.findViewById(R.id.nav_user_name);
        navHeaderLocation = headerView.findViewById(R.id.nav_user_location);
        navHeaderImage = headerView.findViewById(R.id.nav_profile_image);
        dataStorageManager = DataStorageManager.getInstance(context);
        apiRequesthandler = new UsersAPIRequestHandler(activity, context);
        globalUtility = new GlobalUtility();
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

    private void setupNavigationDrawer() {

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, findViewById(R.id.toolbar),
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();
    }


    private void setupBackPressHandler() {
        // Use the new OnBackPressedDispatcher for predictive back gestures
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                }
            }
        });
    }

    private void setupListeners() {
        btnViewMap.setOnClickListener(v -> {
            Toast.makeText(this, "Opening Map...", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Map Activity
        });

        btnEmergency.setOnClickListener(v -> {
            Toast.makeText(this, "Opening Emergency Contacts...", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Emergency Contacts Activity
        });

        btnNotifications.setOnClickListener(v -> {
            Toast.makeText(this, "Opening Notifications...", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Notifications Activity
        });
    }

    private void updateUI() {
        // Update water level
        tvWaterLevel.setText(String.format("%.1fm", currentWaterLevel));

        // Update status based on water level
        String status;
        int statusColor;
        if (currentWaterLevel >= 4) {
            status = "CRITICAL";
            statusColor = getResources().getColor(R.color.red_500, null);
            tvStatus.setBackgroundResource(R.drawable.bg_status_critical);
        } else if (currentWaterLevel >= 3) {
            status = "WARNING";
            statusColor = getResources().getColor(R.color.orange_500, null);
            tvStatus.setBackgroundResource(R.drawable.bg_status_warning);
        } else {
            status = "NORMAL";
            statusColor = getResources().getColor(R.color.green_500, null);
            tvStatus.setBackgroundResource(R.drawable.bg_status_normal);
        }

        // Update other stats
        tvRainfall.setText(rainfall + "%");
        tvAlerts.setText(String.valueOf(activeAlerts));
        tvStation.setText("Calauan River Station");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Navigate to Profile Activity
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_mdrrmo) {
            Toast.makeText(this, "MDRRMO Contacts", Toast.LENGTH_SHORT).show();
            // Navigate to MDRRMO Contacts Activity
            Intent intent = new Intent(HomeActivity.this, MDRRMOContactsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_news) {
            Toast.makeText(this, "Latest News", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to News Activity
            // Intent intent = new Intent(HomeActivity.this, NewsActivity.class);
            // startActivity(intent);
        } else if (id == R.id.nav_history) {
            Toast.makeText(this, "Flood History", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to History Activity
            // Intent intent = new Intent(HomeActivity.this, FloodHistoryActivity.class);
            // startActivity(intent);
        } else if (id == R.id.nav_map) {
            Toast.makeText(this, "Map", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Map Activity
            // Intent intent = new Intent(HomeActivity.this, MapActivity.class);
            // startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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

    public void setDataFromDataStorage() {
        Disposable flowable = dataStorageManager.getString(ACCESS_TOKEN_KEY)
                .firstElement()
                .subscribe(token -> {
                    //Function to get the user information
                    apiRequesthandler.getUserInformation(token, new ResponseCallback<UsersGetInformationResponse>() {
                        @Override
                        public void onSuccess(UsersGetInformationResponse response) {
                            // Save new user data
                            Gson gson = new Gson();
                            String jsonData = gson.toJson(response.getData());
                            dataStorageManager.putString(USER_DATA_KEY, jsonData);

                            //Update the Nav bar data after updating
                            baseHomepageUtility.loadNavBarProfileData(
                                    navHeaderName,
                                    navHeaderLocation,
                                    navHeaderImage,
                                    response.getData());
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
        //refresh again from the api
        setDataFromDataStorage();
    }
}
