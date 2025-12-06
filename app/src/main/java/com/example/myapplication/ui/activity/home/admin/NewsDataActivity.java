package com.example.myapplication.ui.activity.home.admin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.myapplication.Constants;
import com.example.myapplication.R;
import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.admin.NewsPostRequestModel;
import com.example.myapplication.data.models.api_response.NewsAPIResponse;
import com.example.myapplication.data.respository.AdminAPIRequestHandler;
import com.example.myapplication.security.DataSharedPreference;
import com.example.myapplication.utils.GlobalUtility;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewsDataActivity extends AppCompatActivity {
    private DataSharedPreference dataSharedPreference;
    private Uri currentPhotoUri;

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private GlobalUtility globalUtility;
    private AdminAPIRequestHandler adminAPIRequestHandler;
    private Spinner sourceSpinner,tagsSpinner;
    private Context context;
    private Activity activity;
    private String NEWS_DATA_KEY;
    private ImageView newsImage, imageChooser;
    private Button btnUpdate,btnDelete;
    private TextInputEditText newsTitle,newsContent;
    private TextView newsDate;
    private CheckBox cbActivated;
    //to get the current data
    NewsAPIResponse.NewsData currentNewsData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_news_single_item);
        initViews();

        //load data
        loadNewsData();

        //set up the file chooser
        setupActivityResultLaunchers();

        //handle on click listeners
        handleOnClickListeners();


    }

    private void handleOnClickListeners(){
        //listener for image chooser
        newsImageSelector();

        //listener for checkbox
        handleCheckboxListeners();

        //handle update btn
        handleUpdateOnclickListener();


    }

    private void loadNewsData(){
        Gson gson = new Gson();
        NEWS_DATA_KEY = globalUtility.getValueInYAML(Constants.NEWS_DATA_KEY,context);

        String newsData = dataSharedPreference.getData(NEWS_DATA_KEY);
         currentNewsData = gson.fromJson(newsData, NewsAPIResponse.NewsData.class);


        //handle image safely
        if(currentNewsData.getImages() != null){
            for (NewsAPIResponse.NewsImage image: currentNewsData.getImages()) {

                Glide.with(context)
                        .load(image.getImg_url() != null && !image.getImg_url().isEmpty()? image.getImg_url() : R.drawable.news_image_palceholder)
                        .into(newsImage);
            }

        }
        // source spinner
        List<String> sources = Arrays.asList("Select Source", "DICT", "PCO");
        ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sources);
        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sourceSpinner.setAdapter(sourceAdapter);

        sourceSpinner.setSelection(sources.indexOf(currentNewsData.getSource()));

        //tags spinner
        List<String> tags = Arrays.asList("Select Tags","Popular", "Business","Technology", "Other");
        ArrayAdapter<String> tagsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tags);
        tagsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tagsSpinner.setAdapter(tagsAdapter);
        int newsTag = 0;
        //handle the tags for all, it because it is empty.
        if(!currentNewsData.getTags().isEmpty()){
            newsTag = tags.indexOf(currentNewsData.getTags().get(0));
        }
        tagsSpinner.setSelection(newsTag);

        //set the title
        newsTitle.setText(currentNewsData.getTitle());
        newsDate.setText(globalUtility.formatDate(currentNewsData.getCreated_at()));
        newsContent.setText(currentNewsData.getContent());
        //set Text for cb and setChecked
        cbActivated.setText(currentNewsData.getStatus());
        cbActivated.setChecked(currentNewsData.getStatus().equalsIgnoreCase("Active"));
    }

    private void newsImageSelector(){
        imageChooser.setOnClickListener(v ->{
            checkPermissionAndPickImage();
        });
    }

    //checkbox click listeners
    @SuppressLint("SetTextI18n")
    private void handleCheckboxListeners(){
        cbActivated.setOnClickListener(v ->{
            if(cbActivated.isChecked()){
                cbActivated.setText("Active");
            }else{
                cbActivated.setText("Inactive");

            }
        });
    }


    private void handleUpdateOnclickListener(){
        btnUpdate.setOnClickListener(v ->{
            //validate fields
            String title = newsTitle.getText().toString();
            String content = newsContent.getText().toString();
            String newsSource = sourceSpinner.getSelectedItem().toString();
            String newsTags = tagsSpinner.getSelectedItem().toString();
            String newsStatus = cbActivated.getText().toString();


            List<File> listsImages = new ArrayList<>();
            List<String> listsTags = new ArrayList<>();
            //add the tags
            listsTags.add(newsTags);
            File imageFile = null;
            try {
                imageFile = globalUtility.uriToFile(currentPhotoUri,activity);
                listsImages.add(imageFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            NewsPostRequestModel request = new NewsPostRequestModel(
                    title,
                    content,
                    newsStatus,
                    listsImages,
                    listsTags,
                    newsSource
            );
            //call the api
            adminAPIRequestHandler.updateNews(currentNewsData.get_id(), request, new ResponseCallback<NewsAPIResponse>() {
                @Override
                public void onSuccess(NewsAPIResponse response) {
                    Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NewsDataActivity.this, AdminActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(Throwable t) {

                }
            });
        });
    }
    private void initViews(){
        context = this;
        activity = this;

        newsImage = findViewById(R.id.newsImage);
        imageChooser = findViewById(R.id.imageChooser);
        newsTitle = findViewById(R.id.newsTitleEdit);
        newsDate = findViewById(R.id.newsDate);
        newsContent = findViewById(R.id.newsContentEdit);
        btnUpdate = findViewById(R.id.btnEditSave);
        btnDelete = findViewById(R.id.btnDelete);
        sourceSpinner = findViewById(R.id.sourceSpinner);
        tagsSpinner = findViewById(R.id.tagsSpinner);
        cbActivated = findViewById(R.id.cbActivated);

        adminAPIRequestHandler = new AdminAPIRequestHandler(activity,context);

        dataSharedPreference = DataSharedPreference.getInstance(context);
        globalUtility = new GlobalUtility();
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
                            currentPhotoUri = result.getData().getData();
                            if(currentPhotoUri != null){
                                setProfilePictureUsingURL(currentPhotoUri,newsImage);
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

    private <T> void setProfilePictureUsingURL(T profileUrl, ImageView intoImage) {
        Glide.with(this)
                .load(profileUrl)
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .into(intoImage);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
