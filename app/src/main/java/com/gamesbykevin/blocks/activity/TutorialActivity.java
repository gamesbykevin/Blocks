package com.gamesbykevin.blocks.activity;

import android.content.Intent;
import android.os.Bundle;

import com.gamesbykevin.blocks.R;

public class TutorialActivity extends PagerActivity {

    public static final int PAGES = 10;

    public enum Pages {
        Page1(R.drawable.page1, R.string.tutorialTextPage1),
        Page2(R.drawable.page2, R.string.tutorialTextPage2),
        Page3(R.drawable.page3, R.string.tutorialTextPage3),
        Page4(R.drawable.page4, R.string.tutorialTextPage4),
        Page5(R.drawable.page5, R.string.tutorialTextPage5),
        Page6(R.drawable.page6, R.string.tutorialTextPage6),
        Page7(R.drawable.page7, R.string.tutorialTextPage7),
        Page8(R.drawable.page8, R.string.tutorialTextPage8),
        Page9(R.drawable.page9, R.string.tutorialTextPage9),
        Page10(R.drawable.page10, R.string.turorialTextPage10);

        public final int resIdImage, resIdText;

        Pages(final int resIdImage, final int resIdText) {
            this.resIdImage = resIdImage;
            this.resIdText = resIdText;
        }
    }

    public TutorialActivity() {
        super(PAGES);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //inflate content view
        setContentView(R.layout.activity_tutorial);

        //call parent
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {

        //call parent
        super.onPause();
    }

    @Override
    public void onStart() {

        //call parent
        super.onStart();
    }

    @Override
    public void onResume() {

        //call parent
        super.onResume();

        //resume menu music
        BaseActivityHelper.playSound(R.raw.menu, true, false);
    }

    @Override
    public void onDestroy() {

        //call parent
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        //destroy this activity
        finish();

        //go back to main menu
        startActivity(new Intent(getBaseContext(), MainActivity.class));
    }
}