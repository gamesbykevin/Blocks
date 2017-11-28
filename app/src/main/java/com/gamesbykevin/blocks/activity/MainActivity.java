package com.gamesbykevin.blocks.activity;

import android.app.Activity;
import android.os.Bundle;

import com.gamesbykevin.blocks.R;
import com.gamesbykevin.blocks.opengl.Renderer;

import org.rajawali3d.view.ISurface;
import org.rajawali3d.view.SurfaceView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        surfaceView.setFrameRate(60);
        surfaceView.setRenderMode(ISurface.RENDERMODE_WHEN_DIRTY);

        Renderer renderer = new Renderer(this, surfaceView);
        surfaceView.setSurfaceRenderer(renderer);
    }
}
