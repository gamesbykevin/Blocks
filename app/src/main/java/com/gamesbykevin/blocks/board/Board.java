package com.gamesbykevin.blocks.board;

import com.gamesbykevin.blocks.block.Block;
import com.gamesbykevin.blocks.opengl.Renderer;

import org.rajawali3d.Object3D;

/**
 * Created by Kevin on 11/27/2017.
 */
public class Board {

    private Object3D[][] floor;

    private int col, row;

    public Board(final int cols, final int rows) {
        this.floor = new Object3D[rows][cols];
    }

    public void setGoal(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public boolean hasGoal(Block block) {
        if (block.getPrism().getX() < this.col - .1 || block.getPrism().getX() > this.col + 1.1)
            return false;
        if (block.getPrism().getY() < this.row - .1 || block.getPrism().getY() > this.row + 1.1)
            return false;

        return true;
    }

    public void populate(Renderer renderer, Object3D object3D) {

        for (int row = 0; row < floor.length; row++) {
            for (int col = 0; col < floor[0].length; col++) {

                //skip these areas of blocks
                if (row == 0) {
                    if (col > 2)
                        continue;
                } else if (row == 1) {
                    if (col > 5)
                        continue;
                } else if (row == 2) {
                    if (col > 8)
                        continue;
                } else if (row == 3) {
                    if (col < 1)
                        continue;
                    if (col > 9)
                        continue;
                } else if (row == 4) {
                    if (col < 5)
                        continue;
                    if (col > 9)
                        continue;
                    if (col == 7)
                        continue;
                } else if (row == 5) {
                    if (col < 6)
                        continue;
                    if (col > 8)
                        continue;
                }

                floor[row][col] = object3D.clone();
                floor[row][col].setPosition(col, row, 0);
                renderer.getCurrentScene().addChild(floor[row][col]);
            }
        }
    }
}
