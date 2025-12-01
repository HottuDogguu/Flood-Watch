package com.example.myapplication.ui.activity.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.api_response.NewsAPIResponse;
import com.example.myapplication.data.respository.FloodDataAPIHandler;
import com.example.myapplication.ui.adapter.NewsAdapter;
import com.example.myapplication.ui.adapter.NewsCarouselAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

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


    private FloodDataAPIHandler floodDataAPIHandler;
    private String USER_DATA_KEY;
    // News Carousel Views
    private CardView cardNewsCarousel;
    private RecyclerView rvNewsCarousel;
    private List<NewsAPIResponse.NewsData> newsData;
    private List<NewsAPIResponse.NewsData> filterNewsData;
    private NewsAdapter newsAdapter;

    private String currentCategory = "All";
    private Activity activity;
    private Context context;

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

    private void initializedNewsData() {
        floodDataAPIHandler.getNewsPaginated(1, 10, new ResponseCallback<NewsAPIResponse>() {
            @Override
            public void onSuccess(NewsAPIResponse response) {
                //then set data to a list
                newsData.clear();
                newsData = response.getData();

                //add also filter data to filter news data
                filterNewsData.clear();
                filterNewsData.addAll(newsData);

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

    private void initViews() {
        activity = this;
        context = this;
        bottomNavigation = findViewById(R.id.bottom_navigation);
        fabEmergency = findViewById(R.id.fab_emergency);

        // Initialize tab views
        tabAll = findViewById(R.id.tab_all);
        tabPopular = findViewById(R.id.tab_popular);
        tabVideos = findViewById(R.id.tab_videos);
        tabBusiness = findViewById(R.id.tab_business);
        tabTechnology = findViewById(R.id.tab_technology);
        tabOther = findViewById(R.id.tab_other);

        // Initialize News Carousel Views
        cardNewsCarousel = findViewById(R.id.card_news_carousel);
        rvNewsCarousel = findViewById(R.id.rv_news_carousel);
        floodDataAPIHandler = new FloodDataAPIHandler(activity,context);
        newsData = new ArrayList<>();
        filterNewsData = new ArrayList<>();
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

    private void setUpNewsRecycleView() {
        //initialized adapter for news
        newsAdapter = new NewsAdapter(newsData, context);
        rvNewsCarousel.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvNewsCarousel.setAdapter(newsAdapter);
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
        filterNewsData.clear();
        if ("All".equals(category)) {
            filterNewsData.addAll(newsData);
        } else {
            for (NewsAPIResponse.NewsData v : filterNewsData) {
                if (category.equals(v.getStatus())) {
                    filterNewsData.add(v);
                }
            }
        }
        newsAdapter.notifyDataSetChanged();
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
                } else if (id == R.id.nav_weather) {
                    // Navigate to Weather Monitor Activity
                    Intent intent = new Intent(NewsActivity.this, WeatherMonitorActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
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

    @Override
    protected void onResume() {
        super.onResume();
        initializedNewsData();
    }
}