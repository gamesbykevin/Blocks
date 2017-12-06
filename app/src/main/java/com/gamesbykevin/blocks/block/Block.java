package com.gamesbykevin.blocks.block;

import com.gamesbykevin.blocks.common.ICommon;

import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.RectangularPrism;

import static com.gamesbykevin.blocks.block.BlockHelper.HEIGHT_Z_LIMIT_FALL;
import static com.gamesbykevin.blocks.block.BlockHelper.HEIGHT_Z_VERTICAL;
import static com.gamesbykevin.blocks.block.BlockHelper.ROTATION;
import static com.gamesbykevin.blocks.block.BlockHelper.updateLocation;

/**
 * Created by Kevin on 11/29/2017.
 */
public class Block implements ICommon {

    //3d object representing the block
    private RectangularPrism prism;

    //are we rotating vertical?
    private boolean vertical = false;

    //is the block standing?
    private boolean standing = false;

    //if we are falling, that means we lost
    private boolean falling = false;

    //have we reached the goal
    private boolean goal = false;

    //are we setting up this block?
    private boolean setup = false;

    public enum Direction {
        North, East, South, West
    }

    //desired direction by user
    private boolean west = false, north = false, south = false, east = false;

    //current direction we are heading
    private Direction current;

    //how fast are we currently falling
    private float gravity = 0f;

    //how many degrees have we rotated?
    private int rotationCount;

    //location of both blocks that make up the big block
    private int col, row;

    /**
     * The location where we start at beginning of setup
     */
    public static final float START_Z = 20;

    /**
     * Rate at which the block falls during setup
     */
    public static final float FALL_Z = .25f;

    /**
     * The minimum allowed scale
     */
    public static final double SCALE_MIN = .01;

    /**
     * How quickly do we change the scale
     */
    public static final double SCALE_ADJUST = .95;

    /**
     * The lowest allowed z coordinate when we reached the goal
     */
    public static final double MIN_Z_GOAL = .001;

    public Block(final RectangularPrism prism) {
        setPrism(prism);
        reset();
    }

    public void setSetup(boolean setup) {
        this.setup = setup;
    }

    public boolean hasSetup() {
        return this.setup;
    }

    public void setGoal(boolean goal) {
        this.goal = goal;
    }

    public boolean hasGoal() {
        return this.goal;
    }

    /**
     * Is the goal complete?
     * @return true if we have the goal and moved the block onto the ground, false otherwise
     */
    public boolean hasGoalComplete() {
        return (hasGoal() && !getPrism().isVisible());
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getCol() {
        return this.col;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getRow() {
        return this.row;
    }

    @Override
    public void reset() {
        BlockHelper.reset(this);
    }

    /**
     * Stand up the piece at the current location (if not already standing
     */
    public void stand() {

        //don't continue if we are standing
        if (isStanding())
            return;

        getPrism().setX(getPrism().getX() - .5);
        getPrism().setZ(HEIGHT_Z_VERTICAL);

        //flag that we are standing and moving vertically
        setStanding(true);
        setVertical(true);

        //rotate piece 90 degrees
        getPrism().rotate(Vector3.Axis.Y, 90);
    }

    public void setCurrent(Direction current) {
        this.current = current;
    }

    public Direction getCurrent() {
        return this.current;
    }

    public int getRotationCount() {
        return this.rotationCount;
    }

    public void setRotationCount(int rotationCount) {
        this.rotationCount = rotationCount;
    }

    public float getGravity() {
        return this.gravity;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public void setPrism(final RectangularPrism prism) {
        this.prism = prism;
    }

    public RectangularPrism getPrism() {
        return this.prism;
    }

    public boolean isStanding() {
        return this.standing;
    }

    public void setStanding(boolean standing) {
        this.standing = standing;
    }

    public boolean isFalling() {
        return this.falling;
    }

    public void setFalling(boolean falling) {
        this.falling = falling;
    }

    public boolean isVertical() {
        return this.vertical;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    public void setWest(boolean west) {
        this.west = west;
    }

    public void setEast(boolean east) {
        this.east = east;
    }

    public void setNorth(boolean north) {
        this.north = north;
    }

    public void setSouth(boolean south) {
        this.south = south;
    }

    public boolean hasCompleteRotation() {
        return (getRotationCount() >= ROTATION);
    }

    public void rotate() {
        BlockHelper.rotate(this);
    }

    public boolean isDead() {
        return (isFalling() && getPrism().getZ() <= HEIGHT_Z_LIMIT_FALL);
    }

    @Override
    public void update() {

        if (getPrism() == null)
            return;

        //if the block is falling or dead ignore everything else
        if (isFalling() || isDead())
            return;

        //don't continue if we reached the goal
        if (hasGoal())
            return;

        //if we have setup don't continue;
        if (hasSetup()) {

            //figure out where we are landing
            float limit = isStanding() ? HEIGHT_Z_VERTICAL : BlockHelper.HEIGHT_Z;

            if (getPrism().getZ() > limit) {

                //if we haven't reached the limit
                getPrism().setZ(getPrism().getZ() - FALL_Z);

            } else {

                //now we are done with setup
                getPrism().setZ(limit);
                setSetup(false);

            }

            //we can't move right now
            setWest(false);
            setEast(false);
            setNorth(false);
            setSouth(false);
            return;
        }

        //if not done rotating don't continue
        if (!hasCompleteRotation())
            return;

        Direction next = null;

        //setup our next rotation accordingly
        if (west) {
            next = Direction.West;
        } else if (east) {
            next = Direction.East;
        } else if (south) {
            next = Direction.South;
        } else if (north) {
            next = Direction.North;
        }

        //don't continue until we know our next direction
        if (next == null)
            return;

        //check if we are making a shift in direction
        switch (getCurrent()) {

            case East:
            case West:

                switch (next) {
                    case North:
                    case South:

                        setVertical(!isVertical());

                        if (isStanding())
                            setVertical(true);
                        break;
                }
                break;

            case North:
            case South:
            default:

                switch (next) {
                    case East:
                    case West:

                        setVertical(!isVertical());

                        if (isStanding())
                            setVertical(true);
                        break;
                }
                break;
        }

        //reset our rotation count back to 0
        setRotationCount(0);

        //assign current direction
        setCurrent(next);

        //now lets update the blocks location since we know which direction we are heading
        updateLocation(this);
    }
}