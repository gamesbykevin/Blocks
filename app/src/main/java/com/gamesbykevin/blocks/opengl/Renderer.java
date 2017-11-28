package com.gamesbykevin.blocks.opengl;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import com.gamesbykevin.blocks.R;

import org.rajawali3d.Object3D;
import org.rajawali3d.cameras.ArcballCamera;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.RectangularPrism;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Kevin on 11/26/2017.
 */
public class Renderer extends org.rajawali3d.renderer.Renderer {

    private final View view;

    private RectangularPrism prism;

    private int count = 0;

    private int index = 0;

    private boolean down = true;

    public enum Direction {
        North, East, South, West
    }

    public Renderer(Context context, View view) {
        super(context);
        setFrameRate(60);
        this.view = view;
    }

    @Override
    public void initScene() {

        DirectionalLight directionalLight = new DirectionalLight(0, 10f, 2);
        directionalLight.setColor(1.0f, 1.0f, 1.0f);
        directionalLight.setPower(1f);
        //getCurrentScene().addLight(directionalLight);

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

        Object3D target = null;

        try {

            for (int row = 0; row <  10; row++) {
                for (int col = 0; col < 10; col++) {

                    if (row == 5 && col == 5)
                        continue;

                    RectangularPrism floor = new RectangularPrism(1f, 1f, .5f);
                    floor.setMaterial(materialFloor);
                    floor.setPosition(col,row,0);
                    getCurrentScene().addChild(floor);

                    if (row == 4 && col == 5)
                        target = floor;
                }
            }

            prism = new RectangularPrism(2, 1, 1);
            prism.setMaterial(materialBlock);
            prism.setPosition(.5,0,.75);
            getCurrentScene().addChild(prism);

        } catch (Exception e) {
            e.printStackTrace();
        }

        ArcballCamera camera = new ArcballCamera(getContext(), view, target);
        //ArcballCamera camera = new ArcballCamera(getContext(), view);//, prism);

        camera.setPosition(15, 15, 10);
        //camera.setRotZ(60);

        //camera.rotate(Vector3.Axis.X, 10);
        //camera.rotate(Vector3.Axis.Y, 10);
        //camera.rotate(Vector3.Axis.X, 15);
        getCurrentScene().replaceAndSwitchCamera(getCurrentCamera(), camera);

        //getCurrentCamera().rotate(Vector3.Axis.Z, 10);
    }

    @Override
    public void onTouchEvent(MotionEvent event) {

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

            int singleRotation = 90;

            if (count < singleRotation * 8) {

                float speed = 5;

                float xVel = 0;
                float yVel = 0;
                float rotate = 0;
                Vector3.Axis axis = null;

                switch(Direction.values()[index]) {

                    case North:
                        rotate = speed;
                        yVel = (rotate / 90f);
                        axis = Vector3.Axis.X;
                        break;

                    case South:
                        rotate = -speed;
                        yVel = (rotate / 90f);
                        axis = Vector3.Axis.X;
                        break;

                    case East:
                        rotate = -speed;
                        xVel = -(rotate / 180f);
                        axis = Vector3.Axis.Y;
                        break;

                    case West:
                        rotate = speed;
                        xVel = -(rotate / 180f);
                        axis = Vector3.Axis.Y;
                        break;
                }

                prism.rotate(axis, rotate);
                prism.setPosition(prism.getPosition().x + xVel, prism.getPosition().y + yVel, prism.getPosition().z);

                count += Math.abs(rotate);

            } else {

                index++;

                if (index >= Direction.values().length)
                    index = 0;

                switch(Direction.values()[index]) {

                    case North:
                    case South:
                        prism.setPosition(prism.getPosition().x, prism.getPosition().y, .75);
                        break;

                    case East:
                    case West:
                        prism.setPosition(prism.getPosition().x, prism.getPosition().y, 1.25);
                        break;
                }

                count = 0;
            }
        }

    }
}