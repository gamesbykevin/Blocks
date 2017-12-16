package com.gamesbykevin.blocks.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.gamesbykevin.blocks.R;
import com.gamesbykevin.blocks.opengl.MainRenderer;

import org.rajawali3d.view.SurfaceView;

import static com.gamesbykevin.blocks.activity.MainActivityHelper.updateSharedPreferences;
import static com.gamesbykevin.blocks.opengl.MainRenderer.CURRENT_BACKGROUND;
import static com.gamesbykevin.blocks.opengl.MainRenderer.CURRENT_TEXTURE;
import static com.gamesbykevin.blocks.opengl.MainRenderer.RESOURCE_BACKGROUND;

public class MainActivity extends BaseActivity {

    //does the user want to exit?
    private boolean exit = false;

    //the current screen displayed
    private int screen;

    //renderer for the block
    private MainRenderer renderer;

    //container for our background
    private ImageView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //call parent
        super.onCreate(savedInstanceState);

        //setup our visual display
        setContentView(R.layout.activity_main);

        //obtain our image view background container
        this.background = findViewById(R.id.image_view_background);

        //create our renderer
        this.renderer = new MainRenderer(this);

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
    }

    @Override
    public void onResume() {

        //call parent
        super.onResume();

        //we have not yet prompted exit
        this.exit = false;
    }

    @Override
    public void onDestroy() {

        //call parent
        super.onDestroy();

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
                switchScreen(R.id.table_layout_main_menu);
                break;
        }

    }

    public void startGame(View view) {

        //before continuing save the shared preference settings
        updateSharedPreferences(this);

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
                findViewById(R.id.table_layout_main_menu).setVisibility(View.VISIBLE);
                findViewById(R.id.table_layout_options_menu).setVisibility(View.GONE);
                break;

            case R.id.table_layout_options_menu:
                findViewById(R.id.table_layout_main_menu).setVisibility(View.GONE);
                findViewById(R.id.table_layout_options_menu).setVisibility(View.VISIBLE);
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

                //change the background
                background.setImageResource(RESOURCE_BACKGROUND[CURRENT_BACKGROUND]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}