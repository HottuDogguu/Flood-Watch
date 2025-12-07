package com.example.myapplication.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.ui.activity.auth.LoginActivity;


public class MainLoadingAnimation extends AppCompatActivity {

    private ImageView outerRing, middleRing, logoImage;
    private TextView floodText, watchText, tagline;
    private View dot1, dot2, dot3;

    // Minimum loading time in milliseconds (adjust as needed)
    private static final int LOADING_DURATION = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // Hide action bar if present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize views
        initViews();

        // Start animations
        startAnimations();

        // Simulate loading and navigate to next screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToNextScreen();
            }
        }, LOADING_DURATION);
    }

    private void initViews() {
        outerRing = findViewById(R.id.outerRing);
        middleRing = findViewById(R.id.middleRing);
        logoImage = findViewById(R.id.logoImage);
        floodText = findViewById(R.id.floodText);
        watchText = findViewById(R.id.watchText);
        tagline = findViewById(R.id.tagline);
        dot1 = findViewById(R.id.dot1);
        dot2 = findViewById(R.id.dot2);
        dot3 = findViewById(R.id.dot3);
    }

    private void startAnimations() {
        // Rotate outer ring
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_animation);
        outerRing.startAnimation(rotateAnimation);

        // Rotate middle ring (reverse)
        Animation rotateReverseAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_reverse_animation);
        middleRing.startAnimation(rotateReverseAnimation);

        // Pulse logo
        Animation pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_animation);
        logoImage.startAnimation(pulseAnimation);

        // Fade in text elements
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_animation);
        floodText.startAnimation(fadeInAnimation);

        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_animation);
        fadeInAnimation.setStartOffset(200);
        watchText.startAnimation(fadeInAnimation);

        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_animation);
        fadeInAnimation.setStartOffset(400);
        tagline.startAnimation(fadeInAnimation);

        // Bounce dots with delay
        Animation bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce_animation);
        dot1.startAnimation(bounceAnimation);

        bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce_animation);
        bounceAnimation.setStartOffset(150);
        dot2.startAnimation(bounceAnimation);

        bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce_animation);
        bounceAnimation.setStartOffset(300);
        dot3.startAnimation(bounceAnimation);
    }

    private void navigateToNextScreen() {
        // Default to MainActivity if no target specified
        Intent nextIntent = new Intent(MainLoadingAnimation.this, LoginActivity.class);

        startActivity(nextIntent);

        // Add transition animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        // Finish this activity so user can't go back to loading screen
        finish();
    }

    @Override
    public void onBackPressed() {
        // Disable back button during loading
        // Optionally show a toast: Toast.makeText(this, "Please wait...", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }
}

