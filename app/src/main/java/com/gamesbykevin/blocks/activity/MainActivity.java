package com.gamesbykevin.blocks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.gamesbykevin.blocks.R;
import com.gamesbykevin.blocks.opengl.MainActivityRenderer;
import com.gamesbykevin.blocks.ui.CustomButton;

import org.rajawali3d.view.SurfaceView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.gamesbykevin.blocks.activity.BaseActivityHelper.RESOURCE_SOUNDS;
import static com.gamesbykevin.blocks.activity.BaseActivityHelper.SOUND_KEY;
import static com.gamesbykevin.blocks.activity.BaseActivityHelper.stopSound;
import static com.gamesbykevin.blocks.activity.MainActivityHelper.updateSharedPreferences;
import static com.gamesbykevin.blocks.opengl.MainActivityRenderer.CURRENT_BACKGROUND;
import static com.gamesbykevin.blocks.opengl.MainActivityRenderer.CURRENT_TEXTURE;

public class MainActivity extends BaseActivity {

    //does the user want to exit?
    private boolean exit = false;

    //the current screen displayed
    private int screen;

    //renderer for the block
    private MainActivityRenderer renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //call parent
        super.onCreate(savedInstanceState);

        //setup our visual display
        setContentView(R.layout.activity_main);

        //create our renderer
        this.renderer = new MainActivityRenderer(this);

        //obtain our surface view
        SurfaceView surfaceView = findViewById(R.id.game_surfaceView);

        //assign the game's frame rate
        surfaceView.setFrameRate(FPS);

        //assign renderer to our surface view
        surfaceView.setSurfaceRenderer(renderer);

        //display main menu
        switchScreen(R.id.table_layout_main_menu);

        //setup the spinner for different textures
        setupSpinnerTexture();

        //setup the spinner for different backgrounds
        setupSpinnerBackground();

        //pre-pop our sound setting
        setupCustomButtonSound();

        //pre-pop the vibrate setting
        setupCustomButtonVibrate();
    }

    @Override
    public void onResume() {

        //call parent
        super.onResume();

        //we have not yet prompted exit
        this.exit = false;

        //hide the splash page
        findViewById(R.id.layout_splash).setVisibility(GONE);

        //display the rotating block
        findViewById(R.id.game_surfaceView).setVisibility(VISIBLE);

        //resume menu music
        BaseActivityHelper.playSound(R.raw.menu, true, false);
    }

    @Override
    public void onDestroy() {

        //call parent
        super.onDestroy();

        SurfaceView surfaceView = findViewById(R.id.game_surfaceView);

        if (surfaceView != null) {
            surfaceView.destroyDrawingCache();
            surfaceView = null;
        }

        if (SOUND != null) {

            for (int i = 0; i < RESOURCE_SOUNDS.length; i++) {
                if (SOUND.get(RESOURCE_SOUNDS[i]) != null) {
                    SOUND.get(RESOURCE_SOUNDS[i]).release();
                    SOUND.put(RESOURCE_SOUNDS[i], null);
                }
            }

            SOUND.clear();
            SOUND = null;
        }

        if (this.renderer != null) {
            this.renderer.dispose();
            this.renderer = null;
        }
    }

    @Override
    public void onBackPressed() {

        switch (getScreen()) {

            //if main menu prompt exit
            case R.id.table_layout_main_menu:

                if (!exit) {

                    //prompt user for exit
                    super.displayMessage(getString(R.string.exit_app_prompt));

                    //flag exit prompt
                    this.exit = true;

                } else {

                    //close all open activities
                    finishAffinity();
                }
                break;

            //if we are on the options page, we can go back to the main menu
            case R.id.table_layout_options_menu:

                //before we go back, save the shared preferences
                updateSharedPreferences(this);

                //if disabled turn off sound
                if (getPreferences().getInt(SOUND_KEY, 0) != 0) {
                    stopSound();
                } else {
                    BaseActivityHelper.playSound(R.raw.menu, true, false);
                }

                //go from options to the main menu
                switchScreen(R.id.table_layout_main_menu);
                break;
        }

    }

    public void startGame(View view) {

        //hide the rotating block
        findViewById(R.id.game_surfaceView).setVisibility(GONE);

        //display the loading screen
        findViewById(R.id.layout_splash).setVisibility(VISIBLE);

        //start the level select activity
        startActivity(new Intent(this, LevelSelectActivity.class));
    }

    public void showOptions(View view) {
        switchScreen(R.id.table_layout_options_menu);
    }

    public void switchScreen(final int resId) {

        //assign the current screen
        this.screen = resId;

        //we have not yet prompted exit when switching screens
        this.exit = false;

        switch (getScreen()) {

            case R.id.table_layout_main_menu:
                findViewById(R.id.table_layout_main_menu).setVisibility(VISIBLE);
                findViewById(R.id.table_layout_options_menu).setVisibility(GONE);
                break;

            case R.id.table_layout_options_menu:
                findViewById(R.id.table_layout_main_menu).setVisibility(GONE);
                findViewById(R.id.table_layout_options_menu).setVisibility(VISIBLE);
                break;
        }
    }

    public int getScreen() {
        return this.screen;
    }

    private void setupSpinnerTexture() {

        //obtain the spinner object reference
        final Spinner spinner = findViewById(R.id.spinner_texture);

        //assign custom spinner ui layout
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.spinner_text_texture, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //pre-populate the setting
        spinner.setSelection(CURRENT_TEXTURE);

        //assign the on selected listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                //update the spinning 3d block
                renderer.setNext(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setupSpinnerBackground() {

        //obtain the spinner object reference
        final Spinner spinner = findViewById(R.id.spinner_background);

        //assign custom spinner ui layout
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.spinner_text_background, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //default background
        spinner.setSelection(CURRENT_BACKGROUND);

        //assign the on selected listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                //update the background position
                CURRENT_BACKGROUND = position;

                //update the image background
                setupBackground();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setupCustomButtonSound() {
        CustomButton button = findViewById(R.id.customButtonSound);
        button.setIndex(BaseActivity.getPreferences().getInt(getString(R.string.file_key_sound), 0));
    }

    private void setupCustomButtonVibrate() {
        CustomButton button = findViewById(R.id.customButtonVibrate);
        button.setIndex(BaseActivity.getPreferences().getInt(getString(R.string.file_key_vibrate), 0));
    }
}