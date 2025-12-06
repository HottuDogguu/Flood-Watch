package com.example.myapplication.ui.activity.home.admin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.BuildConfig;
import com.example.myapplication.Constants;
import com.example.myapplication.R;
import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.admin.UsersPutRequestModel;
import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.models.api_response.NewsAPIResponse;
import com.example.myapplication.data.respository.AdminAPIRequestHandler;
import com.example.myapplication.security.DataSharedPreference;
import com.example.myapplication.utils.GlobalUtility;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

public class UserDataActivity extends AppCompatActivity {

    private Activity activity;
    private Context  context;
    private GlobalUtility globalUtility;
    private DataSharedPreference dataSharedPreference;
    private TextInputLayout tilFullname, tilContactNo;
    private ImageView imageView;
    private TextInputEditText etFullname, etContactNo,etStreet,etBarangay,etCity;
    private Button btnUpdate, btnDelete;
    private ApiSuccessfulResponse.UserData curreUserData;
    private AdminAPIRequestHandler apiRequestHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_user_information);

        //initialized views
        initViews();

        //handle onClickListener
        handleOnclickListener();
    }

    //load data
    private void loadUserData(){
        //get the user data from sharedPreference
        Gson gson = new Gson();
        String USERDATA = globalUtility.getValueInYAML(Constants.USER_DATA_INFORMATION_ADMIN, context);
        curreUserData = gson.fromJson(dataSharedPreference.getData(USERDATA), ApiSuccessfulResponse.UserData.class);

        //set the data
        Glide.with(context)
                .load(curreUserData.getProfileImage().getImg_url())
                .into(imageView);

        etFullname.setText(curreUserData.getFullname());
        etContactNo.setText(curreUserData.getContact_number());
        etStreet.setText(curreUserData.getAddress().getStreet());
        etBarangay.setText(curreUserData.getAddress().getBarangay());
        etCity.setText(curreUserData.getAddress().getCity());
    }

    private void handleOnclickListener(){
        btnUpdate.setOnClickListener(v ->{
            showUpdateAlertDialog();
        });

        btnDelete.setOnClickListener(v ->{
            showDeleteAlertDialog();
        });
    }

    //initialized variables
    private void initViews(){
        context = this;
        activity = this;

        //initialized the widgets
        tilFullname = findViewById(R.id.tilFullname);
        tilContactNo = findViewById(R.id.tilContactNo);
        etFullname = findViewById(R.id.etFullname);
        etContactNo = findViewById(R.id.etContact);
        etStreet = findViewById(R.id.etStreet);
        etBarangay = findViewById(R.id.etBarangay);
        etCity = findViewById(R.id.etCity);
        btnDelete = findViewById(R.id.btnDeleteUser);
        btnUpdate = findViewById(R.id.btnUpdateUser);
        imageView = findViewById(R.id.userImage);

        globalUtility = new GlobalUtility();
        dataSharedPreference = DataSharedPreference.getInstance(context);
        apiRequestHandler = new AdminAPIRequestHandler(activity,context);

    }

    private void updateUserInformation(){
        String userId = curreUserData.getId();
        String street = etStreet.getText().toString();
        String barangay = etBarangay.getText().toString();
        String city = etCity.getText().toString();
        String fullname = etFullname.getText().toString();
        String contactNo = etContactNo.getText().toString();

        UsersPutRequestModel.UserAddress address = new UsersPutRequestModel.UserAddress(
                street, barangay,city,"Laguna");

        UsersPutRequestModel request = new UsersPutRequestModel(fullname,contactNo,address);
        apiRequestHandler.updateUserInformation(userId, request, new ResponseCallback<ApiSuccessfulResponse>() {
            @Override
            public void onSuccess(ApiSuccessfulResponse response) {
                Toast.makeText(activity, response.getMessage(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserDataActivity.this, AdminActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

    private void deleteUserInformation(){
        apiRequestHandler.deleteUserInformation(curreUserData.getId(), new ResponseCallback<ApiSuccessfulResponse>() {
            @Override
            public void onSuccess(ApiSuccessfulResponse response) {
                Toast.makeText(activity, response.getMessage(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserDataActivity.this, AdminActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ADMIN_DELETE_USER", t.getMessage());

            }
        });

    }
    private void showUpdateAlertDialog(){
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Update user")
                .setMessage("Are you sure you want to update this?")
                .setPositiveButton("Update", (dialog, which) -> {
                    // update users data
                    updateUserInformation();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void showDeleteAlertDialog(){
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete user")
                .setMessage("Are you sure you want to update this?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // update users data
                    deleteUserInformation();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }
}
