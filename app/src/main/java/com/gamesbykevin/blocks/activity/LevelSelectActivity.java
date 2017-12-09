package com.gamesbykevin.blocks.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

import com.gamesbykevin.blocks.R;
import com.gamesbykevin.blocks.levels.Level;
import com.gamesbykevin.blocks.levels.Levels;

import java.util.ArrayList;
import java.util.List;

public class LevelSelectActivity extends BaseActivity {

    /**
     * Object containing layout for our levels
     */
    public static Levels LEVELS;

    //our grid view
    private GridView gridView;

    //our array of completed levels
    private static List<Integer> completed;

    /**
     * The string delimiter separating each level
     */
    private static final String DELIMITER = ",";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //call parent
        super.onCreate(savedInstanceState);

        //assign our ui layout displayed
        setContentView(R.layout.activity_level_select);

        //obtain our list view reference object
        this.gridView = findViewById(R.id.levelSelectGrid);

        //create our list if it doesn't exist yet
        if (this.completed == null)
            this.completed = new ArrayList<>();

        try {
            //create container for all our levels
            LEVELS = new Levels(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //setup our onclick listener for the grid view
        getGridView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //if not enabled, we can't do anything
                if (view == null || !view.isEnabled())
                    return;

                LEVELS.setIndex(position);
                startActivity(new Intent(getBaseContext(), MainActivity.class));
                getGridView().setEnabled(false);
            }
        });
    }

    @Override
    public void onResume() {

        //call parent
        super.onResume();

        //enable the grid view and scroll to the recent index
        if (getGridView() != null) {
            getGridView().setEnabled(true);
            getGridView().smoothScrollToPosition(LEVELS.getIndex());
        }

        //load our preferences
        readPreferences();

        //populate our grid view
        getGridView().setAdapter(new CustomAdapter(this));
    }

    private void readPreferences() {

        //obtain our level data for the levels we have completed
        String data = BaseActivity.getPreferences().getString(getString(R.string.file_key_completed_levels), null);

        //if data exists, lets parse it
        if (data != null && data.trim().length() > 0) {

            //convert string to list of levels we have completed
            String[] entries = data.split(DELIMITER);

            //populate our levels array list
            for (int i = 0; i < entries.length; i++) {

                //parse level index to integer
                final int index = Integer.parseInt(entries[i]);

                //add to list if it hasn't been added
                if (!hasCompleted(index))
                    this.completed.add(index);
            }
        }
    }

    private static boolean saveCompleted() {

        //create our string builder
        StringBuilder sb = new StringBuilder();

        //check every element
        for (int i = 0; i < completed.size(); i++) {

            //don't add delimiter to first element
            if (i > 0)
                sb.append(DELIMITER);

            //add level index
            sb.append(completed.get(i));
        }

        //get the correct key that we want to update
        final String key = BaseActivity.getActivity().getString(R.string.file_key_completed_levels);

        //update our shared preferences to save all existing completed levels
        return BaseActivityHelper.updatePreferences(key, sb.toString());
    }

    public static void addCompleted(final int index) {

        //only add if it doesn't exist yet
        if (!hasCompleted(index)) {
            completed.add(index);
            saveCompleted();
        }
    }

    public static boolean hasCompleted(final int index) {

        if (completed == null)
            return false;

        //check every element
        for (int i = 0; i < completed.size(); i++) {

            //if match return true
            if (completed.get(i) == index)
                return true;
        }

        //didn't find it, return false
        return false;
    }

    private GridView getGridView() {
        return this.gridView;
    }

    private class CustomAdapter extends ArrayAdapter<Level> {

        public CustomAdapter(Context context) {
            super(context, 0, LEVELS.getLevelList());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_level_item, parent, false);

            //obtain our table layout
            TableLayout tableLayout = convertView.findViewById(R.id.level_select_background);

            //resource id of our background image
            final int resId;

            //determine which background image is displayed
            if (hasCompleted(position)) {

                //if we completed the level, green background
                resId = R.drawable.level_solved;

            } else if (hasCompleted(position - 1) || position == 0) {

                //if we completed the previous level or if we are the first level
                resId = R.drawable.level_select;

            } else {

                //every other level is locked
                resId = R.drawable.level_locked;
            }

            //assign the appropriate background image
            tableLayout.setBackgroundResource(resId);

            //we won't be able to select if this is locked
            BaseActivityHelper.setEnabled((ViewGroup)convertView, resId != R.drawable.level_locked);

            //display the level # only if we can play the level
            if (tableLayout.isEnabled()) {
                if (position < 9) {
                    ((TextView)convertView.findViewById(R.id.textView)).setText("0" + (position + 1));
                } else {
                    ((TextView)convertView.findViewById(R.id.textView)).setText("" + (position + 1));
                }
            } else {
                ((TextView)convertView.findViewById(R.id.textView)).setText("");
            }

            //return our view
            return convertView;
        }
    }
}