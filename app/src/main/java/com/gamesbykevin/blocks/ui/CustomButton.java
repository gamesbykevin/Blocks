package com.gamesbykevin.blocks.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;
import com.gamesbykevin.blocks.R;

/**
 * Created by Kevin on 12/15/2017.
 */
public class CustomButton extends AppCompatButton implements View.OnClickListener {

    //our array of words we want to toggle through
    private String[] desc;

    //our current location
    private int index = 0;

    public CustomButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomButton(Context context) {
        this(context, null, 0);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        //setup the onclick listener
        init();

        //obtain the array container for our values
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomButton, defStyle, 0);

        try {

            //get the text description array from the user specified attribute
            CharSequence[] entries = a.getTextArray(R.styleable.CustomButton_android_entries);

            //make sure the array exists
            if (entries != null && entries.length > 0) {

                //create matching array
                this.desc = new String[entries.length];

                //populate the array
                for (int i = 0; i < entries.length; i++) {
                    this.desc[i] = entries[i].toString();
                }

            } else {
                throw new RuntimeException("No entries have been provided. Example: android:entries=\"@array/custom_array\"");
            }

            //assign our index if the user passed in via xml
            setIndex(a.getInteger(R.styleable.CustomButton_entry_index_default, 0));
        }
        finally
        {
            //clean up resources
            a.recycle();
        }

        //update the text
        displayText();
    }

    @Override
    public void onClick(View view) {

        //move to the next index
        setIndex(getIndex() + 1);
    }

    private void displayText() {

        //display the appropriate text
        super.setText(this.desc[this.getIndex()]);
    }

    private void init() {
        setOnClickListener(this);
    }

    public void setIndex(final int index) {
        this.index = index;

        //keep array index in bounds
        if (getIndex() >= this.desc.length)
            setIndex(0);

        displayText();
    }

    public int getIndex() {
        return this.index;
    }
}