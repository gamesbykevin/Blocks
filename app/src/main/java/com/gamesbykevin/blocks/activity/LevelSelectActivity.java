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
import android.widget.TextView;

import com.gamesbykevin.blocks.R;
import com.gamesbykevin.blocks.levels.Level;
import com.gamesbykevin.blocks.levels.Levels;

public class LevelSelectActivity extends Activity {

    /**
     * Object containing layout for our levels
     */
    public static Levels LEVELS;

    //our grid view
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

        //obtain our list view reference object
        this.gridView = findViewById(R.id.levelSelectGrid);

        try {
            //create container for all our levels
            LEVELS = new Levels(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //create adapter and populate
        CustomAdapter adapter = new CustomAdapter(this);

        //add adapter to bind our data
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LEVELS.setIndex(position);
                startActivity(new Intent(getBaseContext(), MainActivity.class));
                gridView.setEnabled(false);
            }
        });
    }

    @Override
    public void onResume() {

        //call parent
        super.onResume();

        //enable the gridview
        if (this.gridView != null)
            this.gridView.setEnabled(true);
    }

    private class CustomAdapter extends ArrayAdapter<Level> {

        public CustomAdapter(Context context) {
            super(context, 0, LEVELS.getLevelList());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //get the data item for this position
            Level level = getItem(position);

            if (convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_level_item, parent, false);

            if (position < 9) {
                ((TextView)convertView.findViewById(R.id.textView)).setText("0" + (position+1));
            } else {
                ((TextView)convertView.findViewById(R.id.textView)).setText("" + (position+1));
            }

            //return our view
            return convertView;
        }
    }
}