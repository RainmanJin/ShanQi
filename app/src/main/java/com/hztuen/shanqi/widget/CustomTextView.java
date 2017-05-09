package com.hztuen.shanqi.widget;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 自定义字体
 */

public class CustomTextView extends TextView {

    public CustomTextView(Context context) {
        this(context, null);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/DINCond-Medium.otf");
        this.setTypeface(face);

    }

    public void setLine(boolean isShow) {

        if (isShow) {
            this.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        }

    }


}
