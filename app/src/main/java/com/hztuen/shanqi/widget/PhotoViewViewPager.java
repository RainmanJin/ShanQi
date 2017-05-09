package com.hztuen.shanqi.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 *  PhotoView 嵌套在ViewPager中 事件分发时存在Bug造成崩溃
 */

public class PhotoViewViewPager extends ViewPager {

    public PhotoViewViewPager(Context context) {
        super(context);
    }

    public PhotoViewViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
        }
        return false;

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {

        }
        return false;

    }
}
