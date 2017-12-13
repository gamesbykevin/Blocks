package com.gamesbykevin.blocks.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gamesbykevin.blocks.R;

import java.util.Random;

import static android.view.View.VISIBLE;

public class BaseActivity extends AppCompatActivity {

    public static final String TAG = "gamesbykevin";

    /**
     * Speed of our game
     */
    public static final int FPS = 60;

    /**
     * How many milliseconds in 1 second
     */
    public static final long MILLISECONDS_PER_SECOND = 1000L;

    /**
     * How long does each thread update expected to last
     */
    public static final long THREAD_DURATION = (MILLISECONDS_PER_SECOND / FPS);

    //our shared preferences object
    private static SharedPreferences preferences;

    //keep reference to this activity
    private static BaseActivity instance;

    //our object used to generate random numbers
    private static Random RANDOM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //call parent
        super.onCreate(savedInstanceState);

        //obtain our shared preferences for reference if we haven't already
        if (preferences == null)
            preferences = getBaseContext().getSharedPreferences(getString(R.string.app_preferences), Context.MODE_PRIVATE);

        //store instance of activity if null
        if (instance == null)
            instance = this;
    }

    @Override
    public void onResume() {

        //call parent
        super.onResume();
    }

    @Override
    public void onPause() {

        //call parent
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        //call parent
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {

        //call parent
        super.onDestroy();
    }

    public static BaseActivity getActivity() {
        return instance;
    }

    public static SharedPreferences getPreferences() {
        return preferences;
    }

    public void displayMessage(final String message) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static Random getRandom() {

        if (RANDOM == null) {

            //get current time stamp
            long time = System.currentTimeMillis();

            //create random with seed
            RANDOM = new Random(time);

            //display seed used
            Log.d(TAG, "Random seed = " + time);
        }

        return RANDOM;
    }
}