package com.gamesbykevin.blocks.board;

import com.gamesbykevin.blocks.block.Block;
import com.gamesbykevin.blocks.common.ICommon;
import com.gamesbykevin.blocks.levels.Level;
import com.gamesbykevin.blocks.opengl.Renderer;

import org.rajawali3d.Object3D;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by Kevin on 11/27/2017.
 */
public class Board implements ICommon {

    //list of tiles that makes up the board
    private Tile[][] tiles;

    //the goal of the level
    private int goalCol, goalRow;

    //the starting point of our game
    private int startCol, startRow;

    public Board(final Level level) {
        this.tiles = new Tile[level.getRows()][level.getCols()];

        //check every line in our level key
        for (int row = 0; row < level.getKey().size(); row++) {

            //check every string character in the current line key
            for (int col = 0; col < level.getKey().get(row).length(); col++) {

                //get the current key
                final String key = level.getKey().get(row).substring(col, col + 1);

                if (key.equalsIgnoreCase(Tile.Type.Goal.key)) {
                    this.goalCol = col;
                    this.goalRow = row;
                } else if (key.equalsIgnoreCase(Tile.Type.Start.key)) {
                    this.startCol = col;
                    this.startRow = row;
                }

                for (int i = 0; i < Tile.Type.values().length; i++) {
                    if (key.equalsIgnoreCase(Tile.Type.values()[i].key)) {
                        getTiles()[row][col] = new Tile(Tile.Type.values()[i]);
                        break;
                    }
                }
            }
        }
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
        if (col >= getTiles()[0].length || row >= getTiles().length)
            return false;

        //if not null and visible, there is a floor here
        return (getTiles()[row][col] != null && getTiles()[row][col].getObject3D() != null && getTiles()[row][col].getObject3D().isVisible());
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

    public void populate(Renderer renderer, Object3D object3D, Object3D triangle, Object3D circle) {

        //renderer.getCurrentScene().addChild(triangle);
        //renderer.getCurrentScene().addChild(circle);

        for (int row = 0; row < getTiles().length; row++) {
            for (int col = 0; col < getTiles()[0].length; col++) {

                if (getTiles()[row][col] == null)
                    continue;

                if (!hasGoal(col, row)) {
                    getTiles()[row][col].setObject3D(object3D.clone());

                    //assign the position
                    getTiles()[row][col].getObject3D().setPosition(col, row, 0);

                    //add it to the render scene so we can view it
                    renderer.getCurrentScene().addChild(getTiles()[row][col].getObject3D());
                }
            }
        }
    }

    public Tile[][] getTiles() {
        return this.tiles;
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