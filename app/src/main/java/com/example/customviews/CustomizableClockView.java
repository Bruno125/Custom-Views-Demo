package com.example.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by fanlat on 14/08/17.
 */

public class CustomizableClockView extends OptimizedClockView {
    public CustomizableClockView(Context context) {
        this(context, null);
    }

    public CustomizableClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs){
        if(attrs == null)
            return;

        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.clock_view,
                0, 0);

        try {
            //Read custom background color
            int backgroundColor = a.getColor(R.styleable.clock_view_background_color, -1);
            if(backgroundColor != -1){
                mBackgroundColor = backgroundColor;
            }
            //Read custom active text color
            int activeTextColor = a.getColor(R.styleable.clock_view_active_text_color, -1);
            if(activeTextColor != -1){
                mActiveTextColor = activeTextColor;
            }
            //Read custom inactive text color
            int inactiveTextColor = a.getColor(R.styleable.clock_view_inactive_text_color, -1);
            if(inactiveTextColor != -1){
                mInactiveTextColor = inactiveTextColor;
            }
            //Set grid
            mShowGrid = a.getBoolean(R.styleable.clock_view_show_grid,false);
            mShowGridBackground = a.getBoolean(R.styleable.clock_view_show_square,false);
            //Set default number
            int defaultNumber = a.getInt(R.styleable.clock_view_default_value,24);
            if(defaultNumber >= 0 && defaultNumber < 25)
                mCurrentNumber = defaultNumber;

        } finally {
            a.recycle();
        }

        super.init();
    }


}
