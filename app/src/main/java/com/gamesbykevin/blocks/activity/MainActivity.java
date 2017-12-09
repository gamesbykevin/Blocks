package com.gamesbykevin.blocks.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.gamesbykevin.blocks.R;
import com.gamesbykevin.blocks.game.Game;
import com.gamesbykevin.blocks.opengl.Renderer;
import com.gamesbykevin.blocks.util.Timer;

import org.rajawali3d.view.ISurface;
import org.rajawali3d.view.SurfaceView;

import java.util.Random;

public class MainActivity extends Activity implements Runnable {

    public static final String TAG = "gamesbykevin";

    /**
     * Speed of our game
     */
    public static final int FPS = 60;

    /**
     * How many milliseconds in 1 second
     */
    public static final long MILLISECONDS_PER_SECOND = 1000L;

    /**
     * How long does each thread update expected to last
     */
    public static final long THREAD_DURATION = (MILLISECONDS_PER_SECOND / FPS);

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

    //all our user control buttons
    private Button buttonLeft, buttonRight, buttonUp, buttonDown;

    //keep reference to our renderer object
    private Renderer renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            //create new game
            this.game = new Game(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //create our surface view and assign the frame rate
        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        surfaceView.setFrameRate(FPS);
        surfaceView.setRenderMode(ISurface.RENDERMODE_WHEN_DIRTY);

        this.renderer = new Renderer(this);
        surfaceView.setSurfaceRenderer(getRenderer());

        //obtain our button reference controls
        this.buttonLeft = findViewById(R.id.buttonLeft);
        this.buttonLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    if (getGame() != null && getGame().getBlock() != null)
                        getGame().getBlock().setWest(true);

                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    if (getGame() != null && getGame().getBlock() != null)
                        getGame().getBlock().setWest(false);
                }
                return false;
            }
        });

        this.buttonRight = findViewById(R.id.buttonRight);
        this.buttonRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    if (getGame() != null && getGame().getBlock() != null)
                        getGame().getBlock().setEast(true);

                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    if (getGame() != null && getGame().getBlock() != null)
                        getGame().getBlock().setEast(false);
                }
                return false;
            }
        });

        this.buttonDown = findViewById(R.id.buttonDown);
        this.buttonDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    if (getGame() != null && getGame().getBlock() != null)
                        getGame().getBlock().setSouth(true);

                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    if (getGame() != null && getGame().getBlock() != null)
                        getGame().getBlock().setSouth(false);
                }
                return false;
            }
        });

        this.buttonUp = findViewById(R.id.buttonUp);
        this.buttonUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    if (getGame() != null && getGame().getBlock() != null)
                        getGame().getBlock().setNorth(true);

                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    if (getGame() != null && getGame().getBlock() != null)
                        getGame().getBlock().setNorth(false);
                }
                return false;
            }
        });
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
            this.thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //flag null
        this.thread = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

                //if 1 second passed
                if (end - previous >= 1000) {

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

    public void displayMessage(final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

    }
}