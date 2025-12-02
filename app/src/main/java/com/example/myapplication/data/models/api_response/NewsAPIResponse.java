package com.example.myapplication.data.models.api_response;

import java.util.ArrayList;
import java.util.List;

public class NewsAPIResponse {
    String status, message;
    List<NewsData> data;
    PaginatedData paginated;

    public PaginatedData getPaginated() {
        return paginated != null ? paginated : new PaginatedData();
    }

    public void setPaginated(PaginatedData paginated) {
        this.paginated = paginated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<NewsData> getData() {
        return data != null ? data : new ArrayList<NewsData>();
    }

    public void setData(List<NewsData> data) {
        this.data = data;
    }

    public static class PaginatedData{
        int start_page, end_page, total_records;
        boolean has_next;

        public int getStart_page() {
            return start_page;
        }

        public void setStart_page(int start_page) {
            this.start_page = start_page;
        }

        public int getEnd_page() {
            return end_page;
        }

        public void setEnd_page(int end_page) {
            this.end_page = end_page;
        }

        public int getTotal_records() {
            return total_records;
        }

        public void setTotal_records(int total_records) {
            this.total_records = total_records;
        }

        public boolean isHas_next() {
            return has_next;
        }

        public void setHas_next(boolean has_next) {
            this.has_next = has_next;
        }
    }

    public static  class NewsData {
        String user_id,title,content,status,source,created_at;
        List<NewsImage> images;
        List<String> tags;


        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getSource() {
            return source != null ? source : "";
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<NewsImage> getImages() {
            return images != null ? images : new ArrayList<>();
        }

        public void setImages(List<NewsImage> images) {
            this.images = images;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }
    }

    public static class NewsImage{
        String id, public_key,img_url,created_at;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPublic_key() {
            return public_key != null ? public_key : "";
        }

        public void setPublic_key(String public_key) {
            this.public_key = public_key;
        }

        public String getImg_url() {
            return img_url  != null ? img_url : "";
        }

        public void setImg_url(String img_url) {
            this.img_url = img_url;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
    }
}
