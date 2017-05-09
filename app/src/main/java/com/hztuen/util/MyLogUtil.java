package com.hztuen.util;

import android.util.Log;



public class MyLogUtil {

    public static boolean isDebug = true;

    public static void i(String msg) {
        if (isDebug) {
            Log.i("zangyi", msg);
        }
    }
    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i("zangyi",tag+" \n"+msg);
        }
    }
}
