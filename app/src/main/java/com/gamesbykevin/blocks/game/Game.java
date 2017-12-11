package com.gamesbykevin.blocks.game;

import com.gamesbykevin.blocks.activity.LevelSelectActivity;
import com.gamesbykevin.blocks.activity.MainActivity;
import com.gamesbykevin.blocks.block.Block;
import com.gamesbykevin.blocks.board.Board;
import com.gamesbykevin.blocks.common.ICommon;
import com.gamesbykevin.blocks.levels.Level;
import com.gamesbykevin.blocks.opengl.Renderer;
import com.gamesbykevin.blocks.util.Timer;

import static com.gamesbykevin.blocks.activity.LevelSelectActivity.LEVELS;

/**
 * Created by Kevin on 11/29/2017.
 */
public class Game implements ICommon {

    //reference to our main activity
    private final MainActivity activity;

    //game block used to solve level
    private Block block;

    //board where block plays on
    private Board board;

    //keep track of our game timer
    private Timer timer;

    public Game(final MainActivity activity) throws Exception {

        //save our activity reference
        this.activity = activity;

        //create new game timer
        this.timer = new Timer();
    }

    @Override
    public void dispose() {

        if (block != null) {
            block.dispose();
            block = null;
        }

        if (board != null) {
            board.dispose();
            board = null;
        }

        if (timer != null) {
            timer.dispose();
            timer = null;
        }
    }

    public MainActivity getActivity() {
        return this.activity;
    }

    @Override
    public void reset() {

        if (getBlock() != null) {

            //reset the block
            getBlock().reset();

            //assign 3d model render location
            getBlock().getPrism().setX(getLevel().getStartCol() + .5);
            getBlock().getPrism().setY(getLevel().getStartRow());

            //if we are supposed to start standing, stand the block up
            if (getLevel().isStanding())
                getBlock().stand();

            //start z location at top of the screen
            getBlock().getPrism().setZ(Block.START_Z);

            //assign starting location
            getBlock().setCol(getLevel().getStartCol());
            getBlock().setRow(getLevel().getStartRow());
        }

        //reset the board
        if (getBoard() != null)
            getBoard().reset();
    }

    /**
     * Create a new level now that the 3d objects in the renderer have been loaded
     */
    public void create() {

        //create new board instance
        this.board = new Board();

        //assign our block reference
        this.block = new Block(getActivity().getRenderer().getBlocks()[Renderer.PRISM_BLOCK]);

        //reset game timer
        getTimer().reset();

        //add the game block to the 3d scene
        getActivity().getRenderer().getCurrentScene().addChild(getActivity().getRenderer().getBlocks()[Renderer.PRISM_BLOCK]);

        //create the board based on the current level
        getBoard().create(getLevel());

        //add 3d models to our board
        getBoard().populate(getActivity().getRenderer());

        //update the camera angle
        getActivity().getRenderer().updateCamera(getLevel());

        //reset the game
        reset();
    }

    public Level getLevel() {
        return LEVELS.getLevel();
    }

    public Board getBoard() {
        return this.board;
    }

    public Block getBlock() {
        return this.block;
    }

    public Timer getTimer() {
        return this.timer;
    }

    /**
     * Update our game
     */
    public void update() {

        if (getBlock() != null && getBoard() != null) {

            if (getBlock().isDead()) {

                //if the block is falling out of bounds let's reset the level
                reset();

                //update timer
                getTimer().update();

                //update timer display
                getActivity().getRenderer().updateTimer(getTimer());

            } else {

                if (getBlock().hasGoalComplete()) {

                    //if we are done, save the level as completed
                    LevelSelectActivity.addCompleted(LEVELS.getIndex());

                } else if (!getBoard().hasSetup()) {

                    //update the block based on the user input
                    getBlock().update();

                    if (!getBlock().hasGoal()) {

                        //update our game timer
                        getTimer().update();

                        //update timer display
                        getActivity().getRenderer().updateTimer(getTimer());

                    } else {

                        //if we are done, save the level as completed
                        LevelSelectActivity.addCompleted(LEVELS.getIndex());
                    }
                }
            }
        }
    }
}