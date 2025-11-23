package com.example.myapplication.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.WindowInsets;
import android.view.WindowInsetsController;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.channels.FileChannel;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Enumeration;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.api_response.ApiErrorResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import retrofit2.Response;

public class GlobalUtility {


    private static final Logger log = LoggerFactory.getLogger(GlobalUtility.class);

    public GlobalUtility() {

    }


    public String getLocalIP() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                for (InetAddress addr : java.util.Collections.list(iface.getInetAddresses())) {
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "127.0.0.1";
    }

    public String getTimeAgo(String timestamp) {
        try {
            // Some timestamps might not have 'Z' (UTC indicator), so normalize it.
            if (!timestamp.endsWith("Z")) {
                timestamp = timestamp + "Z";
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
                    .withZone(ZoneId.of("UTC"));

            Instant instant = formatter.parse(timestamp, Instant::from);
            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("Asia/Manila"));
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Manila"));


            // Calculate difference
            long seconds = ChronoUnit.SECONDS.between(zonedDateTime, now);
            long minutes = ChronoUnit.MINUTES.between(zonedDateTime, now);
            long hours = ChronoUnit.HOURS.between(zonedDateTime, now);
            long days = ChronoUnit.DAYS.between(zonedDateTime, now);

            // Convert to human-readable format
            if (seconds < 60) {
                return "Just now";
            } else if (minutes < 60) {
                return minutes + " minute" + (minutes == 1 ? "" : "s") + " ago";
            } else if (hours < 24) {
                return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
            } else if (days < 7) {
                return days + " day" + (days == 1 ? "" : "s") + " ago";
            } else if (days < 30) {
                long weeks = days / 7;
                return weeks + " week" + (weeks == 1 ? "" : "s") + " ago";
            } else if (days < 365) {
                long months = days / 30;
                return months + " month" + (months == 1 ? "" : "s") + " ago";
            } else {
                long years = days / 365;
                return years + " year" + (years == 1 ? "" : "s") + " ago";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

    public void showExitDialog(Context context) {
        // Only proceed if context is an Activity
        if (!(context instanceof Activity)) return;
        Activity activity = (Activity) context;
        if (activity.isTaskRoot()) {
            new AlertDialog.Builder(context)
                    .setTitle("Exit App")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", (dialog, which) -> activity.finishAffinity())
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            // Default back behavior
            ((AppCompatActivity) activity).getOnBackPressedDispatcher().onBackPressed();
        }
    }

    @SuppressLint("WrongConstant")
    public void hideSystemUI(Activity activity) {
        // Android 11 and above
        final WindowInsetsController controller = activity.getWindow().getInsetsController();
        if (controller != null) {
            controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            controller.setSystemBarsBehavior(
                    WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE
            );
        }
    }

    public File uriToFile(Uri uri, Activity activity) throws IOException {
        File file = File.createTempFile("upload_", ".jpg", activity.getCacheDir());

        try (InputStream in = activity.getContentResolver().openInputStream(uri);
             FileOutputStream out = new FileOutputStream(file)) {

            if (in == null) {
                throw new IOException("Cannot open input stream from URI");
            }

            FileChannel inChannel = ((FileInputStream) in).getChannel();
            FileChannel outChannel = out.getChannel();

            long transferred = inChannel.transferTo(0, inChannel.size(), outChannel);

            if (transferred == 0) {
                throw new IOException("No bytes transferred - file may be empty");
            }

            Log.d("FileCopy", "File copied successfully. Size: " + file.length() + " bytes");
            return file;

        } catch (Exception e) {
            Log.e("FileCopy", "Error copying file: " + e.getMessage(), e);
            // Fallback to traditional copy if FileChannel fails
            return null;
        }
    }


    public void deleteFile(File file) {
        if (file != null && file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                Log.i("FILE_DELETE", "File deleted");
            } else {
                Log.w("FILE_DELETE", "Failed to delete file: " + file.getAbsolutePath());
            }
        } else {
            Log.w("FILE_DELETE", "File does not exist or is null");
        }
    }

    public <T> void parseAPIResponse(Response<T> response, ResponseCallback<T> callback) {
        if (response.isSuccessful() && response.body() != null) {
            callback.onSuccess(response.body());
        } else {
            if (response.errorBody() != null) {
                // Parse the error response using Gson
                try {
                    String errorBody = response.errorBody().string();
                    Log.e("API_RESPONSE", "Error body: " + errorBody);
                    Gson gson = new Gson();
                    ApiErrorResponse errorResponse = gson.fromJson(errorBody, ApiErrorResponse.class);
                    callback.onError(new Exception(errorResponse.getMessage()));
                } catch (JsonSyntaxException e) {
                    // If parsing fails, fall back to raw error
                    Log.e("JSON ERROR", "Failed to parse error response", e);
                    callback.onError(new Exception(e.getMessage()));
                } catch (IOException ioe) {
                    Log.e("JSON ERROR", "Failed to parse error response");
                    callback.onError(new Exception(ioe.getMessage()));
                }
            }
        }
    }

    public String formatAddress(String street, String barangay, String city) {
        return street + ", " + barangay + ", " + city + ", Laguna, Philippines";
    }

    public String getValueInYAML(String key, Context context) {
        Yaml yamlReader = new Yaml();
        try {
            InputStream inputStream = context.getAssets().open("secrets.yaml");
            Map<String, String> data = yamlReader.load(inputStream);
            inputStream.close();

            if (data != null) {
                //return the value
                return data.get(key);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    public String formatDateIntoHourOnly(String date) {
        if (date != null) {
            // Parse string into LocalDateTime
            LocalDateTime localDateTime = LocalDateTime.parse(date);

            // Convert to Philippine timezone
            ZonedDateTime phTime = localDateTime.atZone(ZoneId.of("UTC"))
                    .withZoneSameInstant(ZoneId.of("Asia/Manila"));

            // Format hour only (24-hour format)
            return phTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        }

        return "";
    }
}
