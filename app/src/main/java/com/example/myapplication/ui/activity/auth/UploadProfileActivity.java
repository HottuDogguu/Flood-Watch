package com.example.myapplication.ui.activity.auth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.example.myapplication.ui.fragments.auth.PhotoSelectorFragment;

import java.io.IOException;
import java.io.InputStream;

public class UploadProfileActivity extends AppCompatActivity {
    private ImageView ivProfilePreview;
    private Button btnSelectPhoto;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_profile_picture);
        // Registers a photo picker activity launcher in single-select mode.
        btnSelectPhoto = (Button) findViewById(R.id.btnUploadPhoto);
        ivProfilePreview = (ImageView) findViewById(R.id.ivProfilePreview);

        // ✅ Register the Photo Picker launcher ONCE
        pickMedia = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        Log.i("PhotoPicker", "Selected URI: " + uri);
                        // ✅ Show image preview
                       ivProfilePreview.setImageURI(uri);
                    } else {
                        Log.i("PhotoPicker", "No media selected");
                    }
                });
        btnSelectPhoto.setOnClickListener(v -> {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());

        });
    }

}
