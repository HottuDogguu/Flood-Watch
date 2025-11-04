package com.example.myapplication.ui.activity.home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NewsActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private FloatingActionButton fabEmergency;

    // Category tabs
    private TextView tabAll;
    private TextView tabPopular;
    private TextView tabVideos;
    private TextView tabBusiness;
    private TextView tabTechnology;
    private TextView tabOther;

    private String currentCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Initialize views
        initViews();

        // Setup bottom navigation
        setupBottomNavigation();

        // Setup FAB (Emergency button)
        setupEmergencyFab();

        // Setup category tabs
        setupCategoryTabs();
    }

    private void initViews() {
        bottomNavigation = findViewById(R.id.bottom_navigation);
        fabEmergency = findViewById(R.id.fab_emergency);

        // Initialize tab views
        tabAll = findViewById(R.id.tab_all);
        tabPopular = findViewById(R.id.tab_popular);
        tabVideos = findViewById(R.id.tab_videos);
        tabBusiness = findViewById(R.id.tab_business);
        tabTechnology = findViewById(R.id.tab_technology);
        tabOther = findViewById(R.id.tab_other);
    }

    private void setupCategoryTabs() {
        // Set click listeners for each tab
        tabAll.setOnClickListener(v -> selectTab(tabAll, "All"));
        tabPopular.setOnClickListener(v -> selectTab(tabPopular, "Popular"));
        tabVideos.setOnClickListener(v -> selectTab(tabVideos, "Videos"));
        tabBusiness.setOnClickListener(v -> selectTab(tabBusiness, "Business"));
        tabTechnology.setOnClickListener(v -> selectTab(tabTechnology, "Technology"));
        tabOther.setOnClickListener(v -> selectTab(tabOther, "Other"));
    }

    private void selectTab(TextView selectedTab, String category) {
        // Update current category
        currentCategory = category;

        // Reset all tabs to default style
        resetTab(tabAll);
        resetTab(tabPopular);
        resetTab(tabVideos);
        resetTab(tabBusiness);
        resetTab(tabTechnology);
        resetTab(tabOther);

        // Highlight selected tab
        selectedTab.setBackgroundResource(R.drawable.tab_background_selected);
        selectedTab.setTextColor(Color.parseColor("#1A1A1A"));
        selectedTab.setTypeface(null, Typeface.BOLD);

        // Load news for the selected category
        loadNewsForCategory(category);
    }

    private void resetTab(TextView tab) {
        tab.setBackgroundResource(R.drawable.tab_background);
        tab.setTextColor(Color.parseColor("#1A1A1A"));
        tab.setTypeface(null, Typeface.NORMAL);
    }

    private void loadNewsForCategory(String category) {
        // TODO: Implement loading news based on category
        // This is where you would filter/load news articles based on the selected category
        // For example:
        // - Make API call to fetch news for this category
        // - Filter existing news list
        // - Update RecyclerView with filtered data

        // Placeholder for now
        System.out.println("Loading news for category: " + category);
    }

    private void setupBottomNavigation() {
        // Set News as selected
        bottomNavigation.setSelectedItemId(R.id.nav_news);

        bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    Intent intent = new Intent(NewsActivity.this, HomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.nav_news) {
                    // Already on news, do nothing
                    return true;
                } else if (id == R.id.nav_emergency) {
                    Intent intent = new Intent(NewsActivity.this, MDRRMOContactsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_history) {
                    Intent intent = new Intent(NewsActivity.this, FloodHistoryActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.nav_profile) {
                    Intent intent = new Intent(NewsActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }

                return false;
            }
        });
    }

    private void setupEmergencyFab() {
        fabEmergency.setOnClickListener(v -> {
            Intent intent = new Intent(NewsActivity.this, MDRRMOContactsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        // Go back to HomeActivity when back is pressed
        Intent intent = new Intent(NewsActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();

        // Properly call the parent method to clear the warning
        super.onBackPressed();
    }
}