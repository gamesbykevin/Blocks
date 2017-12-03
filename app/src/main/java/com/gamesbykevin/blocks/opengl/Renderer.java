package com.gamesbykevin.blocks.opengl;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.gamesbykevin.blocks.R;
import com.gamesbykevin.blocks.activity.MainActivity;
import com.gamesbykevin.blocks.block.Block;

import org.rajawali3d.Object3D;
import org.rajawali3d.loader.LoaderSTL;
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

    private RectangularPrism block, floor;

    private Object3D switch1, switch2;

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

        //create the floor for the level
        createFloor();

        //create our game block
        createBlock();

        //create the misc objects
        createMisc();

        //add the floor and block, etc... to our game
        MainActivity.getGame().create(this, block, floor, switch1, switch2);

        //position our camera accordingly
        setupCamera();
    }

    private void setupCamera() {

        /*
        //where we want our camera located at
        Vector3 position = new Vector3(4, -4, 0);

        //getCurrentCamera().setLookAt(target.getPosition());
        getCurrentCamera().setPosition(position.x, position.y, position.z + 7);
        */

        Vector3 position = new Vector3(8, -10, 0);
        getCurrentCamera().setPosition(position.x, position.y, position.z + 10);

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
        this.block = new RectangularPrism(2, 1, 1);

        //add texture
        this.block.setMaterial(materialBlock);
    }

    /**
     * Create the miscellaneous objects in the game (light switch, heavy switch, etc...)
     */
    private void createMisc() {

        Material material = new Material();
        material.setColor(Color.BLUE);

        try {

            LoaderSTL loaderSTL = new LoaderSTL(getContext().getResources(), getTextureManager(), R.raw.switch_1_stl);
            loaderSTL.parse();
            this.switch1 = loaderSTL.getParsedObject();
            this.switch1.setPosition(0,0,.75);
            this.switch1.setMaterial(material);
            this.switch1.setScale(.02);

            loaderSTL = new LoaderSTL(getContext().getResources(), getTextureManager(), R.raw.switch_2_stl);
            loaderSTL.parse();
            this.switch2 = loaderSTL.getParsedObject();
            this.switch2.setPosition(1,0,.75);
            this.switch2.setMaterial(material);
            this.switch2.setScale(.04);

        } catch (Exception e) {
            e.printStackTrace();
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
            materialFloor.setColor(Color.GRAY);
        }

        this.floor = new RectangularPrism(1f, 1f, .5f);
        this.floor.setPosition(0,0,0);
        this.floor.setMaterial(materialFloor);
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