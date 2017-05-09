package com.hztuen.shanqi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.hztuen.shanqi.R;

/**
 * 启动界面2秒跳转到主界面
 */
public class WelcomeActivity extends BaseActivity {

    private final int SLEEP_TIME = 2000;
    private final int MAIN_TAG = 2;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MAIN_TAG) {
                Intent mainIntent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mHandler.sendEmptyMessageDelayed(MAIN_TAG, SLEEP_TIME);
    }

}
