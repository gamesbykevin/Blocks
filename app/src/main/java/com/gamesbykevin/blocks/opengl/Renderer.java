package com.gamesbykevin.blocks.opengl;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.gamesbykevin.blocks.R;
import com.gamesbykevin.blocks.activity.MainActivity;
import com.gamesbykevin.blocks.common.IDisposable;

import org.rajawali3d.Object3D;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.cameras.Camera2D;
import org.rajawali3d.cameras.OrthographicCamera;
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
public class Renderer extends org.rajawali3d.renderer.Renderer implements IDisposable {

    private final View view;

    /**
     * How we know to reference the blocks array
     */
    public static final int PRISM_BLOCK = 0;
    public static final int PRISM_FLOOR_STANDARD = 1;
    public static final int PRISM_FLOOR_WEAK = 2;
    public static final int PRISM_FLOOR_HIDDEN = 3;
    public static final int PRISM_FLOOR_GOAL = 4;

    private RectangularPrism[] blocks;// block, floor, floorWeak;

    /**
     * How we know to reference the misc array
     */
    public static final int OBJECT3D_SWITCH_1 = 0;
    public static final int OBJECT3D_SWITCH_2 = 1;
    public static final int OBJECT3D_SWITCH_3 = 2;

    private Object3D[] misc;// switch1, switch2;

    /**
     * The top height of the floor
     */
    public static final float FLOOR_DEPTH = .5f;

    /**
     * The top height of the switches etc...
     */
    public static final double MISC_HEIGHT_Z = FLOOR_DEPTH + .25;

    public Renderer(Context context, View view) {

        //call parent
        super(context);

        //store view reference
        this.view = view;

        //assign speed
        setFrameRate(MainActivity.FPS);

        //create new array for the blocks in the game
        this.blocks = new RectangularPrism[5];

        //create new array for our switches etc...
        this.misc = new Object3D[3];
    }

    @Override
    public void dispose() {

    }

    @Override
    public void initScene() {

        Log.d(TAG, "initScene");

        //remove all children, if exist
        getCurrentScene().clearChildren();

        //create the floors for the level
        createFloors();

        //create our game block
        createBlock();

        //create the misc objects
        createMisc();

        //add the floor and block, etc... to our game
        MainActivity.getGame().create();
    }

    public void updateCamera(Vector3 position) {

        //position the camera accordingly
        getCurrentCamera().setPosition(position);

        //rotate so we are viewing from an angle
        getCurrentCamera().rotate(Vector3.Axis.X, -45);

        //getCurrentCamera().rotate(Vector3.Axis.X, -55);
        //getCurrentCamera().rotate(Vector3.Axis.Z, -15);
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
        getBlocks()[PRISM_BLOCK] = new RectangularPrism(2, 1, 1);

        //add texture
        getBlocks()[PRISM_BLOCK].setMaterial(materialBlock);
    }

    /**
     * Create the miscellaneous objects in the game (light switch, heavy switch, etc...)
     */
    private void createMisc() {

        try {

            Material material = new Material();
            material.enableLighting(true);
            material.setDiffuseMethod(new DiffuseMethod.Lambert());
            material.setColorInfluence(0f);

            try {
                material.addTexture(new Texture("Switch", R.drawable.switches));
            } catch (Exception e) {

                e.printStackTrace();

                //color material if we couldn't load a texture
                material.setColor(Color.BLUE);
            }

            LoaderSTL loaderSTL = new LoaderSTL(getContext().getResources(), getTextureManager(), R.raw.switch_1_stl);
            loaderSTL.parse();
            getMisc()[OBJECT3D_SWITCH_1] = loaderSTL.getParsedObject();
            getMisc()[OBJECT3D_SWITCH_1].setPosition(0,0, MISC_HEIGHT_Z);
            getMisc()[OBJECT3D_SWITCH_1].setMaterial(material);
            getMisc()[OBJECT3D_SWITCH_1].setScale(.0115);

            loaderSTL = new LoaderSTL(getContext().getResources(), getTextureManager(), R.raw.switch_2_stl);
            loaderSTL.parse();
            getMisc()[OBJECT3D_SWITCH_2] = loaderSTL.getParsedObject();
            getMisc()[OBJECT3D_SWITCH_2].setPosition(1,0, MISC_HEIGHT_Z);
            getMisc()[OBJECT3D_SWITCH_2].setMaterial(material);
            getMisc()[OBJECT3D_SWITCH_2].setScale(.04);

            loaderSTL = new LoaderSTL(getContext().getResources(), getTextureManager(), R.raw.switch_3_stl);
            loaderSTL.parse();
            getMisc()[OBJECT3D_SWITCH_3] = loaderSTL.getParsedObject();
            getMisc()[OBJECT3D_SWITCH_3].setPosition(1,0,MISC_HEIGHT_Z);
            getMisc()[OBJECT3D_SWITCH_3].setMaterial(material);
            getMisc()[OBJECT3D_SWITCH_3].setScale(.02);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the floor for the level
     */
    private void createFloors() {

        Material materialFloor = new Material();
        materialFloor.enableLighting(true);
        materialFloor.setDiffuseMethod(new DiffuseMethod.Lambert());
        materialFloor.setColorInfluence(0f);

        Material materialFloorWeak = new Material();
        materialFloorWeak.enableLighting(true);
        materialFloorWeak.setDiffuseMethod(new DiffuseMethod.Lambert());
        materialFloorWeak.setColorInfluence(0f);

        Material materialFloorHidden = new Material();
        materialFloorHidden.enableLighting(true);
        materialFloorHidden.setDiffuseMethod(new DiffuseMethod.Lambert());
        materialFloorHidden.setColorInfluence(0f);

        Material materialFloorGoal = new Material();
        materialFloorGoal.enableLighting(true);
        materialFloorGoal.setDiffuseMethod(new DiffuseMethod.Lambert());
        materialFloorGoal.setColorInfluence(0f);

        try {

            materialFloor.addTexture(new Texture("Floor", R.drawable.floor));
            materialFloorWeak.addTexture(new Texture("FloorWeak", R.drawable.floor_weak));
            materialFloorHidden.addTexture(new Texture("FloorHidden", R.drawable.floor_hidden));
            materialFloorGoal.addTexture(new Texture("FloorGoal", R.drawable.goal));

        } catch (Exception e) {
            e.printStackTrace();

            //color material if we couldn't load a texture
            materialFloor.setColor(Color.GRAY);
            materialFloorWeak.setColor(Color.YELLOW);
            materialFloorHidden.setColor(Color.GREEN);
            materialFloorGoal.setColor(Color.RED);
        }

        getBlocks()[PRISM_FLOOR_STANDARD] = new RectangularPrism(1f, 1f, FLOOR_DEPTH);
        getBlocks()[PRISM_FLOOR_STANDARD].setPosition(0,0,0);
        getBlocks()[PRISM_FLOOR_STANDARD].setMaterial(materialFloor);

        getBlocks()[PRISM_FLOOR_WEAK] = new RectangularPrism(1f, 1f, FLOOR_DEPTH);
        getBlocks()[PRISM_FLOOR_WEAK].setPosition(0,0,0);
        getBlocks()[PRISM_FLOOR_WEAK].setMaterial(materialFloorWeak);

        getBlocks()[PRISM_FLOOR_HIDDEN] = new RectangularPrism(1f, 1f, FLOOR_DEPTH);
        getBlocks()[PRISM_FLOOR_HIDDEN].setPosition(0,0,0);
        getBlocks()[PRISM_FLOOR_HIDDEN].setMaterial(materialFloorHidden);

        getBlocks()[PRISM_FLOOR_GOAL] = new RectangularPrism(1f, 1f, FLOOR_DEPTH);
        getBlocks()[PRISM_FLOOR_GOAL].setPosition(0,0,0);
        getBlocks()[PRISM_FLOOR_GOAL].setMaterial(materialFloorGoal);
        getBlocks()[PRISM_FLOOR_GOAL].rotate(Vector3.Axis.Z, 90);
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

        if (MainActivity.getGame() != null &&
            MainActivity.getGame().getBoard() != null &&
            MainActivity.getGame().getBlock() != null) {

            if (MainActivity.getGame().getBoard().hasSetup()) {
                //update the board setup
                MainActivity.getGame().getBoard().update();
            } else if (MainActivity.getGame().getBlock().hasSetup()) {
                //update the block setup
                MainActivity.getGame().getBlock().update();
            } else {
                //update the block animation (if exists)
                MainActivity.getGame().getBlock().rotate();
            }
        }
    }

    public RectangularPrism[] getBlocks() {
        return this.blocks;
    }

    public Object3D[] getMisc() {
        return this.misc;
    }
}