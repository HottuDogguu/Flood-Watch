package com.example.myapplication.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;

import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.ViewHolder> {

    private List<ApiSuccessfulResponse.UserData> users;



    public AdminUserAdapter(List<ApiSuccessfulResponse.UserData> users) {
        this.users = users;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ApiSuccessfulResponse.UserData user = users.get(position);

        holder.userName.setText(user.getFullname());
        holder.userEmail.setText(user.getEmail());
        holder.userAvatar.setText(String.valueOf(user.getFullname().charAt(0)));
        String userStatus = user.getStatus() != null && user.getStatus().equalsIgnoreCase("activated") ? "Active" : "Inactive";
        holder.statusBadge.setText(userStatus);
        holder.statusBadge.setBackgroundResource(
                user.getStatus() != null && user.getStatus().equalsIgnoreCase("activated") ? R.drawable.badge_active_user : R.drawable.badge_inactive_user
        );
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView userEmail;
        TextView userAvatar;
        TextView statusBadge;

        ViewHolder(View view) {
            super(view);
            userName = view.findViewById(R.id.userName);
            userEmail = view.findViewById(R.id.userEmail);
            userAvatar = view.findViewById(R.id.userAvatar);
            statusBadge = view.findViewById(R.id.statusBadge);
        }
    }
}