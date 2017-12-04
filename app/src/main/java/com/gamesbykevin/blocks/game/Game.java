package com.gamesbykevin.blocks.game;

import com.gamesbykevin.blocks.activity.LevelSelectActivity;
import com.gamesbykevin.blocks.activity.MainActivity;
import com.gamesbykevin.blocks.block.Block;
import com.gamesbykevin.blocks.board.Board;
import com.gamesbykevin.blocks.common.ICommon;
import com.gamesbykevin.blocks.levels.Level;
import com.gamesbykevin.blocks.opengl.Renderer;

import org.rajawali3d.Object3D;
import org.rajawali3d.primitives.RectangularPrism;

import static com.gamesbykevin.blocks.block.BlockHelper.HEIGHT_Z_LIMIT_FALL;

/**
 * Created by Kevin on 11/29/2017.
 */
public class Game implements ICommon {

    private final MainActivity activity;

    //game block used to solve level
    private Block block;

    //board where block plays on
    private Board board;

    public Game(final MainActivity activity) throws Exception {

        //save our activity reference
        this.activity = activity;
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

            //assign starting location
            getBlock().setCol(getLevel().getStartCol());
            getBlock().setRow(getLevel().getStartRow());
        }

        if (getBoard() != null)
            getBoard().reset();
    }

    public void create(final Renderer renderer, final RectangularPrism[] blocks, final Object3D[] misc) {

        //assign our block reference
        this.block = new Block(blocks[Renderer.PRISM_BLOCK]);

        //create new board based on the current level
        this.board = new Board(getLevel());

        //add 3d models to our board
        this.board.populate(renderer, blocks, misc);

        //add the game block to the 3d scene
        renderer.getCurrentScene().addChild(blocks[Renderer.PRISM_BLOCK]);

        //reset the game
        reset();

        //update the camera angle
        renderer.updateCamera(getLevel().getCamera());
    }

    public Level getLevel() {
        return LevelSelectActivity.LEVELS.getLevel();
    }

    public Board getBoard() {
        return this.board;
    }

    public Block getBlock() {
        return this.block;
    }

    /**
     * Update our game
     */
    public void update() {

        if (getBlock() != null) {

            if (getBlock().isDead()) {
                //if the block is falling out of bounds let's reset the level
                reset();
            } else {
                //update the block based on the user input
                getBlock().update();
            }
        }
    }
}