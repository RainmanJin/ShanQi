package com.hztuen.shanqi.activity.set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hztuen.contacts.AppUrl;
import com.hztuen.contacts.Constants;
import com.hztuen.shanqi.MyApplication;
import com.hztuen.shanqi.R;
import com.hztuen.shanqi.activity.BaseActivity;
import com.hztuen.shanqi.activity.MainActivity;
import com.hztuen.shanqi.activity.PersonalCenterActivity;
import com.hztuen.shanqi.activity.WebViewActivity;
import com.hztuen.shanqi.activity.myself.GuideActivity02;
import com.hztuen.shanqi.activity.state.RunningActivity;

/**
 * 设置界面
 */

public class SettingsActivity extends BaseActivity {

    private CountDownTimer timer;//倒计时类


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings02);

        initUI();
        initDialog();
        timer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //动态更新textView里面的内容
                //获得倒计时时间
            }

            @Override
            public void onFinish() {
                mProgressDialog.dismiss();
                Toast.makeText(SettingsActivity.this, "清除缓存成功", Toast.LENGTH_SHORT).show();
            }
        };


    }

    private void initUI() {
        //设置标题
        TextView tvHeadTitle = (TextView) findViewById(R.id.tvHeadTitle);
        tvHeadTitle.setText(R.string.set_up);
        //左边按钮
        LinearLayout layoutLeft = (LinearLayout) findViewById(R.id.layoutLeft);
        layoutLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
//        //右边按钮
//        TextView tvLeft = (TextView) findViewById(R.id.tvRight);
//        tvLeft.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent mIntent = new Intent(GuideActivity.this, RegisterActivity04.class);
//                startActivity(mIntent);
//            }
//        });


    }

    public void myClick(View view) {

        switch (view.getId()) {
            case R.id.layoutSet01:
//                goToActivity(WebViewActivity.class);
                Intent mIntent=new Intent( SettingsActivity.this,GuideActivity02.class);
                mIntent.putExtra("title","意见反馈");
                mIntent.putExtra("url", AppUrl.FEEDBACK);
                startActivity(mIntent);
                break;
            case R.id.layoutSet02:
                //清除缓存
                mProgressDialog.setMessage("正在清除缓存");
                mProgressDialog.show();
                timer.start();


                break;
            case R.id.layoutSet03:
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.layoutSet04:

                aboutUs();
                break;
            case R.id.layoutSet05:
                goToActivity(AboutUsActivity.class);
                break;
            case R.id.btQuit:
                //清空本地保存数据

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("确认");
                builder.setMessage("确认要退出吗");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences settings = getSharedPreferences(Constants.MECHINE_ACHE, MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();


                        MyApplication.sn = "0";
                        MyApplication.orderStatus = "0";
                        MyApplication.paymentStatus = "free";
                        MyApplication.isDeposit = false;
                        MyApplication.isIdentity = false;
                        MyApplication.isPhone = false;
                        MyApplication.isLogin = false;
                        MyApplication.userId = "0";

                        MyApplication.isFromSetting = true;
                        editor.clear();
                        editor.commit();

                        if (RunningActivity.runningActivity != null) {
                            RunningActivity.runningActivity.finish();
                        }
                        if (PersonalCenterActivity.personalCenterActivity != null) {
                            PersonalCenterActivity.personalCenterActivity.finish();
                        }
                        Intent mIntent = new Intent(SettingsActivity.this, MainActivity.class);
                        startActivity(mIntent);

                        finish();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }

    }

    /**
     * 关于我们
     */
    private AlertDialog dialog2;

    private void aboutUs() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_custom_02, null);
        ImageView ivClose = (ImageView) v.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });

        Button btCall = (Button) v.findViewById(R.id.btCall);
        btCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "110"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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

    private void goToActivity(Class pClass) {
        Intent mIntent = new Intent(SettingsActivity.this, pClass);
        startActivity(mIntent);
    }

    private ProgressDialog mProgressDialog;

    //初始化Dialog
    private void initDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("还车中请稍后...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
    }
}

