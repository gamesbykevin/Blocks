package com.gamesbykevin.blocks.board;

import com.gamesbykevin.blocks.activity.MainActivity;
import com.gamesbykevin.blocks.block.Block;
import com.gamesbykevin.blocks.block.BlockHelper;
import com.gamesbykevin.blocks.common.ICommon;
import com.gamesbykevin.blocks.levels.Level;
import com.gamesbykevin.blocks.opengl.Renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.gamesbykevin.blocks.board.Tile.START_Z;
import static com.gamesbykevin.blocks.board.Tile.VELOCITY_Z_MAX;
import static com.gamesbykevin.blocks.board.Tile.VELOCITY_Z_MIN;
import static com.gamesbykevin.blocks.opengl.Renderer.FLOOR_DEPTH;

/**
 * Created by Kevin on 11/27/2017.
 */
public class Board implements ICommon {

    //list of tiles that makes up the board
    private Tile[][] tiles;

    //the goal of the level
    private int goalCol, goalRow;

    //how high are the switches on the board
    public static final float HEIGHT_Z_SWITCH = FLOOR_DEPTH / 2;

    //list of all connectors on this board
    private List<Level.Connector> switchesList;

    //are we starting the board
    private boolean setup = false;

    public Board() {
        //standard constructor
    }

    @Override
    public void dispose() {

        if (this.tiles != null) {
            for (int row = 0; row < tiles.length; row++) {
                for (int col = 0; col < tiles[0].length; col++) {

                    if (tiles[row][col] != null) {
                        tiles[row][col].dispose();
                        tiles[row][col] = null;
                    }
                }
            }

            this.tiles = null;
        }

        if (switchesList != null) {
            this.switchesList.clear();
            this.switchesList = null;
        }
    }

    public void create(Level level) {

        //create new array list of tiles
        this.tiles = new Tile[level.getRows()][level.getCols()];

        //copy the switches from the level
        this.switchesList = level.copySwitches();

        //assign the goal
        this.goalCol = level.getGoalCol();
        this.goalRow = level.getGoalRow();

        //check every line in our level key
        for (int row = 0; row < level.getKey().size(); row++) {

            //check every string character in the current line key
            for (int col = 0; col < level.getKey().get(row).length(); col++) {

                //get the current key
                final String key = level.getKey().get(row).substring(col, col + 1);

                //figure out the correct enum
                for (int i = 0; i < Tile.Type.values().length; i++) {
                    if (key.equalsIgnoreCase(Tile.Type.values()[i].key)) {
                        getTiles()[row][col] = new Tile(Tile.Type.values()[i]);
                        break;
                    }
                }
            }
        }
    }

    public void checkMisc(Block block) {

        Tile tile1 = null, tile2 = null;

        //we the base tile
        tile1 = getTile(block.getCol(), block.getRow());

        //no tile beneath
        if (tile1 == null)
            return;

        //handle the tile type
        switch (tile1.getType()) {

            case Goal:

                //if we are standing on the goal, let's hide it
                //if (block.isStanding())
                //    getTile(block.getCol(), block.getRow()).getObject3D().setVisible(false);
                break;

            case Weak:

                //if standing on a weak block we need to fall through the floor
                if (block.isStanding()) {

                    //setup block to fall
                    BlockHelper.setupBlockFall(block, null);

                    //hide the tile
                    tile1.getObject3D().setVisible(false);
                }
                break;

            case SwitchLight:

                for (int i = 0; i < switchesList.size(); i++) {
                    if (block.getCol() == switchesList.get(i).sourceCol && block.getRow() == switchesList.get(i).sourceRow) {

                        for (int x = 0; x < switchesList.get(i).connections.size(); x++) {
                            Tile tmp = getTile(switchesList.get(i).connections.get(x).col, switchesList.get(i).connections.get(x).row);
                            tmp.getObject3D().setVisible(!tmp.getObject3D().isVisible());
                        }
                        break;
                    }
                }
                break;

            case SwitchHeavy:

                //we are required to stand on this switch
                if (block.isStanding()) {

                    for (int i = 0; i < switchesList.size(); i++) {
                        if (block.getCol() == switchesList.get(i).sourceCol && block.getRow() == switchesList.get(i).sourceRow) {

                            for (int x = 0; x < switchesList.get(i).connections.size(); x++) {
                                Tile tmp = getTile(switchesList.get(i).connections.get(x).col, switchesList.get(i).connections.get(x).row);
                                tmp.getObject3D().setVisible(!tmp.getObject3D().isVisible());
                            }
                            break;
                        }
                    }
                }
                break;

            case SwitchHeavyOnlyHidden:

                //we are required to stand on this switch
                if (block.isStanding()) {

                    for (int i = 0; i < switchesList.size(); i++) {
                        if (block.getCol() == switchesList.get(i).sourceCol && block.getRow() == switchesList.get(i).sourceRow) {

                            for (int x = 0; x < switchesList.get(i).connections.size(); x++) {
                                Tile tmp = getTile(switchesList.get(i).connections.get(x).col, switchesList.get(i).connections.get(x).row);
                                tmp.getObject3D().setVisible(false);
                            }
                            break;
                        }
                    }
                }
        }


        int col2 = -1, row2 = -1;

        //if the block isn't standing we need to get the other tile
        if (!block.isStanding()) {

            switch (block.getCurrent()) {
                case North:
                case South:

                    if (!block.isVertical()) {
                        col2 = block.getCol() + 1;
                        row2 = block.getRow();
                    } else {
                        col2 = block.getCol();
                        row2 = block.getRow() + 1;
                    }
                    break;

                case West:
                case East:

                    if (!block.isVertical()) {
                        col2 = block.getCol();
                        row2 = block.getRow() + 1;
                    } else {
                        col2 = block.getCol() + 1;
                        row2 = block.getRow();
                    }
                    break;
            }
        }

        //get the tile reference
        tile2 = getTile(col2, row2);

        //if the tile exists, check it
        if (tile2 != null) {

            switch (tile2.getType()) {
                case SwitchLight:

                    for (int i = 0; i < switchesList.size(); i++) {
                        if (col2 == switchesList.get(i).sourceCol && row2 == switchesList.get(i).sourceRow) {

                            for (int x = 0; x < switchesList.get(i).connections.size(); x++) {
                                Tile tmp = getTile(switchesList.get(i).connections.get(x).col, switchesList.get(i).connections.get(x).row);
                                tmp.getObject3D().setVisible(!tmp.getObject3D().isVisible());
                            }
                            break;
                        }
                    }
                    break;
            }
        }
    }

    public boolean hasGoal(Block block) {

        //we have to be standing on the goal
        if (!block.isStanding())
            return false;

        return (hasGoal(block.getCol(), block.getRow()));
    }

    public boolean hasGoal(int col, int row) {
        return (col == this.goalCol && row == this.goalRow);
    }

    public boolean hasFloor(Block block) {

        //if we don't have a floor at the current location
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

        //return true by standard
        return true;
    }

    public boolean hasFloor(int col, int row) {

        //if not null and visible, there is a floor here
        return (getTile(col, row) != null && getTile(col, row).getObject3D() != null && getTile(col, row).getObject3D().isVisible());
    }

    public Tile getTile(int col, int row) {

        //if out of bounds, return null
        if (col < 0 || row < 0)
            return null;
        if (col >= getTiles()[0].length || row >= getTiles().length)
            return null;

        return getTiles()[row][col];
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

    public void populate(Renderer renderer) {

        //flag setup true
        setSetup(true);

        for (int row = 0; row < getTiles().length; row++) {
            for (int col = 0; col < getTiles()[0].length; col++) {

                //if null don't continue
                if (getTile(col, row) == null)
                    continue;

                if (renderer != null) {

                    //assign the 3d model floor reference accordingly
                    switch (getTile(col, row).getType()) {

                        case Goal:
                            getTile(col, row).setObject3D(renderer.getBlocks()[Renderer.PRISM_FLOOR_GOAL].clone());
                            break;

                        case Weak:
                            getTile(col, row).setObject3D(renderer.getBlocks()[Renderer.PRISM_FLOOR_WEAK].clone());
                            break;

                        case Hidden:
                        case HiddenDisplay:
                            getTile(col, row).setObject3D(renderer.getBlocks()[Renderer.PRISM_FLOOR_HIDDEN].clone());
                            break;

                        default:
                            getTile(col, row).setObject3D(renderer.getBlocks()[Renderer.PRISM_FLOOR_STANDARD].clone());
                            break;
                    }

                    //assign the position where the 3d model is displayed
                    getTile(col, row).getObject3D().setPosition(col, row, START_Z);

                    //pick a random velocity at which to move
                    getTile(col, row).setVelocityZ(VELOCITY_Z_MIN + (MainActivity.getRandom().nextFloat() * (VELOCITY_Z_MAX - VELOCITY_Z_MIN)));
                }

                //make everything visible (for now)
                getTile(col, row).getObject3D().setVisible(true);

                //add it to the render scene so we can view it
                if (renderer != null)
                    renderer.getCurrentScene().addChild(getTile(col, row).getObject3D());

                switch (getTile(col, row).getType()) {

                    case Hidden:
                        getTile(col, row).getObject3D().setVisible(false);
                        break;

                    case SwitchLight:

                        if (renderer != null)
                            getTile(col, row).setMisc3D(renderer.getMisc()[Renderer.OBJECT3D_SWITCH_1].clone());
                        break;


                    case SwitchHeavyOnlyHidden:
                    case SwitchHeavy:

                        if (renderer != null)
                            getTile(col, row).setMisc3D(renderer.getMisc()[Renderer.OBJECT3D_SWITCH_2].clone());
                        break;
                }

                if (getTile(col, row).getMisc3D() != null) {
                    getTile(col, row).getMisc3D().setPosition(col, row, getTile(col, row).getObject3D().getZ() + HEIGHT_Z_SWITCH);

                    if (renderer != null)
                        renderer.getCurrentScene().addChild(getTile(col, row).getMisc3D());
                }
            }
        }
    }

    public Tile[][] getTiles() {
        return this.tiles;
    }

    public void setSetup(boolean setup) {
        this.setup = setup;
    }

    public boolean hasSetup() {
        return this.setup;
    }

    @Override
    public void reset() {
        populate(null);
    }

    @Override
    public void update() {

        if (hasSetup()) {

            boolean completed = true;

            for (int row = 0; row < getTiles().length; row++) {
                for (int col = 0; col < getTiles()[0].length; col++) {

                    //if null don't continue
                    if (getTile(col, row) == null)
                        continue;

                    //update the tile
                    getTile(col, row).update();

                    //if one tile isn't complete, we are not yet done
                    if (!getTile(col, row).hasEndZ())
                        completed = false;
                }
            }

            //if we are done with setup
            if (completed)
                setSetup(false);
        }
    }
}