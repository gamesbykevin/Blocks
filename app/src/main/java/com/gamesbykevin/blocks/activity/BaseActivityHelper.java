package com.gamesbykevin.blocks.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.gamesbykevin.blocks.R;

import static com.gamesbykevin.blocks.activity.BaseActivity.SOUND;
import static com.gamesbykevin.blocks.activity.BaseActivity.getPreferences;
import static com.gamesbykevin.blocks.activity.BaseActivity.getRandom;

/**
 * Created by Kevin on 12/9/2017.
 */
public class BaseActivityHelper {

    /**
     * Number of allowed attempts to save our shared preferences before giving up
     */
    private static final int LIMIT = 5;

    /**
     * How long do we vibrate the phone for when we reach the goal?
     */
    public static final long VIBRATION_DURATION_GOAL = 500;

    /**
     * Vibrate when the block is first placed on the board
     */
    public static final long VIBRATION_DURATION_PLACE = 200;

    /**
     * Here we have a default vibrate
     */
    public static final long VIBRATION_DURATION_DEFAULT = 100;

    public static final int[] RESOURCE_SOUNDS = {
        R.raw.fall_1,
        R.raw.goal_1,
        R.raw.rotate_strong_1, R.raw.rotate_strong_2, R.raw.rotate_strong_3,
        R.raw.rotate_weak_1, R.raw.rotate_weak_2, R.raw.rotate_weak_3, R.raw.rotate_weak_4,
        R.raw.rotate_hidden_1, R.raw.rotate_hidden_2,
        R.raw.switch_off, R.raw.switch_on
    };

    protected static String SOUND_KEY;

    public static boolean updatePreferences(final String key, final Object object) {

        //we won't update if null
        if (object == null)
            return false;

        //obtain our editor object
        SharedPreferences.Editor editor = getPreferences().edit();

        //what is the result of our update
        boolean result = false;

        //how many times have we tried to update
        int attempts = 0;

        //try to update until we are successful or after # of failed attempts
        while (!result && attempts < LIMIT) {

            if (object instanceof Boolean) {
                editor.putBoolean(key, (boolean)object);
            } else if (object instanceof Integer) {
                editor.putInt(key, (int)object);
            } else {
                editor.putString(key, (String)object);
            }

            //apply the change and get our result
            result = editor.commit();

            //keep track of the number of attempts
            attempts++;
        }

        //return our result
        return result;
    }

    public static void setEnabled(ViewGroup layout, final boolean enabled) {

        if (layout == null)
            return;

        layout.setEnabled(enabled);

        for (int i = 0; i < layout.getChildCount(); i++) {

            View child = layout.getChildAt(i);

            if (child != null) {
                if (child instanceof ViewGroup) {
                    setEnabled((ViewGroup)child, enabled);
                } else {
                    child.setEnabled(enabled);
                }
            }
        }
    }

    protected static void setupVibration(BaseActivity activity) {

        //if null obtain our vibrator object
        if (activity.vibrator == null)
            activity.vibrator = (Vibrator)activity.getSystemService(Context.VIBRATOR_SERVICE);
    }

    protected static void loadSound(BaseActivity activity) {

        if (activity.SOUND == null)
            activity.SOUND = new SparseArray<>();

        //load all the sounds in the game
        for (int i = 0; i < RESOURCE_SOUNDS.length; i++) {
            activity.SOUND.put(RESOURCE_SOUNDS[i], MediaPlayer.create(activity, RESOURCE_SOUNDS[i]));
        }
    }

    /**
     * Stop all in-game sound
     */
    protected static void stopSound() {

        for (int i = 0; i < RESOURCE_SOUNDS.length; i++) {
            stopSound(SOUND.get(RESOURCE_SOUNDS[i]));
        }
    }

    protected static void stopSound(MediaPlayer sound) {

        //can't do anything if null
        if (sound == null)
            return;

        //don't stop, only pause the sound
        sound.pause();
    }

    public static void playSoundGoal() {
        playSound(R.raw.goal_1);
    }

    public static void playSoundFall() {
        playSound(R.raw.fall_1);
    }

    public static void playSoundRotateWeak() {

        switch (getRandom().nextInt(4)) {

            case 0:
                playSound(R.raw.rotate_weak_1);
                break;

            case 1:
                playSound(R.raw.rotate_weak_2);
                break;

            case 2:
                playSound(R.raw.rotate_weak_3);
                break;

            case 3:
                playSound(R.raw.rotate_weak_4);
                break;
        }
    }

    public static void playSoundRotateStrong() {

        switch (getRandom().nextInt(3)) {

            case 0:
                playSound(R.raw.rotate_strong_1);
                break;

            case 1:
                playSound(R.raw.rotate_strong_2);
                break;

            case 2:
                playSound(R.raw.rotate_strong_3);
                break;
        }
    }

    public static void playSoundRotateHidden() {

        switch (getRandom().nextInt(2)) {

            case 0:
                playSound(R.raw.rotate_hidden_1);
                break;

            case 1:
                playSound(R.raw.rotate_hidden_2);
                break;
        }
    }

    public static void playSound(final int resId) {
        playSound(resId, false, true);
    }

    public static void playSound(final int resId, final boolean loop, final boolean restart) {

        //we can't play the sound if it doesn't exist
        if (SOUND.get(resId) == null)
            return;

        //we can't play if the sound is not enabled
        if (getPreferences().getInt(SOUND_KEY, 0) != 0)
            return;

        //if restarting, set back to the beginning
        if (restart)
            SOUND.get(resId).seekTo(0);

        //do we loop the sound
        SOUND.get(resId).setLooping(loop);

        //resume playing
        SOUND.get(resId).start();
    }
}