package com.gamesbykevin.blocks.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gamesbykevin.blocks.R;
import com.gamesbykevin.blocks.game.Game;
import com.gamesbykevin.blocks.opengl.Renderer;
import com.gamesbykevin.blocks.util.Timer;

import org.rajawali3d.view.ISurface;
import org.rajawali3d.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.view.View.VISIBLE;

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

    //our layout parameters
    private LinearLayout.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //call parent
        super.onCreate(savedInstanceState);

        super.setContentView(R.layout.activity_main);

        try {
            //create new game
            this.game = new Game(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //create our surface view and assign the frame rate
        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        surfaceView.setFrameRate(FPS);

        //create our renderer
        this.renderer = new Renderer(this);

        //assign to our surface view
        surfaceView.setSurfaceRenderer(getRenderer());

        //set this after assigning our renderer
        //surfaceView.setZOrderOnTop(false);
        surfaceView.setZOrderMediaOverlay(true);

        //add our on click listeners for all the game control buttons
        MainActivityHelper.setupControlListener(findViewById(R.id.game_controls));

        /*
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View gameControls = inflater.inflate(R.layout.layout_game_controls, (ViewGroup)findViewById(R.id.game_controls));

        //add our on click listeners for all the game control buttons
        MainActivityHelper.setupControlListener(gameControls);

        //create our layout
        RelativeLayout layout = new RelativeLayout(this);

        //assign full size screen params
        layout.setLayoutParams(getLayoutParams());

        ImageView iv = new ImageView(this);
        iv.setLayoutParams(getLayoutParams());
        iv.setImageResource(R.drawable.background);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);

        layout.addView(iv);

        layout.addView(surfaceView);

        View gameOver = inflater.inflate(R.layout.layout_game_over, (ViewGroup)findViewById(R.id.game_over));


        layout.addView(gameControls, getLayoutParams());
        layout.addView(gameOver, getLayoutParams());
        //layout.addView(findViewById(R.id.game_over), getLayoutParams());
        //layout.addView(findViewById(R.id.game_controls), getLayoutParams());

        gameOver.invalidate();
        gameOver.bringToFront();

        //setup our ui
        setContentView(layout);
        */
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

        try {

            //wait for thread to finish
            this.thread.join();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //flag null
        this.thread = null;

        //flag false to stop loop
        this.running = false;
    }

    @Override
    public void onBackPressed() {

        //call parent
        super.onBackPressed();

        //pause to stop the thread
        onPause();

        //destroy the activity
        finish();
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

    private LinearLayout.LayoutParams getLayoutParams() {

        if (this.layoutParams == null)
            this.layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.MATCH_PARENT);

        return this.layoutParams;
    }
}