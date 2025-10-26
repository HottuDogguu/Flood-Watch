package com.example.myapplication.ui.activity.auth;


import android.app.Activity;

import android.content.Intent;
import android.net.Uri;

import android.os.Bundle;

import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.example.myapplication.calbacks.auth.AuthCallback;
import com.example.myapplication.data.models.auth.UploadPhotoResponse;
import com.example.myapplication.data.respository.auth.AuthenticationAPI;
import com.example.myapplication.security.DataStorageManager;

import com.example.myapplication.ui.activity.BaseActivity;
import com.example.myapplication.ui.activity.HomeActivity;
import com.example.myapplication.utils.GlobalUtility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UploadProfileActivity extends BaseActivity {
    private ImageView ivProfilePreview;
    private Button btnSelectPhoto;
    private Button saveAndContinue;
    private GlobalUtility globalUtility;

    private Activity activity;
    private Uri uri;
    private AuthenticationAPI authenticationAPI;
    private DataStorageManager dataStoreManager;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_profile_picture);
        activity = this;
        // Registers a photo picker activity launcher in single-select mode.
        btnSelectPhoto = (Button) findViewById(R.id.btnUploadPhoto);
        ivProfilePreview = (ImageView) findViewById(R.id.ivProfilePreview);
        saveAndContinue = (Button) findViewById(R.id.btnContinue);
        dataStoreManager = DataStorageManager.getInstance(this);


        authenticationAPI = new AuthenticationAPI(activity);
        globalUtility = new GlobalUtility();


        pickMedia = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        Log.i("PhotoPicker", "Selected URI: " + uri);
                        // ✅ Show image preview
                        ivProfilePreview.setImageURI(uri);
                        this.uri = uri;
                        Toast.makeText(activity, "File path" + this.uri, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("PhotoPicker", "No media selected");
                    }
                });
        btnSelectPhoto.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        saveAndContinue.setOnClickListener(v -> {
            try {
                if (this.uri != null) {
                    File imageFile = globalUtility.uriToFile(this.uri, activity);
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
                    MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image_file", imageFile.getName(), requestFile);

                    //to get the access token in data store
                    Disposable token = dataStoreManager.getString(BuildConfig.ACCESS_TOKEN_KEY)

                            .subscribe(access_token -> { //
                                Toast.makeText(activity, "message: " + access_token, Toast.LENGTH_SHORT).show();
                          authenticationAPI.uploadProfilePhoto(imagePart, "Bearer "+access_token, new AuthCallback<UploadPhotoResponse>() {
                               @Override
                              public void onSuccess(UploadPhotoResponse response) {
                                  Toast.makeText(activity, "message"+response.getMessage(), Toast.LENGTH_SHORT).show();
                               }

                               @Override
                               public void onError(Throwable t) {
                                  Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                               }
                            });
                            });
                    compositeDisposable.add(token);

                } else {
                    Toast.makeText(activity, "No selected photo", Toast.LENGTH_SHORT).show();
                }

//                Intent intent = new Intent(UploadProfileActivity.this, HomeActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//                finish();
            } catch (IOException e) {
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }

}
