package com.gamesbykevin.blocks.opengl;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.gamesbykevin.blocks.R;
import com.gamesbykevin.blocks.activity.MainActivity;
import com.gamesbykevin.blocks.block.Block;

import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.RectangularPrism;

import javax.microedition.khronos.opengles.GL10;

import static com.gamesbykevin.blocks.activity.MainActivity.TAG;

/**
 * Created by Kevin on 11/26/2017.
 */
public class Renderer extends org.rajawali3d.renderer.Renderer {

    private final View view;

    public Renderer(Context context, View view) {

        //call parent
        super(context);

        //store view reference
        this.view = view;

        //assign speed
        setFrameRate(MainActivity.FPS);
    }

    @Override
    public void initScene() {

        Log.d(TAG, "initScene");

        //remove all children, if exist
        getCurrentScene().clearChildren();

        //create our game block
        createBlock();

        //create the floor for the level
        createFloor();

        //position our camera accordingly
        setupCamera();
    }

    private void setupCamera() {

        //where we want our camera located at
        Vector3 position = new Vector3(4, -4, 0);

        //getCurrentCamera().setLookAt(target.getPosition());
        getCurrentCamera().setPosition(position.x, position.y, position.z + 7);
        getCurrentCamera().rotate(Vector3.Axis.X, -45);
    }

    /**
     * Create the 3d object representing the game block
     */
    private void createBlock() {

        Material materialBlock = new Material();
        materialBlock.enableLighting(true);
        materialBlock.setDiffuseMethod(new DiffuseMethod.Lambert());
        materialBlock.setColorInfluence(0f);

        try {

            //load and add texture to material
            materialBlock.addTexture(new Texture("Block", R.drawable.block));

        } catch (Exception e) {

            e.printStackTrace();

            //color material if we couldn't load a texture
            materialBlock.setColor(Color.CYAN);
        }

        //create our rectangle block
        RectangularPrism prism = new RectangularPrism(2, 1, 1);

        //add texture
        prism.setMaterial(materialBlock);

        //start at origin (for now)
        prism.setPosition(.5,0,.75);

        //add it to the scene so we can see it
        getCurrentScene().addChild(prism);

        //if the block exists just update the 3d model, else create the block
        if (MainActivity.getGame().getBlock() != null) {
            MainActivity.getGame().getBlock().setPrism(prism);
        } else {
            MainActivity.getGame().createBlock(new Block(prism));
        }
    }

    /**
     * Create the floor for the level
     */
    private void createFloor() {

        Material materialFloor = new Material();
        materialFloor.enableLighting(true);
        materialFloor.setDiffuseMethod(new DiffuseMethod.Lambert());
        materialFloor.setColorInfluence(0f);

        try {

            materialFloor.addTexture(new Texture("Floor", R.drawable.floor));

        } catch (Exception e) {
            e.printStackTrace();

            //color material if we couldn't load a texture
            materialFloor.setColor(Color.RED);
        }


        RectangularPrism floor = new RectangularPrism(1f, 1f, .5f);
        floor.setPosition(0,0,0);
        floor.setMaterial(materialFloor);

        //if the block exists just update the 3d model, else create the block
        if (MainActivity.getGame().getBoard() != null) {
            MainActivity.getGame().getBoard().populate(this, floor);
        } else {
            MainActivity.getGame().createBoard(this, floor, 10, 6);
        }
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        Log.d("gamesbykevin" , "Touch event");
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

        //update the block animation (if exists)
        if (MainActivity.getGame() != null && MainActivity.getGame().getBlock() != null)
            MainActivity.getGame().getBlock().rotate();
    }
}