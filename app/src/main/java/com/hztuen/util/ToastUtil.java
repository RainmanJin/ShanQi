package com.hztuen.util;

import android.content.Context;
import android.widget.Toast;

/**
 * 防止toast频繁的弹出
 */

public class ToastUtil {
    private static Toast toast;

    public static void showToast(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }
}
