package com.hztuen.shanqi.activity.state;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hztuen.shanqi.MyApplication;
import com.hztuen.shanqi.R;
import com.hztuen.shanqi.activity.BaseActivity;
import com.hztuen.shanqi.activity.CouponActivity;
import com.hztuen.shanqi.activity.MainActivity;
import com.hztuen.shanqi.activity.ProblemActivity;
import com.hztuen.shanqi.widget.CustomTextView;

/**
 * 骑行结束支付状态
 */

public class PayActivity extends BaseActivity {
    TextView tvRule;
    TextView tvMoney2;
    TextView tvCoupon, tvCouponAmount;
    CustomTextView tvMoney;
    String minutes = "";
    String sn = "";
    double amount = 0;//支付金额
    String couponAmount = "";//优惠券金额
    double mCouponAmount = 0;//优惠券金额

    String code = "";//优惠券code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        MyApplication.isFromPayActivity = true;
        initUI();
        initHeadUI();//初始化 标题栏布局
        initDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    //初始化布局
    private void initUI() {
        tvMoney = (CustomTextView) findViewById(R.id.tvMoney);
        tvMoney2 = (TextView) findViewById(R.id.tvMoney2);
        tvRule = (TextView) findViewById(R.id.tvRule);
        tvCoupon = (TextView) findViewById(R.id.tvCoupon);
        tvCouponAmount = (TextView) findViewById(R.id.tvCouponAmount);
        tvCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(PayActivity.this, CouponActivity.class);
                startActivityForResult(mIntent, 10086);
            }
        });



    }

    //初始化 标题栏布局
    private void initHeadUI() {

        LinearLayout layoutLeft = (LinearLayout) findViewById(R.id.layoutLeft);
        layoutLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.paymentStatus = "wait";
                Intent mIntent = new Intent(PayActivity.this, MainActivity.class);
                startActivity(mIntent);
                finish();
            }
        });

        TextView tvHeadTitle = (TextView) findViewById(R.id.tvHeadTitle);
        tvHeadTitle.setText(R.string.order_payment);

        TextView tvRight = (TextView) findViewById(R.id.tvRight);
        tvRight.setText(R.string.problem_feedback);

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mIntent = new Intent(PayActivity.this, ProblemActivity.class);
                startActivity(mIntent);

            }
        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyApplication.paymentStatus = "wait";
        Intent mIntent = new Intent(PayActivity.this, MainActivity.class);
        startActivity(mIntent);
    }


    //初始化Dialog
    private ProgressDialog mProgressDialog;

    private void initDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("还车中请稍后...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //从优惠券界面返回优惠券内容
        if (requestCode == 10086) {
            if (data != null) {
                //获取优惠券金额
                int amount2 = data.getIntExtra("amount", 0);
                //获取优惠券code
                code = data.getStringExtra("code");
                //获取优惠券名字
                String name = data.getStringExtra("name");

                tvCoupon.setText(name);
                tvCouponAmount.setText("-" + amount2 + "元");
                double money = (amount - amount2) * 1.00;
                if (money > 0) {
                    tvMoney.setText("" + money + "");

                } else {
                    tvMoney.setText("0.00");

                }
            }
        }
    }


    /**
     * 订单支付
     */
    public void pay(View view) {
        Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
        MyApplication.orderStatus="0";
        Intent mIntent = new Intent(PayActivity.this, MainActivity.class);
        startActivity(mIntent);
        finish();
    }
}
