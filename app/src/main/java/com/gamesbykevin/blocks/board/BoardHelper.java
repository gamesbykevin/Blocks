package com.gamesbykevin.blocks.board;

import com.gamesbykevin.blocks.R;
import com.gamesbykevin.blocks.activity.BaseActivityHelper;
import com.gamesbykevin.blocks.block.Block;
import com.gamesbykevin.blocks.block.BlockHelper;
import com.gamesbykevin.blocks.levels.Level;

import java.util.List;

/**
 * Created by Kevin on 12/18/2017.
 */

public class BoardHelper {

    //are we on a weak block?
    private static boolean weak = false;

    //are we on a strong block?
    private static boolean strong = false;

    //are we activating a switch?
    private static boolean switchOff = false;
    private static boolean switchOn = false;

    //are we on a hidden block
    private static boolean hidden = false;

    //is the block falling
    private static boolean fall = false;

    public static void checkTiles(final Board board, final Block block) {

        //are we on a weak block?
        weak = false;

        //are we on a strong block?
        strong = false;

        //are we activating a switch?
        switchOff = false;
        switchOn = false;

        //are we on a hidden block
        hidden = false;

        //is the block falling
        fall = false;

        //get the tile reference
        Tile tile1 = board.getTile(block.getCol(), block.getRow());

        //check the first tile
        checkTile(board, block, tile1, block.getCol(), block.getRow());

        //location of the other tile
        int col2 = -1, row2 = -1;

        //if the block isn't standing let's check the other tile
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
        Tile tile2 = board.getTile(col2, row2);

        //check the second tile
        checkTile(board, block, tile2, col2, row2);

        //play the appropriate sound effect
        if (switchOn) {
            BaseActivityHelper.playSound(R.raw.switch_on);
        } else if (switchOff) {
            BaseActivityHelper.playSound(R.raw.switch_off);
        } else if (fall) {
            BaseActivityHelper.playSoundFall();
        } else if (strong && !weak && !hidden) {
            BaseActivityHelper.playSoundRotateStrong();
        } else if (weak) {
            BaseActivityHelper.playSoundRotateWeak();
        } else if (hidden) {
            BaseActivityHelper.playSoundRotateHidden();
        }
    }

    protected static void checkTile(Board board, Block block, Tile tile, int col, int row) {

        if (tile == null)
            return;

        //handle the tile type
        switch (tile.getType()) {

            case Hidden:
            case HiddenDisplay:

                //flag we are on this type of block
                hidden = true;
                break;

            case Standard:
            case Start:
            case StartStanding:
            case Goal:

                //flag we are on this type of block
                strong = true;
                break;

            case Weak:

                //if standing on a weak block we need to fall through the floor
                if (block.isStanding()) {

                    //setup block to fall
                    BlockHelper.setupBlockFall(block, null);

                    //we are falling
                    fall = true;

                    //hide the tile
                    tile.getObject3D().setVisible(false);

                } else {

                    //flag we are on this type of block
                    weak = true;
                }
                break;

            case SwitchLight:
            case SwitchLightHiddenOnly:
            case SwitchHeavy:

                for (int i = 0; i < board.getSwitchesList().size(); i++) {

                    Level.Connector connect =  board.getSwitchesList().get(i);

                    if (col != connect.sourceCol || row != connect.sourceRow)
                        continue;

                    //heavy blocks require you to be standing
                    if (tile.getType() == Tile.Type.SwitchHeavy && !block.isStanding())
                        continue;

                    for (int x = 0; x < connect.connections.size(); x++) {

                        //get the current tile
                        Tile tmp = board.getTile(connect.connections.get(x).col, connect.connections.get(x).row);

                        //we will manipulate the tmp tile depending on the tile type
                        if (tile.getType() == Tile.Type.SwitchLight || tile.getType() == Tile.Type.SwitchHeavy) {
                            tmp.getObject3D().setVisible(!tmp.getObject3D().isVisible());
                        } else if (tile.getType() == Tile.Type.SwitchLightHiddenOnly) {
                            tmp.getObject3D().setVisible(false);
                        }

                        //only need to check the first tile
                        if (x == 0) {
                            if (tmp.getObject3D().isVisible()) {
                                switchOn = true;
                                switchOff = false;
                            } else {
                                switchOn = false;
                                switchOff = true;
                            }
                        }
                    }
                }
                break;
        }
    }
}