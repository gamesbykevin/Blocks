package com.gamesbykevin.blocks.levels;

import android.content.Context;

import com.gamesbykevin.blocks.common.IDisposable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 12/2/2017.
 */
public class Levels implements IDisposable {

    /**
     * Indicates the camera position when viewing the level
     */
    private static final String LEVEL_CAMERA = "!";

    /**
     * Indicates what locations are connected to others
     */
    private static final String LEVEL_CONNECTOR = "@";

    /**
     * This character means we are done with the current level
     */
    private static final String LEVEL_END = "#";

    /**
     * Name of our text file containing all the levels
     */
    private static final String FILE_NAME = "levels.txt";

    //list of all levels in the game
    private List<Level> levelList;

    //the location of the current level
    private int index = 0;

    public Levels(Context context) throws Exception {

        //create new list for our levels
        this.levelList = new ArrayList<>();

        //load the text file reader so we can build a level
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(FILE_NAME)));

        //this will contain the current line of the text file
        String line;

        //object representing a level
        Level level = null;

        //flag dirty every time we need to create a new level
        boolean dirty = true;

        //read every line in our text file
        while ((line = reader.readLine()) != null) {

            if (dirty) {
                level = new Level(levelList.size() + 1);
                dirty = false;
            }

            if (line.contains(LEVEL_END)) {
                getLevelList().add(level);
                dirty = true;
            } else if (line.startsWith(LEVEL_CONNECTOR)) {
                level.addSwitch(line);
            } else if (line.startsWith(LEVEL_CAMERA)) {
                level.setCamera(line);
            } else if (line.trim().length() > 0) {
                level.getKey().add(line);
            }
        }

        //analyze all the levels so we know the start, finish, size, etc...
        for (int i = 0; i < getLevelList().size(); i++) {
            getLevelList().get(i).analyze();
        }
    }

    @Override
    public void dispose() {

        if (levelList != null) {
            for (int i = 0; i < levelList.size(); i++) {
                if (levelList.get(i) != null) {
                    levelList.get(i).dispose();
                    levelList.set(i, null);
                }
            }

            levelList.clear();
        }

        levelList = null;
    }

    public void setIndex(final int index) {
        this.index = index;

        //keep index in bounds
        if (getIndex() < 0) {
            setIndex(getLevelList().size() - 1);
        } else if (getIndex() >= getLevelList().size()) {
            setIndex(0);
        }
    }

    public int getIndex() {
        return this.index;
    }

    public Level getLevel() {
        return getLevelList().get(getIndex());
    }

    public List<Level> getLevelList() {
        return this.levelList;
    }
}