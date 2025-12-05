package com.example.myapplication.ui.activity.home;

import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.example.myapplication.data.respository.UsersAPIRequestHandler;
import com.example.myapplication.ui.adapter.NewsAdapter;
import com.example.myapplication.ui.adapter.NewsCarouselAdapter;
import com.example.myapplication.utils.VerticalSpaceItemDecoration;
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
    LinearLayout paginationLayout;
    ImageButton nextBtn,prevBtn;
    private TextView paginatedNumber;
    int skip = 1;
    private int nextPage = 0;


    private UsersAPIRequestHandler apiRequesthandler;
    // News Carousel Views
    private RecyclerView rvNewsCarousel;
    private List<NewsAPIResponse.NewsData> newsData;
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

        //handle skip
        prevBtn.setOnClickListener(v ->{
            nextBtn.setClickable(true);
            nextBtn.setBackgroundResource(R.drawable.pagination_button_active);
            if(skip > 1) {
                skip -= 1;
                initializedNewsData(currentCategory);
            }

            //to disable prev button if equal to one
            if(skip == 1) {
                prevBtn.setClickable(false);
                prevBtn.setBackgroundResource(R.drawable.pagination_button_inactive);

            }

        });
        nextBtn.setOnClickListener(v -> {
            if(nextBtn.getVisibility() == VISIBLE){
              skip += 1;
              //make the previous button clickable again
              prevBtn.setClickable(true);
              prevBtn.setBackgroundResource(R.drawable.pagination_button_active);
              initializedNewsData(currentCategory);
          }


        });
    }

    private void initializedNewsData(String tags) {

        apiRequesthandler.getNewsPaginated(skip, 10, tags, new ResponseCallback<NewsAPIResponse>() {
            @Override
            public void onSuccess(NewsAPIResponse response) {
                //then set data to a list
                newsData.clear();
                newsData = response.getData();

                //then set up the recycle view
                setUpNewsRecycleView();

                    if (response.getPaginated().getTotal_records() > 10) {
                        //visible the layout
                        paginationLayout.setVisibility(VISIBLE);
                        String pageInfo = "Showing " + response.getPaginated().getStart_page() +" to "+response.getPaginated().getEnd_page() + " of " + response.getPaginated().getTotal_records();
                        paginatedNumber.setText(pageInfo);
                        if(!response.getPaginated().isHas_next()){
                            Toast.makeText(activity, "End of news page.", Toast.LENGTH_SHORT).show();
                            nextBtn.setClickable(false);
                            nextBtn.setBackgroundResource(R.drawable.pagination_button_inactive);

                        }else{
                            nextBtn.setClickable(true);
                            nextBtn.setBackgroundResource(R.drawable.pagination_button_active);
                        }

                    } else {
                        paginationLayout.setVisibility(View.GONE);
                    }

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
        tabBusiness = findViewById(R.id.tab_business);
        tabTechnology = findViewById(R.id.tab_technology);
        tabOther = findViewById(R.id.tab_other);

        // Initialize News Carousel Views
        rvNewsCarousel = findViewById(R.id.rv_news_page);
        apiRequesthandler = new UsersAPIRequestHandler(activity,context);
        newsData = new ArrayList<>();
        paginationLayout = findViewById(R.id.pagination_container);
        nextBtn = findViewById(R.id.btn_next);
        prevBtn = findViewById(R.id.btn_prev);
        paginatedNumber = findViewById(R.id.tv_page_info);
        setupSpaceForRecycle();
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
        skip = 1;
        prevBtn.setClickable(false);
        prevBtn.setBackgroundResource(R.drawable.pagination_button_inactive);
        initializedNewsData(category);
    }

    private void resetTab(TextView tab) {
        tab.setBackgroundResource(R.drawable.tab_background);
        tab.setTextColor(Color.parseColor("#1A1A1A"));
        tab.setTypeface(null, Typeface.NORMAL);
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

    /**
     * Helper function to convert density-independent pixels (dp) to screen pixels (px).
     */
    public static int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }


    protected void setupSpaceForRecycle() {
        rvNewsCarousel = findViewById(R.id.rv_news_page);

        // Define the desired spacing in DP (e.g., 16dp)
        final int spacingInDp = 16;

        // Convert the DP value to pixels
        final int spacingInPx = dpToPx(this, spacingInDp);

        // Create the ItemDecoration instance
        final RecyclerView.ItemDecoration itemDecoration = new VerticalSpaceItemDecoration(spacingInPx);

        // Apply the decoration to the RecyclerView
        rvNewsCarousel.removeItemDecoration(itemDecoration);
        rvNewsCarousel.addItemDecoration(itemDecoration);

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
        initializedNewsData(currentCategory);
    }
}