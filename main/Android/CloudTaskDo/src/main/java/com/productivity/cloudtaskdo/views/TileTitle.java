package com.productivity.cloudtaskdo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.productivity.cloudtaskdo.R;

/**
 * Created by JuanCarlos on 13/01/2015.
 */
public class TileTitle extends TextView {

    public TileTitle(Context context) {
        super(context);
    }

    public TileTitle(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.tileTitleStyle);
    }

    public TileTitle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
