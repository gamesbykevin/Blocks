package com.gamesbykevin.blocks.block;

import android.util.Log;

import com.gamesbykevin.blocks.activity.MainActivity;

import org.rajawali3d.math.vector.Vector3;

import static com.gamesbykevin.blocks.activity.MainActivity.TAG;
import static com.gamesbykevin.blocks.activity.MainActivity.getGame;
import static com.gamesbykevin.blocks.opengl.Renderer.FLOOR_DEPTH;

/**
 * Created by Kevin on 11/30/2017.
 */
public class BlockHelper {

    /**
     * How high does the block reside when laying down
     */
    private static final float HEIGHT_Z = FLOOR_DEPTH;

    /**
     * Hide the block if it falls below this
     */
    private static final float HEIGHT_Z_HIDE = -2f;

    /**
     * This is the allowed maximum limit for the block to fall
     */
    public static final float HEIGHT_Z_LIMIT_FALL = -20f;

    /**
     * Place the block slightly above the floor to prevent texture bleeding
     */
    private static final float HEIGHT_Z_ADJUST = .08f;

    /**
     * How high does the block reside when rotating vertically
     */
    public static final float HEIGHT_Z_VERTICAL = FLOOR_DEPTH + .5f;

    /**
     * How fast do we adjust the z depth when rotating vertically?
     */
    private static final float VELOCITY_Z = 2f;

    /**
     * We need to adjust the z velocity when the block is standing up and falling down
     */
    private static final float VELOCITY_Z_ADJUST = 16f;

    /**
     * How fast can the block fall when they fall over
     */
    private static final float VELOCITY_Z_FALL = .05f;

    /**
     * How fast do we move when rotating?
     */
    private static final float VELOCITY_X = 90f;

    /**
     * How fast do we move when rotating vertically?
     */
    private static final float VELOCITY_X_VERTICAL = 60f;

    //here is where the block will start
    private static final float START_X = .5f;
    private static final float START_Y = 0;
    private static final float START_Z = HEIGHT_Z;

    /**
     * How fast can the block move?
     */
    private static final float SPEED = 6f;

    /**
     * How many degrees is a single rotation
     */
    protected static final int ROTATION = 90;

    protected static void reset(Block block) {
        block.getPrism().setPosition(START_X, START_Y, START_Z);
        block.getPrism().setRotation(Vector3.Axis.X, 0);
        block.getPrism().setRotation(Vector3.Axis.Y, 0);
        block.setGoal(false);
        block.setFalling(false);
        block.setNorth(false);
        block.setSouth(false);
        block.setWest(false);
        block.setEast(false);
        block.setVertical(false);
        block.setStanding(false);
        block.setGravity(0f);
        block.setCurrent(Block.Direction.North);
        block.setCol(0);
        block.setRow(0);
        block.setRotationCount(ROTATION);
    }

    protected static void rotate(Block block) {

        if (block.getPrism() == null || block.isDead())
            return;

        //if we completed the rotation and we are standing, and we haven't fallen off the platform
        if (block.hasGoal()) {

            //make the block fall
            if (block.getPrism().getZ() > HEIGHT_Z_HIDE)
                block.getPrism().setZ(block.getPrism().getZ() - VELOCITY_Z_FALL);

            //if it fell enough, let's hide it
            if (block.getPrism().getZ() <= HEIGHT_Z_HIDE)
                block.getPrism().setVisible(false);

            //no need to continue
            return;
        }

        //if done rotating, don't continue here
        if (block.hasCompleteRotation())
            return;

        //how fast do we move?
        float xVel = 0;
        float yVel = 0;
        float zVel = 0;
        float rotate = 0;

        //only rotate if we have a direction
        if (block.getCurrent() != null) {

            //which direction do we rotate?
            Vector3.Axis axis = null;

            //determine the rotation angle, and the rotation direction
            switch (block.getCurrent()) {

                case North:
                    rotate = SPEED;
                    axis = Vector3.Axis.X;
                    break;

                case South:
                    rotate = -SPEED;
                    axis = Vector3.Axis.X;
                    break;

                case West:
                    rotate = SPEED;
                    axis = Vector3.Axis.Y;
                    break;

                case East:
                    rotate = -SPEED;
                    axis = Vector3.Axis.Y;
                    break;
            }

            //we need to adjust the z height etc... depending on  the rotation direction
            switch (block.getCurrent()) {

                case North:
                case South:
                    if (block.isVertical()) {

                        yVel = (rotate / VELOCITY_X_VERTICAL);

                        if (!block.isStanding()) {
                            zVel = (VELOCITY_Z / ROTATION) * SPEED;

                            if (block.getPrism().getZ() + zVel > HEIGHT_Z_VERTICAL)
                                zVel = HEIGHT_Z_VERTICAL - (float) block.getPrism().getZ();
                        } else {
                            zVel = -((VELOCITY_Z / VELOCITY_Z_ADJUST) / ROTATION) * SPEED;

                            if (block.getPrism().getZ() + zVel < HEIGHT_Z)
                                zVel = HEIGHT_Z - (float) block.getPrism().getZ();
                        }

                    } else {
                        yVel = (rotate / VELOCITY_X);
                    }
                    break;

                case West:
                case East:
                    if (block.isVertical()) {

                        xVel = -(rotate / VELOCITY_X_VERTICAL);

                        if (!block.isStanding()) {
                            zVel = (VELOCITY_Z / ROTATION) * SPEED;

                            if (block.getPrism().getZ() + zVel > HEIGHT_Z_VERTICAL)
                                zVel = HEIGHT_Z_VERTICAL - (float) block.getPrism().getZ();
                        } else {

                            zVel = -((VELOCITY_Z / VELOCITY_Z_ADJUST) / ROTATION) * SPEED;

                            if (block.getPrism().getZ() + zVel < HEIGHT_Z)
                                zVel = HEIGHT_Z - (float) block.getPrism().getZ();
                        }

                    } else {
                        xVel = -(rotate / VELOCITY_X);
                    }
                    break;
            }

            //rotate accordingly
            block.getPrism().rotate(axis, rotate);
        }

        if (block.isFalling()) {

            //remove the z-velocity
            zVel = 0;

            //increase the rate at which we fall
            block.setGravity(block.getGravity() + VELOCITY_Z_FALL);

            //the block needs to fall now
            block.getPrism().setZ(block.getPrism().getZ() - block.getGravity());

        } else {

            //if not rotating vertical keep right above floor to prevent texture bleeding
            if (!block.isVertical())
                block.getPrism().setZ(HEIGHT_Z + HEIGHT_Z_ADJUST);
        }

        //update the location
        block.getPrism().setPosition(
                block.getPrism().getX() + xVel,
                block.getPrism().getY() + yVel,
                block.getPrism().getZ() + zVel
        );

        //keep track of were we are at with our rotation
        block.setRotationCount(block.getRotationCount() + (int)Math.abs(rotate));

        //if we completed a rotation, check if standing
        if (block.hasCompleteRotation()) {

            //if we are rotating vertical switch between standing and non-standing
            if (block.isVertical())
                block.setStanding(!block.isStanding());

            //correct the z height as long as we haven't fallen
            if (!block.isFalling()) {

                //correct the height
                block.getPrism().setZ(block.isStanding() ? HEIGHT_Z_VERTICAL : HEIGHT_Z);

                //update our goal status
                block.setGoal(getGame().getBoard().hasGoal(block));

                if (getGame().getBoard().hasFloor(block)) {

                    //check the floor to see if anything needs to be changed
                    getGame().getBoard().checkMisc(block);

                } else {

                    //if we aren't falling, and we are not on the goal then we need to start falling
                    if (!block.hasGoal()) {
                        setupBlockFall(block, getGame().getBoard().getDirectionFall(block));
                    }
                }

            } else {

                //if falling reset rotation count
                block.setRotationCount(0);
            }
        }
    }

    public static void setupBlockFall(Block block, Block.Direction direction) {

        //flag falling true
        block.setFalling(true);

        //determine where to rotate while we fall
        block.setCurrent(direction);

        //if falling reset rotation count automatically
        block.setRotationCount(0);
    }

    protected static void updateLocation(Block block) {

        if (block.isVertical()) {

            if (block.isStanding()) {

                switch (block.getCurrent()) {

                    case East:
                        block.setCol(block.getCol() + 1);
                        break;

                    case West:
                        block.setCol(block.getCol() - 2);
                        break;

                    case North:
                        block.setRow(block.getRow() + 1);
                        break;

                    case South:
                        block.setRow(block.getRow() - 2);
                        break;
                }

            } else {

                switch (block.getCurrent()) {

                    case East:
                        block.setCol(block.getCol() + 2);
                        break;

                    case West:
                        block.setCol(block.getCol() - 1);
                        break;

                    case North:
                        block.setRow(block.getRow() + 2);
                        break;

                    case South:
                        block.setRow(block.getRow() - 1);
                        break;
                }
            }

        } else {

            switch (block.getCurrent()) {

                case East:
                    block.setCol(block.getCol() + 1);
                    break;

                case West:
                    block.setCol(block.getCol() - 1);
                    break;

                case North:
                    block.setRow(block.getRow() + 1);
                    break;

                case South:
                    block.setRow(block.getRow() - 1);
                    break;
            }
        }
    }
}