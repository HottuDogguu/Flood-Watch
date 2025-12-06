package com.example.myapplication.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Constants;
import com.example.myapplication.R;
import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.security.DataSharedPreference;
import com.example.myapplication.ui.activity.home.admin.UserDataActivity;
import com.example.myapplication.utils.GlobalUtility;
import com.google.gson.Gson;

import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.ViewHolder> {

    private List<ApiSuccessfulResponse.UserData> users;
    private Context context;
    private DataSharedPreference dataSharedPreference;
    private GlobalUtility globalUtility;



    public AdminUserAdapter(List<ApiSuccessfulResponse.UserData> users,
                            Context context,
                            DataSharedPreference dataSharedPreference,
                            GlobalUtility globalUtility) {
        this.users = users;
        this.context = context;
        this.dataSharedPreference = dataSharedPreference;
        this.globalUtility = globalUtility;

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

        //Convert data into json object
        Gson gson = new Gson();
        String userData = gson.toJson(user);

        holder.userName.setText(user.getFullname());
        holder.userEmail.setText(user.getEmail());
        holder.userAvatar.setText(String.valueOf(user.getFullname().charAt(0)));
        String userStatus = user.getStatus() != null && user.getStatus().equalsIgnoreCase("activated") ? "Active" : "Inactive";
        holder.statusBadge.setText(userStatus);
        holder.statusBadge.setBackgroundResource(
                user.getStatus() != null && user.getStatus().equalsIgnoreCase("activated") ? R.drawable.badge_active_user : R.drawable.badge_inactive_user
        );

        //when click the card, redirect to the UserDatActivity
        holder.userCard.setOnClickListener(v ->{
            String USERDATA = globalUtility.getValueInYAML(Constants.USER_DATA_INFORMATION_ADMIN, context);
            Intent intent = new Intent(context.getApplicationContext(), UserDataActivity.class);
            //save the data
            dataSharedPreference.saveData(USERDATA,userData);
            //start the activity
            context.startActivity(intent);
        });
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
        CardView userCard;

        ViewHolder(View view) {
            super(view);
            userCard = view.findViewById(R.id.userCard);
            userName = view.findViewById(R.id.userName);
            userEmail = view.findViewById(R.id.userEmail);
            userAvatar = view.findViewById(R.id.userAvatar);
            statusBadge = view.findViewById(R.id.statusBadge);
        }
    }
}