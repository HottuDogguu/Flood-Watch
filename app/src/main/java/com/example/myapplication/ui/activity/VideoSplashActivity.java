package com.example.myapplication.ui.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.ui.activity.auth.LoginActivity;
import com.example.myapplication.ui.activity.home.HomeActivity;

public class VideoSplashActivity extends AppCompatActivity {

    private VideoView videoSplash;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_splash);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize views
        videoSplash = findViewById(R.id.videoSplash);
        progressBar = findViewById(R.id.progressBar);

        // Setup and play video
        setupVideo();
    }

    private void setupVideo() {
        try {
            // Set video path - using your startup_animation video
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.startup_animation);
            videoSplash.setVideoURI(videoUri);

            // Show progress bar while video is preparing
            progressBar.setVisibility(View.VISIBLE);

            // Set listener for when video is ready to play
            videoSplash.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // Hide progress bar
                    progressBar.setVisibility(View.GONE);

                    // Don't loop the video
                    mp.setLooping(false);

                    // Start playing video
                    videoSplash.start();
                }
            });

            // Set listener for when video completes
            videoSplash.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // Video finished, navigate to MainActivity
                    navigateToMain();
                }
            });

            // Set error listener
            videoSplash.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // If video fails to load, skip to main activity
                    navigateToMain();
                    return true;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            // If any error occurs, navigate to main activity
            navigateToMain();
        }
    }

    private void navigateToMain() {
        // Navigate to MainActivity
        Intent intent = new Intent(VideoSplashActivity.this, LoginActivity.class);
        startActivity(intent);

        // Add fade transition
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        // Finish splash activity
        finish();
    }

    @Override
    public void onBackPressed() {
        // Disable back button during splash video
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause video if activity is paused
        if (videoSplash != null && videoSplash.isPlaying()) {
            videoSplash.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume video if activity is resumed
        if (videoSplash != null && !videoSplash.isPlaying()) {
            videoSplash.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up video resources
        if (videoSplash != null) {
            videoSplash.stopPlayback();
        }
    }
}