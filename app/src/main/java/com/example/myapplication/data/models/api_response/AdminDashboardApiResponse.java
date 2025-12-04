package com.example.myapplication.data.models.api_response;

public class AdminDashboardApiResponse {
    String message;
    UserData data;

    public UserData getData() {
        return data != null ? data : new UserData();
    }

    public void setData(UserData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class UserData{
        int total_users, total_news;

        public int getTotal_news() {
            return total_news;
        }

        public void setTotal_news(int total_news) {
            this.total_news = total_news;
        }

        public int getTotal_users() {
            return total_users;
        }

        public void setTotal_users(int total_users) {
            this.total_users = total_users;
        }
    }
}
