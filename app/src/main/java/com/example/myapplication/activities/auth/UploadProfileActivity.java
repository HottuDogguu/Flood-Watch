package com.example.myapplication.activities.auth;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.myapplication.R;
import com.example.myapplication.ui.fragments.auth.PhotoSelectorFragment;

public class UploadProfileActivity extends AppCompatActivity {
    private ImageView ivProfilePreview;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_profile_picture);
        ivProfilePreview = (ImageView) findViewById(R.id.ivProfilePreview);

         getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentSelectPhoto,
                        PhotoSelectorFragment.class,null)
                .setReorderingAllowed(true)
                .addToBackStack("name")
                .commit();
    }
    public void showSelectedImage(Uri uri) {
        if (uri != null) {
            ivProfilePreview.setImageURI(uri);
        }
    }

}
