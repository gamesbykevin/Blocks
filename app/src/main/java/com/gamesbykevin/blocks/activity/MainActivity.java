package com.gamesbykevin.blocks.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.gamesbykevin.blocks.R;

public class MainActivity extends BaseActivity {

    //does the user want to exit?
    private boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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