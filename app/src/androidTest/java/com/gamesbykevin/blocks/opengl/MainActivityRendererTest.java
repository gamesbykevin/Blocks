package com.gamesbykevin.blocks.opengl;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.widget.ArrayAdapter;

import com.gamesbykevin.blocks.R;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Kevin on 12/18/2017.
 */
public class MainActivityRendererTest {

    @Test
    public void blockTextures() {

        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        //assign custom spinner ui layout
        ArrayAdapter adapter = ArrayAdapter.createFromResource(appContext, R.array.spinner_text_texture, R.layout.spinner_item);

        //make sure the string array matches the length of block textures
        assertEquals(adapter.getCount(), MainActivityRenderer.BLOCK_TEXTURES.length);
    }

    @Test
    public void backgroundTextures() {

        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        //assign custom spinner ui layout
        ArrayAdapter adapter = ArrayAdapter.createFromResource(appContext, R.array.spinner_text_background, R.layout.spinner_item);

        //make sure the string array matches the length of background textures
        assertEquals(adapter.getCount(), MainActivityRenderer.RESOURCE_BACKGROUND.length);
    }
}