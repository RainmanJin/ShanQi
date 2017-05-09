package com.hztuen.shanqi.activity.myself;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hztuen.shanqi.R;
import com.hztuen.shanqi.activity.BaseActivity;

/**
 * 我的消息界面
 */

public class MyMessageActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message);
        initUI();

    }
    private void initUI() {
        //设置标题
        TextView tvHeadTitle = (TextView) findViewById(R.id.tvHeadTitle);
        tvHeadTitle.setText(R.string.message_content);
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
}

