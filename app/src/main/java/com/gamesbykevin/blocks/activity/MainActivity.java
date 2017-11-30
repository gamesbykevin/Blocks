package com.gamesbykevin.blocks.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.gamesbykevin.blocks.R;
import com.gamesbykevin.blocks.game.Game;
import com.gamesbykevin.blocks.opengl.Renderer;

import org.rajawali3d.view.ISurface;
import org.rajawali3d.view.SurfaceView;

public class MainActivity extends Activity implements Runnable {

    public static final String TAG = "gamesbykevin";

    /**
     * Speed of our game
     */
    public static final int FPS = 60;

    /**
     * How long does each thread update expected to last
     */
    private static final long THREAD_DURATION = (1000 / FPS);

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

    //all our user control buttons
    private Button buttonLeft, buttonRight, buttonUp, buttonDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create new game
        this.game = new Game(this);

        //create our surface view and assign the framerate
        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        surfaceView.setFrameRate(FPS);
        surfaceView.setRenderMode(ISurface.RENDERMODE_WHEN_DIRTY);

        Renderer renderer = new Renderer(this, surfaceView);
        surfaceView.setSurfaceRenderer(renderer);

        //obtain our button reference controls
        this.buttonLeft = findViewById(R.id.buttonLeft);
        this.buttonLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    Log.d(TAG, "MotionEvent Action Down");

                    if (getGame() != null && getGame().getBlock() != null)
                        getGame().getBlock().setWest(true);

                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    Log.d(TAG, "MotionEvent Action Up");

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

                    Log.d(TAG, "MotionEvent Action Down");

                    if (getGame() != null && getGame().getBlock() != null)
                        getGame().getBlock().setEast(true);

                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    Log.d(TAG, "MotionEvent Action Up");

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

                    Log.d(TAG, "MotionEvent Action Down");

                    if (getGame() != null && getGame().getBlock() != null)
                        getGame().getBlock().setSouth(true);

                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    Log.d(TAG, "MotionEvent Action Up");

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

                    Log.d(TAG, "MotionEvent Action Down");

                    if (getGame() != null && getGame().getBlock() != null)
                        getGame().getBlock().setNorth(true);

                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    Log.d(TAG, "MotionEvent Action Up");

                    if (getGame() != null && getGame().getBlock() != null)
                        getGame().getBlock().setNorth(false);
                }
                return false;
            }
        });

    }

    @Override
    public void onResume() {

        //if null, create a new game
        if (getGame() == null)
            this.game = new Game(this);

        //call parent
        super.onResume();

        //flag our thread running
        this.running = true;

        //start a new thread
        this.thread = new Thread(this);
        this.thread.start();
        this.thread.setName("thread " + System.currentTimeMillis());

        //display thread created
        Log.d(TAG, "Thread created: " + this.thread.getName());
    }

    public static Game getGame() {
        return game;
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
    public void onDestroy() {

        //call parent
        super.onDestroy();
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
}