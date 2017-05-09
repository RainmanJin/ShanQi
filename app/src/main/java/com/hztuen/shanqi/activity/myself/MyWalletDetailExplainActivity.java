package com.hztuen.shanqi.activity.myself;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hztuen.shanqi.R;
import com.hztuen.shanqi.activity.BaseActivity;

/**
 * 退款说明界面
 */

public class MyWalletDetailExplainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet_explain);
        initUI();
    }


    private void initUI() {
        //设置标题
        TextView tvHeadTitle = (TextView) findViewById(R.id.tvHeadTitle);
        tvHeadTitle.setText(R.string.refund_instructions);
        ImageView ivLeft = (ImageView) findViewById(R.id.ivLeft);
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
