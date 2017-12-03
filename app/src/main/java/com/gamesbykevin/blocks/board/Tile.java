package com.gamesbykevin.blocks.board;

import org.rajawali3d.Object3D;

/**
 * Created by Kevin on 12/2/2017.
 */
public class Tile {

    //the 3d object representing the tile
    private Object3D object3D;

    public enum Type {

        //where the game will start
        Start("O"),

        //standard floor tile
        Standard("S"),

        //tile is weak and you can only lay down on it, you can't stand on it
        Weak("W"),

        //this switch needs a lot of pressure and only works when standing
        SwitchHeavy("B"),

        //this switch you just need to lay on it
        SwitchLight("L"),

        //this switch is hidden and can't be seen
        Hidden("H"),

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

    public void setObject3D(final Object3D object3D) {
        this.object3D = object3D;
    }

    public Object3D getObject3D() {
        return this.object3D;
    }

    public Type getType() {
        return this.type;
    }
}