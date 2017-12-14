package com.gamesbykevin.blocks.activity;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.gamesbykevin.blocks.R;

import static com.gamesbykevin.blocks.activity.GameActivity.getGame;

/**
 * Created by Kevin on 12/11/2017.
 */
public class GameActivityHelper {

    protected static void setupControlListener(View view) {

        //obtain our button reference controls
        Button buttonLeft = view.findViewById(R.id.buttonLeft);
        buttonLeft.setOnTouchListener(new View.OnTouchListener() {
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

        Button buttonRight = view.findViewById(R.id.buttonRight);
        buttonRight.setOnTouchListener(new View.OnTouchListener() {
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

        Button buttonDown = view.findViewById(R.id.buttonDown);
        buttonDown.setOnTouchListener(new View.OnTouchListener() {
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

        Button buttonUp = view.findViewById(R.id.buttonUp);
        buttonUp.setOnTouchListener(new View.OnTouchListener() {
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
}
