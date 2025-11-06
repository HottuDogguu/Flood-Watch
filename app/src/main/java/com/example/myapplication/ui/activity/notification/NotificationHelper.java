package com.example.myapplication.ui.activity.notification;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class NotificationHelper {

    public static final String CHANNEL_HIGH = "channel_high";
    public static final String CHANNEL_MEDIUM = "channel_medium";
    public static final String CHANNEL_LOW = "channel_low";

    public static void createChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);

            // High priority channel
            NotificationChannel highChannel = new NotificationChannel(
                    CHANNEL_HIGH,
                    "Important Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            highChannel.setDescription("Important notifications with sound and vibration");

            // Medium priority channel
            NotificationChannel mediumChannel = new NotificationChannel(
                    CHANNEL_MEDIUM,
                    "Regular Updates",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            mediumChannel.setDescription("Regular notifications");

            // Low priority channel
            NotificationChannel lowChannel = new NotificationChannel(
                    CHANNEL_LOW,
                    "Info Messages",
                    NotificationManager.IMPORTANCE_LOW
            );
            lowChannel.setDescription("Informational notifications");

            // Create all channels
            manager.createNotificationChannel(highChannel);
            manager.createNotificationChannel(mediumChannel);
            manager.createNotificationChannel(lowChannel);
        }
    }

}
