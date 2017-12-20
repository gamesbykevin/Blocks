package com.gamesbykevin.blocks.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gamesbykevin.blocks.R;

import java.util.Random;

import static com.gamesbykevin.blocks.activity.BaseActivityHelper.VIBRATION_DURATION_DEFAULT;
import static com.gamesbykevin.blocks.activity.BaseActivityHelper.loadSound;
import static com.gamesbykevin.blocks.activity.BaseActivityHelper.setupVibration;
import static com.gamesbykevin.blocks.activity.BaseActivityHelper.stopSound;
import static com.gamesbykevin.blocks.opengl.MainActivityRenderer.CURRENT_BACKGROUND;
import static com.gamesbykevin.blocks.opengl.MainActivityRenderer.RESOURCE_BACKGROUND;

public class BaseActivity extends GooglePlayActivity {

    /**
     * Global tag for logcat events
     */
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

    //our social media urls etc....
    protected static final String URL_YOUTUBE = "https://youtube.com/gamesbykevin";
    protected static final String URL_FACEBOOK = "https://facebook.com/gamesbykevin";
    protected static final String URL_TWITTER = "https://twitter.com/gamesbykevin";
    protected static final String URL_INSTAGRAM = "https://www.instagram.com/gamesbykevin";
    protected static final String URL_WEBSITE = "http://gamesbykevin.com";
    protected static final String URL_RATE = "https://play.google.com/store/apps/details?id=com.gamesbykevin.block";

    //our intent to access our social media urls
    private Intent intent;

    //our vibrator object
    protected Vibrator vibrator;

    //list of all our game sounds
    protected static SparseArray<MediaPlayer> SOUND;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //call parent
        super.onCreate(savedInstanceState);

        //obtain our shared preferences for reference if we haven't already
        if (preferences == null)
            preferences = getBaseContext().getSharedPreferences(getString(R.string.app_preferences), Context.MODE_PRIVATE);

        //store instance of activity if null
        if (instance == null) {
            instance = this;

            String desc = "";

            //display the screen density to the user
            switch (getResources().getDisplayMetrics().densityDpi) {

                case DisplayMetrics.DENSITY_LOW:
                    desc = "LDPI";
                    break;

                case DisplayMetrics.DENSITY_MEDIUM:
                    desc = "MDPI";
                    break;

                case DisplayMetrics.DENSITY_HIGH:
                    desc = "HDPI";
                    break;

                case DisplayMetrics.DENSITY_XHIGH:
                    desc = "XHDPI";
                    break;

                case DisplayMetrics.DENSITY_XXHIGH:
                    desc = "XXHDPI";
                    break;

                case DisplayMetrics.DENSITY_XXXHIGH:
                    desc = "XXXHDPI";
                    break;

                default:
                    desc = "Unknown";
                    break;
            }

            //print screen density
            Log.d(TAG, "Screen Density: " + desc);
        }

        //load our game sound
        loadSound(getActivity());

        //assign the sound key so we can call from static context
        BaseActivityHelper.SOUND_KEY = getString(R.string.file_key_sound);
    }

    @Override
    public void onResume() {

        //call parent
        super.onResume();

        //setup our vibration object
        setupVibration(this);

        //pre-populate the background (if exists)
        setupBackground();
    }

    @Override
    public void onPause() {

        //call parent
        super.onPause();

        //stop any existing sound on pause
        stopSound();
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

        //create our layout view for the toast text
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup)findViewById(R.id.custom_toast_container));

        //update our text view object with the desired text
        TextView text = layout.findViewById(R.id.text);
        text.setText(message);

        //create our toast text and assign the inflated layout
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
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

    public void showTwitter(View view) {
        openUrl(URL_TWITTER);
    }

    public void showYoutube(View view) {
        openUrl(URL_YOUTUBE);
    }

    public void showFacebook(View view) {
        openUrl(URL_FACEBOOK);
    }

    public void showInstagram(View view) {
        openUrl(URL_INSTAGRAM);
    }

    public void showRating(View view) {
        openUrl(URL_RATE);
    }

    public void showWebsite(View view) {
        openUrl(URL_WEBSITE);
    }

    protected void openUrl(final String url) {

        //if not established create the intent
        if (this.intent == null) {
            this.intent = new Intent(Intent.ACTION_VIEW);
        }

        //set the url
        this.intent.setData(Uri.parse(url));

        //start the activity opening the app / web browser
        startActivity(this.intent);
    }

    /**
     * Setup the background if the image view container exists
     */
    protected void setupBackground() {

        //obtain the image view for the background
        ImageView imageView = findViewById(R.id.image_view_background);

        //update the image background if the container exists
        if (imageView != null)
            imageView.setImageResource(RESOURCE_BACKGROUND[CURRENT_BACKGROUND]);

        //obtain the splash page layout
        View view = findViewById(R.id.layout_splash);

        //if the view exists
        if (view != null) {

            //obtain the background from the splash page
            ImageView iv = view.findViewById(R.id.image_view_background);

            //if it exists we will change it to match
            if (iv != null)
                iv.setImageResource(RESOURCE_BACKGROUND[CURRENT_BACKGROUND]);
        }
    }

    /**
     * Vibrate the phone for the default duration
     */
    public void vibrate() {
        vibrate(VIBRATION_DURATION_DEFAULT);
    }

    public void vibrate(final long duration) {

        //if null we can't vibrate
        if (this.vibrator == null)
            return;

        //if vibration isn't enabled, we can't vibrate
        if (getPreferences().getInt(getString(R.string.file_key_vibrate), 0) != 0)
            return;

        this.vibrator.vibrate(duration);
    }
}