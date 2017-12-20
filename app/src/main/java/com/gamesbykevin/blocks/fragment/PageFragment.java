package com.gamesbykevin.blocks.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by Kevin on 12/19/2017.
 */
public class PageFragment extends Fragment {

    /**
     * The argument key for the page number this fragment represents.
     */
    protected static final String ARG_PAGE = "page";

    //the fragment's page number
    private int pageNumber;

    public PageFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the arguments passed
        this.pageNumber = getArguments().getInt(ARG_PAGE);
    }

    public static PageFragment create(int pageNumber) {

        //our page fragment
        PageFragment fragment = new TutorialFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);

        //return our fragment
        return fragment;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public void onResume() {

        //call parent
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}