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
    private int index;

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

    public MainRenderer(Context context) {

        //call parent
        super(context);

        //assign speed
        setFrameRate(GameActivity.FPS);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void initScene() {

        Log.d(TAG, "initScene");

        //remove all children, if exist
        getCurrentScene().clearChildren();

        Material material = new Material();
        material.enableLighting(true);
        material.setDiffuseMethod(new DiffuseMethod.Lambert());
        material.setColorInfluence(0f);

        try {

            //load and add texture to material
            material.addTexture(new Texture("Block", R.drawable.block));

        } catch (Exception e) {

            e.printStackTrace();

            //color material if we couldn't load a texture
            material.setColor(Color.CYAN);
        }

        //create our rectangle block
        block = new RectangularPrism(2, 1, 1);

        //add texture to the block
        block.setMaterial(material);

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
}