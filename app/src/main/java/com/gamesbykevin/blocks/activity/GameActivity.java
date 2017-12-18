package com.gamesbykevin.blocks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.gamesbykevin.blocks.R;
import com.gamesbykevin.blocks.game.Game;
import com.gamesbykevin.blocks.opengl.Renderer;

import org.rajawali3d.view.SurfaceView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.gamesbykevin.blocks.activity.GameActivityHelper.setupControlListener;
import static com.gamesbykevin.blocks.activity.GameOverActivity.PARAM_NAME;

public class GameActivity extends BaseActivity implements Runnable {

    //does the user want to exit?
    private boolean exit = false;

    //our main thread
    private Thread thread;

    //is the thread running
    private boolean running = false;

    //our game object
    private static Game game;

    //figure out when a second has passed
    private long previous = System.currentTimeMillis();

    //count our fps
    private int count = 0;

    //keep reference to our renderer object
    private Renderer renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //call parent
        super.onCreate(savedInstanceState);

        //set our content view
        super.setContentView(R.layout.activity_game);

        try {
            //create new game
            this.game = new Game(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //create our renderer
        this.renderer = new Renderer(this);

        //obtain our surface view
        SurfaceView surfaceView = findViewById(R.id.game_surfaceView);

        //assign the game's frame rate
        surfaceView.setFrameRate(FPS);

        //assign to our surface view
        surfaceView.setSurfaceRenderer(getRenderer());

        //add our on click listeners for all the game control buttons
        setupControlListener(findViewById(R.id.game_controls));
    }

    @Override
    public void onResume() {

        //we have not yet prompted exit
        this.exit = false;

        try {

            //if null, create a new game
            if (getGame() == null)
                this.game = new Game(this);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //call parent
        super.onResume();

        //if there is a thread already running, no need to continue
        if (this.running || this.thread != null)
            return;

        //flag our thread running
        this.running = true;

        //start a new thread
        this.thread = new Thread(this);
        this.thread.start();
        this.thread.setName("thread " + System.currentTimeMillis());

        //display thread created
        Log.d(TAG, "Thread created: " + this.thread.getName());

        //resume theme music
        BaseActivityHelper.playSound(R.raw.theme, true, false);
    }

    @Override
    public void onPause() {

        //call parent
        super.onPause();

        //flag false to stop loop
        this.running = false;

        try {

            //wait for thread to finish
            if (this.thread != null)
                this.thread.join();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //flag null
        this.thread = null;
    }

    @Override
    public void onBackPressed() {

        if (!exit) {

            //prompt user for exit
            super.displayMessage(getString(R.string.exit_game_prompt));

            //flag exit prompt
            this.exit = true;

        } else {

            //close the activity
            finish();
        }

    }

    @Override
    public void onDestroy() {

        //call parent
        super.onDestroy();

        if (this.renderer != null) {
            this.renderer.dispose();
            this.renderer = null;
        }

        if (game != null) {
            game.dispose();
            game = null;
        }
    }

    @Override
    public void run() {

        try {

            while (running) {

                //get the current time
                long time = System.currentTimeMillis();

                //update our game
                if (getGame() != null)
                    getGame().update();

                //get time at finish
                long end = System.currentTimeMillis();

                //figure out how long the update took
                final long duration = end - time;

                //how long to we sleep for
                long sleep = THREAD_DURATION - duration;

                //maintain minimum sleep duration
                if (sleep < 1)
                    sleep = 1;

                //keep track of fps
                count++;

                //if 1 second has passed
                if (end - previous >= MILLISECONDS_PER_SECOND) {

                    //store new time
                    previous = end;

                    //print fps
                    Log.d(TAG, "FPS: " + count);

                    //reset count
                    count = 0;
                }

                //sleep our thread to maintain a consistent fps
                Thread.sleep(sleep);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showGameOver() {

        //go to the game over page
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //create our intent
                Intent intent = new Intent(getBaseContext(), GameOverActivity.class);

                //format the time for the user to see
                DateFormat formatter = new SimpleDateFormat(GameOverActivity.TIME_FORMAT);
                String dateFormatted = formatter.format(new Date(getGame().getTimer().getLapsed()));

                //add the time parameter to the intent so we can display the time to beat the level
                intent.putExtra(PARAM_NAME, dateFormatted);

                //start the activity
                startActivity(intent);
            }
        });
    }

    public static Game getGame() {
        return game;
    }

    public Renderer getRenderer() {
        return this.renderer;
    }
}