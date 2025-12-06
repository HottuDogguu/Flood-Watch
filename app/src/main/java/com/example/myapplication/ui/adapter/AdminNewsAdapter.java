package com.example.myapplication.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.BuildConfig;
import com.example.myapplication.Constants;
import com.example.myapplication.R;
import com.example.myapplication.data.models.api_response.NewsAPIResponse;
import com.example.myapplication.security.DataSharedPreference;
import com.example.myapplication.ui.activity.home.admin.NewsDataActivity;
import com.example.myapplication.utils.GlobalUtility;
import com.google.gson.Gson;

import java.util.List;

public class AdminNewsAdapter extends RecyclerView.Adapter<AdminNewsAdapter.ViewHolder> {

    private List<NewsAPIResponse.NewsData> newsList;
    private GlobalUtility globalUtility;
    private DataSharedPreference sharedPreference;
    private Context context ;
    private String NEWS_DATA_KEY ;


    public AdminNewsAdapter(List<NewsAPIResponse.NewsData> newsList,
                            Context context,
                            GlobalUtility globalUtility,
                            DataSharedPreference sharedPreference) {
        this.newsList = newsList;
        this.context = context;
        this.globalUtility = globalUtility;
        this.sharedPreference = sharedPreference;
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
        holder.title.setText(news.getTitle());
        holder.timeAgo.setText(globalUtility.getTimeAgo(news.getCreated_at()));
        holder.sourceName.setText(news.getSource());

        // Set source icon based on source name
        if (news.getSource().equals("PCO")) {
            holder.sourceIcon.setImageResource(R.drawable.ic_pco);
        } else {
            holder.sourceIcon.setImageResource(R.drawable.ic_dict);
        }
        // Handle image visibility
        List<NewsAPIResponse.NewsImage> images = news.getImages();

        if (images != null && !images.isEmpty()) {
            String imageUrl = images.get(0).getImg_url();

            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                holder.newsImage.setVisibility(View.VISIBLE);

                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_placeholder_image)
                        .error(R.drawable.ic_placeholder_image)
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
        //handle on click on specific data
        holder.newsCard.setOnClickListener(v ->{
            // convert data into json
            Gson gson = new Gson();

            //convert to json
            String newsData = gson.toJson(newsList.get(position));

            //get the data from intent
            NEWS_DATA_KEY = globalUtility.getValueInYAML(Constants.NEWS_DATA_KEY,context);
            sharedPreference.saveData(NEWS_DATA_KEY,newsData);

            // then go to the NewsDataActivity

            Intent intent = new Intent(context.getApplicationContext(), NewsDataActivity.class);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView sourceIcon;
        TextView sourceName;
        TextView timeAgo;
        TextView title;
        ImageView newsImage;
        CardView newsCard;

        ViewHolder(View view) {
            super(view);
            newsCard = itemView.findViewById(R.id.newsCard);
            sourceIcon = itemView.findViewById(R.id.ivSourceIcon);
            sourceName = itemView.findViewById(R.id.tvSourceName);
            timeAgo = itemView.findViewById(R.id.tvTimeAgo);
            title = itemView.findViewById(R.id.tvTitle);
            newsImage = itemView.findViewById(R.id.ivNewsImage);
        }
    }
}