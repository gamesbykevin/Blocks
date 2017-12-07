package com.gamesbykevin.blocks.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.gamesbykevin.blocks.R;
import com.gamesbykevin.blocks.activity.MainActivity;
import com.gamesbykevin.blocks.common.IDisposable;

/**
 * Created by Kevin on 12/6/2017.
 */
public class Timer implements IDisposable {

    //temp time tracker
    private long tmp;

    //how much time has lapsed
    private long lapsed;

    //values for our display timer
    private int clock1, clock2, clock3, clock4;

    //are we counting up or down ?
    private boolean ascending = true;

    //keep track of what fields have changed
    private boolean flag1 = false;
    private boolean flag2 = false;
    private boolean flag3 = false;
    private boolean flag4 = false;

    //save activity reference to update the ai
    private final MainActivity activity;

    //image object for the timer
    private ImageView time1, time2, time3, time4;

    //array of images for each number
    private Bitmap[] images;

    //runnable process
    private Runnable runnable;

    public Timer(final MainActivity activity) {

        //save reference
        this.activity = activity;

        //load images into array
        this.images = new Bitmap[10];
        this.images[0] = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.zero);
        this.images[1] = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.one);
        this.images[2] = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.two);
        this.images[3] = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.three);
        this.images[4] = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.four);
        this.images[5] = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.five);
        this.images[6] = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.six);
        this.images[7] = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.seven);
        this.images[8] = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.eight);
        this.images[9] = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.nine);

        //obtain our clock references
        this.time1 = getActivity().findViewById(R.id.clock1);
        this.time2 = getActivity().findViewById(R.id.clock2);
        this.time3 = getActivity().findViewById(R.id.clock3);
        this.time4 = getActivity().findViewById(R.id.clock4);

        //reset timer
        reset();
    }

    private MainActivity getActivity() {
        return this.activity;
    }

    @Override
    public void dispose() {
        //clean up here
    }

    public void setAscending(final boolean ascending) {
        this.ascending = ascending;
    }

    public boolean isAscending() {
        return this.ascending;
    }

    public void reset() {

        //reset temp timer
        tmp = 0;

        //time lapsed reset to 0
        this.lapsed = 0;

        //reset all values to 0
        clock1 = 0;
        clock2 = 0;
        clock3 = 0;
        clock4 = 0;

        //turn on all flags to update
        this.flag1 = true;
        this.flag2 = true;
        this.flag3 = true;
        this.flag4 = true;
    }

    public boolean hasFlag1() {
        return this.flag1;
    }

    public boolean hasFlag2() {
        return this.flag2;
    }

    public boolean hasFlag3() {
        return this.flag3;
    }

    public boolean hasFlag4() {
        return this.flag4;
    }

    public int getClock1() {
        return this.clock1;
    }

    public int getClock2() {
        return this.clock2;
    }

    public int getClock3() {
        return this.clock3;
    }

    public int getClock4() {
        return this.clock4;
    }

    /**
     * Get the time lapsed
     *
     * @return The total time (milliseconds) elapsed
     */
    public long getLapsed() {
        return this.lapsed;
    }

    public boolean hasExpired() {

        if (isAscending()) {
            return false;
        } else {
            return (clock1 <= 0 && clock2 <= 0 && clock3 <= 0 && clock4 <= 0);
        }
    }

    public void update() {

        //keep track of time elapsed (temporary timer)
        tmp += MainActivity.THREAD_DURATION;

        //keep track of time elapsed (overall)
        lapsed += MainActivity.THREAD_DURATION;

        //if 1 second has passed, update our timer
        if (tmp >= MainActivity.MILLISECONDS_PER_SECOND) {

            //turn off all flags at first
            this.flag1 = false;
            this.flag2 = false;
            this.flag3 = false;
            this.flag4 = false;

            //reset lapsed time back to 0
            tmp = 0;

            if (isAscending()) {
                ascend();
            } else {
                descend();
            }

            //update timer display
            //updateTimer();
        }
    }

    private void ascend() {

        //increase the seconds
        clock4++;

        //flag that a change was made
        flag4 = true;

        //10 seconds
        if (clock4 > 9) {

            //reset
            clock4 = 0;

            //increase the tens (seconds)
            clock3++;

            //flag change
            flag3 = true;
        }

        //60 seconds
        if (clock3 > 5) {

            //reset
            clock3 = 0;

            //increase the minutes
            clock2++;

            //flag change
            flag2 = true;
        }

        //10 minutes
        if (clock2 > 9) {

            //reset
            clock2 = 0;

            //increase the tens (minutes)
            clock1++;

            //flag change
            flag1 = true;
        }

        if (clock1 > 9) {

            //we won't go past 99:59
            clock1 = 9;
            clock2 = 9;
            clock3 = 5;
            clock4 = 9;

            //flag all changed
            flag1 = true;
            flag2 = true;
            flag3 = true;
            flag4 = true;
        }
    }

    private void descend() {

        //decrease the seconds
        clock4--;

        //flag that a change was made
        flag4 = true;

        //10 seconds
        if (clock4 < 0) {

            //reset
            if (clock3 > 0 || clock2 > 0 || clock1 > 0) {
                clock4 = 9;
            } else {
                clock4 = 0;
            }

            //decrease the tens (seconds)
            clock3--;

            //flag change
            flag3 = true;
        }

        //60 seconds
        if (clock3 < 0) {

            //reset back to 59
            if (clock2 > 0 || clock1 > 0) {
                clock3 = 5;
            } else {
                clock3 = 0;
            }

            //decrease the minutes
            clock2--;

            //flag change
            flag2 = true;
        }

        //10 minutes
        if (clock2 < 0) {

            //reset
            if (clock1 > 0) {
                clock2 = 9;
            } else {
                clock2 = 0;
            }

            //decrease the tens (minutes)
            clock1--;

            //flag change
            flag1 = true;
        }

        //don't go below 0
        if (clock1 < 0)
            clock1 = 0;
    }

    /**
     * Update timer display
     */
    public void updateTimer() {

        if (this.runnable == null) {
            this.runnable = new Runnable() {
                @Override
                public void run() {
                    if (flag1)
                        time1.setImageBitmap(images[clock1]);
                    if (flag2)
                        time2.setImageBitmap(images[clock2]);
                    if (flag3)
                        time3.setImageBitmap(images[clock3]);
                    if (flag4)
                        time4.setImageBitmap(images[clock4]);
                }
            };
        }

        //run on ui thread
        getActivity().runOnUiThread(runnable);
    }
}