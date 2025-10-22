package com.example.myapplication.ui.fragments.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.activities.auth.UploadProfileActivity;

public class PhotoSelectorFragment extends Fragment {

    private Button btnUploadPhoto;
    private LifeCycleObserver myLifeCycleObserver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLifeCycleObserver = new LifeCycleObserver(requireActivity().getActivityResultRegistry(),
                uri -> {
                    if (getActivity() instanceof UploadProfileActivity) {
                        ((UploadProfileActivity) getActivity()).showSelectedImage(uri);
                    }
                }
        );
        getLifecycle().addObserver(myLifeCycleObserver);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        this.btnUploadPhoto.setOnClickListener(v -> {
            this.myLifeCycleObserver.selectImage();
        });
    }

    private void initViews(View view) {
        btnUploadPhoto = (Button) view.findViewById(R.id.btnUploadPhoto);
    }
}
