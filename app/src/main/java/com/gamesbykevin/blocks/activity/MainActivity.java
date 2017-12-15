package com.gamesbykevin.blocks.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.gamesbykevin.blocks.R;
import com.gamesbykevin.blocks.opengl.MainRenderer;

import org.rajawali3d.view.SurfaceView;

public class MainActivity extends BaseActivity {

    //does the user want to exit?
    private boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //call parent
        super.onCreate(savedInstanceState);

        //setup our visual display
        setContentView(R.layout.activity_main);

        //obtain our surface view
        SurfaceView surfaceView = findViewById(R.id.game_surfaceView);

        //assign the game's frame rate
        surfaceView.setFrameRate(FPS);

        //assign renderer to our surface view
        surfaceView.setSurfaceRenderer(new MainRenderer(this));

    }

    @Override
    public void onResume() {

        //call parent
        super.onResume();

        //we have not yet prompter exit
        this.exit = false;
    }

    @Override
    public void onBackPressed() {

        if (!exit) {

            //prompt user for exit
            super.displayMessage(getString(R.string.exit_app_prompt));

            //flag exit prompt
            this.exit = true;

        } else {

            //close all open activities
            finishAffinity();
        }
    }

    public void startGame(View view) {

        //start the level select activity
        startActivity(new Intent(this, LevelSelectActivity.class));
    }
}