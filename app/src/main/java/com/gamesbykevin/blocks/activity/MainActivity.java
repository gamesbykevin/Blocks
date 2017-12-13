package com.gamesbykevin.blocks.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.gamesbykevin.blocks.R;
import com.gamesbykevin.blocks.game.Game;
import com.gamesbykevin.blocks.opengl.Renderer;

import org.rajawali3d.view.SurfaceView;

import java.util.Random;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.gamesbykevin.blocks.activity.MainActivityHelper.setupControlListener;

public class MainActivity extends BaseActivity implements Runnable {

    //our main thread
    private Thread thread;

    //is the thread running
    private boolean running = false;

    //our game object
    private static Game game;

    //our object used to generate random numbers
    private static Random RANDOM;

    //figure out when a second has passed
    private long previous = System.currentTimeMillis();

    //count our fps
    private int count = 0;

    //keep reference to our renderer object
    private Renderer renderer;

    //the current assiged screen
    private int screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //call parent
        super.onCreate(savedInstanceState);

        //set our content view
        super.setContentView(R.layout.activity_main);

        try {
            //create new game
            this.game = new Game(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //create our renderer
        this.renderer = new Renderer(this);

        //create our surface view and assign the frame rate
        SurfaceView surfaceView = findViewById(R.id.game_surfaceView);
        surfaceView.setFrameRate(FPS);

        //assign to our surface view
        surfaceView.setSurfaceRenderer(getRenderer());

        //add our on click listeners for all the game control buttons
        setupControlListener(findViewById(R.id.game_controls));

        //switch to the game screen
        switchScreen(R.id.game_surfaceView);
    }

    @Override
    public void onResume() {

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

        switch (getScreen()) {

            case R.id.game_controls:
            case R.id.game_surfaceView:
                switchScreen(R.id.game_exit);
                break;

            case R.id.game_exit:
            case R.id.game_over:
                return;

            default:
                throw new RuntimeException("Screen not handled : " + getScreen());
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

    public static Game getGame() {
        return game;
    }

    public static Random getRandom() {

        if (RANDOM == null) {

            //get current time stamp
            long time = System.currentTimeMillis();

            //create random with seed
            RANDOM = new Random(time);

            //display seed used
            Log.d(TAG, "Random seed = " + time);
        }

        return RANDOM;
    }

    public Renderer getRenderer() {
        return this.renderer;
    }

    public int getScreen() {
        return this.screen;
    }

    public void switchScreen(final int resid) {

        //assign the current screen
        this.screen = resid;

        //hide all by default
        findViewById(R.id.game_surfaceView).setVisibility(INVISIBLE);
        findViewById(R.id.game_controls).setVisibility(INVISIBLE);
        findViewById(R.id.game_over).setVisibility(INVISIBLE);
        findViewById(R.id.game_exit).setVisibility(INVISIBLE);

        //display the correct screen(s)
        switch (resid) {

            case R.id.game_controls:
            case R.id.game_surfaceView:
                findViewById(R.id.game_surfaceView).setVisibility(VISIBLE);
                findViewById(R.id.game_controls).setVisibility(VISIBLE);
                break;

            case R.id.game_exit:
                findViewById(R.id.game_exit).setVisibility(VISIBLE);
                break;

            case R.id.game_over:
                findViewById(R.id.game_over).setVisibility(VISIBLE);
                break;

            default:
                throw new RuntimeException("Screen not handled : " + resid);
        }
    }

    public void confirmYes(View view) {

        //call parent
        super.onBackPressed();

        //pause to stop the thread
        onPause();

        //destroy the activity
        finish();
    }

    public void confirmNo(View view) {

        //go back to our game
        switchScreen(R.id.game_surfaceView);
    }
}