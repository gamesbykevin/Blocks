package com.gamesbykevin.blocks.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gamesbykevin.blocks.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gamesbykevin.blocks.activity.BaseActivity.TAG;
import static com.google.android.gms.games.leaderboard.LeaderboardVariant.COLLECTION_PUBLIC;
import static com.google.android.gms.games.leaderboard.LeaderboardVariant.TIME_SPAN_ALL_TIME;

/**
 * Created by Kevin on 12/20/2017.
 */
public class GooglePlayActivityHelper {

    //list of pending achievements
    private static ArrayList<Integer> PENDING_ACHIEVEMENTS;

    //list of pending scores
    private static SparseArray<Long> PENDING_SCORES;

    private static final int RC_UNUSED = 5001;

    protected static void addPendingAchievement(final int resId) {

        //create if null
        if (PENDING_ACHIEVEMENTS == null)
            PENDING_ACHIEVEMENTS = new ArrayList<>();

        //only add if it isn't already part of the list
        if (PENDING_ACHIEVEMENTS.indexOf(resId) < 0)
            PENDING_ACHIEVEMENTS.add(resId);
    }

    protected static void addPendingScore(final int resId, final long score) {

        //create if null
        if (PENDING_SCORES == null)
            PENDING_SCORES = new SparseArray<>();

        //update the hash map with the score
        PENDING_SCORES.put(resId, score);
    }

    protected static void checkPendingScores(GooglePlayActivity activity) {

        //we don't have anything to check
        if (PENDING_SCORES == null || PENDING_SCORES.size() < 1)
            return;

        //submit every pending score to the leader board
        for (int i = 0; i < PENDING_SCORES.size(); i++) {

            //obtain our resource id
            final int resId = PENDING_SCORES.keyAt(i);

            //get the score duration
            final long duration = PENDING_SCORES.get(resId);

            //update the leader board accordingly
            activity.updateLeaderboard(resId, duration);
        }

        //clear list now that we are done
        PENDING_SCORES.clear();
    }

    protected static void checkPendingAchievements(GooglePlayActivity activity, HashMap<String, Achievement> achievementHashMap) {

        //we don't have anything to check
        if (PENDING_ACHIEVEMENTS == null || PENDING_ACHIEVEMENTS.isEmpty())
            return;

        Log.d(TAG, "checkPendingAchievements()");

        //check each pending achievement
        for (int i = 0; i < PENDING_ACHIEVEMENTS.size(); i++) {

            //get the current achievementId
            String achievementId = activity.getString(PENDING_ACHIEVEMENTS.get(i));

            //get the achievement from our hash map
            Achievement achievement = achievementHashMap.get(achievementId);

            //if the achievement is not unlocked, let's unlock it
            if (achievement.getState() != Achievement.STATE_UNLOCKED) {

                Log.d(TAG, "Achievement not unlocked: " + achievementId);

                //unlock the achievement
                activity.unlockAchievement(PENDING_ACHIEVEMENTS.get(i), achievement.getName());
            }
        }

        //clear list now that we are done
        PENDING_ACHIEVEMENTS.clear();

        Log.d(TAG, "Pending achievement list cleared");
    }

    protected static void displayMessage(Activity activity, String message) {

        //create our layout view for the toast text
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup)activity.findViewById(R.id.custom_toast_container));

        //update our text view object with the desired text
        TextView text = layout.findViewById(R.id.text);
        text.setText(message);

        //create our toast text and assign the inflated layout
        Toast toast = new Toast(activity.getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    protected static void handleException(GooglePlayActivity activity, Exception e, String details) {

        int status = 0;

        e.printStackTrace();

        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            status = apiException.getStatusCode();
        }

        String message = activity.getString(R.string.status_exception_error, details, status, e);

        new AlertDialog.Builder(activity)
            .setMessage(message)
            .setNeutralButton(android.R.string.ok, null)
            .show();
    }

    protected static void displayLeaderboard(final GooglePlayActivity activity) {

        //if we want to access and are not logged in
        if (!activity.isSignedIn()) {

            //flag we want to access after login
            activity.leaderboard = true;

            //begin google login
            activity.startSignInIntent();

        } else {

            //we no longer are attempting to access
            activity.leaderboard = false;

            if (activity.resourceIdLeaderboard == -1) {
                //display master leader board
                activity.getLeaderboardsClient().getAllLeaderboardsIntent().addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        activity.startActivityForResult(intent, RC_UNUSED);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        handleException(activity, e, activity.getString(R.string.leaderboards_exception));
                    }
                });
            } else {

                //display all time, public leader board for a specific leader board
                activity.getLeaderboardsClient().getLeaderboardIntent(
                    activity.getString(activity.resourceIdLeaderboard),
                    TIME_SPAN_ALL_TIME,
                    COLLECTION_PUBLIC
                ).addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        activity.startActivityForResult(intent, RC_UNUSED);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        handleException(activity, e, activity.getString(R.string.leaderboards_exception));
                    }
                });

                //reset back to -1
                activity.resourceIdLeaderboard = -1;
            }
        }
    }

    protected static void displayAchievements(final GooglePlayActivity activity) {

        //if we want to access and are not logged in
        if (!activity.isSignedIn()) {

            //flag we want to access after login
            activity.achievements = true;

            //begin google login
            activity.startSignInIntent();

        } else {

            //we no longer are attempting to access
            activity.achievements = false;

            //display achievements list
            activity.getAchievementsClient().getAchievementsIntent().addOnSuccessListener(new OnSuccessListener<Intent>() {
                @Override
                public void onSuccess(Intent intent) {
                    activity.startActivityForResult(intent, RC_UNUSED);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    handleException(activity, e, activity.getString(R.string.achievements_exception));
                }
            });
        }
    }

    protected static void trackLogin(GooglePlayActivity activity) {

        if (activity.firebaseAnalytics == null)
            return;

        //log login attempt
        activity.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, new Bundle());
    }

    protected static void trackAchievement(GooglePlayActivity activity, String achievementId) {

        if (activity.firebaseAnalytics == null)
            return;

        //log event
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ACHIEVEMENT_ID, achievementId);
        activity.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.UNLOCK_ACHIEVEMENT, bundle);
    }

    protected static void trackLeaderboard(GooglePlayActivity activity, String leaderboardId, long score) {

        if (activity.firebaseAnalytics == null)
            return;

        //log event
        Bundle bundle = new Bundle();
        bundle.putLong(FirebaseAnalytics.Param.SCORE, score);
        bundle.putString("leaderboard_id", leaderboardId);
        activity.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.POST_SCORE, bundle);
    }
}