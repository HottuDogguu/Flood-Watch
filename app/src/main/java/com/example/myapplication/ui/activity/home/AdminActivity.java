package com.example.myapplication.ui.activity.home;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.api_response.AdminDashboardApiResponse;
import com.example.myapplication.data.models.api_response.NewsAPIResponse;
import com.example.myapplication.data.network.APIBuilder;
import com.example.myapplication.data.network.endpoints.users.UserEndpoints;
import com.example.myapplication.data.respository.UsersAPIRequestHandler;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private FrameLayout contentContainer;
    private Context context;
    private Activity activity;
    private UsersAPIRequestHandler usersAPIRequestHandler;

    private AdminDashboardApiResponse dashboardApiResponse = new AdminDashboardApiResponse();
//    private List<NewsAPIResponse.NewsData> newsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        context = this;
        activity = this;

        usersAPIRequestHandler = new UsersAPIRequestHandler(activity, context);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin Dashboard");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tabLayout = findViewById(R.id.tabLayout);
        contentContainer = findViewById(R.id.contentContainer);

        setupTabs();
        showDashboard();
    }


    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Dashboard"));
        tabLayout.addTab(tabLayout.newTab().setText("Users"));
        tabLayout.addTab(tabLayout.newTab().setText("News"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        showDashboard();
                        break;
                    case 1:
//                        showUserManagement();
                        break;
                    case 2:
//                        showNewsManagement();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void showDashboard() {
        View view = getLayoutInflater().inflate(R.layout.fragment_admin_dashboard, contentContainer, false);
        contentContainer.removeAllViews();
        contentContainer.addView(view);

        usersAPIRequestHandler.getAdminDashboardData(new ResponseCallback<AdminDashboardApiResponse>() {
            @Override
            public void onSuccess(AdminDashboardApiResponse response) {

                TextView totalUsers = view.findViewById(R.id.totalUsersText);
                TextView totalNews = view.findViewById(R.id.totalNewsText);

                totalUsers.setText(String.valueOf(response.getData().getTotal_users()));
                totalNews.setText(String.valueOf(response.getData().getTotal_news()));
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

//    private void showUserManagement() {
//        View view = getLayoutInflater().inflate(R.layout.fragment_user_management, contentContainer, false);
//        contentContainer.removeAllViews();
//        contentContainer.addView(view);
//
//        RecyclerView recyclerView = view.findViewById(R.id.usersRecyclerView);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(new AdminUserAdapter(usersList, new AdminUserAdapter.OnUserActionListener() {
//            @Override
//            public void onAction(AdminUser user, String action) {
//                handleUserAction(user, action);
//            }
//        }));
//
//    }
//
//    private void handleUserAction(AdminUser user, String action) {
//        switch (action) {
//            case "toggle_alert":
//                user.alertsEnabled = !user.alertsEnabled;
//                Toast.makeText(this, "Alert settings updated for " + user.name, Toast.LENGTH_SHORT).show();
//                showUserManagement(); // Refresh
//                break;
//            case "toggle_notification":
//                user.notificationsEnabled = !user.notificationsEnabled;
//                Toast.makeText(this, "Notification settings updated for " + user.name, Toast.LENGTH_SHORT).show();
//                showUserManagement(); // Refresh
//                break;
//        }
//    }
//

//    private void showNewsManagement() {
//        View view = getLayoutInflater().inflate(R.layout.fragment_news_management, contentContainer, false);
//        contentContainer.removeAllViews();
//        contentContainer.addView(view);
//
//        RecyclerView recyclerView = view.findViewById(R.id.newsRecyclerView);
//        Button createNewsBtn = view.findViewById(R.id.createNewsBtn);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(new AdminNewsAdapter(newsList, new AdminNewsAdapter.OnNewsActionListener() {
//            @Override
//            public void onAction(AdminNews news, String action) {
//                handleNewsAction(news, action);
//            }
//        }));
//
//        createNewsBtn.setOnClickListener(v -> showCreateNewsDialog());
//    }
//
//    private void handleNewsAction(AdminNews news, String action) {
//        switch (action) {
//            case "edit":
//                showEditNewsDialog(news);
//                break;
//            case "delete":
//                new AlertDialog.Builder(this)
//                        .setTitle("Delete News")
//                        .setMessage("Are you sure you want to delete this news?")
//                        .setPositiveButton("Delete", (dialog, which) -> {
//                            newsList.remove(news);
//                            Toast.makeText(this, "News deleted", Toast.LENGTH_SHORT).show();
//                            showNewsManagement(); // Refresh
//                        })
//                        .setNegativeButton("Cancel", null)
//                        .show();
//                break;
//        }
//    }
//
//    private void showCreateNewsDialog() {
//        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_news, null);
//        TextInputEditText titleInput = dialogView.findViewById(R.id.newsTitle);
//        TextInputEditText contentInput = dialogView.findViewById(R.id.newsContent);
//        TextView imagePathText = dialogView.findViewById(R.id.newsImagePath);
//        TextView videoPathText = dialogView.findViewById(R.id.newsVideoPath);
//        Button selectImageBtn = dialogView.findViewById(R.id.selectImageBtn);
//        Button selectVideoBtn = dialogView.findViewById(R.id.selectVideoBtn);
//
//        // TODO: Implement file picker functionality
//        selectImageBtn.setOnClickListener(v -> {
//            Toast.makeText(this, "Image picker - To be implemented", Toast.LENGTH_SHORT).show();
//            // You'll implement file picker here later
//            // After selecting: imagePathText.setText(selectedFileName);
//        });
//
//        selectVideoBtn.setOnClickListener(v -> {
//            Toast.makeText(this, "Video picker - To be implemented", Toast.LENGTH_SHORT).show();
//            // You'll implement file picker here later
//            // After selecting: videoPathText.setText(selectedFileName);
//        });
//
//        new AlertDialog.Builder(this)
//                .setTitle("Create News Post")
//                .setView(dialogView)
//                .setPositiveButton("Publish", (dialog, which) -> {
//                    String title = titleInput.getText().toString();
//                    String content = contentInput.getText().toString();
//                    String imagePath = imagePathText.getText().toString();
//                    String videoPath = videoPathText.getText().toString();
//
//                    if (!title.isEmpty() && !content.isEmpty()) {
//                        String newId = String.valueOf(newsList.size() + 1);
//                        newsList.add(new AdminNews(newId, title, content, 0, "published"));
//                        Toast.makeText(this, "News published successfully", Toast.LENGTH_SHORT).show();
//                        showNewsManagement(); // Refresh
//                    } else {
//                        Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .setNegativeButton("Cancel", null)
//                .show();
//    }
//
//    private void showEditNewsDialog(AdminNews news) {
//        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_news, null);
//        TextInputEditText titleInput = dialogView.findViewById(R.id.newsTitle);
//        TextInputEditText contentInput = dialogView.findViewById(R.id.newsContent);
//        TextView imagePathText = dialogView.findViewById(R.id.newsImagePath);
//        TextView videoPathText = dialogView.findViewById(R.id.newsVideoPath);
//        Button selectImageBtn = dialogView.findViewById(R.id.selectImageBtn);
//        Button selectVideoBtn = dialogView.findViewById(R.id.selectVideoBtn);
//
//        // Pre-fill with existing data
//        titleInput.setText(news.title);
//        contentInput.setText(news.content);
//
//        // TODO: Implement file picker functionality
//        selectImageBtn.setOnClickListener(v -> {
//            Toast.makeText(this, "Image picker - To be implemented", Toast.LENGTH_SHORT).show();
//            // After selecting: imagePathText.setText(selectedFileName);
//        });
//
//        selectVideoBtn.setOnClickListener(v -> {
//            Toast.makeText(this, "Video picker - To be implemented", Toast.LENGTH_SHORT).show();
//            // After selecting: videoPathText.setText(selectedFileName);
//        });
//
//        new AlertDialog.Builder(this)
//                .setTitle("Edit News Post")
//                .setView(dialogView)
//                .setPositiveButton("Update", (dialog, which) -> {
//                    news.title = titleInput.getText().toString();
//                    news.content = contentInput.getText().toString();
//                    Toast.makeText(this, "News updated", Toast.LENGTH_SHORT).show();
//                    showNewsManagement(); // Refresh
//                })
//                .setNegativeButton("Cancel", null)
//                .show();
//    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}