package com.example.myapplication.ui.activity.home.admin;

import static android.view.View.VISIBLE;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.admin.NewsPostRequestModel;
import com.example.myapplication.data.models.api_response.AdminDashboardApiResponse;
import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.models.api_response.ApiUsesInformationResponse;
import com.example.myapplication.data.models.api_response.NewsAPIResponse;
import com.example.myapplication.data.respository.AdminAPIRequestHandler;
import com.example.myapplication.data.respository.UsersAPIRequestHandler;
import com.example.myapplication.security.DataSharedPreference;
import com.example.myapplication.ui.activity.auth.LoginActivity;
import com.example.myapplication.ui.activity.home.NewsActivity;
import com.example.myapplication.ui.activity.home.ProfileActivity;
import com.example.myapplication.ui.adapter.AdminNewsAdapter;
import com.example.myapplication.ui.adapter.AdminUserAdapter;
import com.example.myapplication.utils.GlobalUtility;
import com.example.myapplication.utils.VerticalSpaceItemDecoration;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private DataSharedPreference sharedPreference;
    private Uri currentPhotoUri;

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Button btnCreateNews;
    private FrameLayout contentContainer;
    private Context context;
    private Activity activity;
    private UsersAPIRequestHandler usersAPIRequestHandler;
    private AdminAPIRequestHandler adminAPIRequestHandler;
    private AdminUserAdapter adminUserAdapter;
    private AdminNewsAdapter newsAdapter;
    private List<ApiSuccessfulResponse.UserData> userData;
    private List<NewsAPIResponse.NewsData> newsData;
    private int userSkip = 1;
    private int newsSkip = 1;
    private String newsTag = "All";
    private ImageView imageRemover;
    private GlobalUtility globalUtility;
    private boolean isImageUploaded = false;
    LinearLayout paginationLayoutNews, paginationLayoutUsers;
    ImageButton btnNextNews, btnPrevNews, btnNextUsers, btnPrevUsers;
    private TextView paginatedNumberNews, paginatedNumberUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        context = this;
        activity = this;

        usersAPIRequestHandler = new UsersAPIRequestHandler(activity, context);
        adminAPIRequestHandler = new AdminAPIRequestHandler(activity, context);
        setupActivityResultLaunchers();
        globalUtility = new GlobalUtility();
        sharedPreference = DataSharedPreference.getInstance(context);

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

    @NonNull
    @Override
    public OnBackInvokedDispatcher getOnBackInvokedDispatcher() {
        return super.getOnBackInvokedDispatcher();

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
                        showUserManagement();
                        break;
                    case 2:
                        showNewsManagement();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }


    private void showDashboard() {
        View view = getLayoutInflater().inflate(R.layout.fragment_admin_dashboard, contentContainer, false);
        contentContainer.removeAllViews();
        contentContainer.addView(view);

        adminAPIRequestHandler.getAdminDashboardData(new ResponseCallback<AdminDashboardApiResponse>() {
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

    private void showUserManagement() {
        userData = new ArrayList<>();
        adminAPIRequestHandler.getUsersInformation(userSkip, 10, new ResponseCallback<ApiUsesInformationResponse>() {
            @Override
            public void onSuccess(ApiUsesInformationResponse response) {
                userData = response.getData();

                //initialized the recycle view
                initializedUsersInformationRecycleView();

                if (response.getPaginated().getTotal_records() > 10) {
                    //visible the layout
                    paginationLayoutUsers.setVisibility(VISIBLE);

                    //check if the skip is greater than 1
                    if (userSkip > 1) {
                        btnPrevUsers.setClickable(true);
                        btnPrevUsers.setBackgroundResource(R.drawable.pagination_button_active);
                    }
                    String pageInfo = "Showing " + response.getPaginated().getStart_page() + " to " + response.getPaginated().getEnd_page() + " of " + response.getPaginated().getTotal_records();
                    paginatedNumberUsers.setText(pageInfo);
                    if (!response.getPaginated().isHas_next()) {
                        Toast.makeText(activity, "End of news page.", Toast.LENGTH_SHORT).show();
                        btnNextUsers.setClickable(false);
                        btnNextUsers.setBackgroundResource(R.drawable.pagination_button_inactive);

                    } else {
                        btnNextUsers.setClickable(true);
                        btnNextUsers.setBackgroundResource(R.drawable.pagination_button_active);
                    }

                } else {
                    paginationLayoutUsers.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable t) {

            }
        });


    }

    private void initializedUsersInformationRecycleView() {
        View view = getLayoutInflater().inflate(R.layout.fragment_user_management, contentContainer, false);
        contentContainer.removeAllViews();
        contentContainer.addView(view);
        RecyclerView recyclerView = view.findViewById(R.id.usersRecyclerView);
        adminUserAdapter = new AdminUserAdapter(userData,context,sharedPreference,globalUtility);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adminUserAdapter);

        //initialized all about in pagination
        paginationLayoutUsers = view.findViewById(R.id.pagination_container);
        btnNextUsers = view.findViewById(R.id.btn_next);
        btnPrevUsers = view.findViewById(R.id.btn_prev);
        paginatedNumberUsers = view.findViewById(R.id.tv_page_info);

        handleUsersPaginationListener();

    }

    private void showNewsManagement() {
        usersAPIRequestHandler.getNewsPaginated(newsSkip, 10, newsTag, new ResponseCallback<NewsAPIResponse>() {
            @Override
            public void onSuccess(NewsAPIResponse response) {
                newsData = response.getData();
                setUpNewsRecycleView();
                if (response.getPaginated().getTotal_records() > 10) {
                    //visible the layout
                    paginationLayoutNews.setVisibility(VISIBLE);
                    //check if the skip is greater than 1
                    if (newsSkip > 1) {
                        btnPrevNews.setClickable(true);
                        btnPrevNews.setBackgroundResource(R.drawable.pagination_button_active);
                    }
                    String pageInfo = "Showing " + response.getPaginated().getStart_page() + " to " + response.getPaginated().getEnd_page() + " of " + response.getPaginated().getTotal_records();
                    paginatedNumberNews.setText(pageInfo);
                    if (!response.getPaginated().isHas_next()) {
                        Toast.makeText(activity, "End of news page.", Toast.LENGTH_SHORT).show();
                        btnNextNews.setClickable(false);
                        btnNextNews.setBackgroundResource(R.drawable.pagination_button_inactive);

                    } else {
                        btnNextNews.setClickable(true);
                        btnNextNews.setBackgroundResource(R.drawable.pagination_button_active);
                    }

                } else {
                    paginationLayoutNews.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable t) {

            }
        });

    }

    private void setUpNewsRecycleView() {
        View view = getLayoutInflater().inflate(R.layout.fragment_news_management, contentContainer, false);
        contentContainer.removeAllViews();
        contentContainer.addView(view);

        RecyclerView recyclerView = view.findViewById(R.id.newsRecyclerView);
        btnCreateNews = view.findViewById(R.id.createNewsBtn);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new AdminNewsAdapter(newsData, context, globalUtility, sharedPreference);
        recyclerView.setAdapter(newsAdapter);

        //initialized all about in pagination
        paginationLayoutNews = view.findViewById(R.id.pagination_container);
        btnNextNews = view.findViewById(R.id.btn_next);
        btnPrevNews = view.findViewById(R.id.btn_prev);
        paginatedNumberNews = view.findViewById(R.id.tv_page_info);


        //add spaces for every items
        setupSpaceForRecycle(recyclerView);
        //add on click listeners

        btnCreateNews.setOnClickListener(v -> {
            showCreateNewsDialog();
        });

        //call the pagination on click listeners
        handleNewsPaginationListener();
    }

    /**
     * To initialized the space for recycle view.
     *
     * @param recyclerView to be added space to this component.
     */
    protected void setupSpaceForRecycle(RecyclerView recyclerView) {

        // Define the desired spacing in DP (e.g., 16dp)
        final int spacingInDp = 16;

        // Convert the DP value to pixels
        final int spacingInPx = NewsActivity.dpToPx(this, spacingInDp);

        // Create the ItemDecoration instance
        final RecyclerView.ItemDecoration itemDecoration = new VerticalSpaceItemDecoration(spacingInPx);

        // Apply the decoration to the RecyclerView
        recyclerView.removeItemDecoration(itemDecoration);
        recyclerView.addItemDecoration(itemDecoration);

    }

    /**
     * it is a function to handle the listeners for
     * previous and next button for pagination ins news.
     */
    private void handleNewsPaginationListener() {

        //handle skip
        btnPrevNews.setOnClickListener(v -> {
            btnNextNews.setClickable(true);
            btnNextNews.setBackgroundResource(R.drawable.pagination_button_active);
            if (newsSkip > 1) {
                newsSkip -= 1;
                showNewsManagement();
            }

            //to disable prev button if equal to one
            if (newsSkip == 1) {
                btnPrevNews.setClickable(false);
                btnPrevNews.setBackgroundResource(R.drawable.pagination_button_inactive);

            }

        });

        btnNextNews.setOnClickListener(v -> {
            newsSkip += 1;
            //make the previous button clickable again
            btnPrevNews.setClickable(true);
            btnPrevNews.setBackgroundResource(R.drawable.pagination_button_active);
            showNewsManagement();
        });
    }

    private void handleUsersPaginationListener() {
        //handle skip
        btnPrevUsers.setOnClickListener(v -> {
            btnNextUsers.setClickable(true);
            btnNextUsers.setBackgroundResource(R.drawable.pagination_button_active);
            if (userSkip > 1) {
                userSkip -= 1;
                showUserManagement();
            }

            //to disable prev button if equal to one
            if (userSkip == 1) {
                btnPrevUsers.setClickable(false);
                btnPrevUsers.setBackgroundResource(R.drawable.pagination_button_inactive);

            }

        });
        btnNextUsers.setOnClickListener(v -> {
            if (btnNextUsers.getVisibility() == VISIBLE) {
                userSkip += 1;
                //make the previous button clickable again
                btnPrevUsers.setClickable(true);
                btnPrevUsers.setBackgroundResource(R.drawable.pagination_button_active);
                showUserManagement();
            }


        });
    }

    //will set this after successful pick an image
    TextView imagePathText;


    private void showCreateNewsDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_news, null);
        TextInputEditText titleInput = dialogView.findViewById(R.id.newsTitle);
        TextInputEditText contentInput = dialogView.findViewById(R.id.newsContent);
        imageRemover = dialogView.findViewById(R.id.ivSelectedImageIcon);
        imagePathText = dialogView.findViewById(R.id.newsImagePath);
        Button selectImageBtn = dialogView.findViewById(R.id.selectImageBtn);
        Spinner tagSpinner = dialogView.findViewById(R.id.tagSpinner);
        Spinner sourceSpinner = dialogView.findViewById(R.id.sourceSpinner);

        // TAG SPINNER
        List<String> tagLists = new ArrayList<>();
        tagLists.add("Select Tag of News");
        tagLists.add("Popular");
        tagLists.add("Business");
        tagLists.add("Technology");
        tagLists.add("Other");

        ArrayAdapter<String> tagsAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tagLists
        );
        tagsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagSpinner.setAdapter(tagsAdapter);
        tagSpinner.setSelection(0);


        // SOURCE SPINNER
        List<String> sourceLists = new ArrayList<>();
        sourceLists.add("Select Source of News");
        sourceLists.add("DICT");
        sourceLists.add("PCO");

        ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                sourceLists
        );
        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sourceSpinner.setAdapter(sourceAdapter);
        sourceSpinner.setSelection(0);


        //to select image in gallery for news
        selectImageBtn.setOnClickListener(v -> {
            //check the permission and open gallery
            checkPermissionAndPickImage();
        });
        //check if there's image selected

        //add onclick listener on image remover
        imageRemover.setOnClickListener(v -> {
            if (isImageUploaded) {
                isImageUploaded = false;
                imageRemover.setImageDrawable(null);
                imageRemover.setBackgroundResource(android.R.drawable.ic_menu_gallery);
                currentPhotoUri = null;
                imagePathText.setText("No image selected");
            }
        });

        //create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create News Post");
        builder.setView(dialogView);
        builder.setPositiveButton("Publish", null); //
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {

            String title = titleInput.getText().toString().trim();
            String content = contentInput.getText().toString().trim();
            String newsTag = tagSpinner.getSelectedItem().toString();
            String newsSource = sourceSpinner.getSelectedItem().toString();
            List<File> listsImages = new ArrayList<>();
            List<String> listsTags = new ArrayList<>();
            //add the tags
            listsTags.add(newsTag);
            File imageFile = null;
            try {
                imageFile = globalUtility.uriToFile(currentPhotoUri, activity);
                listsImages.add(imageFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Validate title
            if (title.isEmpty()) {
                titleInput.setError("Title is required");
                return; // DON'T CLOSE DIALOG
            }

            // Validate content
            if (content.isEmpty()) {
                contentInput.setError("Content is required");
                return;
            }

            // Validate tag
            if (newsTag.equals("Select Tag of News")) {
                Toast.makeText(this, "Please select a tag", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate source
            if (newsSource.equals("Select source of News")) {
                Toast.makeText(this, "Please select a source", Toast.LENGTH_SHORT).show();
                return;
            }

            //call api
            NewsPostRequestModel request = new NewsPostRequestModel(
                    title, content, "active",
                    listsImages, listsTags, sourceSpinner.getSelectedItem().toString());

            adminAPIRequestHandler.createNews(request, new ResponseCallback<NewsAPIResponse>() {
                @Override
                public void onSuccess(NewsAPIResponse response) {
                    Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
                    //update the news
                    showNewsManagement();
                    dialog.dismiss(); // Close once it's updated
                }

                @Override
                public void onError(Throwable t) {
                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("CREATE_NEWS_ERROR", t.getMessage());
                }
            });
        });

    }

    // Pick image from gallery
    private void checkPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ requires READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                showImagePickerDialog();
            }
        } else {
            // Android 6-12 requires READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                showImagePickerDialog();
            }
        }
    }

    private void showImagePickerDialog() {
        String[] options = {"Choose from Gallery", "Cancel"};

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Select Profile Picture")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Choose from Gallery
                            openGallery();
                            break;
                        case 1: // Cancel
                            dialog.dismiss();
                            break;
                    }
                })
                .show();
    }

    private void setupActivityResultLaunchers() {
        // Permission launcher
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        showImagePickerDialog();
                    } else {
                        Toast.makeText(this, "Permission denied. Cannot access photos.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Pick image from gallery
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        //set the uri of select image to the global variable currentPhotoUri
                        if (!isImageUploaded) {
                            currentPhotoUri = result.getData().getData();
                            imagePathText.setText(result.getData().getDataString());
                            imageRemover.setImageDrawable(null);
                            imageRemover.setBackgroundResource(R.drawable.image_remover);
                            isImageUploaded = true;
                        }

                    }
                }
        );
    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }


    //if the arrow clicked
    @Override
    public boolean onSupportNavigateUp() {

        showLogoutDialog();
        return true;
    }

    private void showLogoutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    // Clear user session
                    clearUserSession();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void clearUserSession() {
        // Clear SharedPreferences or any stored session data
        String ACCESS_TOKEN_KEY = globalUtility.getValueInYAML(BuildConfig.ACCESS_TOKEN_KEY, context);
        String accessToken = sharedPreference.getData(ACCESS_TOKEN_KEY);
        usersAPIRequestHandler.logOutUser(accessToken, new ResponseCallback<ApiSuccessfulResponse>() {
            @Override
            public void onSuccess(ApiSuccessfulResponse response) {
                Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show();
                sharedPreference.clearPreference();
                Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                sharedPreference.clearPreference();
                Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}