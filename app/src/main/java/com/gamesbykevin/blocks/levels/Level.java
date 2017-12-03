package com.gamesbykevin.blocks.levels;

import com.gamesbykevin.blocks.board.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 12/2/2017.
 */

public class Level {

    //key holds the list of tiles in this level
    private List<String> key;

    //list of locations that are linked to other locations
    private List<Connector> connectors;

    //the size of the level
    private int cols = 0, rows = 0;

    //where is the goal located
    private int goalCol = 0, goalRow = 0;

    //where do we start the level
    private int startCol = 0, startRow = 0;

    public Level() {
        this.key = new ArrayList<>();
        this.connectors = new ArrayList<>();
    }

    public int getCols() {
        return this.cols;
    }

    public int getRows() {
        return this.rows;
    }

    public int getGoalCol() {
        return this.goalCol;
    }

    public int getGoalRow() {
        return this.goalRow;
    }

    public int getStartCol() {
        return this.startCol;
    }

    public int getStartRow() {
        return this.startRow;
    }

    public List<Connector> getConnectors() {
        return this.connectors;
    }

    public List<String> getKey() {
        return this.key;
    }

    /**
     * Now that the level is complete, let's figure out the size and the location of the goal
     */
    public void analyze() {

        //did we find these?
        boolean goal = false, start = false;

        this.rows = getKey().size();
        this.cols = 0;

        //figure out how may columns there are in this level, and locate the goal
        for (int i = 0; i < getKey().size(); i++) {

            if (getKey().get(i).length() > this.cols)
                this.cols = getKey().get(i).length();

            //check the entire line for start/finish
            for (int x = 0; x < getKey().get(i).length(); x++) {

                if (getKey().get(i).substring(x, x + 1).equalsIgnoreCase(Tile.Type.Goal.key)) {

                    this.goalCol = x;
                    this.goalRow = i;
                    goal = true;

                } else if (getKey().get(i).substring(x, x + 1).equalsIgnoreCase(Tile.Type.Start.key)) {

                    this.startCol = x;
                    this.startRow = i;
                    start = true;

                }
            }
        }

        if (!goal)
            throw new RuntimeException("Level does not contain a goal");
        if (!start)
            throw new RuntimeException("Level does not have a start");
    }

    /**
     * A connector is a source location is linked to other locations (example hit switch, change other blocks)
     */
    private class Connector {

        //source of the connector
        private final int sourceCol, sourceRow;

        //list of locations connected to the connector source
        private final int[] connections;

        private Connector(final int col, final int row, final int[] connections) {
            this.sourceCol = col;
            this.sourceRow = row;
            this.connections = connections;
        }
    }
}