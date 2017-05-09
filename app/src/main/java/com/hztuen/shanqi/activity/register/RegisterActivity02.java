package com.hztuen.shanqi.activity.register;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hztuen.shanqi.MyApplication;
import com.hztuen.shanqi.R;
import com.hztuen.shanqi.activity.BaseActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手机验证界面
 */

public class RegisterActivity02 extends BaseActivity implements View.OnClickListener {

    private CheckBox checkBox;
    private EditText etPhoneNum, etCode;
    private Button btStart;
    private TextView tvGetCode;
    private CountDownTimer timer;//倒计时类
    private TextView user_agreement_textview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register02);
        initUI();
        initDialog();
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //动态更新textView里面的内容
                tvGetCode.setText((millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                //倒计时结束时操作
                tvGetCode.setEnabled(true);
                tvGetCode.setText(R.string.verification_code);

            }
        };

    }


    private void initUI() {
        //设置标题
        TextView tvHeadTitle = (TextView) findViewById(R.id.tvHeadTitle);
        tvHeadTitle.setText(R.string.mobile_phone_verification);
        //左边按钮
        LinearLayout layoutLeft = (LinearLayout) findViewById(R.id.layoutLeft);
        layoutLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        checkBox = (CheckBox) findViewById(R.id.checkBox);
        etPhoneNum = (EditText) findViewById(R.id.etPhoneNum);
        etCode = (EditText) findViewById(R.id.etCode);

        user_agreement_textview = (TextView) findViewById(R.id.user_agreement_textview);
        user_agreement_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(RegisterActivity02.this, UserAgreementActivity.class);
                mIntent.putExtra("title", "用户协议");
                startActivity(mIntent);
            }
        });

        btStart = (Button) findViewById(R.id.btStart);
        btStart.setOnClickListener(this);
        tvGetCode = (TextView) findViewById(R.id.tvGetCode);
        tvGetCode.setOnClickListener(this);
        etPhoneNum.setText("15168212330");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btStart:

                String mPhoneNum = etPhoneNum.getText().toString();
                String mCode = etCode.getText().toString();
                if (!checkBox.isChecked()) {
                    Toast.makeText(this, R.string.first_input_01, Toast.LENGTH_SHORT).show();
                } else if (!isMobileNO(mPhoneNum)) {
                    Toast.makeText(this, R.string.first_input_02, Toast.LENGTH_SHORT).show();
                } else if ("".equals(mCode)) {
                    Toast.makeText(this, R.string.first_input_03, Toast.LENGTH_SHORT).show();
                } else {
                    MyApplication.isLogin=true;
                    Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                    Intent mIntent=new Intent(RegisterActivity02.this,RegisterActivity03.class);
                    startActivity(mIntent);
                    finish();
                }
                break;

            case R.id.tvGetCode:
                String mPhoneNum2 = etPhoneNum.getText().toString().trim();
                if (!isMobileNO(mPhoneNum2)) {
                    Toast.makeText(this, R.string.first_input_02, Toast.LENGTH_SHORT).show();
                } else {
                    //发送验证码
                    timer.start();
                    tvGetCode.setEnabled(false);//点击的时候设置不可点击
                    etCode.requestFocus();
                }
                break;
        }
    }

    private ProgressDialog mProgressDialog;

    //初始化Dialog
    private void initDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("还车中请稍后...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
    }

    /**
     * 正则表达式判断是否手机号
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");
//            Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
            Matcher m = p.matcher(mobiles);
            return m.matches();
    }

}
