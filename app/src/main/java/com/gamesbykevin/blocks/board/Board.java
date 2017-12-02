package com.gamesbykevin.blocks.board;

import com.gamesbykevin.blocks.block.Block;
import com.gamesbykevin.blocks.common.ICommon;
import com.gamesbykevin.blocks.opengl.Renderer;

import org.rajawali3d.Object3D;

/**
 * Created by Kevin on 11/27/2017.
 */
public class Board implements ICommon {

    private Object3D[][] floor;

    private int goalCol, goalRow;

    public Board(final int cols, final int rows) {
        this.floor = new Object3D[rows][cols];
    }

    public void setGoal(int col, int row) {
        this.goalCol = col;
        this.goalRow = row;
    }

    public boolean hasGoal(Block block) {
        return (hasGoal(block.getCol(), block.getRow()));
    }

    public boolean hasGoal(int col, int row) {
        return (col == this.goalCol && row == this.goalRow);
    }

    public boolean hasFloor(Block block) {

        //if we don't have a floor at the current location and it isn't the goal
        if (!hasFloor(block.getCol(), block.getRow()) && !hasGoal(block.getCol(), block.getRow()))
            return false;

        //now let's check the extra location of the block
        if (block.isVertical()) {
            if (!block.isStanding()) {
                switch (block.getCurrent()) {
                    case West:
                    case East:
                        if (!hasFloor(block.getCol() + 1, block.getRow()) && !hasGoal(block.getCol() + 1, block.getRow()))
                            return false;
                        break;

                    case North:
                    case South:
                        if (!hasFloor(block.getCol(), block.getRow() + 1) && !hasGoal(block.getCol(), block.getRow() + 1))
                            return false;
                        break;
                }
            }
        } else {
            if (!block.isStanding()) {
                switch (block.getCurrent()) {
                    case West:
                    case East:
                        if (!hasFloor(block.getCol(), block.getRow() + 1) && !hasGoal(block.getCol(), block.getRow() + 1))
                            return false;
                        break;

                    case North:
                    case South:
                        if (!hasFloor(block.getCol() + 1, block.getRow()) && !hasGoal(block.getCol() + 1, block.getRow()))
                            return false;
                        break;
                }
            }
        }

        //return true by default
        return true;
    }

    public boolean hasFloor(int col, int row) {

        //if out of bounds, return false
        if (col < 0 || row < 0)
            return false;
        if (col >= floor[0].length || row >= floor.length)
            return false;

        //if not null and visible, there is a floor here
        return (floor[row][col] != null && floor[row][col].isVisible());
    }

    public Block.Direction getDirectionFall(Block block) {

        int col = (int)block.getPrism().getX();
        int row = (int)block.getPrism().getY();

        switch (block.getCurrent()) {

            case East:

                if (!hasFloor(col + 1, row))
                    return Block.Direction.East;
                break;

            case West:

                if (!hasFloor(col - 1, row))
                    return Block.Direction.West;
                break;

            case North:
                break;

            case South:
                break;
        }

        //return the current direction
        return block.getCurrent();
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

                //don't add a block to the goal
                if (hasGoal(col, row))
                    continue;


                floor[row][col] = object3D.clone();
                floor[row][col].setPosition(col, row, 0);
                renderer.getCurrentScene().addChild(floor[row][col]);
            }
        }
    }

    @Override
    public void reset() {
        //add logic here
    }

    @Override
    public void update() {
        //add logic here
    }
}