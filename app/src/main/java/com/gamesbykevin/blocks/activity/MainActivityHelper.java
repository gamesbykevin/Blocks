package com.gamesbykevin.blocks.activity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Spinner;

import com.gamesbykevin.blocks.R;
import com.gamesbykevin.blocks.opengl.MainRenderer;
import com.gamesbykevin.blocks.ui.CustomButton;

/**
 * Created by Kevin on 12/15/2017.
 */
public class MainActivityHelper {

    public static void retrieveSharedPreferences(BaseActivity activity) {

        //obtain our shared preferences object reference
        SharedPreferences preferences = BaseActivity.getPreferences();

        //retrieve our background setting
        MainRenderer.CURRENT_BACKGROUND = preferences.getInt(activity.getString(R.string.file_key_background), 0);

        //retrieve our texture setting
        MainRenderer.CURRENT_TEXTURE = preferences.getInt(activity.getString(R.string.file_key_texture), 0);
    }

    protected static void updateSharedPreferences(MainActivity activity) {

        //obtain our shared preferences object reference
        SharedPreferences preferences = BaseActivity.getPreferences();

        //obtain our editor
        Editor edit = preferences.edit();

        //update the shared preference values
        populatePreferences(activity, edit);

        //how many times have we already tried?
        int attempt = 0;

        //how many times do we try until fail
        final int limit = 5;

        //continue to loop if not successful x number of times
        while (!edit.commit() && attempt < limit) {

            //update the shared preference values
            populatePreferences(activity, edit);

            //keep count of # attempts
            attempt++;
        }
    }

    private static void populatePreferences(MainActivity activity, Editor edit) {

        //update sound setting
        CustomButton button = activity.findViewById(R.id.customButtonSound);
        edit.putInt(activity.getString(R.string.file_key_sound), button.getIndex());

        //update vibrate setting
        button = activity.findViewById(R.id.customButtonVibrate);
        edit.putInt(activity.getString(R.string.file_key_vibrate), button.getIndex());

        //update desired block texture
        Spinner spinner = activity.findViewById(R.id.spinner_texture);
        edit.putInt(activity.getString(R.string.file_key_texture), spinner.getSelectedItemPosition());

        //update desired background
        spinner = activity.findViewById(R.id.spinner_background);
        edit.putInt(activity.getString(R.string.file_key_background), spinner.getSelectedItemPosition());
    }
}