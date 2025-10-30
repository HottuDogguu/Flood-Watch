package com.example.myapplication.utils.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.myapplication.R;
import com.example.myapplication.data.models.users.UsersGetInformationResponse;
import com.example.myapplication.security.DataStorageManager;
import com.example.myapplication.utils.GlobalUtility;
import com.google.gson.Gson;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class BaseHomepageUtility {
    private DataStorageManager dataStorageManager;
    private Context context;
    private Activity activity;
    private CompositeDisposable compositeDisposable;
    private GlobalUtility globalUtility;


    public BaseHomepageUtility(Context context,
                               Activity activity
    ) {
        this.context = context;
        this.activity = activity;

        globalUtility = new GlobalUtility();
        dataStorageManager = DataStorageManager.getInstance(context);
        compositeDisposable = new CompositeDisposable();

    }

    public void loadNavBarProfileData(TextView navHeaderName,
                                      TextView navHeaderLocation,
                                      ImageView navHeaderImage,
                                      String USER_DATA_KEY) {
        Disposable disposable = dataStorageManager.getString(USER_DATA_KEY)
                .firstElement()
                .subscribe(data -> {
                    Gson gson = new Gson();
                    UsersGetInformationResponse.UserData userData = gson.fromJson(data, UsersGetInformationResponse.UserData.class);
                    String formattedAddress = userData.getAddress().getCity() + ", " + userData.getAddress().getProvince();
                    navHeaderName.setText(userData.getFullname());
                    navHeaderLocation.setText(formattedAddress);
                    String profileUrl = userData.getProfileImage().getImg_url();
                    Glide.with(context)
                            .load(profileUrl)
                            .circleCrop()
                            .placeholder(R.drawable.ic_user)
                            .into(navHeaderImage);
                });
        compositeDisposable.add(disposable);
    }


}
