package com.hztuen.shanqi.popup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.hztuen.shanqi.R;

/**
 * Created by asuss on 2016/12/23.
 */

public class KeFuPopupWindow extends PopupWindow {


    private View popWindow;
    public KeFuPopupWindow(Context context) {
        super(context);

        popWindow= LayoutInflater.from(context).inflate(R.layout.popupwindow_kefu,null);

        this.setContentView(popWindow);// 设置View
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);// 设置弹出窗口的高
        this.setFocusable(true);// 设置弹出窗口可
        this.setAnimationStyle(R.style.mypopwindow_anim_style);// 设置动画
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));// 设置背景透明


    }
}
