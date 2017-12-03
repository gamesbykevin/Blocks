package com.gamesbykevin.blocks.game;

import com.gamesbykevin.blocks.activity.MainActivity;
import com.gamesbykevin.blocks.block.Block;
import com.gamesbykevin.blocks.board.Board;
import com.gamesbykevin.blocks.common.ICommon;
import com.gamesbykevin.blocks.levels.Levels;
import com.gamesbykevin.blocks.opengl.Renderer;

import org.rajawali3d.Object3D;
import org.rajawali3d.primitives.RectangularPrism;

/**
 * Created by Kevin on 11/29/2017.
 */
public class Game implements ICommon {

    private final MainActivity activity;

    //game block used to solve level
    private Block block;

    //board where block plays on
    private Board board;

    //list of levels in our game
    private final Levels levels;

    public Game(final MainActivity activity) throws Exception {

        //save our activity reference
        this.activity = activity;

        //create container for all our levels
        this.levels = new Levels(getActivity());
    }

    public MainActivity getActivity() {
        return this.activity;
    }

    @Override
    public void reset() {

        if (getBlock() != null)
            getBlock().reset();
    }

    public void create(final Renderer renderer, final RectangularPrism prism, final Object3D object3D, final Object3D switch1, final Object3D switch2) {

        //assign our block reference
        this.block = new Block(prism);

        //add it to the 3d scene
        renderer.getCurrentScene().addChild(prism);

        this.block.getPrism().setX(this.levels.getLevel().getStartCol() + .5);
        this.block.getPrism().setY(this.levels.getLevel().getStartRow());
        this.block.setCol(this.levels.getLevel().getStartCol());
        this.block.setRow(this.levels.getLevel().getStartRow());

        //create new board based on the current level
        this.board = new Board(this.levels.getLevel());

        //add 3d models to our board
        this.board.populate(renderer, object3D, switch1, switch2);
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

        if (getBlock() != null)
            getBlock().update();
    }
}