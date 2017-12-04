package com.gamesbykevin.blocks.board;

import android.util.Log;

import com.gamesbykevin.blocks.block.Block;
import com.gamesbykevin.blocks.block.BlockHelper;
import com.gamesbykevin.blocks.common.ICommon;
import com.gamesbykevin.blocks.levels.Level;
import com.gamesbykevin.blocks.opengl.Renderer;

import org.rajawali3d.Object3D;
import org.rajawali3d.primitives.RectangularPrism;

import java.util.List;

import static com.gamesbykevin.blocks.activity.MainActivity.TAG;
import static com.gamesbykevin.blocks.opengl.Renderer.FLOOR_DEPTH;

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

    //how high are the switches on the board
    private static final float HEIGHT_Z_SWITCH = FLOOR_DEPTH / 2;

    //list of all connectors on this board
    private final List<Level.Connector> switchesList, teleportersList;

    public Board(final Level level) {

        //create new array list of tiles
        this.tiles = new Tile[level.getRows()][level.getCols()];

        //get our list of connectors
        this.switchesList = level.getSwitches();
        this.teleportersList = level.getTeleporters();

        //assign the goal
        this.goalCol = level.getGoalCol();
        this.goalRow = level.getGoalRow();

        //assign the start
        this.startCol = level.getStartCol();
        this.startRow = level.getStartRow();

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
                if (block.isStanding())
                    getTile(block.getCol(), block.getRow()).getObject3D().setVisible(false);
                break;

            case Teleport:

                //can only be activated if we are standing
                if (block.isStanding()) {

                    for (int i = 0; i < teleportersList.size(); i++) {
                        if (block.getCol() == teleportersList.get(i).sourceCol && block.getRow() == teleportersList.get(i).sourceRow) {

                            //split up our block into 2 smaller blocks at these locations
                            for (int x = 0; x < teleportersList.get(i).connections.size(); x++) {
                                Tile tmp = getTile(teleportersList.get(i).connections.get(x).col, teleportersList.get(i).connections.get(x).row);
                                tmp.getObject3D().setVisible(!tmp.getObject3D().isVisible());
                            }
                            break;
                        }
                    }
                }
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

        //return true by default
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

    public void populate(Renderer renderer, final RectangularPrism[] blocks, final Object3D[] misc) {

        for (int row = 0; row < getTiles().length; row++) {
            for (int col = 0; col < getTiles()[0].length; col++) {

                //if null don't continue
                if (getTile(col, row) == null)
                    continue;

                //if this is a goal, we don't need to add a model here
                //if (hasGoal(col, row))
                //    continue;

                if (blocks != null) {

                    //assign the 3d model floor reference accordingly
                    switch (getTile(col, row).getType()) {

                        case Goal:
                            getTile(col, row).setObject3D(blocks[Renderer.PRISM_FLOOR_GOAL].clone());
                            break;

                        case Weak:
                            getTile(col, row).setObject3D(blocks[Renderer.PRISM_FLOOR_WEAK].clone());
                            break;

                        case Hidden:
                        case HiddenDisplay:
                            getTile(col, row).setObject3D(blocks[Renderer.PRISM_FLOOR_HIDDEN].clone());
                            break;

                        default:
                            getTile(col, row).setObject3D(blocks[Renderer.PRISM_FLOOR_STANDARD].clone());
                            break;
                    }

                }

                //assign the position where the 3d model is displayed
                getTile(col, row).getObject3D().setPosition(col, row, 0);

                //make everything visible (for now)
                getTiles()[row][col].getObject3D().setVisible(true);

                //add it to the render scene so we can view it
                if (renderer != null)
                    renderer.getCurrentScene().addChild(getTile(col, row).getObject3D());

                switch (getTile(col, row).getType()) {

                    case Hidden:
                        getTiles()[row][col].getObject3D().setVisible(false);
                        break;

                    case SwitchLight:

                        if (misc != null && renderer != null) {
                            Object3D circle = misc[Renderer.OBJECT3D_SWITCH_1].clone();
                            circle.setPosition(col, row, HEIGHT_Z_SWITCH);
                            renderer.getCurrentScene().addChild(circle);
                        }
                        break;


                    case SwitchHeavyOnlyHidden:
                    case SwitchHeavy:

                        if (misc != null && renderer != null) {
                            Object3D x = misc[Renderer.OBJECT3D_SWITCH_2].clone();
                            x.setPosition(col, row, HEIGHT_Z_SWITCH);
                            renderer.getCurrentScene().addChild(x);
                        }
                        break;

                    case Teleport:

                        if (misc != null && renderer != null) {
                            Object3D teleport = misc[Renderer.OBJECT3D_SWITCH_3].clone();
                            teleport.setPosition(col, row, HEIGHT_Z_SWITCH);
                            renderer.getCurrentScene().addChild(teleport);
                        }
                        break;
                }
            }
        }
    }

    public Tile[][] getTiles() {
        return this.tiles;
    }

    @Override
    public void reset() {
        populate(null, null, null);
    }

    @Override
    public void update() {
        //add logic here
    }
}