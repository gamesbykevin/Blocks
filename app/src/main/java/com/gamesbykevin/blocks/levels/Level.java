package com.gamesbykevin.blocks.levels;

import com.gamesbykevin.blocks.board.Tile;
import com.gamesbykevin.blocks.common.IDisposable;

import org.rajawali3d.math.vector.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 12/2/2017.
 */
public class Level implements IDisposable {

    /**
     * This is the end of the source coordinate
     */
    private static final String CONNECTOR_SOURCE_END = "=";

    /**
     * This is what separates the col / row
     */
    private static final String COORDINATE_SEPARATOR = ",";

    /**
     * This separates each coordinates from connecting to the source
     */
    private static final String CONNECTOR_SEPARATOR = ":";

    //key holds the list of tiles in this level
    private List<String> key;

    //list of locations that are linked to other locations
    private List<Connector> switches;

    //the size of the level
    private int cols = 0, rows = 0;

    //where is the goal located
    private int goalCol = 0, goalRow = 0;

    //where do we start the level
    private int startCol = 0, startRow = 0;

    //do we stand at the start location?
    private boolean standing = false;

    //the location of the camera for this level
    private Vector3 camera = null;

    //keep track of the level number
    private final int number;

    public Level(final int number) {

        //what level # is this
        this.number = number;

        this.key = new ArrayList<>();
        this.switches = new ArrayList<>();
    }

    @Override
    public void dispose() {

        if (key != null)
            key.clear();
        if (switches != null)
            switches.clear();

        camera = null;
        key = null;
        switches = null;
    }

    public int getNumber() {
        return this.number;
    }

    public void setCamera(final String desc) {

        //parse our camera coordinates
        final double x = Double.parseDouble(desc.substring(1).split(COORDINATE_SEPARATOR)[0]);
        final double y = Double.parseDouble(desc.substring(1).split(COORDINATE_SEPARATOR)[1]);
        final double z = Double.parseDouble(desc.substring(1).split(COORDINATE_SEPARATOR)[2]);

        //assign the camera coordinates
        this.camera = new Vector3();
        this.camera.setAll(x, y, z);
    }

    public Vector3 getCamera() {
        return this.camera;
    }

    public void setStanding(final boolean standing) {
        this.standing = standing;
    }

    public boolean isStanding() {
        return this.standing;
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

    public List<Connector> getSwitches() {
        return this.switches;
    }

    public void addSwitch(final String desc) {
        addConnector(desc, getSwitches());
    }

    private void addConnector(final String desc, final List<Connector> connectorList) {

        //find where the source coordinates are
        int index = desc.indexOf(CONNECTOR_SOURCE_END);

        //obtain our source coordinates
        final int sourceCol = Integer.parseInt(desc.substring(1, index).split(COORDINATE_SEPARATOR)[0]);
        final int sourceRow = Integer.parseInt(desc.substring(1, index).split(COORDINATE_SEPARATOR)[1]);

        //create a new connector
        Connector connector = new Connector(sourceCol, sourceRow);

        //split each (col,row) into an array
        String[] misc = desc.substring(index + 1).split(CONNECTOR_SEPARATOR);

        //add each connection that is part of the connector
        for (int i = 0; i < misc.length; i++) {

            //now we can split each coordinate
            final int col = Integer.parseInt(misc[i].split(COORDINATE_SEPARATOR)[0]);
            final int row = Integer.parseInt(misc[i].split(COORDINATE_SEPARATOR)[1]);

            //add location to array list
            connector.connections.add(new Cell(col, row));
        }

        //add to our list
        connectorList.add(connector);
    }

    public List<String> getKey() {
        return this.key;
    }

    /**
     * Now that the level is complete, let's figure out the size and the location of the goal
     */
    public void analyze() {

        if (getCamera() == null)
            throw new RuntimeException("The camera coordinates are not set");

        //did we find these?
        boolean goal = false, start = false, startStanding = false;

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
                    setStanding(false);

                } else if (getKey().get(i).substring(x, x + 1).equalsIgnoreCase(Tile.Type.StartStanding.key)) {

                    this.startCol = x;
                    this.startRow = i;
                    startStanding = true;
                    setStanding(true);
                }
            }
        }

        if (!goal)
            throw new RuntimeException("Level does not contain a goal");
        if (!start && !startStanding)
            throw new RuntimeException("Level does not have a starting location");
        if (start && startStanding)
            throw new RuntimeException("You have more than 1 starting location");
    }

    /**
     * A connector is a source location is linked to other locations (example hit switch, change other blocks)
     */
    public class Connector {

        //source of the connector
        public final int sourceCol, sourceRow;

        //list of locations connected to the connector source
        public List<Cell> connections;

        private Connector(final int col, final int row) {
            this.sourceCol = col;
            this.sourceRow = row;
            this.connections = new ArrayList<>();
        }
    }

    public class Cell {

        public int col, row;

        private Cell(Cell cell) {
            this(cell.col, cell.row);
        }

        private Cell(int col, int row) {
            this.col = col;
            this.row = row;
        }
    }

    public List<Connector> copySwitches() {

        //create our copied list
        List<Connector> result = new ArrayList<>();

        for (int i = 0; i < getSwitches().size(); i++) {

            //create new connector
            Connector tmp = new Connector(getSwitches().get(i).sourceCol, getSwitches().get(i).sourceRow);

            //add all the connections to the connector
            for (int x = 0; x < getSwitches().get(i).connections.size(); x++) {
                tmp.connections.add(new Cell(getSwitches().get(i).connections.get(x)));
            }

            //now add the connector to our list
            result.add(tmp);
        }

        //return our result
        return result;
    }
}