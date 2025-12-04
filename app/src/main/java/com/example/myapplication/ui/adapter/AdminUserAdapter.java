//package com.example.myapplication.ui.adapter;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.myapplication.R;
//
//import java.util.List;
//
//public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.ViewHolder> {
//
//    private List<AdminUser> users;
//    private OnUserActionListener listener;
//
//    public interface OnUserActionListener {
//        void onAction(AdminUser user, String action);
//    }
//
//    public AdminUserAdapter(List<AdminUser> users, OnUserActionListener listener) {
//        this.users = users;
//        this.listener = listener;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_user, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        AdminUser user = users.get(position);
//
//        holder.userName.setText(user.name);
//        holder.userEmail.setText(user.email);
//        holder.userAvatar.setText(String.valueOf(user.name.charAt(0)));
//
//        holder.statusBadge.setText(user.isActive ? "Active" : "Inactive");
//        holder.statusBadge.setBackgroundResource(
//                user.isActive ? R.drawable.badge_active_user : R.drawable.badge_inactive_user
//        );
//
//        // Optional: Make the entire card clickable to view/edit user details
//        holder.itemView.setOnClickListener(v -> {
//            if (listener != null) {
//                listener.onAction(user, "view_details");
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return users.size();
//    }
//
//    static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView userName;
//        TextView userEmail;
//        TextView userAvatar;
//        TextView statusBadge;
//
//        ViewHolder(View view) {
//            super(view);
//            userName = view.findViewById(R.id.userName);
//            userEmail = view.findViewById(R.id.userEmail);
//            userAvatar = view.findViewById(R.id.userAvatar);
//            statusBadge = view.findViewById(R.id.statusBadge);
//        }
//    }
//}