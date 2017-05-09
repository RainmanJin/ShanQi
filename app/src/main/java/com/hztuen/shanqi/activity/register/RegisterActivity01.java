package com.hztuen.shanqi.activity.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hztuen.shanqi.R;
import com.hztuen.shanqi.activity.BaseActivity;
import com.hztuen.util.StatusBarUtils;

/**
 * 注册/登录 界面01
 */

public class RegisterActivity01 extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register01);
        StatusBarUtils.setWindowStatusBarColor(this);
    }

    public void loginClick(View v) {
        Intent mIntent = new Intent(RegisterActivity01.this, RegisterActivity02.class);
        startActivity(mIntent);
        finish();
    }

    public void back(View v) {
        finish();
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }
}
