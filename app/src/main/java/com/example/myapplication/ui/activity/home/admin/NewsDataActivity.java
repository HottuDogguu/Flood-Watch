package com.example.myapplication.ui.activity.home.admin;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.BuildConfig;
import com.example.myapplication.Constants;
import com.example.myapplication.R;
import com.example.myapplication.data.models.api_response.NewsAPIResponse;
import com.example.myapplication.security.DataSharedPreference;
import com.example.myapplication.utils.GlobalUtility;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

public class NewsDataActivity extends AppCompatActivity {
    private DataSharedPreference dataSharedPreference;
    private GlobalUtility globalUtility;
    private Spinner sourceSpinner;
    private Context context;
    private String NEWS_DATA_KEY;
    private ImageView newsImage, imageChooser;
    private Button btnUpdate,btnDelete;
    private TextInputEditText newsTitle,newsContent;
    private TextView newsDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_news_single_item);
        initViews();




    }

    private void loadNewsData(){
        Gson gson = new Gson();
        NEWS_DATA_KEY = globalUtility.getValueInYAML(Constants.NEWS_DATA_KEY,context);

        String newsData = dataSharedPreference.getData(NEWS_DATA_KEY);
        NewsAPIResponse.NewsData currentNewsData = gson.fromJson(newsData, NewsAPIResponse.NewsData.class);


        //handle image safely
        if(currentNewsData.getImages() != null){
            for (NewsAPIResponse.NewsImage image: currentNewsData.getImages()) {

                Glide.with(context)
                        .load(image.getImg_url() != null && !image.getImg_url().isEmpty()? image.getImg_url() : R.drawable.news_image_palceholder)
                        .into(newsImage);
            }

        }
        Log.i("TESTER", newsData);
        // Populate spinner
        List<String> sources = Arrays.asList("Select Source", "DICT", "PCO");
        ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sources);
        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sourceSpinner.setAdapter(sourceAdapter);
        //set the title
        newsTitle.setText(currentNewsData.getTitle());
        newsDate.setText(globalUtility.formatDate(currentNewsData.getCreated_at()));
        newsContent.setText(currentNewsData.getContent());




    }
    private void initViews(){
        context = this;

        newsImage = findViewById(R.id.newsImage);
        imageChooser = findViewById(R.id.imageChooser);
        newsTitle = findViewById(R.id.newsTitleEdit);
        newsDate = findViewById(R.id.newsDate);
        newsContent = findViewById(R.id.newsContentEdit);
        btnUpdate = findViewById(R.id.btnEditSave);
        btnDelete = findViewById(R.id.btnDelete);
        sourceSpinner = findViewById(R.id.sourceSpinner);


        dataSharedPreference = DataSharedPreference.getInstance(context);
        globalUtility = new GlobalUtility();


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNewsData();
    }
}
