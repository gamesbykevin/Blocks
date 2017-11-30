package com.gamesbykevin.blocks.block;

import com.gamesbykevin.blocks.activity.MainActivity;
import com.gamesbykevin.blocks.common.ICommon;

import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.RectangularPrism;

import java.util.Random;

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

    public enum Direction {
        North, East, South, West
    }

    //desired direction by user
    private boolean west = false, north = false, south = false, east = false;

    //current direction we are heading
    private Direction current = Direction.North;

    /**
     * How fast can the block move?
     */
    private static final float SPEED = 6f;

    /**
     * How many degrees is a single rotation
     */
    private static final int ROTATION = 90;

    //how many degrees have we rotated?
    private int rotationCount = ROTATION;

    public Block(final RectangularPrism prism) {
        setPrism(prism);
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

    public boolean isFalling() {
        return this.falling;
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
        return (rotationCount >= ROTATION);
    }

    public void rotate() {

        if (prism == null)
            return;

        if (isFalling()) {

            //make piece fall
            prism.setPosition(prism.getX(), prism.getY(), prism.getZ() - .25);
            return;

        } else if (hasCompleteRotation() && isStanding()) {

            //if we are at the goal, we just fall through the floor
            if (MainActivity.getGame().getBoard().hasGoal(this)) {

                if (prism.getZ() > -3)
                    prism.setPosition(prism.getX(), prism.getY(), prism.getZ() - .15);
                return;
            }
        }

        //if done rotating, continue
        if (hasCompleteRotation())
            return;

        //how fast do we move?
        float xVel = 0;
        float yVel = 0;
        float zVel = 0;
        float rotate = 0;

        //which direction do we rotate?
        Vector3.Axis axis = null;

        switch (current) {

            case North:
                rotate = SPEED;
                axis = Vector3.Axis.X;

                if (vertical) {

                    yVel = (rotate / 60f);

                    if (!isStanding()) {
                        zVel = (.5f / ROTATION) * SPEED;

                        if (prism.getZ() + zVel > 1.25)
                            zVel = 1.25f - (float) prism.getZ();
                    } else {
                        zVel = -(.5f / ROTATION) * SPEED;

                        if (prism.getZ() + zVel < .75)
                            zVel = .75f - (float) prism.getZ();
                    }
                } else {
                    yVel = (rotate / 90f);
                }
                break;

            case South:
                rotate = -SPEED;
                axis = Vector3.Axis.X;

                if (vertical) {

                    yVel = (rotate / 60f);

                    if (!isStanding()) {
                        zVel = (.5f / ROTATION) * SPEED;

                        if (prism.getZ() + zVel > 1.25)
                            zVel = 1.25f - (float) prism.getZ();
                    } else {
                        zVel = -(.5f / ROTATION) * SPEED;

                        if (prism.getZ() + zVel < .75)
                            zVel = .75f - (float) prism.getZ();
                    }
                } else {
                    yVel = (rotate / 90f);
                }
                break;

            case East:
                rotate = -SPEED;
                axis = Vector3.Axis.Y;

                if (vertical) {

                    xVel = -(rotate / 60f);

                    if (!isStanding()) {
                        zVel = (.5f / ROTATION) * SPEED;

                        if (prism.getZ() + zVel > 1.25)
                            zVel = 1.25f - (float) prism.getZ();
                    } else {
                        zVel = -(.5f / ROTATION) * SPEED;

                        if (prism.getZ() + zVel < .75)
                            zVel = .75f - (float) prism.getZ();
                    }
                } else {
                    xVel = -(rotate / 90f);
                }
                break;

            case West:

                rotate = SPEED;
                axis = Vector3.Axis.Y;

                if (vertical) {

                    xVel = -(rotate / 60f);

                    if (!isStanding()) {
                        zVel = (.5f / ROTATION) * SPEED;

                        if (prism.getZ() + zVel > 1.25)
                            zVel = 1.25f - (float) prism.getZ();
                    } else {
                        zVel = -(.5f / ROTATION) * SPEED;

                        if (prism.getZ() + zVel < .75)
                            zVel = .75f - (float) prism.getZ();
                    }
                } else {
                    xVel = -(rotate / 90f);
                }
                break;
        }

        //rotate accordingly
        prism.rotate(axis, rotate);

        //update the location
        prism.setPosition(prism.getX() + xVel, prism.getY() + yVel, prism.getZ() + zVel);

        //keep track of were we are at with our rotation
        rotationCount += Math.abs(rotate);

        //if we completed a rotation, check if standing
        if (hasCompleteRotation()) {

            //if we are rotating vertical switch between standing and non-standing
            if (vertical)
                standing = !standing;
        }
    }

    @Override
    public void update() {

        if (prism == null)
            return;

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
        switch (current) {

            case East:
            case West:

                switch(next) {
                    case North:
                    case South:

                        vertical = !vertical;

                        if (isStanding())
                            vertical = true;
                        break;
                }
                break;

            case North:
            case South:
            default:

                switch(next) {
                    case East:
                    case West:

                        vertical = !vertical;

                        if (isStanding())
                            vertical = true;
                        break;
                }
                break;
        }

        //reset our rotation count back to 0
        rotationCount = 0;

        //assign current direction
        current = next;
    }
}