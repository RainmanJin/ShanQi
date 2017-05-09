package com.hztuen.shanqi.activity.set;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hztuen.contacts.AppUrl;
import com.hztuen.shanqi.R;
import com.hztuen.shanqi.activity.BaseActivity;
import com.hztuen.shanqi.activity.myself.GuideActivity02;
import com.hztuen.util.MyLogUtil;

/**
 * 关于我们
 */

public class AboutUsActivity extends BaseActivity {


    private TextView current_version;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_about_us);

        current_version = (TextView) findViewById(R.id.current_version);
        LinearLayout layoutLeft= (LinearLayout) findViewById(R.id.layoutLeft);
        layoutLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String currentVersion = getVersionName();
        if (MyLogUtil.isDebug) {
            current_version.setText("当前测试版本:"+currentVersion);
        }else {
            current_version.setText("当前版本:"+currentVersion);
        }
    }



    private String getVersionName()
    {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(),0);
            String version = packInfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.0.0";
    }


    public void myClick(View v){
//        Toast.makeText(this, "新浪微博", Toast.LENGTH_SHORT).show();

        Intent mIntent=new Intent( AboutUsActivity.this,GuideActivity02.class);
        mIntent.putExtra("title","新浪微博");
        mIntent.putExtra("url", AppUrl.WEIBO);
        startActivity(mIntent);
    }
}
