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
import com.example.myapplication.security.DataSharedPreference;

import com.example.myapplication.utils.GlobalUtility;
import com.example.myapplication.utils.home.BaseHomepageUtility;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
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
    private DataSharedPreference dataSharedPreference;
    private LinearLayout passwordStrengthContainer;
    private View strengthBar1, strengthBar2, strengthBar3, strengthBar4;
    private TextView tvStrengthText;
    private String ACCESS_TOKEN;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private boolean isValidPassword = false;

    private static final Pattern LOWER = Pattern.compile(".*[a-z].*");
    private static final Pattern UPPER = Pattern.compile(".*[A-Z].*");
    private static final Pattern DIGIT = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

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
        dataSharedPreference = DataSharedPreference.getInstance(context);
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
        //for new password
        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //reset the error message indicator
                etNewPassword.setError(null);
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

        //for confirm password
        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //reset the error message indicator
                etConfirmPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void changePassword() {
        String oldPassword = etCurrentPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        UserChangePasswordRequest request = new UserChangePasswordRequest(oldPassword, newPassword);
        //validate password first
        String errorMessage = getErrorMessage(newPassword);

        int passwordStrength = calculatePasswordStrength(newPassword);

        if(oldPassword.isEmpty()){
            etCurrentPassword.setError("Current password must not be empty.");
            etCurrentPassword.requestFocus();
            return;
        }

        if (!errorMessage.isEmpty()) {
            etNewPassword.setError(errorMessage);
            etNewPassword.requestFocus();
            return;
        }


        if (passwordStrength <= 2) {
            etNewPassword.setError("Weak password. Try adding uppercase letters, " +
                    "numbers, and special characters to make it more secure.");
            etNewPassword.requestFocus();
            return;
        }

        if (!confirmPassword.equalsIgnoreCase(newPassword)) {
            etConfirmPassword.setError(errorMessage);
            etConfirmPassword.requestFocus();
            return;
        }


        usersAPIRequestHandler.changeUserPassword(request, new ResponseCallback<ApiSuccessfulResponse>() {
            @Override
            public void onSuccess(ApiSuccessfulResponse response) {
                Toast.makeText(context, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                dismiss();
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();

                if (Objects.requireNonNull(t.getMessage()).toLowerCase().contains("invalid token")) {
                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                    baseHomepageUtility.navigateToLogin();
                }
            }
        });
    }

    private String getErrorMessage(String password) {
        // Check if password contains at least one letter and one number
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasNumber = password.matches(".*\\d.*");

        boolean isEightCharacter = password.length() > 8;

        if (!hasLetter) return "Must contain letters.";
        if (isEightCharacter) return "New password must greater than to 8.";
        if (!hasNumber) return "Must contain numbers.";

        return "";
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
        if (password == null || password.isEmpty()) return 1;

        int strength = 0;

        if (password.length() >= 8) strength++;
        if (LOWER.matcher(password).matches()) strength++;
        if (UPPER.matcher(password).matches()) strength++;
        if (DIGIT.matcher(password).matches()) strength++;
        if (SPECIAL.matcher(password).matches()) strength++;

        if (strength <= 1) return 1;
        if (strength == 2) return 2;
        if (strength == 3) return 3;
        return 4;
    }
}
