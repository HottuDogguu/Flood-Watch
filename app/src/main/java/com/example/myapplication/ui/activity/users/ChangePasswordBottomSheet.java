package com.example.myapplication.ui.activity.users;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePasswordBottomSheet  extends BottomSheetDialogFragment {

    private TextInputLayout tilCurrentPassword, tilNewPassword, tilConfirmPassword;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private MaterialButton btnCancel, btnSavePassword;
    private ImageView btnClose;
    private LinearLayout passwordStrengthContainer;
    private View strengthBar1, strengthBar2, strengthBar3, strengthBar4;
    private TextView tvStrengthText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_change_password, container, false);

        initViews(view);
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        tilCurrentPassword = view.findViewById(R.id.til_current_password);
        tilNewPassword = view.findViewById(R.id.til_new_password);
        tilConfirmPassword = view.findViewById(R.id.til_confirm_password);

        etCurrentPassword = view.findViewById(R.id.et_current_password);
        etNewPassword = view.findViewById(R.id.et_new_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);

        btnCancel = view.findViewById(R.id.btn_cancel_password);
        btnSavePassword = view.findViewById(R.id.btn_save_password);
        btnClose = view.findViewById(R.id.btn_close);

        passwordStrengthContainer = view.findViewById(R.id.password_strength_container);
        strengthBar1 = view.findViewById(R.id.strength_bar_1);
        strengthBar2 = view.findViewById(R.id.strength_bar_2);
        strengthBar3 = view.findViewById(R.id.strength_bar_3);
        strengthBar4 = view.findViewById(R.id.strength_bar_4);
        tvStrengthText = view.findViewById(R.id.tv_strength_text);
    }

    private void setupListeners() {
        // Close button
        btnClose.setOnClickListener(v -> dismiss());

        // Cancel button
        btnCancel.setOnClickListener(v -> dismiss());

        // Save password button
        btnSavePassword.setOnClickListener(v -> changePassword());

        // Password strength indicator
        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    passwordStrengthContainer.setVisibility(View.VISIBLE);
                    updatePasswordStrength(s.toString());
                } else {
                    passwordStrengthContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void changePassword() {
        // Clear previous errors
        tilCurrentPassword.setError(null);
        tilNewPassword.setError(null);
        tilConfirmPassword.setError(null);

        // Get input values
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validation
        if (currentPassword.isEmpty()) {
            tilCurrentPassword.setError("Current password is required");
            etCurrentPassword.requestFocus();
            return;
        }

        // Check if current password matches saved password
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserProfile", getActivity().MODE_PRIVATE);
        String savedPassword = prefs.getString("password", "password123"); // Default password

        if (!currentPassword.equals(savedPassword)) {
            tilCurrentPassword.setError("Current password is incorrect");
            etCurrentPassword.requestFocus();
            return;
        }

        if (newPassword.isEmpty()) {
            tilNewPassword.setError("New password is required");
            etNewPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 8) {
            tilNewPassword.setError("Password must be at least 8 characters");
            etNewPassword.requestFocus();
            return;
        }

        if (!isPasswordValid(newPassword)) {
            tilNewPassword.setError("Password must contain letters and numbers");
            etNewPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.setError("Please confirm your password");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        if (currentPassword.equals(newPassword)) {
            tilNewPassword.setError("New password must be different from current password");
            etNewPassword.requestFocus();
            return;
        }

        // Save new password
        prefs.edit().putString("password", newPassword).apply();

        Toast.makeText(getActivity(), "Password changed successfully!", Toast.LENGTH_SHORT).show();
        dismiss();
    }

    private boolean isPasswordValid(String password) {
        // Check if password contains at least one letter and one number
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        return hasLetter && hasNumber;
    }

    private void updatePasswordStrength(String password) {
        int strength = calculatePasswordStrength(password);

        // Reset all bars
        resetStrengthBars();

        switch (strength) {
            case 1: // Weak
                strengthBar1.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                tvStrengthText.setText("Weak");
                tvStrengthText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                break;
            case 2: // Fair
                strengthBar1.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                strengthBar2.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                tvStrengthText.setText("Fair");
                tvStrengthText.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                break;
            case 3: // Good
                strengthBar1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                strengthBar2.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                strengthBar3.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                tvStrengthText.setText("Good");
                tvStrengthText.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                break;
            case 4: // Strong
                strengthBar1.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                strengthBar2.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                strengthBar3.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                strengthBar4.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                tvStrengthText.setText("Strong");
                tvStrengthText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                break;
        }
    }

    private void resetStrengthBars() {
        int grayColor = getResources().getColor(android.R.color.darker_gray);
        strengthBar1.setBackgroundColor(grayColor);
        strengthBar2.setBackgroundColor(grayColor);
        strengthBar3.setBackgroundColor(grayColor);
        strengthBar4.setBackgroundColor(grayColor);
    }

    private int calculatePasswordStrength(String password) {
        int strength = 0;

        // Length check
        if (password.length() >= 8) strength++;
        if (password.length() >= 12) strength++;

        // Contains lowercase
        if (password.matches(".*[a-z].*")) strength++;

        // Contains uppercase
        if (password.matches(".*[A-Z].*")) strength++;

        // Contains number
        if (password.matches(".*\\d.*")) strength++;

        // Contains special character
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) strength++;

        // Map strength score to 1-4 scale
        if (strength <= 2) return 1; // Weak
        if (strength <= 3) return 2; // Fair
        if (strength <= 4) return 3; // Good
        return 4; // Strong
    }
}
