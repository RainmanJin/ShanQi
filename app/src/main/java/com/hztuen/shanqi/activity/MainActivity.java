package com.hztuen.shanqi.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.hztuen.contacts.AppUrl;
import com.hztuen.shanqi.MyApplication;
import com.hztuen.shanqi.R;
import com.hztuen.shanqi.activity.myself.MyWalletActivity;
import com.hztuen.shanqi.activity.register.RegisterActivity01;
import com.hztuen.shanqi.activity.register.RegisterActivity03;
import com.hztuen.shanqi.activity.register.RegisterActivity04;
import com.hztuen.shanqi.activity.state.PayActivity;
import com.hztuen.shanqi.activity.state.RunningActivity;
import com.hztuen.shanqi.service.DownloadService;
import com.hztuen.shanqi.widget.CustomTextView;
import com.hztuen.util.MyLogUtil;
import com.hztuen.util.SensorEventHelper;
import com.hztuen.util.StringUtil;
import com.hztuen.util.WalkRouteOverlay;
import com.hztuen.zxing.CaptureActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

import static com.hztuen.shanqi.MyApplication.sn;
import static com.hztuen.util.StringUtil.getCountdownTime;
import static java.lang.System.currentTimeMillis;


/**
 * 主界面
 */

public class MainActivity extends BaseAppComActivity implements LocationSource, AMapLocationListener, AMap.OnInfoWindowClickListener, RouteSearch.OnRouteSearchListener, AMap.OnMarkerClickListener, AMap.OnMapClickListener, View.OnClickListener {



    private double falselatitude=0;
    private double falselongitude=0;
    private boolean isMyFirst=true;

    //车辆详情弹出框布局
    private LinearLayout layoutBikeDetail;

    //高德地图相关
    private MapView mMapView;
    private AMap aMap;
    private UiSettings mUiSettings;//定义一个UiSettings对象

    //寻路
    private RouteSearch routeSearch;

    private android.view.animation.Animation loadAnimation;
    private CameraPosition lastCameraPosition;
    private String mUid = "";//点击车辆（marker）的bikeId

    //是不是在地图上选择车辆中？（绘制路径）
    private boolean isChose = false;
    //在“第一次”定位时绘制方向传感器marker，并把视角移动到定位点
    private boolean isFirst = true;
    //大头针 当前位置的坐标
    private double screenMarkerX;
    private double screenMarkerY;
    private LatLng screenMarkerLatLng;
    //最新定位坐标
    private double currentLocationX;
    private double currentLocationY;
    private AMapLocation currentLocation;

    //倒计时类
    private CountDownTimer timer;
    //电动单车使用说明布局
    private LinearLayout linearLayout3;
    private TextView tvTop;
    //当前地图上 所有的单车图标
    private List<Marker> mMarkerList;
    private Marker mLocMarker;

    /**
     * 进度条初始化
     */
    private ProgressDialog mProgressDialog;
    //高德地图方向传感器Marker
    private SensorEventHelper mSensorHelper;


    private DownloadService.DownloadBinder downloadBinder;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            MyLogUtil.i("onServiceDisconnected", "onServiceDisconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isMyFirst=true;
        setContentView(R.layout.activity_main);
        initMainUI();//初始化主布局
        initBikeDetail();//初始化车辆详情弹框
        initAmap(savedInstanceState);//初始化高德地图部分
    }

    /**
     * Activity显示处于栈顶
     */
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        MyLogUtil.i("onResume");
        mMapView.onResume();

        //方向传感器
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        } else {
            mSensorHelper = new SensorEventHelper(this);
            if (mSensorHelper != null) {
                mSensorHelper.registerSensorListener();

                if (mSensorHelper.getCurrentMarker() == null && mLocMarker != null) {
                    mSensorHelper.setCurrentMarker(mLocMarker);
                }
            }
        }


        MyApplication.isFromMain = false;

        //设置退出后 重新改变状态
        if (MyApplication.isFromSetting) {
            isShowCarDetail(false);
            isAppointment(false);
            isChose = false;
            isFirst = true;
            MyApplication.isAppointment = false;
            //
            addMarkerInScreenCenter();
//            getNewMarker(screenMarkerX, screenMarkerY);
            MyApplication.isFromSetting = false;
        }

        //通过判断订单（orderStatus）不同的状态来选择跳转的界面
        //cycling:骑行中→→骑行中界面
        //appointed:预约中→→显示预约菜单
        //temporaryLock:临时锁车状态→→骑行中界面

        if ("cycling".equals(MyApplication.orderStatus)) {
            Intent mIntent = new Intent(MainActivity.this, RunningActivity.class);
            startActivity(mIntent);
            finish();
        } else if ("appointed".equals(MyApplication.orderStatus)) {
            MyApplication.isAppointment = true;
            isShowCarDetail(true);
            isAppointment(true);
            addMarkerInScreenCenter();
            getOrderDetails();
        } else if ("temporaryLock".equals(MyApplication.orderStatus)) {
            Intent mIntent = new Intent(MainActivity.this, RunningActivity.class);
            startActivity(mIntent);
            finish();
        }
        //通过判断订单状态来选择入口状态
        //wait:有订单未支付→→订单支付界面
        if (MyApplication.isLogin){
            linearLayout3.setVisibility(View.GONE);
        }
        if (MyApplication.isLogin && MyApplication.paymentStatus.equals("free")) {
            linearLayout3.setVisibility(View.GONE);
        } else if (MyApplication.paymentStatus.equals("wait")) {
            linearLayout3.setVisibility(View.VISIBLE);
            tvTop.setText(R.string.You_have_an_order_not_paid);
            //跳转到支付界面
            linearLayout3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(MainActivity.this, PayActivity.class);
                    startActivity(mIntent);
                    MyApplication.isFromMain = true;
                    finish();
                }
            });

        } else if (!MyApplication.isLogin) {
            linearLayout3.setVisibility(View.VISIBLE);
            linearLayout3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(MainActivity.this, WebViewActivity.class);
                    startActivity(mIntent);


                }
            });

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentMarker(null);
            mSensorHelper = null;
        }
        mMapView.onPause();
    }

    /**
     * Activity销毁
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();

        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }

        if (timer != null) {
            timer.cancel();
        }

    }


    public void zzz(View v) {
//        Intent intent = new Intent(MainActivity.this, TestActivity.class);
//        startActivity(intent);
    }

    //初始化高德地图部分
    private void initAmap(Bundle savedInstanceState) {
        mMarkerList = new ArrayList<>();
        mSensorHelper = new SensorEventHelper(this);
        mSensorHelper.registerSensorListener();

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mMapView.onCreate(savedInstanceState);
        //初始化地图变量
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        // 设置定位监听
        aMap.setLocationSource(this);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        // 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种
//        aMap.setMyLocationType(AMap.LOCATION_TYPE_);
        //设置地图类型
//        aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 设置常规地图模式
        mUiSettings = aMap.getUiSettings();//实例化UiSettings类
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setScaleControlsEnabled(false);//显示比例尺控件
        mUiSettings.setGestureScaleByMapCenter(true);//缩放在中点
        mUiSettings.setTiltGesturesEnabled(false);//打开倾斜手势
        mUiSettings.setCompassEnabled(false);//设置指南针

        aMap.setMyLocationEnabled(true);
        mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);//设置高德地图Logo的位置
        aMap.setOnMapClickListener(this);
        //寻路的监听
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);
        //初始化query对象，fromAndTo是包含起终点信息，walkMode是步行路径规划的模式
        //监听 地图摄像头移动的时候
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                MyLogUtil.i("移动", "我正在移动！！！！！！");
//               if(ivRefresh.getVisibility()==View.VISIBLE){
//                   ivRefresh.setVisibility(View.GONE);
//               }
            }

            @Override
            public void onCameraChangeFinish(CameraPosition position) {

//                ivRefresh.setVisibility(View.VISIBLE);//
//                if (loadAnimation == null) {
//                    loadAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate);
//                }
//                ivRefresh.startAnimation(loadAnimation);

                if (canGetNewMarkers(position)) {
                    if (!MyApplication.isAppointment) {
                        //屏幕中心的Marker跳动
                        if (!isChose) {//marker是否被选中(处于绘制路径状态)

                            screenMarkerLatLng = position.target;//获取大头针的经纬度
                            screenMarkerX = screenMarkerLatLng.latitude;
                            screenMarkerY = screenMarkerLatLng.longitude;
                            //从大头针附近获取最新的车辆
//                            removeAllBikeMarker();
                            getNewMarker(screenMarkerX, screenMarkerY);
                        }
                    }

                }
                //获得最新的定位信息
                lastCameraPosition = position;
            }
        });

        //加载地图的时候 设置大头针在屏幕中间
        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                addMarkerInScreenCenter();
            }
        });
        //注册marker 的点击事件
        aMap.setOnMarkerClickListener(this);
    }

    //是否需要重新获得 新的单车
    private boolean canGetNewMarkers(CameraPosition position) {

        // 在经线上，相差一纬度约为111km
        // 在纬线上，相差一经度约为111cosα（α为该纬线的纬度）
        //通过比较两次经纬度
        double resultxy = 10001;
        if (lastCameraPosition != null) {
            //纬度差
            double x = position.target.latitude - lastCameraPosition.target.latitude;
            //经度差
            double y = position.target.longitude - lastCameraPosition.target.longitude;
            resultxy = (x * 111 * 1000) * (x * 111 * 1000) + (y * 111 * Math.cos(x) * 1000) * (y * 111 * Math.cos(x) * 1000);
        }

        if (resultxy > 100) {
            return true;
        } else {
            return false;
        }


    }

    private void addMarker(LatLng latlng) {
        if (mLocMarker != null) {
            return;
        }
        Bitmap bMap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.myseat);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);

//		BitmapDescriptor des = BitmapDescriptorFactory.fromResource(R.mipmap.myseat);
        MarkerOptions options = new MarkerOptions();
        options.icon(des);
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocMarker = aMap.addMarker(options);
        mLocMarker.setClickable(false);
        addCircle(new LatLng(currentLocationX, currentLocationY), currentLocation.getAccuracy());
//        mLocMarker.setTitle(LOCATION_MARKER_FLAG);
    }

    private ImageView ivRefresh, ivGoto, ivKefu;//左下角两个菜单按钮以及右下角的 客服按钮

    /**
     * 初始化主布局的内容
     */
    private void initMainUI() {
        //3个圆形按钮
        ivRefresh = (ImageView) findViewById(R.id.ivRefresh);
        ivGoto = (ImageView) findViewById(R.id.ivGoto);
        ivKefu = (ImageView) findViewById(R.id.ivKefu);

        tvTop = (TextView) findViewById(R.id.tvTop);
        //拦截标题栏的点击事件
        findViewById(R.id.linearLayout).setOnClickListener(this);
        linearLayout3 = (LinearLayout) findViewById(R.id.linearLayout3);

        ivRefresh.setOnClickListener(this);
        ivKefu.setOnClickListener(this);
        ivGoto.setOnClickListener(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在开锁...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);

    }

    private CustomTextView tvDetail01;
    private CustomTextView tvDetail02;
    private CustomTextView tvDetail03, tvTime;
    private TextView tvPosition;
    private Button btAppointment, btAppointmentCancel;
    private LinearLayout layoutDetailAppointment;


    //初始化车辆细节菜单
    private void initBikeDetail() {

        //车辆详情弹出框
        layoutBikeDetail = (LinearLayout) findViewById(R.id.layoutBikeDetail);
        //取消预约的布局
        layoutDetailAppointment = (LinearLayout) findViewById(R.id.layoutDetailAppointment);

        //车辆细节3个参数
        tvDetail01 = (CustomTextView) findViewById(R.id.tvDetail01);
        tvDetail02 = (CustomTextView) findViewById(R.id.tvDetail02);
        tvDetail03 = (CustomTextView) findViewById(R.id.tvDetail03);

        //倒计时时间
        tvTime = (CustomTextView) findViewById(R.id.tvTime);

        //车辆定位位置
        tvPosition = (TextView) findViewById(R.id.tvPosition);

        //预约、取消预约按钮
        btAppointment = (Button) findViewById(R.id.btAppointment);
        btAppointmentCancel = (Button) findViewById(R.id.btAppointmentCancel);

        //取消预约、预约按钮的点击事件
        btAppointmentCancel.setOnClickListener(this);
        btAppointment.setOnClickListener(this);
        layoutBikeDetail.setOnClickListener(this);
    }


    /**
     * "我的"按钮
     * 判断逻辑：
     * 判断用户有无登录
     * Y：跳转到已登录界面
     * N：跳转到未登录界面
     */
    public void myClick(View v) {
        Intent mIntent;
        //判断登录状态跳转到不同的界面
        if (MyApplication.isLogin) {
            //跳转到我的界面
            mIntent = new Intent(MainActivity.this, PersonalCenterActivity.class);
        } else {
            //跳转登录界面
            mIntent = new Intent(MainActivity.this, RegisterActivity01.class);
        }
        startActivity(mIntent);
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }



    /**
     * "扫码"按钮
     * 判断逻辑：
     * 判断用户有无登录
     * N：跳转到未登录界面
     * Y：→→→→→→→
     * 判断有无交押金
     * N：跳转到交押金界面
     * Y：→→→→→→→
     * 判断有无实名认证
     * N：跳转到实名认证界面
     * Y：→→→→→→→
     * 判断有无余额
     * N：跳转到充值界面
     * Y：→→→→→→→
     * 判断用户是否正在骑行中....
     * N:跳转到、。。。。。。
     * 逻辑能整理好再让我写代码？？？？
     */
    public void scanCode(View v) {
        //权限查询
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)  != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                //申请相机权限
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 10010);
            } else {
                //申请相机权限
                ActivityCompat.requestPermissions(this,  new String[]{Manifest.permission.CAMERA}, 10010);
            }
        } else {
            toCamera();
        }
    }


    private void toCamera(){
        Intent mIntent;
        if (MyApplication.isLogin) {
            //已经交押金
            if (MyApplication.isDeposit) {
                //已经实名
                if (MyApplication.isIdentity) {
                    //有余额
                    if (MyApplication.myMoney > 0) {
                        //扫码
                        mIntent = new Intent(MainActivity.this, CaptureActivity.class);
                        startActivityForResult(mIntent, 300);
                    } else {
                        //余额不足
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(R.string.confirm);
                        builder.setMessage(R.string.credit_is_running_low);
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //访问网络再弹出一个Dialog
                                //充值界面
                                Intent mIntent = new Intent(MainActivity.this, MyWalletActivity.class);
                                startActivity(mIntent);
                            }


                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                } else {
                    //实名认证
                    mIntent = new Intent(MainActivity.this, RegisterActivity04.class);
                    startActivity(mIntent);
                }

            } else {
                //交押金界面
                mIntent = new Intent(MainActivity.this, RegisterActivity03.class);
                startActivity(mIntent);
            }
        } else {
            //跳转到登录界面
            mIntent = new Intent(MainActivity.this, RegisterActivity01.class);
            startActivity(mIntent);
        }
    }


    /**
     * Activity结果返回
     * 1、二维码扫描结果返回
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 300) {
            if (data != null) {

                String result = data.getStringExtra("result");
                MyLogUtil.i("扫码结果", "" + result);

                if (result != null && !"result".equals("")) {

//                    String result2 = StringUtil.parseUrlString(result);

//                    if ("".equals(result2)) {
//                        Toast.makeText(this, "扫描的二维码错误", Toast.LENGTH_SHORT).show();
//                    } else {
                        openCar(result);
//                    }
                }
            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mMapView.onSaveInstanceState(outState);
    }

    private long firstBackTime = 0;

    /**
     * 返回按钮的监听
     */

    @Override
    public void onBackPressed() {
        long SecondBackTime = currentTimeMillis();//获取当前时间
        boolean isFirstClickBack = true;
        if (SecondBackTime - firstBackTime < 2000) {
            isFirstClickBack = false;
            firstBackTime = currentTimeMillis();
        } else {
            isFirstClickBack = true;
            firstBackTime = currentTimeMillis();
        }
        if (isFirstClickBack) {
            Toast.makeText(this, "再按一次退出闪骑", Toast.LENGTH_SHORT).show();
        } else {
            MainActivity.this.finish();
        }
    }


    //接口LocationSource 的两个方法
    OnLocationChangedListener mListener;
    AMapLocationClient mlocationClient;
    AMapLocationClientOption mLocationOption;

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            //初始化定位
            mlocationClient = new AMapLocationClient(this);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();//启动定位

        }
    }

    //停止定位
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /**
     * AMapLocationListener实现的接口（定位监听）
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {

        if(isMyFirst){
            falselatitude = amapLocation.getLatitude();
            falselongitude = amapLocation.getLongitude();
            isMyFirst=false;
        }


        if (mListener != null && amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //重新赋值当前位置的坐标
                currentLocationX = amapLocation.getLatitude();
                currentLocationY = amapLocation.getLongitude();
                currentLocation = amapLocation;
                //
                LatLng location = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                MyApplication.mLocation = location;

                if (isFirst) {
                    isFirst = false;
                    //把地图视角移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                            new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()),//新的中心点坐标
                            16, //新的缩放级别
                            0, //俯仰角0°~45°（垂直与地图时为0）
                            0  ////偏航角 0~360° (正北方为0)
                    )));
                    addMarkerInScreenCenter();
                    addMarker(location);//添加定位图标
                    mSensorHelper.setCurrentMarker(mLocMarker);//定位图标旋转
                }

                //每次刷新定位坐标 重新绘制定位marker
                if (mLocMarker != null) {
                    mLocMarker.setPosition(location);
                    addCircle(new LatLng(currentLocationX, currentLocationY), currentLocation.getAccuracy());
                }


            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                MyLogUtil.i("定位失败", errText);
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    //路径所有RouteSearch.OnRouteSearchListener 的4个接口
    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    private WalkRouteOverlay walkRouteOverlay;

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {


        //如果不在预约状态下，需要清除原来的单车marker
        if (!MyApplication.isAppointment) {

            //需要清除原来的轨迹
            screenMarker.remove();
            //绘制固定的大头针
            drawFixedMarker();
            //绘制原来的单车
            reDrawAllBikeMarker();
        }


        //绘制路径
        final WalkPath walkPath = walkRouteResult.getPaths().get(0);

        walkRouteOverlay = new WalkRouteOverlay(this, aMap, walkPath, walkRouteResult.getStartPos(), walkRouteResult.getTargetPos());
        walkRouteOverlay.removeFromMap();
        walkRouteOverlay.addToMap();
        walkRouteOverlay.zoomToSpan();
        //把镜头移动到两点之间
        //绘制不能动的大头针

    }


    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }


    /**
     * 标记点的点击事件
     */
    @Override
    public boolean onMarkerClick(Marker marker) {

        if (!MyApplication.isAppointment) {
            //弹出详情菜单
            isShowCarDetail(true);
            if (walkRouteOverlay != null) {
                walkRouteOverlay.removeFromMap();
            }
            isChose = true;
            screenMarker.setPosition(screenMarkerLatLng);
            LatLonPoint mStartPoint = new LatLonPoint(screenMarkerX, screenMarkerY);//起点
            LatLonPoint mEndPoint = new LatLonPoint(marker.getPosition().latitude, marker.getPosition().longitude);//终点
            final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint, mEndPoint);
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, RouteSearch.WalkDefault);
            routeSearch.calculateWalkRouteAsyn(query);//开始算路
            getAddress(new LatLonPoint(marker.getPosition().latitude, marker.getPosition().longitude));
            mUid = marker.getTitle();
            getCarDetail();//获取车辆详情
        }
        return true;
    }



    /**
     * 地图的单击事件，点击marker时事件会被拦截
     */
    @Override
    public void onMapClick(LatLng latLng) {


        if (lastCameraPosition != null) {
            screenMarkerLatLng = lastCameraPosition.target;
            screenMarkerX = screenMarkerLatLng.latitude;
            screenMarkerY = screenMarkerLatLng.longitude;
        }
        if (!MyApplication.isAppointment) {
            //如果车辆细节显示则隐藏
            if (layoutBikeDetail.getVisibility() == View.VISIBLE) {
                layoutBikeDetail.setVisibility(View.GONE);
                isChose = false;
                //重新获取地图上的点
                if (fixedMarker != null) {
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(fixedMarker.getPosition(), 1000));
                }
                addMarkerInScreenCenter();
                //重新获取新的点
                getNewMarker(screenMarkerX, screenMarkerY);
            }
        }
    }

    /**
     * View的点击事件
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            //客服按钮
            case R.id.ivKefu:
                Intent mIntent = new Intent(MainActivity.this, ProblemActivity.class);
                startActivity(mIntent);
                break;
            //定位到当前位置
            case R.id.ivGoto:
                if (!MyApplication.isAppointment) {
                    reposition();
                }
                break;
            //刷新按钮
            case R.id.ivRefresh:
                if (!MyApplication.isAppointment) {
                    refreshMarker();
                }
                break;


            //取消预约按钮
            case R.id.btAppointmentCancel:
                cancelLation();
                break;
            //预约按钮
            case R.id.btAppointment:

                if (!"".equals(mUid)) {
                    if (MyApplication.isLogin) {
                        if (MyApplication.myMoney < 0) {
                            Intent mIntent2 = new Intent(MainActivity.this, MyWalletActivity.class);
                            startActivity(mIntent2);
                        } else {
                            appointment();
                        }

                    } else {
                        Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                    }

                }

                break;
            //把预约布局点击事件拦截掉
            case R.id.layoutDetailAppointment:
                break;
            //把标题栏的点击事件拦截掉
            case R.id.linearLayout:
                break;

        }

    }
    ///////////////////////实现的方法//////////////////////////////////////

    /**
     * 重新定位按钮方法
     */
    private void reposition() {
        isShowCarDetail(false);

        isFirst = false;
        isChose = false;
        //从定位的位置重新获得新的坐标
        addMarkerInScreenCenter();
        getNewMarker(currentLocationX, currentLocationY);
        //把视角移动到当前位置
        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                new LatLng(currentLocationX, currentLocationY),//新的中心点坐标
                16, //新的缩放级别
                0, //俯仰角0°~45°（垂直与地图时为0）
                0  ////偏航角 0~360° (正北方为0)
        )));

    }

    /**
     * 刷新按钮方法
     */
    private void refreshMarker() {
        isFirst = false;
        isChose = false;
        screenMarkerLatLng = lastCameraPosition.target;
        screenMarkerX = screenMarkerLatLng.latitude;
        screenMarkerY = screenMarkerLatLng.longitude;
        isShowCarDetail(false);
        addMarkerInScreenCenter();
        //让他转起来
        if (loadAnimation == null) {
            loadAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate);
        }
        ivRefresh.startAnimation(loadAnimation);

        //从大头针 附近获取最新的车辆
        getNewMarker(screenMarkerX, screenMarkerY);

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //可在此继续其他操作。
        if (requestCode == 10011) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "文件权限已申请", Toast.LENGTH_SHORT).show();

            } else {
                //用户勾选了不再询问
                //提示用户手动打开权限
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                    Toast.makeText(this, "文件权限已申请", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "拒绝权限将无法更新", Toast.LENGTH_SHORT).show();
//                finish();
            }
        }
    }



    //dip和px转换
    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * 屏幕中心marker 跳动
     */
    public void startJumpAnimation() {

        MyLogUtil.i("我在跳动...");
        if (screenMarker != null) {
            //根据屏幕距离计算需要移动的目标点
            final LatLng latLng = screenMarker.getPosition();
            Point point = aMap.getProjection().toScreenLocation(latLng);
            point.y -= dip2px(this, 80);
            LatLng target = aMap.getProjection()
                    .fromScreenLocation(point);
            //使用TranslateAnimation,填写一个需要移动的目标点
            Animation animation = new TranslateAnimation(target);
            animation.setInterpolator(new Interpolator() {
                @Override
                public float getInterpolation(float input) {
                    // 模拟重加速度的interpolator
                    if (input <= 0.5) {
                        return (float) (0.5f - 2 * (0.5 - input) * (0.5 - input));
                    } else {
                        return (float) (0.5f - Math.sqrt((input - 0.5f) * (1.5f - input)));
                    }
                }
            });
            //整个移动所需要的时间
            animation.setDuration(500);
            //设置动画
            screenMarker.setAnimation(animation);
            //开始动画
            screenMarker.startAnimation();

        } else {
            Log.e("ama", "screenMarker is null");
        }
    }


    private GeocodeSearch geocoderSearch;

    /**
     * 响应逆地理编码，坐标转中文
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

                String result = regeocodeResult.getRegeocodeAddress().getFormatAddress();

//                String position = result.substring(6, result.length());

                tvPosition.setText(StringUtil.getAddress(result));
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });
//        showDialog();
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
    }


    /**
     * 是否显示车辆菜单
     */
    private void isShowCarDetail(boolean isShowCarDetail) {

        if (isShowCarDetail) {
            layoutBikeDetail.setVisibility(View.VISIBLE);
        } else {
            layoutBikeDetail.setVisibility(View.GONE);
        }
    }


    /**
     * 是否在预约中 菜单显示的内容不同
     */
    private void isAppointment(boolean isAppointment) {

        if (isAppointment) {
            layoutDetailAppointment.setVisibility(View.VISIBLE);
            btAppointment.setVisibility(View.GONE);
        } else {
            layoutDetailAppointment.setVisibility(View.GONE);
            btAppointment.setVisibility(View.VISIBLE);
        }
    }

    /////////////////////////////////////画Marker//////////////////////////////////////////////

    /**
     * 画一辆自行车Marker
     */

    private Marker aBikeMarker;

    private void drawABikeMarker(double latitude, double longitude) {
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(new LatLng(latitude, longitude));
        markerOption.draggable(true);
        markerOption.icon(BitmapDescriptorFactory.fromResource(R.mipmap.bike));
        // 将Marker设置为贴地显示，可以双指下拉看效果
        markerOption.setFlat(false);
        markerOption.anchor(0.5f, 0.5f);
        if (aBikeMarker != null) {
            aBikeMarker.remove();
        }
        aBikeMarker = aMap.addMarker(markerOption);
        aBikeMarker.setClickable(false);
    }


    /**
     * 给地图设置精度范围
     */
    private Circle mCircle;

    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(Color.argb(10, 0, 0, 180));
        options.strokeColor(Color.argb(180, 3, 145, 255));
        options.center(latlng);
        options.radius(radius);
        if (mCircle != null) {
            mCircle.remove();
        }
        mCircle = aMap.addCircle(options);
    }

    /**
     * 在屏幕中心添加一个跟随屏幕移动的大头针
     */
    private Marker screenMarker;

    private void addMarkerInScreenCenter() {
        LatLng latLng = aMap.getCameraPosition().target;
        Point screenPosition = aMap.getProjection().toScreenLocation(latLng);

        if (screenMarker != null) {
            screenMarker.remove();
        }

        screenMarker = aMap.
                addMarker(new MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.image_me)));
        screenMarker.setPositionByPixels(screenPosition.x, screenPosition.y);
        screenMarker.setClickable(false);
    }


    /**
     * 画一个不会跳动
     * 且不会跟随屏幕移动的大头针
     */

    private Marker fixedMarker;

    private void drawFixedMarker() {
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(screenMarkerLatLng);
        markerOption.draggable(true);
        markerOption.icon(BitmapDescriptorFactory.fromResource(R.mipmap.image_me));
        // 将Marker设置为贴地显示，可以双指下拉看效果
        markerOption.setFlat(false);
        markerOption.anchor(0.5f, 0.5f);

        if (fixedMarker != null) {
            fixedMarker.remove();
        }

        fixedMarker = aMap.addMarker(markerOption);
        fixedMarker.setClickable(false);
    }

    /**
     * 移除所有的单车Marker
     */
    private void removeAllBikeMarker() {

        for (int i = 0; i < mMarkerList.size(); i++) {
            mMarkerList.get(i).remove();
        }
    }

    /**
     * 把所有单车marker重新绘制回来
     */
    private void reDrawAllBikeMarker() {

        for (int i = 0; i < mMarkerList.size(); i++) {
            mMarkerList.get(i).remove();
            mMarkerList.set(i, aMap.addMarker(mMarkerList.get(i).getOptions()));
        }

    }
    /////////////////////////////////////接口请求数据/////////////////////////////////////////////////

    /**
     * 获取订单详情
     * token登录进来的时候如果有预约车辆，将调用这个接口
     * 1、绘制预约车辆到的起点的路径
     */
    private void getOrderDetails() {

        String sign = "";
        List<String> attributes = new ArrayList<>();
        attributes.add("sn=" + sn);
        try {
            sign = StringUtil.sign(attributes);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        OkHttpUtils.
                post()
                .url(AppUrl.GET_ORDER_INFO)
                .addParams("sn", sn)
                .addParams("sign", sign)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(MainActivity.this, R.string.http_error, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        MyLogUtil.i("主界面获取订单状态:", response + "");

                        try {
                            JSONObject result = new JSONObject(response);
                            String resultCode = result.getString("resultCode");
                            if ("200".equals(resultCode)) {
                                JSONObject resultContent = result.getJSONObject("resultContent");
                                String bikeId = resultContent.getString("bikeId");

                                //获得预约车辆的三个参数
                                String distance = resultContent.getString("distance");
                                String price = resultContent.getString("price");
                                String electricity = resultContent.getString("electricity");

                                //得到预约车辆坐标
                                double longitude = resultContent.getDouble("longitude");
                                double latitude = resultContent.getDouble("latitude");

                                //设置预约车辆的三个参数
                                tvDetail01.setText(price);
                                tvDetail02.setText(electricity);
                                tvDetail03.setText(distance);


                                long getTime = resultContent.getLong("getTime");
                                JSONObject payRule = resultContent.getJSONObject("payRule");
                                int freeAppointed = payRule.getInt("freeAppointed");

                                //计算已经经过的时间
                                long time = System.currentTimeMillis() - getTime;
                                long lTime = freeAppointed * 1000 * 60 - time;
                                //车辆预约时间倒计时开始
                                if (timer != null) {
                                    timer.cancel();
                                }
                                timer = new CountDownTimer(lTime, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        //动态更新textView里面的内容
                                        //获得倒计时时间
                                        tvTime.setText(getCountdownTime(millisUntilFinished));
                                    }

                                    @Override
                                    public void onFinish() {
                                        //倒计时结束时操作
                                        //取消预约状态
                                        //并重新获取附近车辆
                                        MyLogUtil.i("计时结束");
                                        MyApplication.orderStatus = "free";
                                        MyApplication.isAppointment = false;
                                        isChose = false;
                                        isAppointment(false);
                                        mUid = "";
                                        isShowCarDetail(false);//关闭车辆详情

                                        //添加可跳动的大头针
                                        addMarkerInScreenCenter();
                                        //绘制旧的点或者重新获取新的点
                                        getNewMarker(screenMarkerX, screenMarkerY);
                                        Toast.makeText(MainActivity.this, "预约时间已到", Toast.LENGTH_SHORT).show();
                                    }
                                };
                                timer.start();

                                if (walkRouteOverlay != null) {
                                    walkRouteOverlay.removeFromMap();
                                }

                                if (aBikeMarker != null) {
                                    aBikeMarker.remove();
                                }
                                //开启自动寻路
                                LatLonPoint mStartPoint = new LatLonPoint(currentLocationX, currentLocationY);//起点
                                LatLonPoint mEndPoint = new LatLonPoint(latitude, longitude);//终点
                                final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint, mEndPoint);
                                RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, RouteSearch.WalkDefault);
                                routeSearch.calculateWalkRouteAsyn(query);//开始算路
                                getAddress(new LatLonPoint(latitude, longitude));

                                drawABikeMarker(latitude, longitude);//绘制预约车辆


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 获得新的marker
     */
    private void getNewMarker(double screenMarkerX, double screenMarkerY) {
        //由于是重新获得marker 所以没有的marker不管是否存在都将它从地图移除
        //路径
        if (walkRouteOverlay != null) {
            walkRouteOverlay.removeFromMap();
        }
        //固定的大头针
        if (fixedMarker != null) {
            fixedMarker.remove();
        }
        //token登录时绘制的预约车辆
        if (aBikeMarker != null) {
            aBikeMarker.remove();
        }

        //大头针跳动动画
        startJumpAnimation();

        //使用假数据
        removeAllBikeMarker();
        mMarkerList.clear();
        double a=0.001;
        createMarker(falselatitude+a,falselongitude);
        createMarker(falselatitude,falselongitude+a);
        createMarker(falselatitude+a,falselongitude+a);
        createMarker(falselatitude-a,falselongitude);
        createMarker(falselatitude,falselongitude-a);
        createMarker(falselatitude-a,falselongitude-a);
        createMarker(falselatitude-2*a,falselongitude-2*a);
        createMarker(falselatitude+2*a,falselongitude+2*a);

    }

    private void createMarker(double x,double y) {
        LatLng latLng = new LatLng(x, y);
        //设置marker参数
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.draggable(true);
        markerOption.icon(BitmapDescriptorFactory.fromResource(R.mipmap.bike));
        markerOption.setFlat(false);
        markerOption.anchor(0.5f, 0.5f);
        markerOption.title("1");

        //在地图上将这个marker绘制出来
        Marker marker = aMap.addMarker(markerOption);
        //绘制marker显示时候的生长动画
        Animation animation = new ScaleAnimation(0, 1, 0, 1);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(500); //整个移动所需要的时间
        marker.setAnimation(animation);//设置动画
        marker.startAnimation();//开始动画
        //将单车marker保存起来
        mMarkerList.add(marker);

    }

    /**
     * 扫码开锁
     */
    private void openCar(String result) {
        mProgressDialog.setMessage("正在开锁");
        mProgressDialog.show();
        Toast.makeText(this, "开锁成功", Toast.LENGTH_SHORT).show();
        MyApplication.isAppointment = false;
        //跳转到骑行中界面
        Intent mIntent = new Intent(MainActivity.this, RunningActivity.class);
        startActivity(mIntent);
        finish();
        mProgressDialog.dismiss();
    }

    /**
     * 获取车辆详情
     */
    public void getCarDetail() {
        //设置点击车辆的三个状态
        tvDetail01.setText("100");
        tvDetail02.setText("20%");
        tvDetail03.setText("20");
    }

    /**
     * 预约车辆
     */
    private void appointment() {
        mProgressDialog.setMessage("正在预约");
        mProgressDialog.show();

        isAppointment(true);//预约成功，显示预约菜单
        //获取预约车辆的订单
        sn = "";
        MyApplication.isAppointment = true;
        //车辆预约时间倒计时开始
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(30 * 1000 * 60, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //动态更新textView里面的内容
                //获得倒计时时间
                tvTime.setText(StringUtil.getCountdownTime(millisUntilFinished));
            }
            @Override
            public void onFinish() {
                //倒计时结束时操作
                //取消预约状态
                MyLogUtil.i("计时结束");
                MyApplication.orderStatus = "free";
                MyApplication.isAppointment = false;
                isChose = false;
                isAppointment(false);
                mUid = "";
                isShowCarDetail(false);//关闭车辆详情
                //绘制能移动的大头针
                addMarkerInScreenCenter();
                //重新获取大头针附近的车辆
                getNewMarker(screenMarkerX, screenMarkerY);
                Toast.makeText(MainActivity.this, "预约时间已到", Toast.LENGTH_SHORT).show();
            }
        };
        timer.start();
        mProgressDialog.dismiss();
    }

    /***
     * 取消预约
     */
    private void cancelLation() {
        mProgressDialog.setMessage("正在取消预约");
        mProgressDialog.show();


        //取消预约倒计时
        if (timer != null) {
            timer.cancel();
        }
        //改变全局状态
        MyApplication.orderStatus = "free";
        MyApplication.isAppointment = false;
        isChose = false;

        //取消预约成功
        isAppointment(false);
        mUid = "";
        isShowCarDetail(false);//关闭车辆详情
        if (fixedMarker != null) {
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(fixedMarker.getPosition(), 1000));
        }
        //绘制能移动的大头针
        addMarkerInScreenCenter();
        //重新获取附近单车
        getNewMarker(screenMarkerX, screenMarkerY);
        mProgressDialog.dismiss();

    }

}
