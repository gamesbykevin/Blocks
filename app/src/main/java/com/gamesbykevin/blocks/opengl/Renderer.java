package com.gamesbykevin.blocks.opengl;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.gamesbykevin.blocks.R;

import org.rajawali3d.Object3D;
import org.rajawali3d.cameras.ArcballCamera;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.RectangularPrism;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Kevin on 11/26/2017.
 */
public class Renderer extends org.rajawali3d.renderer.Renderer {

    private final View view;

    private RectangularPrism prism;

    private int count = 0;

    private int index = 0;

    //is the block standing up?
    private boolean standing = false;

    //are we rotating vertically?
    private boolean vertical = false;

    public enum Direction {
        North, East, South, West
    }

    //current direction we are heading
    private Direction current = Direction.values()[index];

    private boolean goal = false;

    public Renderer(Context context, View view) {
        super(context);
        setFrameRate(60);
        this.view = view;
    }

    @Override
    public void initScene() {

        getCurrentScene().clearChildren();

        Material materialBlock = new Material();
        Material materialFloor = new Material();

        try {

            materialBlock.enableLighting(true);
            materialBlock.setDiffuseMethod(new DiffuseMethod.Lambert());
            materialBlock.setColorInfluence(0f);
            materialBlock.addTexture(new Texture("Block", R.drawable.block));

            materialFloor.enableLighting(true);
            materialFloor.setDiffuseMethod(new DiffuseMethod.Lambert());
            materialFloor.setColorInfluence(0f);
            materialFloor.addTexture(new Texture("Floor", R.drawable.floor));

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            for (int row = 0; row <  10; row++) {
                for (int col = 0; col < 10; col++) {

                    if (row == 5 && col == 5)
                        continue;

                    RectangularPrism floor = new RectangularPrism(1f, 1f, .5f);
                    floor.setMaterial(materialFloor);
                    floor.setPosition(col,row,0);
                    getCurrentScene().addChild(floor);
                }
            }

            prism = new RectangularPrism(2, 1, 1);
            prism.setMaterial(materialBlock);
            prism.setPosition(.5,0,.75);
            getCurrentScene().addChild(prism);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Object3D target = new Object3D();
        target.setPosition(4, -2, 0);

        //getCurrentCamera().setLookAt(target.getPosition());
        getCurrentCamera().setPosition(target.getX(), target.getY(), target.getZ() + 10);
        getCurrentCamera().rotate(Vector3.Axis.X, -30);

        /*
        ArcballCamera camera = new ArcballCamera(getContext(), view, target);
        camera.setLookAt(target.getPosition());
        //ArcballCamera camera = new ArcballCamera(getContext(), view);//, prism);

        camera.setPosition(target.getX(), target.getY(), target.getZ() + 10);
        //camera.rotate(Vector3.Axis.Z, 15);
        camera.rotate(Vector3.Axis.X, -30);
        //camera.rotate(Vector3.Axis.Y, -15);

        getCurrentScene().replaceAndSwitchCamera(getCurrentCamera(), camera);
        */
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

        if (prism != null) {

            if (goal) {

                prism.setPosition(prism.getX(), prism.getY(), prism.getZ() - .05);
                return;
            }

            int singleRotation = 90;

            if (count < singleRotation) {

                float speed = 10f;

                float xVel = 0;
                float yVel = 0;
                float zVel = 0;
                float rotate = 0;
                Vector3.Axis axis = null;

                switch(Direction.values()[index]) {

                    case North:
                        rotate = speed;
                        axis = Vector3.Axis.X;

                        if (vertical) {

                            yVel = (rotate / 60f);

                            if (!standing) {
                                zVel = (.5f / singleRotation) * speed;

                                if (prism.getZ() + zVel > 1.25)
                                    zVel = 1.25f - (float) prism.getZ();
                            } else {
                                zVel = -(.5f / singleRotation) * speed;

                                if (prism.getZ() + zVel < .75)
                                    zVel = .75f - (float) prism.getZ();
                            }
                        } else {
                            yVel = (rotate / 90f);
                        }
                        break;

                    case South:
                        rotate = -speed;
                        axis = Vector3.Axis.X;

                        if (vertical) {

                            yVel = (rotate / 60f);

                            if (!standing) {
                                zVel = (.5f / singleRotation) * speed;

                                if (prism.getZ() + zVel > 1.25)
                                    zVel = 1.25f - (float) prism.getZ();
                            } else {
                                zVel = -(.5f / singleRotation) * speed;

                                if (prism.getZ() + zVel < .75)
                                    zVel = .75f - (float) prism.getZ();
                            }
                        } else {
                            yVel = (rotate / 90f);
                        }
                        break;

                    case East:
                        rotate = -speed;
                        axis = Vector3.Axis.Y;

                        if (vertical) {

                            xVel = -(rotate / 60f);

                            if (!standing) {
                                zVel = (.5f / singleRotation) * speed;

                                if (prism.getZ() + zVel > 1.25)
                                    zVel = 1.25f - (float) prism.getZ();
                            } else {
                                zVel = -(.5f / singleRotation) * speed;

                                if (prism.getZ() + zVel < .75)
                                    zVel = .75f - (float) prism.getZ();
                            }
                        } else {
                            xVel = -(rotate / 90f);
                        }
                        break;

                    case West:

                        rotate = speed;
                        axis = Vector3.Axis.Y;

                        if (vertical) {

                            xVel = -(rotate / 60f);

                            if (!standing) {
                                zVel = (.5f / singleRotation) * speed;

                                if (prism.getZ() + zVel > 1.25)
                                    zVel = 1.25f - (float) prism.getZ();
                            } else {
                                zVel = -(.5f / singleRotation) * speed;

                                if (prism.getZ() + zVel < .75)
                                    zVel = .75f - (float) prism.getZ();
                            }
                        } else {
                            xVel = -(rotate / 90f);
                        }
                        break;
                }

                //rotate accordingly
                prism.rotate(axis, rotate);

                //update the location
                prism.setPosition(prism.getX() + xVel, prism.getY() + yVel, prism.getZ() + zVel);

                //keep track of were we are at with our rotation
                count += Math.abs(rotate);

            } else {

                //if we are rotated vertical switch between standing and non-standing
                if (vertical)
                    standing = !standing;

                //if standing check if we met the goal
                if (standing) {

                    if ((int)prism.getX() == 5 && (int)prism.getY() == 5) {
                        goal = true;
                        return;
                    }
                }


                Random random = new Random();

                //pick new random direction
                index = random.nextInt(Direction.values().length);

                //east
                if (prism.getX() < 3)
                    index = 1;

                //west
                if (prism.getX() > 6)
                    index = 3;

                //north
                if (prism.getY() < 3)
                    index = 0;

                //south
                if (prism.getY() > 6)
                    index = 2;

                //check if we are making a shift in direction
                switch (current) {

                    case East:
                    case West:

                        switch(Direction.values()[index]) {
                            case North:
                            case South:

                                vertical = !vertical;

                                if (standing)
                                    vertical = true;
                                break;
                        }
                        break;

                    case North:
                    case South:

                        switch(Direction.values()[index]) {
                            case East:
                            case West:

                                vertical = !vertical;

                                if (standing)
                                    vertical = true;
                                break;
                        }
                        break;
                }

                //reset our count back to 0
                count = 0;

                //assign current direction
                current = Direction.values()[index];
            }
        }
    }
}