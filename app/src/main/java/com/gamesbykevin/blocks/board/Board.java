package com.gamesbykevin.blocks.board;

import org.rajawali3d.Object3D;

/**
 * Created by Kevin on 11/27/2017.
 */
public class Board {

    private Object3D[][] floor;

    public Board(final int cols, final int rows) {
        this.floor = new Object3D[rows][cols];
    }

    public void populate(Object3D object3D) {

        for (int row = 0; row < floor.length; row++) {
            for (int col = 0; col < floor[0].length; col++) {
                floor[row][col] = object3D;
            }
        }
    }
}
