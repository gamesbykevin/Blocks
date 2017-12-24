package com.gamesbykevin.blocks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.gamesbykevin.blocks.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.AnnotatedData;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.gamesbykevin.blocks.activity.BaseActivity.TAG;
import static com.gamesbykevin.blocks.activity.BaseActivity.getActivity;
import static com.gamesbykevin.blocks.activity.GooglePlayActivityHelper.checkPendingAchievements;
import static com.gamesbykevin.blocks.activity.GooglePlayActivityHelper.checkPendingScores;
import static com.gamesbykevin.blocks.activity.GooglePlayActivityHelper.displayAchievements;
import static com.gamesbykevin.blocks.activity.GooglePlayActivityHelper.displayLeaderboard;
import static com.gamesbykevin.blocks.activity.GooglePlayActivityHelper.displayMessage;
import static com.gamesbykevin.blocks.activity.GooglePlayActivityHelper.handleException;
import static com.gamesbykevin.blocks.activity.GooglePlayActivityHelper.trackAchievement;
import static com.gamesbykevin.blocks.activity.GooglePlayActivityHelper.trackLeaderboard;
import static com.gamesbykevin.blocks.activity.GooglePlayActivityHelper.trackLogin;

/**
 * Created by Kevin on 12/19/2017.
 */
public abstract class GooglePlayActivity extends AppCompatActivity {

    /**
     * We ignore google play for amazon products
     */
    public static boolean AMAZON = true;

    //client used to sign in with Google APIs
    private GoogleSignInClient googleSignInClient;

    // Client variables
    private static AchievementsClient achievementsClient;
    private LeaderboardsClient leaderboardsClient;
    private PlayersClient playersClient;

    // request codes we use when invoking an external activity
    private static final int RC_SIGN_IN = 9001;

    //name of the google user logged in
    private String displayName;

    //are we attempting to access while not logged in
    protected boolean achievements = false;

    //are we attempting to access while not logged in
    protected boolean leaderboard = false;

    //the desired leader board
    protected int resourceIdLeaderboard = -1;

    //our fire base analytics object
    protected FirebaseAnalytics firebaseAnalytics;

    //the hash map containing our players achievements
    private HashMap<String, Achievement> achievementHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //call parent
        super.onCreate(savedInstanceState);

        // Create the client used to sign in to Google services.
        this.googleSignInClient = GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build());

        //obtain our fire base analytics object reference
        this.firebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    protected GoogleSignInClient getGoogleSignInClient() {
        return this.googleSignInClient;
    }

    protected LeaderboardsClient getLeaderboardsClient() {
        return this.leaderboardsClient;
    }

    protected static AchievementsClient getAchievementsClient() {
        return achievementsClient;
    }

    protected PlayersClient getPlayersClient() {
        return this.playersClient;
    }

    @Override
    public void onResume() {

        //call parent
        super.onResume();

        // Since the state of the signed in user can change when the activity is not active
        // it is recommended to try and sign in silently from when the app resumes.
        signInSilently();

        //if amazon product, update ui so google play services buttons are disabled
        if (AMAZON)
            updateUI();
    }

    @Override
    public void onPause() {

        //call parent
        super.onPause();
   }

    @Override
    public void onBackPressed() {

        //call parent
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {

        //call parent
        super.onDestroy();
    }

    private void signInSilently() {

        //don't continue if this is an amazon app
        if (AMAZON)
            return;

        Log.d(TAG, "signInSilently()");

        getGoogleSignInClient().silentSignIn().addOnCompleteListener(this,
            new OnCompleteListener<GoogleSignInAccount>() {
                @Override
                public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInSilently(): success");
                        onConnected(task.getResult());
                    } else {
                        Log.d(TAG, "signInSilently(): failure", task.getException());
                        onDisconnected();
                    }
                }
            }
        );
    }

    protected void onConnected(GoogleSignInAccount googleSignInAccount) {
        Log.d(TAG, "onConnected(): connected to Google APIs");

        this.achievementsClient = Games.getAchievementsClient(this, googleSignInAccount);
        this.leaderboardsClient = Games.getLeaderboardsClient(this, googleSignInAccount);
        this.playersClient = Games.getPlayersClient(this, googleSignInAccount);

        //are there pending scores to submit?
        checkPendingScores(this);

        //get a list of game achievements
        getAchievementsClient().load(false).addOnSuccessListener(new OnSuccessListener<AnnotatedData<AchievementBuffer>>() {

            @Override
            public void onSuccess(AnnotatedData<AchievementBuffer> achievementBufferAnnotatedData) {

                //create new list if null
                if (achievementHashMap == null)
                    achievementHashMap = new HashMap<>();

                //clear the list before we update
                achievementHashMap.clear();

                //get the list of achievements
                AchievementBuffer buffer = achievementBufferAnnotatedData.get();

                //loop through the list of achievements
                for (int i = 0; i < buffer.getCount(); i++) {

                    //obtain the current achievement
                    Achievement achievement = buffer.get(i);

                    //update hash map with status of achievement
                    achievementHashMap.put(achievement.getAchievementId(), achievement);
                }

                //compare hash map to our pending list of achievements (if any)
                checkPendingAchievements(getActivity(), achievementHashMap);
            }
        });

        // Set the greeting appropriately on main menu
        getPlayersClient().getCurrentPlayer().addOnCompleteListener(new OnCompleteListener<Player>() {

            @Override
            public void onComplete(@NonNull Task<Player> task) {
                if (task.isSuccessful()) {
                    displayName = task.getResult().getDisplayName();
                } else {
                    handleException(getActivity(), task.getException(), getString(R.string.players_exception));
                    displayName = "???";
                }

                Log.d(TAG, "Hi: " + displayName);

                //update the ui
                updateUI();
            }
        });

        //if we wanted to access the achievements / leader board do so now
        if (achievements) {
            onClickAchievements(null);
        } else if (leaderboard) {
            onClickLeaderboard(null);
        }
    }

    private void onDisconnected() {
        Log.d(TAG, "onDisconnected()");

        achievementsClient = null;
        leaderboardsClient = null;
        playersClient = null;

        //update the ui
        updateUI();
    }

    protected void startSignInIntent() {

        if (AMAZON) {
            displayMessage(this, getString(R.string.google_play_not_supported));
            return;
        }

        //only sign in if we aren't signed in already
        if (!isSignedIn()) {

            //start google's login activity
            startActivityForResult(getGoogleSignInClient().getSignInIntent(), RC_SIGN_IN);

            //log our login attempt
            trackLogin(this);
        }
    }

    protected void signOut() {
        Log.d(TAG, "signOut()");

        if (!isSignedIn()) {
            Log.w(TAG, "signOut() called, but was not signed in!");
            return;
        }

        getGoogleSignInClient().signOut().addOnCompleteListener(this,
            new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    boolean successful = task.isSuccessful();
                    Log.d(TAG, "signOut(): " + (successful ? "success" : "failed"));
                    onDisconnected();
                }
            }
        );
    }

    protected boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this) != null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                onConnected(account);
            } catch (ApiException apiException) {
                onDisconnected();
            }
        }
    }

    private void updateUI() {

        //are we signed in
        boolean signedIn = isSignedIn();

        //obtain our ui components
        TextView textView = findViewById(R.id.text_greeting);
        TextView textViewAchievements = findViewById(R.id.textViewAchievements);
        TextView textViewLeaderboard = findViewById(R.id.textViewLeaderboard);
        ImageView imageViewAchievements = findViewById(R.id.imageViewAchievements);
        ImageView imageViewLeaderboard = findViewById(R.id.imageViewLeaderboard);

        //update based on sign in status
        if (textView != null)
            textView.setText(signedIn ? getString(R.string.signin_success_welcome) + ", " + displayName : getString(R.string.not_signed_in_default_message));
        if (textViewAchievements != null)
            textViewAchievements.setBackgroundResource(signedIn ? R.drawable.button : R.drawable.button_disabled);
        if (textViewLeaderboard != null)
            textViewLeaderboard.setBackgroundResource(signedIn ? R.drawable.button : R.drawable.button_disabled);
        if (imageViewAchievements != null)
            imageViewAchievements.setImageResource(signedIn ? R.drawable.level_complete_achievements : R.drawable.level_complete_achievements_disabled);
        if (imageViewLeaderboard != null)
            imageViewLeaderboard.setImageResource(signedIn ? R.drawable.level_complete_leaderboard : R.drawable.level_complete_leaderboard_disabled);

        TableLayout tableLayout = findViewById(R.id.table_layout_main_menu);

        //hide and show view again so the text is aligned correctly
        if (tableLayout != null) {
            tableLayout.setVisibility(GONE);
            tableLayout.setVisibility(VISIBLE);

            //hide the other table if it exists
            TableLayout tableLayoutOptions = findViewById(R.id.table_layout_options_menu);

            //hide the other table if it exists
            if (tableLayoutOptions != null)
                tableLayoutOptions.setVisibility(GONE);
        }
    }

    public void onClickAchievements(View view) {

        Log.d(TAG, "onClickAchievements()");

        if (AMAZON) {
            displayMessage(this, getString(R.string.google_play_not_supported));
            return;
        }

        //display the achievements ui
        displayAchievements(this);
    }

    public void onClickLeaderboard(View view) {

        Log.d(TAG, "onClickLeaderboard() - " + resourceIdLeaderboard);

        if (AMAZON) {
            displayMessage(this, getString(R.string.google_play_not_supported));
            return;
        }

        //display the leader board
        displayLeaderboard(this);
    }

    protected void unlockAchievement(final int resId, final String achievementName) {

        //we can't continue if our object is null or we aren't signed in
        if (!isSignedIn())
            return;
        if (getAchievementsClient() == null)
            return;

        Log.d(TAG, "unlockAchievement() - " + getString(resId));

        //unlock the specified achievement
        getAchievementsClient().unlock(getString(resId));

        //track the event
        trackAchievement(this, getString(resId));

        //display message to the user after call to unlock achievement
        displayMessage(this, getString(R.string.achievement_unlocked_prompt) + " - " + achievementName);
    }

    protected void updateLeaderboard(final int resId, final long duration) {

        //we can't continue if our object is null or we aren't signed in
        if (!isSignedIn())
            return;
        if (getLeaderboardsClient() == null)
            return;

        Log.d(TAG, "updateLeaderboard() - " + getString(resId) + " : duration = " + duration);

        //submit our score to the leader board
        getLeaderboardsClient().submitScore(getString(resId), duration);

        //track the event
        trackLeaderboard(this, getString(resId), duration);
    }
}