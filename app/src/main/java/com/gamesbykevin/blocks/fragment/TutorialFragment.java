package com.gamesbykevin.blocks.fragment;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamesbykevin.blocks.R;
import com.gamesbykevin.blocks.activity.TutorialActivity;

public class TutorialFragment extends PageFragment {

    public TutorialFragment() {
        //call parent
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //inflate the layout to access the ui elements
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_tutorial, container, false);

        ImageView imageView =   view.findViewById(R.id.modeImage);
        TextView textView =     view.findViewById(R.id.modeText);

        //get the current page we are on
        TutorialActivity.Pages page = TutorialActivity.Pages.values()[getPageNumber()];

        //update bitmap accordingly
        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), page.resIdImage));

        //assign the appropriate text
        textView.setText(page.resIdText);

        // Inflate the layout for this fragment
        return view;
    }
}