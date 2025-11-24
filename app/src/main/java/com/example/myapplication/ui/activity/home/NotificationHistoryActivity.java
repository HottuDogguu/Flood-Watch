package com.example.myapplication.ui.activity.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.example.myapplication.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.api_response.ListOfNotificationResponse;
import com.example.myapplication.data.respository.FloodDataAPIHandler;
import com.example.myapplication.ui.adapter.NewsCarouselAdapter;
import com.example.myapplication.ui.adapter.NotificationAdapter;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class NotificationHistoryActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(NotificationHistoryActivity.class);
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private Activity activity;
    private Context context;
    private FloodDataAPIHandler apiHandler;
    private List<ListOfNotificationResponse.NotificationData> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_history);
        activity = this;
        context = this;

        recyclerView = findViewById(R.id.recyclerViewNotifications);


        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Disable default title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Add centered title
        TextView toolbarTitle = new TextView(this);
        toolbarTitle.setText("History");
        toolbarTitle.setTextColor(Color.BLACK);
        toolbarTitle.setTextSize(18);
        toolbarTitle.setTypeface(null, Typeface.BOLD);

        Toolbar.LayoutParams params = new Toolbar.LayoutParams(
                Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER;
        toolbarTitle.setLayoutParams(params);

        toolbar.addView(toolbarTitle);

        //initialize api handler
        apiHandler = new FloodDataAPIHandler(activity, context);

    }

    private void setUpNewsRecycleView() {
        //initialized adapter for news
        adapter = new NotificationAdapter(this, notificationList);
        // Initialize RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    private void loadNotifications() {
        notificationList = new ArrayList<>();
        apiHandler.getPaginatedNotifications(1, 10, new ResponseCallback<ListOfNotificationResponse>() {
            @Override
            public void onSuccess(ListOfNotificationResponse response) {
                notificationList = response.getData();
                setUpNewsRecycleView();

                for (ListOfNotificationResponse.NotificationData dt: notificationList) {
                    Log.i("TEST", dt.getTitle());
                }



            }

            @Override
            public void onError(Throwable t) {
                Log.e("NOTIFICATION_HISTO", t.getMessage());
            }
        });

        // You can load from database or API here
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }
}