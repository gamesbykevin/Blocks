package com.gamesbykevin.blocks.board;

import com.gamesbykevin.blocks.common.IDisposable;

import org.rajawali3d.Object3D;

import static com.gamesbykevin.blocks.board.Board.HEIGHT_Z_SWITCH;

/**
 * Created by Kevin on 12/2/2017.
 */
public class Tile implements IDisposable {

    //the 3d object representing the tile
    private Object3D object3D;

    //the misc object attached to the tile (example switch)
    private Object3D misc3D;

    //how fast can this tile move to the start position
    private float velocityZ = 1;

    /**
     * How fast can the tile move
     */
    public static float VELOCITY_Z_MAX = 1f;

    /**
     * How slow can the tile move
     */
    public static float VELOCITY_Z_MIN = .25f;

    /**
     * The starting location of the tile
     */
    public static double START_Z = -30;

    /**
     * The end location of the tile
     */
    public static double END_Z = 0;

    public enum Type {

        //where the game will start laying down
        Start("O"),

        //where the game will start standing
        StartStanding("T"),

        //standard floor tile
        Standard("S"),

        //tile is weak and you can only lay down on it, you can't stand on it
        Weak("W"),

        //this switch needs a lot of pressure and only works when standing
        SwitchHeavy("B"),

        //this switch only hides blocks when we hit it
        SwitchLightHiddenOnly("A"),

        //this switch you just need to lay on it
        SwitchLight("L"),

        //this block is hidden at first and can't be seen until the switch is hit
        Hidden("H"),

        //this block is displayed first, but will be hidden if a switch is hit
        HiddenDisplay("D"),

        //this is the goal to complete a level
        Goal("G");

        public final String key;

        /**
         * Each type will have a key matching the text file
         * @param key text representation of what this tile is for
         */
        Type(String key) {
            this.key = key;
        }
    }

    //what type of tile is this?
    private final Type type;

    public Tile(final Type type) {
        this.type = type;
    }

    @Override
    public void dispose() {

        if (object3D != null) {
            object3D.destroy();
            object3D = null;
        }

        if (misc3D != null) {
            misc3D.destroy();
            misc3D = null;
        }
    }

    public void setObject3D(final Object3D object3D) {
        this.object3D = object3D;
    }

    public Object3D getObject3D() {
        return this.object3D;
    }

    public void setMisc3D(final Object3D misc3D) {
        this.misc3D = misc3D;
    }

    public Object3D getMisc3D() {
        return this.misc3D;
    }

    public void setVelocityZ(float velocityZ) {
        this.velocityZ = velocityZ;
    }

    public float getVelocityZ() {
        return this.velocityZ;
    }

    public Type getType() {
        return this.type;
    }

    public boolean hasEndZ() {
        return (getObject3D() != null && getObject3D().getZ() == END_Z);
    }

    public void update() {

        if (!hasEndZ()) {

            //update the tile object (if exists)
            if (getObject3D() != null) {

                //update our location
                getObject3D().setZ(getObject3D().getZ() + getVelocityZ());

                //if we reached the end
                if (getObject3D().getZ() > END_Z || hasEndZ())
                    getObject3D().setZ(END_Z);
            }

            //update the misc object (if exists)
            if (getMisc3D() != null) {

                //update our location
                getMisc3D().setZ(getMisc3D().getZ() + getVelocityZ());

                //if we reached the end
                if (getMisc3D().getZ() > END_Z + HEIGHT_Z_SWITCH || hasEndZ())
                    getMisc3D().setZ(END_Z + HEIGHT_Z_SWITCH);
            }
        }
    }
}