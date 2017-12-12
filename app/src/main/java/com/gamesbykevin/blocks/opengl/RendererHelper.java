package com.gamesbykevin.blocks.opengl;

import android.graphics.Color;
import android.opengl.GLES20;

import com.gamesbykevin.blocks.R;
import com.gamesbykevin.blocks.levels.Level;

import org.rajawali3d.loader.LoaderSTL;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.primitives.RectangularPrism;

/**
 * Created by Kevin on 12/11/2017.
 */
public class RendererHelper {

    /**
     * How we know to reference the blocks array
     */
    public static final int PRISM_BLOCK = 0;
    public static final int PRISM_FLOOR_STANDARD = 1;
    public static final int PRISM_FLOOR_WEAK = 2;
    public static final int PRISM_FLOOR_HIDDEN = 3;
    public static final int PRISM_FLOOR_GOAL = 4;

    /**
     * How we know to reference the misc array
     */
    public static final int OBJECT3D_SWITCH_1 = 0;
    public static final int OBJECT3D_SWITCH_2 = 1;

    /**
     * The top height of the floor
     */
    public static final float FLOOR_DEPTH = .5f;

    /**
     * The top height of the switches etc...
     */
    public static final double MISC_HEIGHT_Z = FLOOR_DEPTH + .25;

    protected static void createContainers(Renderer renderer) {

        //create our containers to place the images within
        createPlane(renderer, 0);
        createPlane(renderer, 1);
        createPlane(renderer, 2);
        createPlane(renderer, 3);
        createPlane(renderer, 4);
        createPlane(renderer, 5);
        createPlane(renderer, 6);
    }

    protected static void createNumbers(Renderer renderer) {
        createMaterial(renderer, 0, R.drawable.zero, "Zero");
        createMaterial(renderer, 1, R.drawable.one, "One");
        createMaterial(renderer, 2, R.drawable.two, "Two");
        createMaterial(renderer, 3, R.drawable.three, "Three");
        createMaterial(renderer, 4, R.drawable.four, "Four");
        createMaterial(renderer, 5, R.drawable.five, "Five");
        createMaterial(renderer, 6, R.drawable.six, "Six");
        createMaterial(renderer, 7, R.drawable.seven, "Seven");
        createMaterial(renderer, 8, R.drawable.eight, "Eight");
        createMaterial(renderer, 9, R.drawable.nine, "Nine");
        createMaterial(renderer, 10, R.drawable.colon, "Colon");
    }

    protected static void resetContainers(Renderer renderer, int index, int numberIndex, int col, int row, double z) {

        //set the image to be displayed
        renderer.containers[index].setMaterial(renderer.textures[numberIndex]);

        //assign position
        renderer.containers[index].setX(col);
        renderer.containers[index].setY(row);
        renderer.containers[index].setZ(z);
    }

    protected static void createPlane(Renderer renderer, final int index) {

        //create our plane
        renderer.containers[index] = new Plane(1,1,1,1);
        renderer.containers[index].setBlendingEnabled(true);
        renderer.containers[index].setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        renderer.containers[index].rotate(Vector3.Axis.X, -45);

        //add it to the current scene
        renderer.getCurrentScene().addChild(renderer.containers[index]);
    }

    protected static void createMaterial(Renderer renderer, final int index, final int resId, final String desc) {

        renderer.textures[index] = new Material();
        renderer.textures[index].enableLighting(true);
        renderer.textures[index].setColorInfluence(0f);

        try {
            renderer.textures[index].addTexture(new Texture(desc, resId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the floor for the level
     */
    protected static void createFloors(Renderer renderer) {

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

        renderer.getBlocks()[PRISM_FLOOR_STANDARD] = new RectangularPrism(1f, 1f, FLOOR_DEPTH);
        renderer.getBlocks()[PRISM_FLOOR_STANDARD].setPosition(0,0,0);
        renderer.getBlocks()[PRISM_FLOOR_STANDARD].setMaterial(materialFloor);

        renderer.getBlocks()[PRISM_FLOOR_WEAK] = new RectangularPrism(1f, 1f, FLOOR_DEPTH);
        renderer.getBlocks()[PRISM_FLOOR_WEAK].setPosition(0,0,0);
        renderer.getBlocks()[PRISM_FLOOR_WEAK].setMaterial(materialFloorWeak);

        renderer.getBlocks()[PRISM_FLOOR_HIDDEN] = new RectangularPrism(1f, 1f, FLOOR_DEPTH);
        renderer.getBlocks()[PRISM_FLOOR_HIDDEN].setPosition(0,0,0);
        renderer.getBlocks()[PRISM_FLOOR_HIDDEN].setMaterial(materialFloorHidden);

        renderer.getBlocks()[PRISM_FLOOR_GOAL] = new RectangularPrism(1f, 1f, FLOOR_DEPTH);
        renderer.getBlocks()[PRISM_FLOOR_GOAL].setPosition(0,0,0);
        renderer.getBlocks()[PRISM_FLOOR_GOAL].setMaterial(materialFloorGoal);
        renderer.getBlocks()[PRISM_FLOOR_GOAL].rotate(Vector3.Axis.Z, 90);
    }

    /**
     * Create the 3d object representing the game block
     */
    protected static void createBlock(Renderer renderer) {

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
        renderer.getBlocks()[PRISM_BLOCK] = new RectangularPrism(2, 1, 1);

        //add texture
        renderer.getBlocks()[PRISM_BLOCK].setMaterial(materialBlock);
    }

    /**
     * Create the miscellaneous objects in the game (light switch, heavy switch, etc...)
     */
    protected static void createMisc(Renderer renderer) {

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

            LoaderSTL loaderSTL = new LoaderSTL(renderer.getContext().getResources(), renderer.getTextureManager(), R.raw.switch_1_stl);
            loaderSTL.parse();
            renderer.getMisc()[OBJECT3D_SWITCH_1] = loaderSTL.getParsedObject();
            renderer.getMisc()[OBJECT3D_SWITCH_1].setPosition(0,0, MISC_HEIGHT_Z);
            renderer.getMisc()[OBJECT3D_SWITCH_1].setMaterial(material);
            renderer.getMisc()[OBJECT3D_SWITCH_1].setScale(.04);

            loaderSTL = new LoaderSTL(renderer.getContext().getResources(), renderer.getTextureManager(), R.raw.switch_2_stl);
            loaderSTL.parse();
            renderer.getMisc()[OBJECT3D_SWITCH_2] = loaderSTL.getParsedObject();
            renderer.getMisc()[OBJECT3D_SWITCH_2].setPosition(1,0, MISC_HEIGHT_Z);
            renderer.getMisc()[OBJECT3D_SWITCH_2].setMaterial(material);
            renderer.getMisc()[OBJECT3D_SWITCH_2].setScale(.04);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void createBackground(Renderer renderer) {

        renderer.backgroundMaterial = new Material();
        renderer.backgroundMaterial.enableLighting(true);
        renderer.backgroundMaterial.setColorInfluence(0f);

        try {
            renderer.backgroundMaterial.addTexture(new Texture("Background", R.drawable.background));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void updateBackground(Renderer renderer, Level level) {

        //create our plane
        renderer.backgroundContainer = new Plane(level.getCols(), level.getRows(),level.getCols(), level.getRows());
        renderer.backgroundContainer.setBlendingEnabled(true);
        renderer.backgroundContainer.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        renderer.backgroundContainer.rotate(Vector3.Axis.X, -45);
        renderer.backgroundContainer.setX(-1);
        renderer.backgroundContainer.setY(-1);
        renderer.backgroundContainer.setZ(-5);

        //add it to the current scene
        renderer.getCurrentScene().addChild(renderer.backgroundContainer);
    }
}