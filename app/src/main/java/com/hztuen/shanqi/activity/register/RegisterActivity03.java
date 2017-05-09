package com.hztuen.shanqi.activity.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hztuen.shanqi.MyApplication;
import com.hztuen.shanqi.R;
import com.hztuen.shanqi.activity.BaseActivity;

/**
 * 注册/登录 充值押金界面
 */

public class RegisterActivity03 extends BaseActivity {

    public static final int WXPAY_RESULT = 3;
    public static final int WX_RESULT = 4;
    private static final int SDK_PAY_FLAG = 1;

    public static RegisterActivity03 mRegisterActivity03;

    /////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register03);
        initUI();
        initStepUI();
        mRegisterActivity03 = this;
        MyApplication.isGoWeChatDeposit = false;
    }

    /**
     * 初始化进度条界面
     */
    private void initStepUI() {
        //4个圈
        ImageView ivStep01 = (ImageView) findViewById(R.id.ivStep1);
        ImageView ivStep02 = (ImageView) findViewById(R.id.ivStep2);
        ImageView ivStep03 = (ImageView) findViewById(R.id.ivStep3);
        ImageView ivStep04 = (ImageView) findViewById(R.id.ivStep4);

        //3条线
        View viewStep1 = findViewById(R.id.viewStep1);
        View viewStep2 = findViewById(R.id.viewStep2);
        View viewStep3 = findViewById(R.id.viewStep3);
        //布局界面本事
        LinearLayout layoutStep = (LinearLayout) findViewById(R.id.layoutStep);

        //绘制进度状态
        if (MyApplication.isIdentity) {
            viewStep2.setBackgroundColor(getResources().getColor(R.color.colorText_red));
            ivStep03.setImageResource(R.mipmap.a5_step2);
        } else {
            ivStep03.setImageResource(R.mipmap.a5_step1);
        }
        viewStep1.setBackgroundColor(getResources().getColor(R.color.colorText_red));

    }

    public void pay2(View v) {
        Intent mIntent = new Intent(RegisterActivity03.this, RegisterActivity04.class);
        startActivity(mIntent);
        finish();
    }

    //微信支付
    public void weChatPay(View v) {
        Toast.makeText(RegisterActivity03.this, "微信支付成功", Toast.LENGTH_SHORT).show();
        MyApplication.isDeposit = true;
        if (MyApplication.isIdentity) {
            finish();
        } else {
            Intent mIntent = new Intent(RegisterActivity03.this, RegisterActivity04.class);
            startActivity(mIntent);
            finish();
        }
    }


    /**
     * 支付宝支付
     */
    public void aliPay(View v) {
        Toast.makeText(RegisterActivity03.this, "支付宝支付成功", Toast.LENGTH_SHORT).show();
        MyApplication.isDeposit = true;
        if (MyApplication.isIdentity) {
            finish();
        } else {
            Intent mIntent = new Intent(RegisterActivity03.this, RegisterActivity04.class);
            startActivity(mIntent);
            finish();
        }

    }

    /**
     * 初始化界面
     */
    private void initUI() {
        //设置标题
        TextView tvHeadTitle = (TextView) findViewById(R.id.tvHeadTitle);
        tvHeadTitle.setText(R.string.prepaid_deposit);
        LinearLayout layoutLeft = (LinearLayout) findViewById(R.id.layoutLeft);
        layoutLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
