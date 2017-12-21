package com.gamesbykevin.blocks.activity;

import android.util.Log;

import com.gamesbykevin.blocks.R;

import static com.gamesbykevin.blocks.activity.BaseActivity.TAG;

/**
 * Created by Kevin on 12/20/2017.
 */
public class GameOverActivityHelper {

    protected static int getResIdAchievement(final int index) {

        //the resource reference
        int resId = -1;

        //figure out which achievement we are unlocking
        if (index == 0) {
            resId = R.string.achievement_level_1;
        } else if (index == 1) {
            resId = R.string.achievement_level_2;
        } else if (index == 2) {
            resId = R.string.achievement_level_3;
        } else if (index == 3) {
            resId = R.string.achievement_level_4;
        } else if (index == 4) {
            resId = R.string.achievement_level_5;
        } else if (index == 5) {
            resId = R.string.achievement_level_6;
        } else if (index == 6) {
            resId = R.string.achievement_level_7;
        } else if (index == 7) {
            resId = R.string.achievement_level_8;
        } else if (index == 8) {
            resId = R.string.achievement_level_9;
        } else if (index == 9) {
            resId = R.string.achievement_level_10;
        } else if (index == 10) {
            resId = R.string.achievement_level_11;
        } else if (index == 11) {
            resId = R.string.achievement_level_12;
        } else if (index == 12) {
            resId = R.string.achievement_level_13;
        } else if (index == 13) {
            resId = R.string.achievement_level_14;
        } else if (index == 14) {
            resId = R.string.achievement_level_15;
        } else if (index == 15) {
            resId = R.string.achievement_level_16;
        } else if (index == 16) {
            resId = R.string.achievement_level_17;
        } else if (index == 17) {
            resId = R.string.achievement_level_18;
        } else if (index == 18) {
            resId = R.string.achievement_level_19;
        } else if (index == 19) {
            resId = R.string.achievement_level_20;
        } else if (index == 20) {
            resId = R.string.achievement_level_21;
        } else if (index == 21) {
            resId = R.string.achievement_level_22;
        } else if (index == 22) {
            resId = R.string.achievement_level_23;
        } else if (index == 23) {
            resId = R.string.achievement_level_24;
        } else if (index == 24) {
            resId = R.string.achievement_level_25;
        } else if (index == 25) {
            resId = R.string.achievement_level_26;
        } else if (index == 26) {
            resId = R.string.achievement_level_27;
        } else if (index == 27) {
            resId = R.string.achievement_level_28;
        } else if (index == 28) {
            resId = R.string.achievement_level_29;
        } else if (index == 29) {
            resId = R.string.achievement_level_30;
        } else if (index == 30) {
            resId = R.string.achievement_level_31;
        } else if (index == 31) {
            resId = R.string.achievement_level_32;
        } else if (index == 32) {
            resId = R.string.achievement_level_33;
        } else if (index == 33) {
            resId = R.string.achievement_level_34;
        } else if (index == 34) {
            resId = R.string.achievement_level_35;
        } else if (index == 35) {
            resId = R.string.achievement_level_36;
        } else if (index == 36) {
            resId = R.string.achievement_level_37;
        } else if (index == 37) {
            resId = R.string.achievement_level_38;
        } else if (index == 38) {
            resId = R.string.achievement_level_39;
        } else if (index == 39) {
            resId = R.string.achievement_level_40;
        } else if (index == 40) {
            resId = R.string.achievement_level_41;
        } else if (index == 41) {
            resId = R.string.achievement_level_42;
        } else if (index == 42) {
            resId = R.string.achievement_level_43;
        } else if (index == 43) {
            resId = R.string.achievement_level_44;
        } else if (index == 44) {
            resId = R.string.achievement_level_45;
        } else if (index == 45) {
            resId = R.string.achievement_level_46;
        } else if (index == 46) {
            resId = R.string.achievement_level_47;
        } else if (index == 47) {
            resId = R.string.achievement_level_48;
        } else if (index == 48) {
            resId = R.string.achievement_level_49;
        } else if (index == 49) {
            resId = R.string.achievement_level_50;
        } else {
            Log.d(TAG, "Achievement not handled: " + index);
        }

        //return our result
        return resId;
    }

    protected static int getResIdLeaderboard(final int index) {

        //the resource reference
        int resId = -1;

        //figure out which leader board we are unlocking
        if (index == 0) {
            resId = R.string.leaderboard_level_1;
        } else if (index == 1) {
            resId = R.string.leaderboard_level_2;
        } else if (index == 2) {
            resId = R.string.leaderboard_level_3;
        } else if (index == 3) {
            resId = R.string.leaderboard_level_4;
        } else if (index == 4) {
            resId = R.string.leaderboard_level_5;
        } else if (index == 5) {
            resId = R.string.leaderboard_level_6;
        } else if (index == 6) {
            resId = R.string.leaderboard_level_7;
        } else if (index == 7) {
            resId = R.string.leaderboard_level_8;
        } else if (index == 8) {
            resId = R.string.leaderboard_level_9;
        } else if (index == 9) {
            resId = R.string.leaderboard_level_10;
        } else if (index == 10) {
            resId = R.string.leaderboard_level_11;
        } else if (index == 11) {
            resId = R.string.leaderboard_level_12;
        } else if (index == 12) {
            resId = R.string.leaderboard_level_13;
        } else if (index == 13) {
            resId = R.string.leaderboard_level_14;
        } else if (index == 14) {
            resId = R.string.leaderboard_level_15;
        } else if (index == 15) {
            resId = R.string.leaderboard_level_16;
        } else if (index == 16) {
            resId = R.string.leaderboard_level_17;
        } else if (index == 17) {
            resId = R.string.leaderboard_level_18;
        } else if (index == 18) {
            resId = R.string.leaderboard_level_19;
        } else if (index == 19) {
            resId = R.string.leaderboard_level_20;
        } else if (index == 20) {
            resId = R.string.leaderboard_level_21;
        } else if (index == 21) {
            resId = R.string.leaderboard_level_22;
        } else if (index == 22) {
            resId = R.string.leaderboard_level_23;
        } else if (index == 23) {
            resId = R.string.leaderboard_level_24;
        } else if (index == 24) {
            resId = R.string.leaderboard_level_25;
        } else if (index == 25) {
            resId = R.string.leaderboard_level_26;
        } else if (index == 26) {
            resId = R.string.leaderboard_level_27;
        } else if (index == 27) {
            resId = R.string.leaderboard_level_28;
        } else if (index == 28) {
            resId = R.string.leaderboard_level_29;
        } else if (index == 29) {
            resId = R.string.leaderboard_level_30;
        } else if (index == 30) {
            resId = R.string.leaderboard_level_31;
        } else if (index == 31) {
            resId = R.string.leaderboard_level_32;
        } else if (index == 32) {
            resId = R.string.leaderboard_level_33;
        } else if (index == 33) {
            resId = R.string.leaderboard_level_34;
        } else if (index == 34) {
            resId = R.string.leaderboard_level_35;
        } else if (index == 35) {
            resId = R.string.leaderboard_level_36;
        } else if (index == 36) {
            resId = R.string.leaderboard_level_37;
        } else if (index == 37) {
            resId = R.string.leaderboard_level_38;
        } else if (index == 38) {
            resId = R.string.leaderboard_level_39;
        } else if (index == 39) {
            resId = R.string.leaderboard_level_40;
        } else if (index == 40) {
            resId = R.string.leaderboard_level_41;
        } else if (index == 41) {
            resId = R.string.leaderboard_level_42;
        } else if (index == 42) {
            resId = R.string.leaderboard_level_43;
        } else if (index == 43) {
            resId = R.string.leaderboard_level_44;
        } else if (index == 44) {
            resId = R.string.leaderboard_level_45;
        } else if (index == 45) {
            resId = R.string.leaderboard_level_46;
        } else if (index == 46) {
            resId = R.string.leaderboard_level_47;
        } else if (index == 47) {
            resId = R.string.leaderboard_level_48;
        } else if (index == 48) {
            resId = R.string.leaderboard_level_49;
        } else if (index == 49) {
            resId = R.string.leaderboard_level_50;
        } else {
            Log.d(TAG, "Leader board not handled: " + index);
        }

        //return our result
        return resId;
    }
}