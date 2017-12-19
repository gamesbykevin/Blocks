package com.gamesbykevin.blocks.board;

import com.gamesbykevin.blocks.R;
import com.gamesbykevin.blocks.activity.BaseActivity;
import com.gamesbykevin.blocks.activity.BaseActivityHelper;
import com.gamesbykevin.blocks.activity.GameActivity;
import com.gamesbykevin.blocks.block.Block;
import com.gamesbykevin.blocks.block.BlockHelper;
import com.gamesbykevin.blocks.common.ICommon;
import com.gamesbykevin.blocks.levels.Level;
import com.gamesbykevin.blocks.opengl.Renderer;

import org.rajawali3d.Object3D;

import java.util.List;

import static com.gamesbykevin.blocks.activity.GameActivity.getGame;
import static com.gamesbykevin.blocks.board.Tile.START_Z;
import static com.gamesbykevin.blocks.board.Tile.VELOCITY_Z_MAX;
import static com.gamesbykevin.blocks.board.Tile.VELOCITY_Z_MIN;
import static com.gamesbykevin.blocks.opengl.RendererHelper.FLOOR_DEPTH;
import static com.gamesbykevin.blocks.opengl.RendererHelper.OBJECT3D_SWITCH_1;
import static com.gamesbykevin.blocks.opengl.RendererHelper.OBJECT3D_SWITCH_2;
import static com.gamesbykevin.blocks.opengl.RendererHelper.PRISM_FLOOR_GOAL;
import static com.gamesbykevin.blocks.opengl.RendererHelper.PRISM_FLOOR_HIDDEN;
import static com.gamesbykevin.blocks.opengl.RendererHelper.PRISM_FLOOR_STANDARD;
import static com.gamesbykevin.blocks.opengl.RendererHelper.PRISM_FLOOR_WEAK;

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

    public List<Level.Connector> getSwitchesList() {
        return this.switchesList;
    }

    public boolean hasGoal(Block block) {

        //we have to be standing on the goal
        if (!block.isStanding())
            return false;

        return (hasGoal(block.getCol(), block.getRow()));
    }

    public boolean hasGoal(int col, int row) {
        return (col == getGoalCol() && row == getGoalRow());
    }

    public int getGoalCol() {
        return this.goalCol;
    }

    public int getGoalRow() {
        return this.goalRow;
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

        //did we clone the objects
        boolean cloneWeak = false;
        boolean cloneStandard = false;
        boolean cloneHidden = false;

        for (int row = 0; row < getTiles().length; row++) {
            for (int col = 0; col < getTiles()[0].length; col++) {

                //if null don't continue
                if (getTile(col, row) == null)
                    continue;

                if (renderer != null) {

                    //assign the 3d model floor reference accordingly
                    switch (getTile(col, row).getType()) {

                        case Goal:

                            //clone the object and assign the reference to the tile
                            getTile(col, row).setObject3D(renderer.getBlocks()[PRISM_FLOOR_GOAL].clone());

                            //add it as a child to the current scene
                            renderer.getCurrentScene().addChild(getTile(col, row).getObject3D());
                            break;

                        case Weak:

                            if (!cloneWeak) {

                                //mark flag
                                cloneWeak = true;

                                //set model up for batch processing
                                renderer.getBlocks()[PRISM_FLOOR_WEAK].setRenderChildrenAsBatch(true);

                                //hide model that is not to be displayed
                                renderer.getBlocks()[PRISM_FLOOR_WEAK].setVisible(false);

                                //add it as a child to the current scene
                                renderer.getCurrentScene().addChild(renderer.getBlocks()[PRISM_FLOOR_WEAK]);
                            }

                            //clone the object and assign the reference to the tile
                            getTile(col, row).setObject3D(renderer.getBlocks()[PRISM_FLOOR_WEAK].clone());

                            //now add the tile to the parent 3d model
                            renderer.getBlocks()[PRISM_FLOOR_WEAK].addChild(getTile(col, row).getObject3D());
                            break;

                        case Hidden:
                        case HiddenDisplay:

                            if (!cloneHidden) {

                                //mark flag
                                cloneHidden = true;

                                //set model up for batch processing
                                renderer.getBlocks()[PRISM_FLOOR_HIDDEN].setRenderChildrenAsBatch(true);

                                //hide model that is not to be displayed
                                renderer.getBlocks()[PRISM_FLOOR_HIDDEN].setVisible(false);

                                //add it as a child to the current scene
                                renderer.getCurrentScene().addChild(renderer.getBlocks()[PRISM_FLOOR_HIDDEN]);
                            }

                            //clone the object and assign the reference to the tile
                            getTile(col, row).setObject3D(renderer.getBlocks()[PRISM_FLOOR_HIDDEN].clone());

                            //now add the tile to the parent 3d model
                            renderer.getBlocks()[PRISM_FLOOR_HIDDEN].addChild(getTile(col, row).getObject3D());
                            break;

                        default:

                            if (!cloneStandard) {

                                //mark flag
                                cloneStandard = true;

                                //set model up for batch processing
                                renderer.getBlocks()[PRISM_FLOOR_STANDARD].setRenderChildrenAsBatch(true);

                                //hide model that is not to be displayed
                                renderer.getBlocks()[PRISM_FLOOR_STANDARD].setVisible(false);

                                //add it as a child to the current scene
                                renderer.getCurrentScene().addChild(renderer.getBlocks()[PRISM_FLOOR_STANDARD]);
                            }

                            //clone the object and assign the reference to the tile
                            getTile(col, row).setObject3D(renderer.getBlocks()[PRISM_FLOOR_STANDARD].clone());

                            //now add the tile to the parent 3d model
                            renderer.getBlocks()[PRISM_FLOOR_STANDARD].addChild(getTile(col, row).getObject3D());
                            break;
                    }

                    //assign the position where the 3d model is displayed
                    getTile(col, row).getObject3D().setPosition(col, row, START_Z);

                    //pick a random velocity at which to move
                    getTile(col, row).setVelocityZ(VELOCITY_Z_MIN + (GameActivity.getRandom().nextFloat() * (VELOCITY_Z_MAX - VELOCITY_Z_MIN)));
                }

                //make everything visible (for now)
                getTile(col, row).getObject3D().setVisible(true);

                switch (getTile(col, row).getType()) {

                    case Hidden:
                        getTile(col, row).getObject3D().setVisible(false);
                        break;

                    case SwitchLightHiddenOnly:
                    case SwitchLight:

                        if (renderer != null)
                            getTile(col, row).setMisc3D(renderer.getMisc()[OBJECT3D_SWITCH_1].clone());
                        break;

                    case SwitchHeavy:

                        if (renderer != null)
                            getTile(col, row).setMisc3D(renderer.getMisc()[OBJECT3D_SWITCH_2].clone());
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