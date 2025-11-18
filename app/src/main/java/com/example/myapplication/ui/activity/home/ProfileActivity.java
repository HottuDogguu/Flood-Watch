package com.example.myapplication.ui.activity.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.models.auth.SignupPostRequest;
import com.example.myapplication.data.models.users.UserNotificationSettingsRequest;
import com.example.myapplication.data.models.users.UsersUpdateInformationRequest;
import com.example.myapplication.data.respository.UsersAPIRequestHandler;
import com.example.myapplication.security.DataStorageManager;
import com.example.myapplication.ui.activity.auth.LoginActivity;
import com.example.myapplication.utils.GlobalUtility;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvUserName, tvLocation, tvEmail, tvPhone;
    private MaterialButton btnEditProfile;
    private SwitchMaterial switchFloodAlerts, switchWeatherUpdates, switchEmergencyAlerts;
    private LinearLayout btnChangePassword, btnPrivacyPolicy, btnLogout;
    private Uri currentPhotoUri;
    private ImageView ivProfilePicture, ivProfilePictureEdit;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private Activity activity;
    private Context context;
    private UsersAPIRequestHandler apiRequestHandler;
    private DataStorageManager dataStorageManager;
    private CompositeDisposable compositeDisposable;
    private GlobalUtility globalUtility;
    private TextView navFullname;
    private String USER_DATA_KEY;
    private String ACCESS_TOKEN_KEY;

    private final String TAG = "PROFILE_ACTIVITY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        initViews();

        // NEW: Setup activity result launchers
        setupActivityResultLaunchers();

        // Setup toolbar
        setupToolbar();

        // Load user data
        initialLoadUserData();

        // Setup click listeners
        setupClickListeners();

    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void updateProfileUI(ApiSuccessfulResponse.UserData userData) {
        String formattedAddress = globalUtility.formatAddress(
                userData.getAddress().getStreet(),
                userData.getAddress().getBarangay(),
                userData.getAddress().getCity());

        tvUserName.setText(userData.getFullname());
        tvEmail.setText(userData.getEmail());
        tvPhone.setText(userData.getPersonalInformation().getContact_number());
        tvLocation.setText(formattedAddress);
    }

    private void setProfilePictureUsingURL(String profileUrl) {
        Glide.with(this)
                .load(profileUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .into(ivProfilePicture);
    }

    private void setProfilePictureWithUri(Uri uri) {
        ivProfilePicture.setImageURI(uri);
    }


    private void initialLoadUserData() {
        Disposable disposable = dataStorageManager.getString(USER_DATA_KEY)
                .firstElement()
                .subscribe(data -> {
                    Gson gson = new Gson();
                    ApiSuccessfulResponse.UserData userData = gson.fromJson(data, ApiSuccessfulResponse.UserData.class);
                    updateProfileUI(userData); // update the data in fields
                    setProfilePictureUsingURL(userData.getProfileImage().getImg_url());
                });

        compositeDisposable.add(disposable);
    }

    private void loadUserData() {
        Disposable disposable = dataStorageManager.getString(USER_DATA_KEY)
                .firstElement()
                .subscribe(data -> {
                    Gson gson = new Gson();
                    ApiSuccessfulResponse.UserData userData = gson.fromJson(data, ApiSuccessfulResponse.UserData.class);
                    updateProfileUI(userData); // update the data in fields
                });
        compositeDisposable.add(disposable);
    }

    private void loadNotificationPreferences() {
        Disposable disposable = dataStorageManager.getString(USER_DATA_KEY)
                .firstElement()
                .subscribe(data -> {
                    Gson gson = new Gson();
                    ApiSuccessfulResponse.UserData userData = gson.fromJson(data, ApiSuccessfulResponse.UserData.class);
                    switchFloodAlerts.setChecked(userData.isIs_flood_alert_on());
                    switchWeatherUpdates.setChecked(userData.isIs_weather_updates_on());
                    switchEmergencyAlerts.setChecked(userData.isIs_emergency_alert_on());
                });
        compositeDisposable.add(disposable);
    }

    private void setupClickListeners() {
        // Edit Profile button
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

        // Change Password - UPDATED
        btnChangePassword.setOnClickListener(v -> {
            ChangePasswordBottomSheet bottomSheet = new ChangePasswordBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), "ChangePasswordBottomSheet");
        });

        // Privacy Policy
        btnPrivacyPolicy.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.termsfeed.com/live/53986221-556f-4ac5-9448-709fd2763fd5"));
            startActivity(browserIntent);
        });

        // Logout
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    // Clear user session
                    clearUserSession();
                    // Navigate to MainActivity (Login screen)

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void clearUserSession() {
        // Clear SharedPreferences or any stored session data
        Disposable disposable = dataStorageManager.getString(ACCESS_TOKEN_KEY)
                .firstElement()
                .subscribe(accessToken -> {
                    apiRequestHandler.logOutUser(accessToken, new ResponseCallback<ApiSuccessfulResponse>() {
                        @Override
                        public void onSuccess(ApiSuccessfulResponse response) {
                            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show();
                            dataStorageManager.clearAll();
                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(Throwable t) {
                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                            dataStorageManager.clearAll();
                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void onBackPressed() {
        updateUserNotificationSetting();

    }

    private void showEditProfileDialog() {
        Gson gson = new Gson();
        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.drawable.screen_background_light);
        }
        // Get dialog views
        TextInputEditText etEditName = dialogView.findViewById(R.id.et_edit_name);
        TextInputEditText etEditEmail = dialogView.findViewById(R.id.et_edit_email);
        TextInputEditText etEditPhone = dialogView.findViewById(R.id.et_edit_phone);
        TextInputEditText etEditSecondPhone = dialogView.findViewById(R.id.et_edit_second_phone);

        TextInputEditText etStreetLoc = dialogView.findViewById(R.id.et_edit_street_location);
        TextInputEditText etBarnagayLoc = dialogView.findViewById(R.id.et_edit_barangay_location);
        TextInputEditText etCityLoc = dialogView.findViewById(R.id.et_edit_city_location);
//        TextInputEditText etEditAddress = dialogView.findViewById(R.id.et_edit_address);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_cancel);
        MaterialButton btnSave = dialogView.findViewById(R.id.btn_save);
        TextView tvChangePhoto = dialogView.findViewById(R.id.tv_change_photo);
        ivProfilePictureEdit = dialogView.findViewById(R.id.iv_profile_picture_edit);

        // Load current data into dialog fields
        Disposable disposable = dataStorageManager.getUserData(USER_DATA_KEY)
                .firstElement()
                .subscribe(data -> {
                    ApiSuccessfulResponse.UserData userDataFromGson = gson.fromJson(data, ApiSuccessfulResponse.UserData.class);
                    etEditName.setText(userDataFromGson.getFullname());
                    etEditEmail.setText(userDataFromGson.getEmail());
                    etEditPhone.setText(userDataFromGson.getPersonalInformation().getContact_number());
                    etEditSecondPhone.setText(userDataFromGson.getPersonalInformation().getSecond_number());
                    etStreetLoc.setText(userDataFromGson.getAddress().getStreet());
                    etBarnagayLoc.setText(userDataFromGson.getAddress().getBarangay());
                    etCityLoc.setText(userDataFromGson.getAddress().getCity());

                    ivProfilePictureEdit.setImageDrawable(ivProfilePicture.getDrawable());
                });
        compositeDisposable.add(disposable);

        // NEW: Change photo click
        tvChangePhoto.setOnClickListener(v -> {
            checkPermissionAndPickImage();
        });

        // Cancel button
        btnCancel.setOnClickListener(v -> {
            this.currentPhotoUri = null;

            dialog.dismiss();
        });
        // Save button
        btnSave.setOnClickListener(v -> {
            Disposable dispo = dataStorageManager.getString(ACCESS_TOKEN_KEY)
                    .firstOrError()
                    .subscribe(accessToken -> {
                        //User Personal information
                        SignupPostRequest.PersonalInformation personalInformation = new SignupPostRequest.PersonalInformation(
                                Objects.requireNonNull(etEditPhone.getText()).toString(),
                                Objects.requireNonNull(etEditSecondPhone.getText()).toString()
                        );
                        //User Address
                        SignupPostRequest.Address address = new SignupPostRequest.Address(
                                Objects.requireNonNull(etStreetLoc.getText()).toString(),
                                Objects.requireNonNull(etBarnagayLoc.getText()).toString(),
                                Objects.requireNonNull(etCityLoc.getText()).toString());
                        //convert URI to File
                        File imageFile = null;
                        if (currentPhotoUri != null) {
                            imageFile = globalUtility.uriToFile(currentPhotoUri, activity);
                        }
                        //The Request Body
                        UsersUpdateInformationRequest request = new UsersUpdateInformationRequest(imageFile,
                                Objects.requireNonNull(etEditName.getText()).toString(),
                                Objects.requireNonNull(etEditEmail.getText()).toString(),
                                personalInformation,
                                address);
                        //Call the api
                        apiRequestHandler.updateUserInfo(request, accessToken, new ResponseCallback<ApiSuccessfulResponse>() {
                            @Override
                            public void onSuccess(ApiSuccessfulResponse response) {

                                setProfilePictureWithUri(currentPhotoUri);
                                String newUserData = gson.toJson(response.getData());
                                dataStorageManager.putString(USER_DATA_KEY, newUserData);
                                updateProfileUI(response.getData());
                                //set the image from current URI
                                setProfilePictureWithUri(currentPhotoUri);

                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                // Show success message
                                Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable t) {
                                onNavigateToLoginIfUnauthorize(t);
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                        });

                    }, error -> {
                        Log.i("Update Profile", Objects.requireNonNull(error.getMessage()));
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    });

            compositeDisposable.add(dispo);

        });
        // Fix 2: Safely show
        if (!isFinishing() && !isDestroyed()) {
            dialog.show();
        }

    }

    private void setupActivityResultLaunchers() {
        // Permission launcher
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        showImagePickerDialog();
                    } else {
                        Toast.makeText(this, "Permission denied. Cannot access photos.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Pick image from gallery
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        //set the uri of select image to the global variable currentPhotoUri
                        currentPhotoUri = result.getData().getData();
                        if (currentPhotoUri != null) {
                            ivProfilePictureEdit.setImageURI(currentPhotoUri);

                        }
                    }
                }
        );

        // Take picture with camera
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && currentPhotoUri != null) {
                        ivProfilePictureEdit.setImageURI(currentPhotoUri);
                    }
                }
        );
    }

    private void checkPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ requires READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                showImagePickerDialog();
            }
        } else {
            // Android 6-12 requires READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                showImagePickerDialog();
            }
        }
    }

    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        new AlertDialog.Builder(this)
                .setTitle("Select Profile Picture")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Take Photo
                            openCamera();
                            break;
                        case 1: // Choose from Gallery
                            openGallery();
                            break;
                        case 2: // Cancel
                            dialog.dismiss();
                            break;
                    }
                })
                .show();
    }

    private void openCamera() {
        // Check camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            return;
        }
        try {
            File photoFile = createImageFile();
            //convert File to Uri and set it to the global variable called currentPhotoUri
            currentPhotoUri = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".fileprovider",
                    photoFile);
            takePictureLauncher.launch(currentPhotoUri);
        } catch (IOException e) {
            Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private File createImageFile() throws IOException {
        String imageFileName = "PROFILE_" + System.currentTimeMillis();
        File storageDir = getExternalFilesDir(null);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void onNavigateToLoginIfUnauthorize(Throwable t) {
        if (Objects.requireNonNull(t.getMessage()).toLowerCase().contains("token")) {
            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
        Log.e(TAG, t.getMessage());
    }

    private void initViews() {
        activity = this;
        context = this;
        toolbar = findViewById(R.id.toolbar);
        tvUserName = findViewById(R.id.tv_user_name);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        tvLocation = findViewById(R.id.tv_address);
        btnEditProfile = findViewById(R.id.btn_edit_profile);


        switchFloodAlerts = findViewById(R.id.switch_flood_alerts);
        switchWeatherUpdates = findViewById(R.id.switch_weather_updates);
        switchEmergencyAlerts = findViewById(R.id.switch_emergency_alerts);

        btnChangePassword = findViewById(R.id.btn_change_password);
        btnPrivacyPolicy = findViewById(R.id.btn_privacy_policy);
        btnLogout = findViewById(R.id.btn_logout);
        navFullname = findViewById(R.id.nav_user_name);


        ivProfilePicture = findViewById(R.id.iv_profile_picture);

        apiRequestHandler = new UsersAPIRequestHandler(activity, context);
        dataStorageManager = DataStorageManager.getInstance(context);

        compositeDisposable = new CompositeDisposable();
        globalUtility = new GlobalUtility();
        //KEYS
        USER_DATA_KEY = globalUtility.getValueInYAML(BuildConfig.USER_INFORMATION_KEY, context);
        ACCESS_TOKEN_KEY = globalUtility.getValueInYAML(BuildConfig.ACCESS_TOKEN_KEY, context);

    }

    private void updateUserNotificationSetting() {
        Disposable disposable = dataStorageManager.getString(ACCESS_TOKEN_KEY)
                .firstElement()
                .subscribe(accessToken -> {

                    boolean isFloodAlert = switchFloodAlerts.isChecked();
                    boolean isEmergencyAlert = switchEmergencyAlerts.isChecked();
                    boolean isWeatherAlert = switchWeatherUpdates.isChecked();
                    UserNotificationSettingsRequest request =
                            new UserNotificationSettingsRequest(isFloodAlert, isWeatherAlert, isEmergencyAlert);
                    apiRequestHandler.updateUserNotificationSettings(request, accessToken, new ResponseCallback<ApiSuccessfulResponse>() {
                        @Override
                        public void onSuccess(ApiSuccessfulResponse response) {
                            Toast.makeText(activity, response.getMessage(), Toast.LENGTH_SHORT).show();
                            ProfileActivity.this.finish();
                        }

                        @Override
                        public void onError(Throwable t) {
                            //navigate to login activity
                            onNavigateToLoginIfUnauthorize(t);
                            ProfileActivity.this.finish();
                        }
                    });
                });
        compositeDisposable.add(disposable);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        loadUserData(); // Refresh data every time the user comes back
        loadNotificationPreferences();// set the notification settings, if it is off or on
        super.onResume();
    }
}