package com.example.myapplication.ui.activity.users;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.R;
import com.example.myapplication.ui.activity.auth.LoginActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.bumptech.glide.Glide;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvUserName, tvLocation, tvEmail, tvPhone, tvAddress;
    private MaterialButton btnEditProfile;
    private SwitchMaterial switchFloodAlerts, switchWeatherUpdates, switchEmergencyAlerts;
    private LinearLayout btnChangePassword, btnPrivacyPolicy, btnLogout;
    private Uri currentPhotoUri;
    private ImageView ivProfilePicture;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;

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
        loadUserData();

        // Setup click listeners
        setupClickListeners();

        // Setup switch listeners
        setupSwitchListeners();

        // NEW: Load profile picture
        loadProfilePicture();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvUserName = findViewById(R.id.tv_user_name);
        tvLocation = findViewById(R.id.tv_location);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        tvAddress = findViewById(R.id.tv_address);
        btnEditProfile = findViewById(R.id.btn_edit_profile);

        switchFloodAlerts = findViewById(R.id.switch_flood_alerts);
        switchWeatherUpdates = findViewById(R.id.switch_weather_updates);
        switchEmergencyAlerts = findViewById(R.id.switch_emergency_alerts);

        btnChangePassword = findViewById(R.id.btn_change_password);
        btnPrivacyPolicy = findViewById(R.id.btn_privacy_policy);
        btnLogout = findViewById(R.id.btn_logout);

        ivProfilePicture = findViewById(R.id.iv_profile_picture);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadUserData() {
        // Load user data from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);

        String name = prefs.getString("name", "Prince Raven F.");
        String email = prefs.getString("email", "prince.raven@email.com");
        String phone = prefs.getString("phone", "+63 912 345 6789");
        String location = prefs.getString("location", "Calauan, Laguna");
        String address = prefs.getString("address", "Calauan, Laguna, Philippines");

        // Set the data to views
        tvUserName.setText(name);
        tvEmail.setText(email);
        tvPhone.setText(phone);
        tvLocation.setText(location);
        tvAddress.setText(address);

        // Load saved notification preferences
        loadNotificationPreferences();
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
        // Save to SharedPreferences
        getSharedPreferences("NotificationPrefs", MODE_PRIVATE)
                .edit()
                .putBoolean(key, value)
                .apply();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    // Clear user session
                    clearUserSession();

                    // Navigate to MainActivity (Login screen)
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void clearUserSession() {
        // Clear SharedPreferences or any stored session data
        getSharedPreferences("UserSession", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void showEditProfileDialog() {
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
        TextInputEditText etEditLocation = dialogView.findViewById(R.id.et_edit_location);
        TextInputEditText etEditAddress = dialogView.findViewById(R.id.et_edit_address);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_cancel);
        MaterialButton btnSave = dialogView.findViewById(R.id.btn_save);
        TextView tvChangePhoto = dialogView.findViewById(R.id.tv_change_photo);
        ImageView ivProfilePictureEdit = dialogView.findViewById(R.id.iv_profile_picture_edit);

        // Load current data into dialog fields
        etEditName.setText(tvUserName.getText().toString());
        etEditEmail.setText(tvEmail.getText().toString());
        etEditPhone.setText(tvPhone.getText().toString());
        etEditLocation.setText(tvLocation.getText().toString());
        etEditAddress.setText(tvAddress.getText().toString());

        // NEW: Load current profile picture in dialog
        String savedImagePath = getSharedPreferences("UserProfile", MODE_PRIVATE)
                .getString("profile_picture", null);
        if (savedImagePath != null && !savedImagePath.isEmpty()) {
            File file = new File(savedImagePath);
            if (file.exists()) {
                Glide.with(this)
                        .load(file)
                        .circleCrop()
                        .placeholder(R.drawable.ic_user)
                        .into(ivProfilePictureEdit);
            }
        }

        // NEW: Change photo click
        tvChangePhoto.setOnClickListener(v -> {
            checkPermissionAndPickImage();
        });

        // Cancel button
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Save button - existing code stays the same
        btnSave.setOnClickListener(v -> {
            // Get updated values
            String name = etEditName.getText().toString().trim();
            String email = etEditEmail.getText().toString().trim();
            String phone = etEditPhone.getText().toString().trim();
            String location = etEditLocation.getText().toString().trim();
            String address = etEditAddress.getText().toString().trim();

            // Validate inputs
            if (name.isEmpty()) {
                etEditName.setError("Name is required");
                etEditName.requestFocus();
                return;
            }

            if (email.isEmpty()) {
                etEditEmail.setError("Email is required");
                etEditEmail.requestFocus();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEditEmail.setError("Please enter a valid email");
                etEditEmail.requestFocus();
                return;
            }

            if (phone.isEmpty()) {
                etEditPhone.setError("Phone number is required");
                etEditPhone.requestFocus();
                return;
            }

            // Save to SharedPreferences
            saveProfileData(name, email, phone, location, address);

            // Update UI
            tvUserName.setText(name);
            tvEmail.setText(email);
            tvPhone.setText(phone);
            tvLocation.setText(location);
            tvAddress.setText(address);

            // Show success message
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();

            // Close dialog
            dialog.dismiss();
        });

        dialog.show();
    }

    private void saveProfileData(String name, String email, String phone, String location, String address) {
        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("phone", phone);
        editor.putString("location", location);
        editor.putString("address", address);
        editor.apply();
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
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            handleImageSelection(imageUri);
                        }
                    }
                }
        );

        // Take picture with camera
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && currentPhotoUri != null) {
                        handleImageSelection(currentPhotoUri);
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
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6-12 requires READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                showImagePickerDialog();
            }
        } else {
            // Below Android 6, no runtime permission needed
            showImagePickerDialog();
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

    private void handleImageSelection(Uri imageUri) {
        try {
            // Save image to internal storage
            String savedPath = saveImageToInternalStorage(imageUri);

            // Save path to SharedPreferences
            getSharedPreferences("UserProfile", MODE_PRIVATE)
                    .edit()
                    .putString("profile_picture", savedPath)
                    .apply();

            // Update UI
            loadProfilePicture();

            Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private String saveImageToInternalStorage(Uri imageUri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

        // Resize bitmap to save space (500x500 max)
        int maxSize = 500;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float ratio = Math.min((float) maxSize / width, (float) maxSize / height);

        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

        // Save to internal storage
        File directory = getDir("profile_images", MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, "profile_picture.jpg");

        FileOutputStream fos = new FileOutputStream(file);
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        fos.close();

        // Clean up
        bitmap.recycle();
        resizedBitmap.recycle();

        return file.getAbsolutePath();
    }

    private void loadProfilePicture() {
        String savedImagePath = getSharedPreferences("UserProfile", MODE_PRIVATE)
                .getString("profile_picture", null);

        if (savedImagePath != null && !savedImagePath.isEmpty()) {
            File file = new File(savedImagePath);
            if (file.exists()) {
                Glide.with(this)
                        .load(file)
                        .circleCrop()
                        .placeholder(R.drawable.ic_user)
                        .error(R.drawable.ic_user)
                        .into(ivProfilePicture);
            }
        }
    }
}