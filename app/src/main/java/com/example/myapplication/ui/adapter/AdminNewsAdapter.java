package com.example.myapplication.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.models.api_response.NewsAPIResponse;

import java.util.List;

public class AdminNewsAdapter extends RecyclerView.Adapter<AdminNewsAdapter.ViewHolder> {

    private List<NewsAPIResponse.NewsData> newsList;


    public AdminNewsAdapter(List<NewsAPIResponse.NewsData> newsList) {
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewsAPIResponse.NewsData news = newsList.get(position);
        holder.newsTitle.setText(news.getTitle());
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView newsTitle;
        TextView newsViews;
        TextView statusBadge;
        Button editBtn;
        Button deleteBtn;

        ViewHolder(View view) {
            super(view);
            newsTitle = view.findViewById(R.id.newsTitle);
            statusBadge = view.findViewById(R.id.statusBadge);
        }
    }
}