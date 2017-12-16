package com.gamesbykevin.blocks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gamesbykevin.blocks.R;

import static com.gamesbykevin.blocks.activity.LevelSelectActivity.LEVELS;

public class GameOverActivity extends BaseActivity {

    /**
     * How we will access the time
     */
    public static final String PARAM_NAME = "duration";

    public static final String TIME_FORMAT = "mm:ss:SSS";

    //duration to complete the level
    private String duration = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //call parent
        super.onCreate(savedInstanceState);

        //set the content view
        setContentView(R.layout.activity_game_over);

        //get the duration
        this.duration = getIntent().getStringExtra(PARAM_NAME);

        //obtain our text view object
        TextView textView = findViewById(R.id.text_view_title);

        if (this.duration != null) {
            this.duration = "  -  " + this.duration;
        } else {
            this.duration = "";
        }

        //assign the correct description
        textView.setText(getString(R.string.game_over_text) + duration);
    }

    @Override
    public void onResume() {

        //call parent
        super.onResume();
    }

    @Override
    public void onBackPressed() {

        //don't allow the user to go back
        return;
    }

    public void levelCompleteNext(View view) {

        //destroy the activity
        finish();

        //move to the next level
        LEVELS.setIndex(LEVELS.getIndex() + 1);

        //let's begin the next level
        startActivity(new Intent(getBaseContext(), GameActivity.class));
    }

    public void levelCompleteAchievements(View view) {

    }

    public void levelCompleteLeaderboard(View view) {

    }

    public void levelCompleteHome(View view) {

        //destroy the activity
        finish();

        //go back to the level selection page
        startActivity(new Intent(getBaseContext(), MainActivity.class));
    }
}
