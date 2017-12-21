package com.gamesbykevin.blocks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gamesbykevin.blocks.R;

import static com.gamesbykevin.blocks.activity.BaseActivityHelper.restartSound;
import static com.gamesbykevin.blocks.activity.GameOverActivityHelper.getResIdAchievement;
import static com.gamesbykevin.blocks.activity.GameOverActivityHelper.getResIdLeaderboard;
import static com.gamesbykevin.blocks.activity.GooglePlayActivityHelper.addPendingAchievement;
import static com.gamesbykevin.blocks.activity.GooglePlayActivityHelper.addPendingScore;
import static com.gamesbykevin.blocks.activity.LevelSelectActivity.LEVELS;

public class GameOverActivity extends BaseActivity {

    /**
     * How we will display the time
     */
    public static final String PARAM_NAME_DURATION_DESC = "durationDesc";

    /**
     * How we will access the time
     */
    public static final String PARAM_NAME_DURATION = "duration";

    /**
     * The index of the level we completed
     */
    public static final String PARAM_NAME_LEVEL_INDEX = "levelIndex";

    /**
     * String format for the displayed time duration
     */
    public static final String TIME_FORMAT = "mm:ss:SSS";

    //the level index
    private int levelIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //call parent
        super.onCreate(savedInstanceState);

        //set the content view
        setContentView(R.layout.activity_game_over);

        //get the duration description
        String durationDesc = getIntent().getStringExtra(PARAM_NAME_DURATION_DESC);

        //get the duration
        final long duration = getIntent().getLongExtra(PARAM_NAME_DURATION, -1);

        //obtain the level index
        this.levelIndex = getIntent().getIntExtra(PARAM_NAME_LEVEL_INDEX, -1);

        //obtain our text view object
        TextView textView = findViewById(R.id.text_view_title);

        //display the duration
        if (durationDesc != null) {
            textView.setText(getString(R.string.game_over_text) + " - " + durationDesc);
        } else {
            textView.setText(getString(R.string.game_over_text));
        }

        //only update if data exists
        if (getLevelIndex() >= 0 && duration > 0) {

            //the identified resource reference of the achievement
            int resId = getResIdAchievement(getLevelIndex());

            //add the achievement to the pending list to be unlocked
            if (resId != -1)
                addPendingAchievement(resId);

            //obtain the correct leader board
            resId = getResIdLeaderboard(getLevelIndex());

            //add the leader board score to the pending list to be submitted
            if (resId != -1)
                addPendingScore(resId, duration);
        }
    }

    @Override
    public void onResume() {

        //call parent
        super.onResume();

        //resume theme music
        BaseActivityHelper.playSound(R.raw.theme, true, false);
    }

    @Override
    public void onDestroy() {

        //call parent
        super.onDestroy();
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
        LEVELS.setIndex(getLevelIndex() + 1);

        //let's begin the next level
        startActivity(new Intent(getBaseContext(), GameActivity.class));
    }

    public void levelCompleteAchievements(View view) {

        //display the achievements list
        super.onClickAchievements(view);
    }

    public void levelCompleteLeaderboard(View view) {

        //assign the leader board resource id
        this.resourceIdLeaderboard = getResIdLeaderboard(getLevelIndex());

        //display the leader board
        super.onClickLeaderboard(view);
    }

    public void levelCompleteHome(View view) {

        //restart menu music
        restartSound(R.raw.menu);

        //destroy the activity
        finish();

        //go back to the level selection page
        startActivity(new Intent(getBaseContext(), MainActivity.class));
    }

    private int getLevelIndex() {
        return this.levelIndex;
    }
}