package com.hztuen.shanqi.activity.myself;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hztuen.shanqi.MyApplication;
import com.hztuen.shanqi.R;
import com.hztuen.shanqi.activity.BaseActivity;
import com.hztuen.shanqi.activity.register.RegisterActivity03;
import com.hztuen.shanqi.widget.CustomTextView;
import com.hztuen.util.MyLogUtil;


/**
 * 我的钱包界面
 */

public class MyWalletActivity extends BaseActivity implements View.OnClickListener {
    private AlertDialog dialog;
    private AlertDialog dialog2;

    private TextView tv01, tv02, tv03;
    private CustomTextView tvMoney;
    private int BUTTON_FLAG = 1;
    private EditText etMoney;


    private static final int SDK_PAY_FLAG = 1;
    private InputMethodManager imm;
    public static MyWalletActivity myWalletActivity;


    private TextView tvDeposit;


    private TextView tvGetMoney;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myWalletActivity = this;
        setContentView(R.layout.activity_my_wallet);
        initUI();

    }

    @Override
    protected void onResume() {
        super.onResume();
        tvMoney.setText(MyApplication.myMoney + "");
        tvDeposit.setText(100 + "元");

        if (MyApplication.isDeposit) {
            tvGetMoney.setText("提取押金");
        } else {
            tvDeposit.setText("0.00 元");
            tvGetMoney.setText("充值押金");
        }


    }

    private void initUI() {
        //设置标题
        TextView tvHeadTitle = (TextView) findViewById(R.id.tvHeadTitle);
        tvHeadTitle.setText(R.string.my_wallet);

        LinearLayout layoutLeft = (LinearLayout) findViewById(R.id.layoutLeft);
        layoutLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //右边按钮
        TextView tvLeft = (TextView) findViewById(R.id.tvRight);
        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(MyWalletActivity.this, MyWalletDetailActivity.class);
                startActivity(mIntent);
            }
        });
        tvGetMoney = (TextView) findViewById(R.id.tvGetMoney);
        tvMoney = (CustomTextView) findViewById(R.id.tvMoney);

        tv01 = (TextView) findViewById(R.id.tv01);
        tv02 = (TextView) findViewById(R.id.tv02);
        tv03 = (TextView) findViewById(R.id.tv03);
        tvDeposit = (TextView) findViewById(R.id.tvDeposit);


        tv01.setOnClickListener(this);
        tv02.setOnClickListener(this);
        tv03.setOnClickListener(this);

        etMoney = (EditText) findViewById(R.id.etMoney);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);//获得软键盘服务


    }

    private void showADialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_custom_01, null);
        ImageView ivClose = (ImageView) v.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });

        Button btSure = (Button) v.findViewById(R.id.btSure);
        btSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });

        builder.setView(v);
        dialog2 = builder.create();
        dialog2.show();
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        WindowManager.LayoutParams params = dialog2.getWindow().getAttributes();
        params.width = width * 3 / 4;
        dialog2.getWindow().setAttributes(params);
        dialog2.getWindow().setWindowAnimations(R.style.mypopwindow_anim_style);


    }

    public void myClick(View view) {
        switch (view.getId()) {

            case R.id.tvGetMoney:
                if (MyApplication.isDeposit) {
                    //弹出一个确认框
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.confirm);
                    builder.setMessage("你确定要提取押金吗");
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //访问网络再弹出一个Dialog
                            showADialog();
                            MyApplication.isDeposit = false;
                            tvDeposit.setText("0.00 元");
                            tvGetMoney.setText("充值押金");
                        }
                    });
                    dialog = builder.create();
                    dialog.show();


                } else {
                    Intent mIntent = new Intent(MyWalletActivity.this, RegisterActivity03.class);
                    startActivity(mIntent);
                }


                break;
        }
    }


    /**
     * 支付宝支付
     */
    public void aliPay(View v) {
        String mMoney = etMoney.getText().toString().trim();
        if ("".equals(mMoney)) {
            Toast.makeText(MyWalletActivity.this, "请输入充值金额", Toast.LENGTH_SHORT).show();
        } else {
            MyApplication.myMoney = MyApplication.myMoney + Integer.parseInt(mMoney);
            Toast.makeText(MyWalletActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
            tvMoney.setText(MyApplication.myMoney + "");
            etMoney.setText("");
        }

    }

    /**
     * 3个选择金额框
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv01:
                BUTTON_FLAG = 1;
                etMoney.setText("10");
                restBackground();
                tv01.setBackgroundResource(R.drawable.money_bg_01);
                tv01.setTextColor(ContextCompat.getColor(this, R.color.colorText_red));
                break;
            case R.id.tv02:
                BUTTON_FLAG = 2;
                etMoney.setText("20");
                restBackground();
                tv02.setBackgroundResource(R.drawable.money_bg_01);
                tv02.setTextColor(ContextCompat.getColor(this, R.color.colorText_red));
                break;

            case R.id.tv03:
                BUTTON_FLAG = 3;
                etMoney.setText("30");
                restBackground();
                tv03.setBackgroundResource(R.drawable.money_bg_01);
                tv03.setTextColor(ContextCompat.getColor(this, R.color.colorText_red));
                break;
        }
    }

    /**
     * 使用微信支付
     */
    public void weChatPay(View view) {
        String mMoney = etMoney.getText().toString().trim();
        if ("".equals(mMoney)) {
            Toast.makeText(MyWalletActivity.this, "请输入充值金额", Toast.LENGTH_SHORT).show();
        } else {
            MyApplication.myMoney = MyApplication.myMoney + Integer.parseInt(mMoney);
            Toast.makeText(MyWalletActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
            tvMoney.setText(MyApplication.myMoney + "");
            etMoney.setText("");
        }
    }

    /**
     * 外部点击事件
     * 隐藏软键盘
     */
    public void layoutClick(View v) {
        MyLogUtil.i("外部点击事件");
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);//隐藏软键盘
        }

    }

    /**
     * 重置选择金额的背景
     */
    private void restBackground() {
        tv01.setBackgroundResource(R.drawable.money_bg_02);
        tv02.setBackgroundResource(R.drawable.money_bg_02);
        tv03.setBackgroundResource(R.drawable.money_bg_02);

        tv01.setTextColor(ContextCompat.getColor(this, R.color.colorText_gray_2));
        tv02.setTextColor(ContextCompat.getColor(this, R.color.colorText_gray_2));
        tv03.setTextColor(ContextCompat.getColor(this, R.color.colorText_gray_2));

    }


}
