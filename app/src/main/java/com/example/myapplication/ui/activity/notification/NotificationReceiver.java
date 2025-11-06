package com.example.myapplication.ui.activity.notification;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.ui.activity.home.HomeActivity;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String title = intent.getStringExtra("title");
        String body = intent.getStringExtra("body");
        String category = intent.getStringExtra("category");
        String source = intent.getStringExtra("source");

        if (action != null) {
            switch (action) {
                case "VIEW_ACTION":
                    // Open the app with notification details
                    Intent mainIntent = new Intent(context, HomeActivity.class);
                    mainIntent.putExtra("from_notification", true);
                    mainIntent.putExtra("notification_title", title);
                    mainIntent.putExtra("notification_body", body);
                    mainIntent.putExtra("notification_category", category);
                    mainIntent.putExtra("notification_source", source);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(mainIntent);

                    Toast.makeText(context, "Opening: " + title, Toast.LENGTH_SHORT).show();
                    break;

                case "DISMISS_ACTION":
                    // Dismiss the notification
                    NotificationManagerCompat.from(context).cancel(getNotificationId(intent));
                    Toast.makeText(context, "Notification dismissed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private int getNotificationId(Intent intent) {
        // Generate the same ID that was used to create the notification
        String category = intent.getStringExtra("category");
        String title = intent.getStringExtra("title");
        String baseId = category + "_" + title + "_" + intent.getLongExtra("timestamp", 0);
        return Math.abs(baseId.hashCode());
    }
}