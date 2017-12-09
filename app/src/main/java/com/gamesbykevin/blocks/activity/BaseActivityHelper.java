package com.gamesbykevin.blocks.activity;

import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;

import static com.gamesbykevin.blocks.activity.BaseActivity.getPreferences;

/**
 * Created by Kevin on 12/9/2017.
 */

public class BaseActivityHelper {

    /**
     * Number of allowed attempts to save our shared preferences before giving up
     */
    private static final int LIMIT = 5;

    public static boolean updatePreferences(final String key, final boolean value) {

        //obtain our editor object
        SharedPreferences.Editor editor = getPreferences().edit();

        //what is the result of our update
        boolean result = false;

        //how many times have we tried to update
        int attempts = 0;

        //try to update until we are successful or after # of failed attempts
        while (!result && attempts < LIMIT) {

            //update our value
            editor.putBoolean(key, value);

            //apply the change and get our result
            result = editor.commit();

            //keep track of the number of attempts
            attempts++;
        }

        //return our result
        return result;
    }

    public static boolean updatePreferences(final String key, final int value) {

        //obtain our editor object
        SharedPreferences.Editor editor = getPreferences().edit();

        //what is the result of our update
        boolean result = false;

        //how many times have we tried to update
        int attempts = 0;

        //try to update until we are successful or after # of failed attempts
        while (!result && attempts < LIMIT) {

            //update our value
            editor.putInt(key, value);

            //apply the change and get our result
            result = editor.commit();

            //keep track of the number of attempts
            attempts++;
        }

        //return our result
        return result;
    }

    public static boolean updatePreferences(final String key, final String value) {

        //obtain our editor object
        SharedPreferences.Editor editor = getPreferences().edit();

        //what is the result of our update
        boolean result = false;

        //how many times have we tried to update
        int attempts = 0;

        //# of allowed failed attempts
        final int limit = 5;

        //try to update until we are successful or after # of failed attempts
        while (!result && attempts < limit) {

            //update our value
            editor.putString(key, value);

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
}