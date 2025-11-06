package com.example.myapplication.ui.activity.notification;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.Constants;
import com.example.myapplication.R;
import com.example.myapplication.ui.activity.home.HomeActivity;

public class LocalNotificationManager {
    public static void createChannels(Context context) {
        createChannel(context, Constants.FLOOD_ALERT, "Flood Alerts", "Flood-related warnings");
        createChannel(context, Constants.WEATHER_ALERT, "Weather Updates", "Rainfall or temperature updates");
        createChannel(context, Constants.EMERGENCY_ALERT, "Emergency Alerts", "Urgent emergency notifications");
    }

    private static void createChannel(Context context, String id, String name, String description) {
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(description);
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    public static void showNotification(Context context,
                                        String title,
                                        String message,
                                        String CHANNEL_ID) {
        // Check if we HAVE permission (not if we don't have it)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

            // Create intent to open your app when notification is clicked

            Intent intent = new Intent(context, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            // Get notification count from SharedPreferences
            int notificationCount = getNotificationCount(context);
            notificationCount++;
            saveNotificationCount(context, notificationCount);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setNumber(notificationCount)
                    .setContentIntent(pendingIntent) // This makes it open your app when clicked
                    .setAutoCancel(true) // This makes the notification disappear when clicked
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            NotificationManagerCompat manager = NotificationManagerCompat.from(context);
            manager.notify((int) System.currentTimeMillis(), builder.build());
        } else {
            Log.e("Notification", "No notification permission granted");
        }
    }

    // Manage notification count for badge
    private static int getNotificationCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("notifications_counter", Context.MODE_PRIVATE);
        return prefs.getInt("count", 0);
    }

    private static void saveNotificationCount(Context context, int count) {
        SharedPreferences prefs = context.getSharedPreferences("notifications_counter", Context.MODE_PRIVATE);
        prefs.edit().putInt("count", count).apply();
    }
}
