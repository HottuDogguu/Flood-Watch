package com.example.myapplication.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.channels.FileChannel;
import java.util.Enumeration;

import androidx.appcompat.app.AppCompatActivity;

import java.util.function.Consumer;

public class GlobalUtility {


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

}
