package com.gamesbykevin.blocks.activity;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 * Created by Kevin on 12/19/2017.
 */

public class TutorialActivityTest {

    @Test
    public void onCreate() {

        //make sure we have resources for the number of defined pages
        assertTrue(TutorialActivity.PAGES == TutorialActivity.Pages.values().length);
    }
}
