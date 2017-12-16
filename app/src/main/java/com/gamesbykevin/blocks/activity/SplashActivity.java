package com.gamesbykevin.blocks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.gamesbykevin.blocks.R;

import static com.gamesbykevin.blocks.activity.MainActivityHelper.retrieveSharedPreferences;

public class SplashActivity extends BaseActivity {

    /**
     * The amount of time to display the splash screen (in milliseconds)
     */
    public static final long SPLASH_DELAY = 2500L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //call parent
        super.onCreate(savedInstanceState);

        //setup content view
        setContentView(R.layout.activity_splash);
    }

    @Override
    public void onResume() {

        //populate our values based on the shared preferences
        retrieveSharedPreferences(this);

        //call parent
        super.onResume();

        //delay a couple seconds before going to main page
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                //start the new activity
                startActivity(new Intent(SplashActivity.this, MainActivity.class));

                //close the activity
                finish();

            }

        }, SPLASH_DELAY);
    }

    @Override
    public void onBackPressed() {

        //don't allow user to press back button
        return;
    }
}