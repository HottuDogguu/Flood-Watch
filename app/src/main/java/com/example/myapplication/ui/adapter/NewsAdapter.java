package com.example.myapplication.ui.adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.models.api_response.NewsAPIResponse;
import com.example.myapplication.utils.GlobalUtility;

import java.util.List;
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsAdapterViewHolder> {
    private List<NewsAPIResponse.NewsData> newsItems;
    private Context context;
    private GlobalUtility globalUtility;

    public NewsAdapter(List<NewsAPIResponse.NewsData> newsItems,Context context) {
        this.newsItems = newsItems;

        this.context = context;
        globalUtility = new GlobalUtility();
    }
    @NonNull
    @Override
    public NewsAdapter.NewsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new NewsAdapterViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.NewsAdapterViewHolder holder, int position) {
        NewsAPIResponse.NewsData newsItem = newsItems.get(position);

        holder.sourceName.setText(newsItem.getSource());
        holder.timeAgo.setText(globalUtility.getTimeAgo(newsItem.getCreated_at()));
        holder.title.setText(newsItem.getTitle());

        // Set source icon based on source name
        if (newsItem.getSource().equals("PCO")) {
            holder.sourceIcon.setImageResource(R.drawable.ic_pco);
        } else {
            holder.sourceIcon.setImageResource(R.drawable.ic_dict);
        }

        // Handle image visibility
        List<NewsAPIResponse.NewsImage> images = newsItem.getImages();

        if (images != null && !images.isEmpty()) {
            String imageUrl = images.get(0).getImg_url();

            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                holder.newsImage.setVisibility(View.VISIBLE);

                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_user)
                        .error(R.drawable.ic_user)
                        .into(holder.newsImage);

            } else {
                // URL is null or empty
                holder.newsImage.setVisibility(View.VISIBLE);
                holder.newsImage.setImageResource(R.drawable.ic_placeholder_image);
            }
        } else {
            // images array is null or empty
            holder.newsImage.setVisibility(View.VISIBLE);
            holder.newsImage.setImageResource(R.drawable.ic_placeholder_image);
        }

    }

    @Override
    public int getItemCount() {
        return Math.min(newsItems.size(),10); // Show max 5 items in carousel
    }

    public static class NewsAdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView sourceIcon;
        TextView sourceName;
        TextView timeAgo;
        TextView title;
        ImageView newsImage;

        public NewsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            sourceIcon = itemView.findViewById(R.id.ivSourceIcon);
            sourceName = itemView.findViewById(R.id.tvSourceName);
            timeAgo = itemView.findViewById(R.id.tvTimeAgo);
            title = itemView.findViewById(R.id.tvTitle);
            newsImage = itemView.findViewById(R.id.ivNewsImage);
        }
    }
}
