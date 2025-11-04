package com.example.myapplication.ui.activity.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<NewsItem> newsItems;

    public NewsAdapter(List<NewsItem> newsItems) {
        this.newsItems = newsItems;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem newsItem = newsItems.get(position);

        holder.sourceName.setText(newsItem.getSource());
        holder.timeAgo.setText(newsItem.getTimeAgo());
        holder.title.setText(newsItem.getTitle());

        // Set source icon based on source name
        switch (newsItem.getSource()) {
            case "DICT":
                holder.sourceIcon.setImageResource(R.drawable.ic_dict);
                break;
            case "PCO":
                holder.sourceIcon.setImageResource(R.drawable.ic_pco);
                break;
            default:
                holder.sourceIcon.setImageResource(R.drawable.ic_default_source);
                break;
        }

        // Show/hide image based on availability
        if (newsItem.getImageUrl() != null && !newsItem.getImageUrl().isEmpty()) {
            holder.newsImage.setVisibility(View.VISIBLE);
            // Load image using your preferred image loading library (Glide, Picasso, etc.)
            // Glide.with(holder.itemView.getContext()).load(newsItem.getImageUrl()).into(holder.newsImage);
        } else {
            holder.newsImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return newsItems.size();
    }

    public void updateNews(List<NewsItem> newItems) {
        this.newsItems = newItems;
        notifyDataSetChanged();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView sourceIcon;
        TextView sourceName;
        TextView timeAgo;
        TextView title;
        ImageView newsImage;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            sourceIcon = itemView.findViewById(R.id.ivSourceIcon);
            sourceName = itemView.findViewById(R.id.tvSourceName);
            timeAgo = itemView.findViewById(R.id.tvTimeAgo);
            title = itemView.findViewById(R.id.tvTitle);
            newsImage = itemView.findViewById(R.id.ivNewsImage);
        }
    }
}