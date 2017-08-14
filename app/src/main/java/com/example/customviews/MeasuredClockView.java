package com.example.customviews;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class MeasuredClockView extends PaddingClockView {

    private int defaultSize;

    public MeasuredClockView(Context context) {
        this(context,null);
    }

    public MeasuredClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        defaultSize = (int) (200 * getContext().getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int resolvedWidth = resolveSize(defaultSize,widthMeasureSpec);
        int resolvedHeight = resolveSize(defaultSize,heightMeasureSpec);

        setMeasuredDimension(resolvedWidth,resolvedHeight);
    }
}
