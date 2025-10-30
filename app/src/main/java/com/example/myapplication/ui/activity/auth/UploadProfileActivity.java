package com.example.myapplication.ui.activity.auth;


import android.app.Activity;

import android.content.Context;
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

import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.auth.UploadPhotoResponse;
import com.example.myapplication.data.respository.auth.AuthenticationAPIRequestHandler;
import com.example.myapplication.security.DataStorageManager;

import com.example.myapplication.ui.activity.BaseActivity;
import com.example.myapplication.utils.GlobalUtility;

import java.io.File;
import java.io.IOException;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UploadProfileActivity extends BaseActivity {
    private ImageView ivProfilePreview;
    private Button btnSelectPhoto;
    private Button saveAndContinue;
    private GlobalUtility globalUtility;

    private Activity activity;
    private Context context;
    private Uri uri;
    private AuthenticationAPIRequestHandler authenticationAPI;
    private DataStorageManager dataStoreManager;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_profile_picture);
        activity = this;
        context = this;
        // Registers a photo picker activity launcher in single-select mode.
        btnSelectPhoto = (Button) findViewById(R.id.btnUploadPhoto);
        ivProfilePreview = (ImageView) findViewById(R.id.ivProfilePreview);
        saveAndContinue = (Button) findViewById(R.id.btnContinue);
        dataStoreManager = DataStorageManager.getInstance(this);


        authenticationAPI = new AuthenticationAPIRequestHandler(activity, this);
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
                    String accessTokenKey = globalUtility.getValueInYAML(BuildConfig.ACCESS_TOKEN_KEY, context);
                    Disposable token = dataStoreManager.getString(accessTokenKey)
                            .subscribe(access_token -> { //
                        authenticationAPI.uploadProfilePhoto(imagePart, "Bearer " + access_token, new ResponseCallback<UploadPhotoResponse>() {
                            @Override
                            public void onSuccess(UploadPhotoResponse response) {
                                Toast.makeText(activity, "message" + response.getMessage(), Toast.LENGTH_SHORT).show();
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
