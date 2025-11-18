package com.example.myapplication.ui.activity.home;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
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

import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.example.myapplication.calbacks.ResponseCallback;
import com.example.myapplication.data.models.api_response.ApiSuccessfulResponse;
import com.example.myapplication.data.models.users.UserChangePasswordRequest;
import com.example.myapplication.data.respository.UsersAPIRequestHandler;
import com.example.myapplication.security.DataStorageManager;
import com.example.myapplication.utils.GlobalUtility;
import com.example.myapplication.utils.home.BaseHomepageUtility;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class ChangePasswordBottomSheet extends BottomSheetDialogFragment {

    private TextInputLayout tilCurrentPassword, tilNewPassword, tilConfirmPassword;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private MaterialButton btnCancel, btnSavePassword;
    private ImageView btnClose;
    private Activity activity;
    private Context context;
    private UsersAPIRequestHandler usersAPIRequestHandler;
    private GlobalUtility globalUtility;
    private DataStorageManager dataStorageManager;
    private LinearLayout passwordStrengthContainer;
    private View strengthBar1, strengthBar2, strengthBar3, strengthBar4;
    private TextView tvStrengthText;
    private String ACCESS_TOKEN;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private BaseHomepageUtility baseHomepageUtility;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_change_password, container, false);

        initViews(view);
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        activity = getActivity();
        context = getContext();
        tilCurrentPassword = view.<TextInputLayout>findViewById(R.id.til_current_password);
        tilNewPassword = view.<TextInputLayout>findViewById(R.id.til_new_password);
        tilConfirmPassword = view.<TextInputLayout>findViewById(R.id.til_confirm_password);

        etCurrentPassword = view.<TextInputEditText>findViewById(R.id.et_current_password);
        etNewPassword = view.<TextInputEditText>findViewById(R.id.et_new_password);
        etConfirmPassword = view.<TextInputEditText>findViewById(R.id.et_confirm_password);

        btnCancel = view.<MaterialButton>findViewById(R.id.btn_cancel_password);
        btnSavePassword = view.<MaterialButton>findViewById(R.id.btn_save_password);
        btnClose = view.<ImageView>findViewById(R.id.btn_close);

        passwordStrengthContainer = view.<LinearLayout>findViewById(R.id.password_strength_container);
        strengthBar1 = view.<View>findViewById(R.id.strength_bar_1);
        strengthBar2 = view.<View>findViewById(R.id.strength_bar_2);
        strengthBar3 = view.<View>findViewById(R.id.strength_bar_3);
        strengthBar4 = view.<View>findViewById(R.id.strength_bar_4);
        tvStrengthText = view.<TextView>findViewById(R.id.tv_strength_text);

        globalUtility = new GlobalUtility();
        dataStorageManager = DataStorageManager.getInstance(context);
        baseHomepageUtility = new BaseHomepageUtility(context, activity);

        usersAPIRequestHandler = new UsersAPIRequestHandler(activity, context);
        //ACCEss token key from yaml
        ACCESS_TOKEN = globalUtility.getValueInYAML(BuildConfig.ACCESS_TOKEN_KEY, context);
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

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
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void changePassword() {
        String oldPassword = etCurrentPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();
        UserChangePasswordRequest request = new UserChangePasswordRequest(oldPassword,newPassword);
        Disposable disposable = dataStorageManager.getString(ACCESS_TOKEN)
                .firstElement()
                .subscribe(accessToken -> {
                    usersAPIRequestHandler.changeUserPassword(accessToken, request, new ResponseCallback<ApiSuccessfulResponse>() {
                        @Override
                        public void onSuccess(ApiSuccessfulResponse response) {
                            Toast.makeText(context, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }

                        @Override
                        public void onError(Throwable t) {
                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();

                            if(Objects.requireNonNull(t.getMessage()).toLowerCase().contains("invalid token")){
                                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                                baseHomepageUtility.navigateToLogin();
                            }
                        }
                    });

                });
        compositeDisposable.add(disposable);


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
                strengthBar1.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark, Resources.getSystem().newTheme()));
                tvStrengthText.setText("Weak");
                tvStrengthText.setTextColor(getResources().getColor(android.R.color.holo_red_dark, Resources.getSystem().newTheme()));
                break;
            case 2: // Fair
                strengthBar1.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark, Resources.getSystem().newTheme()));
                strengthBar2.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark, Resources.getSystem().newTheme()));
                tvStrengthText.setText("Fair");
                tvStrengthText.setTextColor(getResources().getColor(android.R.color.holo_orange_dark, Resources.getSystem().newTheme()));
                break;
            case 3: // Good
                strengthBar1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark, Resources.getSystem().newTheme()));
                strengthBar2.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark, Resources.getSystem().newTheme()));
                strengthBar3.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark, Resources.getSystem().newTheme()));
                tvStrengthText.setText("Good");
                tvStrengthText.setTextColor(getResources().getColor(android.R.color.holo_blue_dark, Resources.getSystem().newTheme()));
                break;
            case 4: // Strong
                strengthBar1.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark, Resources.getSystem().newTheme()));
                strengthBar2.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark, Resources.getSystem().newTheme()));
                strengthBar3.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark, Resources.getSystem().newTheme()));
                strengthBar4.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark, Resources.getSystem().newTheme()));
                tvStrengthText.setText("Strong");
                tvStrengthText.setTextColor(getResources().getColor(android.R.color.holo_green_dark, Resources.getSystem().newTheme()));
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
