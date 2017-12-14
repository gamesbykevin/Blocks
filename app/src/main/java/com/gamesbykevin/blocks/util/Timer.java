package com.gamesbykevin.blocks.util;

import com.gamesbykevin.blocks.activity.GameActivity;
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
    private int clock1 = 0;
    private int clock2 = 0;
    private int clock3 = 0;
    private int clock4 = 0;

    //are we counting up or down ?
    private boolean ascending = true;

    //keep track of what fields have changed
    private boolean flag1 = false;
    private boolean flag2 = false;
    private boolean flag3 = false;
    private boolean flag4 = false;

    public Timer() {

        //reset timer
        reset();
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

        //we are counting up
        setAscending(true);
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
        tmp += GameActivity.THREAD_DURATION;

        //keep track of time elapsed (overall)
        lapsed += GameActivity.THREAD_DURATION;

        //if 1 second has passed, update our timer
        if (tmp >= GameActivity.MILLISECONDS_PER_SECOND) {

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
        }
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
}