package com.gamesbykevin.blocks.opengl;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

import com.gamesbykevin.blocks.R;
import com.gamesbykevin.blocks.activity.GameActivity;
import com.gamesbykevin.blocks.common.IDisposable;

import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.RectangularPrism;
import org.rajawali3d.renderer.Renderer;

import javax.microedition.khronos.opengles.GL10;

import static com.gamesbykevin.blocks.activity.BaseActivity.TAG;

/**
 * Created by Kevin on 12/14/2017.
 */
public class MainRenderer extends Renderer implements IDisposable {

    //our block to rotate
    private RectangularPrism block;

    //how is the block rotating
    private int index = 0;

    //keep track of the number of rotations
    private int count = 0;

    //how fast do we rotate
    private int speed = 1;

    //max rotation speed
    private static final int SPEED_MAX = 20;

    //min rotation speed
    private static final int SPEED_MIN = -20;

    //how long do we rotate before changing
    private static final int ROTATION = 180;

    //the material for each texture
    private Material[] materials;

    //different block textures
    public static final int[] BLOCK_TEXTURES = {
        R.drawable.block, R.drawable.block_grass, R.drawable.block_rock,
        R.drawable.block_water, R.drawable.block_rocky, R.drawable.block_snow,
        R.drawable.block_tile, R.drawable.block_wall, R.drawable.block_moss,
        R.drawable.block_violet, R.drawable.block_speckled, R.drawable.block_aqua,
        R.drawable.block_dark, R.drawable.block_green, R.drawable.block_pattern,
        R.drawable.block_pinkish, R.drawable.block_purple, R.drawable.block_sky,
        R.drawable.block_stone, R.drawable.block_turquoise
    };

    //different backgrounds
    public static final int[] RESOURCE_BACKGROUND = {
        R.drawable.background, R.drawable.background1, R.drawable.background2,
        R.drawable.background3, R.drawable.background4, R.drawable.background5,
        R.drawable.background6, R.drawable.background7, R.drawable.background8,
        R.drawable.background9, R.drawable.background10, R.drawable.background11,
        R.drawable.background12, R.drawable.background13, R.drawable.background14,
        R.drawable.background15, R.drawable.background16, R.drawable.background17,
        R.drawable.background18, R.drawable.background19
    };

    //the current background
    public static int CURRENT_BACKGROUND;

    //the current texture
    public static int CURRENT_TEXTURE;

    //are we changing the texture?
    private int next = CURRENT_TEXTURE;

    public MainRenderer(Context context) {

        //call parent
        super(context);

        //assign speed
        setFrameRate(GameActivity.FPS);
    }

    @Override
    public void dispose() {

        if (block != null) {
            block.destroy();
            block = null;
        }

        if (materials != null) {

            for (int i = 0; i < materials.length; i++) {
                if (materials[i] != null) {
                    materials[i].unbindTextures();
                    materials[i] = null;
                }
            }

            materials = null;
        }
    }

    @Override
    public void initScene() {

        Log.d(TAG, "initScene");

        //remove all children, if exist
        getCurrentScene().clearChildren();

        //create new array for our materials
        this.materials = new Material[BLOCK_TEXTURES.length];

        //create our material(s)
        for (int i = 0; i < this.materials.length; i++) {
            this.materials[i] = createMaterial(new Material(), BLOCK_TEXTURES[i]);
        }

        //create our rectangle block
        block = new RectangularPrism(2, 1, 1);

        //add texture to the block at first
        block.setMaterial(this.materials[CURRENT_TEXTURE]);

        //rotate block accordingly
        //block.rotate(Vector3.Axis.X, -45);

        //rotate so it is standing up
        //block.rotate(Vector3.Axis.Z, -90);

        //move slightly to the left
        block.setX(-1.5);

        //shrink the block a little
        block.setScale(.6);

        //add the block to the scene
        getCurrentScene().addChild(block);
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        Log.d(TAG, "Touch event");
    }


    @Override
    public void onOffsetsChanged(float x, float y, float z, float w, int i, int j) {
        //do something here
    }

    @Override
    public void onRenderFrame(GL10 unused) {
        super.onRenderFrame(unused);
    }

    @Override
    public void onRenderSurfaceSizeChanged(GL10 gl, int width, int height) {
        super.onRenderSurfaceSizeChanged(gl, width, height);
    }

    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {

        //call parent to render objects
        super.onRender(elapsedTime, deltaTime);

        //do we need to switch textures
        if (next != CURRENT_TEXTURE) {

            //update the next index
            CURRENT_TEXTURE = this.next;

            //update material of the block
            block.setMaterial(this.materials[CURRENT_TEXTURE]);
        }

        //rotate the block
        switch(index) {
            case 0:
                block.rotate(Vector3.Axis.Z, speed);
                break;

            case 1:
                block.rotate(Vector3.Axis.Y, speed);
                break;

            default:
            case 2:
                block.rotate(Vector3.Axis.X, speed);
                break;
        }

        //track every rotation
        count++;

        //if we have rotated enough
        if (count >= Math.abs(ROTATION / speed)) {

            //reset the count
            count = 0;

            //change index rotation
            this.index++;

            //keep in range
            if (index > 2) {
                index = 0;
                changeSpeed();
            }
        }
    }

    private void changeSpeed() {

        //increase the speed
        speed++;

        //keep in range
        if (speed > SPEED_MAX)
            speed = SPEED_MIN;

        //keep changing until we get an even amount
        while (speed == 0 || speed > SPEED_MAX || ROTATION % speed != 0) {

            //increase the speed
            speed++;

            //keep in range
            if (speed > SPEED_MAX)
                speed = SPEED_MIN;
        }
    }

    private Material createMaterial(Material material, final int resId) {

        //setup material
        material.enableLighting(true);
        material.setDiffuseMethod(new DiffuseMethod.Lambert());
        material.setColorInfluence(0f);

        try {
            //load and add texture to material
            material.addTexture(new Texture("Block" + resId, resId));

        } catch (Exception e) {
            e.printStackTrace();
        }

        //return our populated material
        return material;
    }

    public void setNext(int next) {

        //if out of bounds, go back to 0
        if (next >= BLOCK_TEXTURES.length)
            next = 0;

        this.next = next;
    }
}