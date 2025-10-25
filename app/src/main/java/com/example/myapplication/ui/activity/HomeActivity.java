package com.example.myapplication.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.myapplication.R;
import com.example.myapplication.utils.GlobalUtility;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;
    private TextView tvWaterLevel;
    private TextView tvStatus;
    private TextView tvRainfall;
    private TextView tvAlerts;
    private TextView tvStation;
    private Button btnViewMap;
    private Button btnEmergency;
    private ImageView btnNotifications;

    private double currentWaterLevel = 2.3;
    private int rainfall = 68;
    private int activeAlerts = 2;
    private GlobalUtility globalUtility;
    private Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Initialize views
        initViews();

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
        drawerLayout = findViewById(R.id.drawer_layout);
        tvWaterLevel = findViewById(R.id.tv_water_level);
        tvStatus = findViewById(R.id.tv_status);
        tvRainfall = findViewById(R.id.tv_rainfall);
        tvAlerts = findViewById(R.id.tv_alerts);
        tvStation = findViewById(R.id.tv_station);
        btnViewMap = findViewById(R.id.btn_view_map);
        btnEmergency = findViewById(R.id.btn_emergency);
        btnNotifications = findViewById(R.id.btn_notifications);
        globalUtility = new GlobalUtility();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupNavigationDrawer() {
        NavigationView navigationView = findViewById(R.id.nav_view);
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
                    // Option 1: Finish the activity
                    finish();

                    // Option 2: Use the default back behavior
                    // setEnabled(false);
                    // getOnBackPressedDispatcher().onBackPressed();
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
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus){
            globalUtility.hideSystemUI(activity);
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        globalUtility.hideSystemUI(activity);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Toast.makeText(this, "My Profile", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Profile Activity
        } else if (id == R.id.nav_mdrrmo) {
            Toast.makeText(this, "MDRRMO Contacts", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to MDRRMO Contacts Activity
        } else if (id == R.id.nav_news) {
            Toast.makeText(this, "Latest News", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to News Activity
        } else if (id == R.id.nav_history) {
            Toast.makeText(this, "Flood History", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to History Activity
        } else if (id == R.id.nav_map) {
            Toast.makeText(this, "Map", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Map Activity
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
}
