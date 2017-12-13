package com.gamesbykevin.blocks.opengl;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.gamesbykevin.blocks.activity.MainActivity;
import com.gamesbykevin.blocks.common.IDisposable;
import com.gamesbykevin.blocks.levels.Level;
import com.gamesbykevin.blocks.util.Timer;

import org.rajawali3d.Object3D;
import org.rajawali3d.materials.Material;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.primitives.RectangularPrism;

import javax.microedition.khronos.opengles.GL10;

import static com.gamesbykevin.blocks.activity.MainActivity.TAG;
import static com.gamesbykevin.blocks.opengl.RendererHelper.createBlock;
import static com.gamesbykevin.blocks.opengl.RendererHelper.createContainers;
import static com.gamesbykevin.blocks.opengl.RendererHelper.createFloors;
import static com.gamesbykevin.blocks.opengl.RendererHelper.createMisc;
import static com.gamesbykevin.blocks.opengl.RendererHelper.createNumbers;
import static com.gamesbykevin.blocks.opengl.RendererHelper.resetContainers;

/**
 * Created by Kevin on 11/26/2017.
 */
public class Renderer extends org.rajawali3d.renderer.Renderer implements IDisposable {

    //array list of block items (player block, floor)
    protected RectangularPrism[] blocks;

    //array list of misc items (switches, etc...)
    protected Object3D[] misc;

    //all the number textures we need for our timer
    protected Material[] textures;

    //list of containers to display our 2d sprite images
    protected Plane[] containers;

    public Renderer(Context context) {

        //call parent
        super(context);

        //assign speed
        setFrameRate(MainActivity.FPS);

        //create new array for the blocks in the game
        this.blocks = new RectangularPrism[5];

        //create new array for our 2 switches etc...
        this.misc = new Object3D[2];

        //create array to contain all our game timer images
        this.textures = new Material[11];

        //create array for our numbers to be displayed
        this.containers = new Plane[7];
    }

    public RectangularPrism[] getBlocks() {
        return this.blocks;
    }

    public Object3D[] getMisc() {
        return this.misc;
    }

    @Override
    public void dispose() {

        if (this.misc != null) {
            for (int i = 0; i < this.misc.length; i++) {
                if (this.misc[i] != null) {
                    this.misc[i].destroy();
                    this.misc[i] = null;
                }
            }

            this.misc = null;
        }

        if (this.textures != null) {
            for (int i = 0; i < this.textures.length; i++) {
                if (this.textures[i] != null) {
                    this.textures[i].unbindTextures();
                    this.textures[i] = null;
                }
            }

            this.textures = null;
        }

        if (this.containers != null) {
            for (int i = 0; i < this.containers.length; i++) {
                if (this.containers[i] != null) {
                    this.containers[i].destroy();
                    this.containers[i] = null;
                }
            }

            this.containers = null;
        }
    }

    @Override
    public void initScene() {

        Log.d(TAG, "initScene");

        //remove all children, if exist
        getCurrentScene().clearChildren();

        //create the numbers for our game timer
        createNumbers(this);

        //create the containers to display our numbers in
        createContainers(this);

        //create the floors for the level
        createFloors(this);

        //create our game block
        createBlock(this);

        //create the misc objects
        createMisc(this);

        //add the floor and block, etc... to our game
        MainActivity.getGame().create();
    }

    public void updateCamera(Level level) {

        //position the camera accordingly
        getCurrentCamera().setPosition(level.getCamera());

        //rotate so we are viewing from an angle
        getCurrentCamera().rotate(Vector3.Axis.X, -45);

        //getCurrentCamera().rotate(Vector3.Axis.X, -55);
        //getCurrentCamera().rotate(Vector3.Axis.Z, -15);

        //the end will be the number of columns
        int end = level.getCols();

        //display 00:00 at start
        resetContainers(this, 1, 00, end - 5, level.getRows(), 1);
        resetContainers(this, 2, 00, end - 4, level.getRows(), 1);
        resetContainers(this, 3, 00, end - 2, level.getRows(), 1);
        resetContainers(this, 4, 00, end - 1, level.getRows(), 1);
        resetContainers(this, 0, 10, end - 3, level.getRows(), 1);

        if (level.getNumber() < 10) {
            resetContainers(this, 5, 00, end - 8, level.getRows(), 1);
            resetContainers(this, 6, level.getNumber(), end - 7, level.getRows(), 1);
        } else {

            int tens = level.getNumber() / 10;
            int ones = level.getNumber() - (tens * 10);

            resetContainers(this, 5, tens, end - 8, level.getRows(), 1);
            resetContainers(this, 6, ones, end - 7, level.getRows(), 1);
        }
    }

    public void updateTimer(Timer timer) {

        if (timer.hasFlag1())
            this.containers[1].setMaterial(this.textures[timer.getClock1()]);
        if (timer.hasFlag2())
            this.containers[2].setMaterial(this.textures[timer.getClock2()]);
        if (timer.hasFlag3())
            this.containers[3].setMaterial(this.textures[timer.getClock3()]);
        if (timer.hasFlag4())
            this.containers[4].setMaterial(this.textures[timer.getClock4()]);
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
}