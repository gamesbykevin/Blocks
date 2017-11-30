package com.gamesbykevin.blocks.game;

import com.gamesbykevin.blocks.activity.MainActivity;
import com.gamesbykevin.blocks.block.Block;
import com.gamesbykevin.blocks.board.Board;
import com.gamesbykevin.blocks.common.ICommon;
import com.gamesbykevin.blocks.opengl.Renderer;

import org.rajawali3d.Object3D;

/**
 * Created by Kevin on 11/29/2017.
 */
public class Game implements ICommon {

    private final MainActivity activity;

    //game block used to solve level
    private Block block;

    //board where block plays on
    private Board board;

    public Game(final MainActivity activity) {
        this.activity = activity;
    }

    public MainActivity getActivity() {
        return this.activity;
    }

    public void createBoard(final Renderer renderer, final Object3D object3D, final int cols, final int rows) {
        this.board = new Board(cols, rows);
        this.board.populate(renderer, object3D);
        this.board.setGoal(7, 4);
    }

    public Board getBoard() {
        return this.board;
    }

    public void createBlock(final Block block) {
        this.block = block;
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