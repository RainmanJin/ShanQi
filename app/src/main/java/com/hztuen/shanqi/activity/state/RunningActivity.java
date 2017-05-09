package com.hztuen.shanqi.activity.state;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hztuen.shanqi.MyApplication;
import com.hztuen.shanqi.R;
import com.hztuen.shanqi.activity.BaseActivity;
import com.hztuen.shanqi.activity.PersonalCenterActivity;
import com.hztuen.shanqi.widget.CustomTextView;

import static java.lang.System.currentTimeMillis;

/**
 * 骑行界面
 * 正在行驶的状态
 */

public class RunningActivity extends BaseActivity {

    private CustomTextView tvMoney;
    private CustomTextView tvMileage;
    private CustomTextView tvTime;
    private Button btReturn01;
    private Button btReturn02;

    private boolean isRun = false;

    private CountDownTimer timer;//倒计时类
    private int costOfOneHour = 1;

    TextView tv2;

    public static RunningActivity runningActivity;

    private  long firstTime;


    private int time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
        time=1;
//
        firstTime=currentTimeMillis();
        runningActivity = this;
        initUI();
//        //
        initDialog();

        //定时60s 每10s刷新一次
        timer = new CountDownTimer(60000, 10000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time++;
                //不断刷新参数
                tvMoney.setText(time*0.5+"");
                tvMileage.setText(time*0.3+"");
                tvTime.setText("" + time + "");
            }

            @Override
            public void onFinish() {
                //倒计时结束时操作
                timer.start();
            }
        };

        if (MyApplication.orderStatus.equals("temporaryLock")) {
            btReturn01.setText("解锁");
            tv2.setVisibility(View.VISIBLE);
        } else {
            btReturn01.setText("锁车");
            tv2.setVisibility(View.INVISIBLE);
        }
        timer.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        getOrderDetails(); //获取订单详情状态
    }

    @Override
    protected void onPause() {
        super.onPause();
//        timer.cancel();
    }

    @Override
    protected void onDestroy() {
        if (null != timer) timer.cancel();
        super.onDestroy();
    }

    private ProgressDialog mProgressDialog;

    //初始化Dialog
    private void initDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("还车中请稍后...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
    }


    private void initUI() {
        isRun = true;
        LinearLayout layoutLeft = (LinearLayout) findViewById(R.id.layoutLeft);
//        layoutLeft.setVisibility(View.INVISIBLE);
        layoutLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mIntent = new Intent(RunningActivity.this, PersonalCenterActivity.class);
                startActivity(mIntent);
//                finish();
//                MyApplication.isFromRunning=true;
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        });
        tvMoney = (CustomTextView) findViewById(R.id.tvMoney);
        tvMileage = (CustomTextView) findViewById(R.id.tvMileage);
        tvTime = (CustomTextView) findViewById(R.id.tvTime);
        //还车
        btReturn02 = (Button) findViewById(R.id.btReturn02);
        btReturn02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //还车接口
                lockCar();
            }
        });

        btReturn01 = (Button) findViewById(R.id.btReturn01);
        btReturn01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //临时还车
//                temporaryCar();
                if (MyApplication.orderStatus.equals("temporaryLock")) {
                    temporaryCar();
                } else {
                    temporaryLockCar();
                }

            }
        });
        TextView tvHeadTitle = (TextView) findViewById(R.id.tvHeadTitle);
        tvHeadTitle.setText(R.string.running);
        tv2 = (TextView) findViewById(R.id.tv2);

    }




    /**
     * 还车
     */
    private void lockCar() {
        Intent mIntent = new Intent(RunningActivity.this, PayActivity.class);
        mIntent.putExtra("amount", "50");
        mIntent.putExtra("sn", "");
        mIntent.putExtra("couponAmount", "");
        mIntent.putExtra("couponCode", "");
        mIntent.putExtra("minutes", "");
        mIntent.putExtra("costOfOneHour", "");
        startActivity(mIntent);
        finish();
    }




    /**
     * 临时锁车
     */
    private void temporaryLockCar() {
//        Toast.makeText(RunningActivity.this, resultMsg, Toast.LENGTH_SHORT).show();
        MyApplication.orderStatus = "temporaryLock";
        btReturn01.setText("解锁");
        tv2.setVisibility(View.VISIBLE);
    }

    /**
     * 临时开锁
     */
    private void temporaryCar() {
        MyApplication.orderStatus = "cycling";
        btReturn01.setText("临时锁车");
        tv2.setVisibility(View.INVISIBLE);
    }


}
