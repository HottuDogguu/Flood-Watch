package com.example.myapplication.data.respository.users;

import android.app.Activity;

import com.example.myapplication.data.models.users.UsersUpdateInformationResponse;
import com.example.myapplication.data.network.APIBuilder;
import com.example.myapplication.data.network.endpoints.users.UserUpdateInformation;
import com.example.myapplication.utils.GlobalUtility;

public class UsersAPIRequestHandler {

    private Activity activity;
    private APIBuilder api;
    private GlobalUtility globalUtility;


    public UsersAPIRequestHandler(Activity activity) {
        this.activity = activity;
        this.api = new APIBuilder();
        this.globalUtility = new GlobalUtility();

    }

    public void updateUserInfo(){
//        UserUpdateInformation updateInformation = this.api.getRetrofit().create(UserUpdateInformation.class);
//    updateInformation.updateInfo();
    }


}
