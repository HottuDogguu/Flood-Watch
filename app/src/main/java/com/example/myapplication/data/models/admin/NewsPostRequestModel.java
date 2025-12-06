package com.example.myapplication.data.models.admin;

import com.example.myapplication.data.models.auth.SignupPostRequest;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class NewsPostRequestModel {
    public String title;
    public String content;
    public String status;
    public List<File> images;   // image files
    public List<String> tags;   // tags (string list)
    public String source;

    public NewsPostRequestModel(String title, String content, String status, List<File> images, List<String> tags, String source) {
        this.title = title;
        this.content = content;
        this.status = status;
        this.images = images;
        this.tags = tags;
        this.source = source;
    }

}
