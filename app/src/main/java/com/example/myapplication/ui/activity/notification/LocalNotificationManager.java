package com.example.myapplication.ui.activity.notification;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.Constants;
import com.example.myapplication.R;

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
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID )
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            NotificationManagerCompat manager = NotificationManagerCompat.from(context);
            manager.notify((int) System.currentTimeMillis(), builder.build());
        }

    }
}
