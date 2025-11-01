package com.example.myapplication.ui.activity.users;

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
import com.example.myapplication.data.models.auth.SignupPostRequest;
import com.example.myapplication.data.models.users.UserLogoutResponse;
import com.example.myapplication.data.models.users.UsersGetInformationResponse;
import com.example.myapplication.data.models.users.UsersUpdateInformationRequest;
import com.example.myapplication.data.models.users.UsersUpdateInformationResponse;
import com.example.myapplication.data.respository.users.UsersAPIRequestHandler;
import com.example.myapplication.security.DataStorageManager;
import com.example.myapplication.ui.activity.auth.LoginActivity;
import com.example.myapplication.utils.GlobalUtility;
import com.example.myapplication.utils.home.BaseHomepageUtility;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
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
    private ActivityResultLauncher<Uri> getPictureFromURI;
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

        // Setup switch listeners
        setupSwitchListeners();


    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void updateProfileUI(UsersGetInformationResponse.UserData userData) {
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
                    UsersGetInformationResponse.UserData userData = gson.fromJson(data, UsersGetInformationResponse.UserData.class);
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
                    UsersGetInformationResponse.UserData userData = gson.fromJson(data, UsersGetInformationResponse.UserData.class);
                    updateProfileUI(userData); // update the data in fields
                });
        compositeDisposable.add(disposable);
    }

    private void loadNotificationPreferences() {
        switchFloodAlerts.setChecked(getSharedPreferences("NotificationPrefs", MODE_PRIVATE)
                .getBoolean("flood_alerts", true));
        switchWeatherUpdates.setChecked(getSharedPreferences("NotificationPrefs", MODE_PRIVATE)
                .getBoolean("weather_updates", true));
        switchEmergencyAlerts.setChecked(getSharedPreferences("NotificationPrefs", MODE_PRIVATE)
                .getBoolean("emergency_alerts", true));
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

    private void setupSwitchListeners() {
        switchFloodAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save preference
            saveNotificationPreference("flood_alerts", isChecked);
            Toast.makeText(this,
                    isChecked ? "Flood alerts enabled" : "Flood alerts disabled",
                    Toast.LENGTH_SHORT).show();
        });

        switchWeatherUpdates.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save preference
            saveNotificationPreference("weather_updates", isChecked);
            Toast.makeText(this,
                    isChecked ? "Weather updates enabled" : "Weather updates disabled",
                    Toast.LENGTH_SHORT).show();
        });

        switchEmergencyAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save preference
            saveNotificationPreference("emergency_alerts", isChecked);
            Toast.makeText(this,
                    isChecked ? "Emergency alerts enabled" : "Emergency alerts disabled",
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void saveNotificationPreference(String key, boolean value) {
        // will implement soon
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
                    apiRequestHandler.logOutUser(accessToken, new ResponseCallback<UserLogoutResponse>() {
                        @Override
                        public void onSuccess(UserLogoutResponse response) {
                            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show();
                            dataStorageManager.clearAll();
                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(Throwable t) {
                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
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
        super.onBackPressed();
        finish();
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
                    UsersGetInformationResponse.UserData userDataFromGson = gson.fromJson(data, UsersGetInformationResponse.UserData.class);
                    etEditName.setText(userDataFromGson.getFullname());
                    etEditEmail.setText(userDataFromGson.getEmail());
                    etEditPhone.setText(userDataFromGson.getPersonalInformation().getContact_number());
                    etEditSecondPhone.setText(userDataFromGson.getPersonalInformation().getSecond_number());
                    etStreetLoc.setText(userDataFromGson.getAddress().getStreet());
                    etBarnagayLoc.setText(userDataFromGson.getAddress().getBarangay());
                    etCityLoc.setText(userDataFromGson.getAddress().getCity());
                    ivProfilePictureEdit.setImageTintMode(null);
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
                        //User Perosonal information
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
                        apiRequestHandler.updateUserInfo(request, accessToken, new ResponseCallback<UsersUpdateInformationResponse>() {
                            @Override
                            public void onSuccess(UsersUpdateInformationResponse response) {

                                apiRequestHandler.getUserInformation(accessToken, new ResponseCallback<UsersGetInformationResponse>() {
                                    @Override
                                    public void onSuccess(UsersGetInformationResponse response1) {
                                        //convert the object to string using gson and store it in data store
                                        String newUserData = gson.toJson(response1.getData());
                                        dataStorageManager.putString(USER_DATA_KEY, newUserData);

                                        updateProfileUI(response1.getData());
                                        //set the image from current URI
                                        setProfilePictureWithUri(currentPhotoUri);
                                    }

                                    @Override
                                    public void onError(Throwable t) {
                                        Log.e(TAG, Objects.requireNonNull(t.getMessage()));
                                        Toast.makeText(ProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                                // Show success message
                                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
//                                dataStorageManager.putUserData(USER_DATA_KEY, r);
                                // Close dialog
                                dialog.dismiss();

                            }

                            @Override
                            public void onError(Throwable t) {
                                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }, error -> {
                        Log.i("Update Profile", Objects.requireNonNull(error.getMessage()));
                    });

            compositeDisposable.add(dispo);

        });

        dialog.show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData(); // Refresh data every time the user comes back

    }
}