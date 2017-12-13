package com.gamesbykevin.blocks.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.gamesbykevin.blocks.R;

import static com.gamesbykevin.blocks.activity.LevelSelectActivity.LEVELS;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
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
        startActivity(new Intent(getBaseContext(), MainActivity.class));
    }

    public void levelCompleteAchievements(View view) {

    }

    public void levelCompleteLeaderboard(View view) {

    }

    public void levelCompleteHome(View view) {

        //destroy the activity
        finish();

        //go back to the level selection page
        startActivity(new Intent(getBaseContext(), LevelSelectActivity.class));
    }
}
