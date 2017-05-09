package com.hztuen.shanqi;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.WindowManager;

import com.amap.api.maps.model.LatLng;
import com.hztuen.util.MyLogUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 *
 */

public class MyApplication extends Application {


    //订单状态状态  orderStatus
    //cycling：骑行中  appointed 预约中 temporaryLock 临时锁车中
    public static String orderStatus = "0";

    //支付状态 paymentStatus
    //free :没有订单需要支付  wait:有订单需要支付
    public static String paymentStatus = "free";

    //登录状态 true已经登录 false登录失败/未登录
    public static boolean isLogin = false;

    //订单状态 sn
    //"0"当前没有订单
    public static String sn = "0";

    //注册状态
    //isDeposit：是否交押金  isIdentity：是否实名认证
    public static boolean isDeposit = false;
    public static boolean isIdentity = false;

    //用户的 userId
    public static String userId = "0";

    //手机屏幕的长宽
    public static int width;
    public static int height;

    //是否处于预约中 用于判断地图的一些处理逻辑
    public static boolean isAppointment = false;

    public static boolean isFromMain = false;
    //当前状态的订单编号


    //登陆有三种状态
    //10001 ：未登录 10002已登录未认证 10003已登录已认证
    public static int LOGIN_STATE = 10001;


    //是否已经手机登录
    public static boolean isPhone = false;


    public static double myMoney = 200;
    public static long startTime = 0;
    public static String bikeId = "0";


    public static boolean isGoWeChatDeposit = false;//去微信充值

    public static long appointmentStartTime = 0;


    public static boolean isFromPayActivity = false;

    public static boolean isFromSetting = false;

    public static String myToken = "";

    public static LatLng mLocation;
    public static Boolean isCheckPgyVersion = false;  //表示是否在蒲公英检查过版本

    @Override
    public void onCreate() {
        super.onCreate();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        MyLogUtil.i("屏幕尺寸", "w=" + width + "h=" + height);




        DisplayImageOptions userOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).
                        showImageForEmptyUri(R.mipmap.logo).
                        showImageOnLoading(R.mipmap.logo).
                        showImageOnFail(R.mipmap.logo).
                        imageScaleType(ImageScaleType.EXACTLY).
                        bitmapConfig(Bitmap.Config.RGB_565).
                        considerExifParams(true)
                .build();


        ImageLoaderConfiguration config =
                new ImageLoaderConfiguration
                        .Builder(getApplicationContext())
                        .diskCacheFileCount(200)
                        .threadPoolSize(Thread.NORM_PRIORITY)
//                        .denyCacheImageMultipleSizesInMemory()
//                        .memoryCacheSize(10 * 1024 * 1024)
                        .defaultDisplayImageOptions(userOptions)
                        .build();

        ImageLoader.getInstance().init(config);
    }
}
