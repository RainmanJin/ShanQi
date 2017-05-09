package com.hztuen.shanqi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hztuen.shanqi.MyApplication;
import com.hztuen.shanqi.R;
import com.hztuen.shanqi.activity.myself.GuideActivity;
import com.hztuen.shanqi.activity.myself.MyMessageActivity;
import com.hztuen.shanqi.activity.myself.MyWalletActivity;
import com.hztuen.shanqi.activity.myself.TripActivity;
import com.hztuen.shanqi.activity.set.SettingsActivity;
import com.hztuen.shanqi.widget.CustomTextView;
import com.hztuen.util.StatusBarUtils;

/**
 * 个人中心界面
 * 已登录已认证状态下的Activity
 * "进度条"点击事件
 * 判断逻辑：
 * 判断是否交押金：
 * N：跳转到交押金界面
 * Y：→→→→→→→→→
 * 判断有无实名认证：
 * N：跳转实名认证界面
 * Y：隐藏进度条
 */

public class PersonalCenterActivity extends BaseActivity {

    private TextView tvPhoneNum, tvDistance;
    private CustomTextView tvMyLeft, tvMyRight;
    private ImageView ivHead;
    public static PersonalCenterActivity personalCenterActivity;
    //进度条


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        personalCenterActivity = this;
        StatusBarUtils.setWindowStatusBarColor(this);
        initUI();//初始化布局
        initStepUI();//初始化进度条
    }

    private String headImg;

    /**
     * 每次都刷新一下界面中的数据
     */
    @Override
    protected void onResume() {
        super.onResume();
        getInformation();//重新获取个人资料
    }

    @Override
    protected void onDestroy() {
//        Glide.with(this).pauseRequests();
        super.onDestroy();

    }

    /**
     * 初始化进度条
     */
    private ImageView ivStep01, ivStep02, ivStep03, ivStep04;
    private View viewStep1, viewStep2, viewStep3;
    private LinearLayout layoutStep;

    private void initStepUI() {
        //4个圈
        ivStep01 = (ImageView) findViewById(R.id.ivStep1);
        ivStep02 = (ImageView) findViewById(R.id.ivStep2);
        ivStep03 = (ImageView) findViewById(R.id.ivStep3);
        ivStep04 = (ImageView) findViewById(R.id.ivStep4);
        //4条线
        viewStep1 = findViewById(R.id.viewStep1);
        viewStep2 = findViewById(R.id.viewStep2);
        viewStep3 = findViewById(R.id.viewStep3);

        layoutStep = (LinearLayout) findViewById(R.id.layoutStep);
        layoutStep.setVisibility(View.GONE);

    }

    /**
     * 初始化布局
     */
    private void initUI() {
        ivHead = (ImageView) findViewById(R.id.ivHead);
        tvMyLeft = (CustomTextView) findViewById(R.id.tvMyLeft);
        tvMyRight = (CustomTextView) findViewById(R.id.tvMyRight);
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvPhoneNum = (TextView) findViewById(R.id.tvPhoneNum);
        //返回按钮点击事件
        LinearLayout layoutLeft = (LinearLayout) findViewById(R.id.layoutLeft);
        layoutLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        });

    }

    /**
     * 个人中心跳转按钮的点击事件
     */
    public void myClick(View v) {
        switch (v.getId()) {
            //用户指南
            case R.id.layoutGuide:
                goToActivity(GuideActivity.class);
                break;
            //我的消息
            case R.id.layoutMessage:
                goToActivity(MyMessageActivity.class);
                break;
            //设置
            case R.id.layoutSettings:
                goToActivity(SettingsActivity.class);
//                    finish();
                break;
            //我的钱包
            case R.id.layoutWallet:
                goToActivity(MyWalletActivity.class);
                break;
            //我的行程
            case R.id.layoutTrip:
                goToActivity(TripActivity.class);
                break;

        }
    }


    /**
     * 跳转界面
     */
    private void goToActivity(Class mClass) {
        Intent mIntent = new Intent(PersonalCenterActivity.this, mClass);
        startActivity(mIntent);
    }

    /**
     * 返回按钮监听
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    /**
     * 获取个人资料
     */
    private void getInformation() {
        tvPhoneNum.setText("15168212330");
        tvMyRight.setText("5.0");
        tvMyLeft.setText("6.2");
        tvDistance.setText("20");//余额
        MyApplication.myMoney = 20;
    }
}
