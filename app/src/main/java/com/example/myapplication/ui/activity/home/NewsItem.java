package com.example.myapplication.ui.activity.home;

public class NewsItem {
    private int id;
    private String source;
    private String title;
    private String timeAgo;
    private String imageUrl;
    private String category;

    public NewsItem(int id, String source, String title, String timeAgo, String imageUrl, String category) {
        this.id = id;
        this.source = source;
        this.title = title;
        this.timeAgo = timeAgo;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}