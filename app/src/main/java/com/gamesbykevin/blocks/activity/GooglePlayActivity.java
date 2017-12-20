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
import com.google.android.gms.games.EventsClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.gamesbykevin.blocks.activity.BaseActivity.TAG;

/**
 * Created by Kevin on 12/19/2017.
 */
public class GooglePlayActivity extends AppCompatActivity {

    //client used to sign in with Google APIs
    private static GoogleSignInClient mGoogleSignInClient;

    // Client variables
    private AchievementsClient mAchievementsClient;
    private LeaderboardsClient mLeaderboardsClient;
    private EventsClient mEventsClient;
    private PlayersClient mPlayersClient;

    // request codes we use when invoking an external activity
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;

    //name of the google user logged in
    private String displayName;

    //are we attempting to access while not logged in
    private boolean achievements = false;

    //are we attempting to access while not logged in
    private boolean leaderboard = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //call parent
        super.onCreate(savedInstanceState);

        // Create the client used to sign in to Google services.
        this.mGoogleSignInClient = GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build());
    }

    @Override
    public void onResume() {

        //call parent
        super.onResume();

        // Since the state of the signed in user can change when the activity is not active
        // it is recommended to try and sign in silently from when the app resumes.
        signInSilently();
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
        Log.d(TAG, "signInSilently()");

        try {
            this.mGoogleSignInClient.silentSignIn().addOnCompleteListener(this,
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onConnected(GoogleSignInAccount googleSignInAccount) {
        Log.d(TAG, "onConnected(): connected to Google APIs");

        mAchievementsClient = Games.getAchievementsClient(this, googleSignInAccount);
        mLeaderboardsClient = Games.getLeaderboardsClient(this, googleSignInAccount);
        mEventsClient = Games.getEventsClient(this, googleSignInAccount);
        mPlayersClient = Games.getPlayersClient(this, googleSignInAccount);

        // Set the greeting appropriately on main menu
        mPlayersClient.getCurrentPlayer().addOnCompleteListener(new OnCompleteListener<Player>() {
            @Override
            public void onComplete(@NonNull Task<Player> task) {
                if (task.isSuccessful()) {
                    displayName = task.getResult().getDisplayName();
                } else {
                    task.getException().printStackTrace();
                    displayName = "???";
                }

                Log.d(TAG, "Hi: " + displayName);

                //update the ui
                updateUI();
            }
        });

        //if we wanted to access the achievements/leaderboard do so now
        if (achievements) {
            onClickAchievements(null);
        } else if (leaderboard) {
            onClickLeaderboard(null);
        }
    }

    private void onDisconnected() {
        Log.d(TAG, "onDisconnected()");

        mAchievementsClient = null;
        mLeaderboardsClient = null;
        mEventsClient = null;
        mPlayersClient = null;

        //update the ui
        updateUI();
    }

    protected void startSignInIntent() {
        if (!isSignedIn())
            startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    protected void signOut() {
        Log.d(TAG, "signOut()");

        if (!isSignedIn()) {
            Log.w(TAG, "signOut() called, but was not signed in!");
            return;
        }

        try {
            mGoogleSignInClient.signOut().addOnCompleteListener(this,
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            boolean successful = task.isSuccessful();
                            Log.d(TAG, "signOut(): " + (successful ? "success" : "failed"));

                            onDisconnected();
                        }
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                String message = apiException.getMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }

                onDisconnected();
                //new AlertDialog.Builder(this).setMessage(message).setNeutralButton(android.R.string.ok, null).show();
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

        if (tableLayout != null) {
            //hide and show again so the text is aligned correct
            tableLayout.setVisibility(GONE);
            tableLayout.setVisibility(VISIBLE);
        }
    }

    public void onClickAchievements(View view) {

        //if we want to access and are not logged in
        if (!isSignedIn()) {

            //flag we want to access after login
            achievements = true;

            //begin google login
            startSignInIntent();

        } else {

            //we no longer are attempting to access
            achievements = false;

            mAchievementsClient.getAchievementsIntent().addOnSuccessListener(new OnSuccessListener<Intent>() {
                @Override
                public void onSuccess(Intent intent) {
                    startActivityForResult(intent, RC_UNUSED);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void onClickLeaderboard(View view) {

        //if we want to access and are not logged in
        if (!isSignedIn()) {

            //flag we want to access after login
            leaderboard = true;

            //begin google login
            startSignInIntent();

        } else {

            //we no longer are attemping to access
            leaderboard = false;

            mLeaderboardsClient.getAllLeaderboardsIntent().addOnSuccessListener(new OnSuccessListener<Intent>() {
                @Override
                public void onSuccess(Intent intent) {
                    startActivityForResult(intent, RC_UNUSED);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}