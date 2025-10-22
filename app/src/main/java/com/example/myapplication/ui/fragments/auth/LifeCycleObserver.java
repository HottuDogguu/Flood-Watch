package com.example.myapplication.ui.fragments.auth;

import android.net.Uri;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.myapplication.calbacks.auth.OnImageSelectedListener;

public class LifeCycleObserver implements DefaultLifecycleObserver {
    private ActivityResultRegistry mRegistry;
    private ActivityResultLauncher<String> mGetContent;
    private OnImageSelectedListener listener;

    LifeCycleObserver(@NonNull ActivityResultRegistry registry,
                      @NonNull OnImageSelectedListener listener) {
        this.mRegistry = registry;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        mGetContent = mRegistry.register("image_key", owner, new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri != null) {
                            listener.onImageSelected(uri);
                        }

                    }
                });
    }

    public void selectImage() {
        // Open the activity to select an image
        mGetContent.launch("image/*");
    }
}

