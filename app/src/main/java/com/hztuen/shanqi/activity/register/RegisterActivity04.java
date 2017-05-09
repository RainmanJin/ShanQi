package com.hztuen.shanqi.activity.register;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hztuen.shanqi.MyApplication;
import com.hztuen.shanqi.R;
import com.hztuen.shanqi.activity.BaseActivity;

/**
 * 实名认证界面
 */

public class RegisterActivity04 extends BaseActivity {

    private ImageView ivStep01, ivStep02, ivStep03, ivStep04;
    private View viewStep1, viewStep2, viewStep3;
    private LinearLayout layoutStep;

    private EditText etName, etIDCard;
    private Button btAuthentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register04);
        initUI();
        initStepUI();//初始化进度条UI
    }


    //初始化进度条UI
    private void initStepUI() {
        ivStep01 = (ImageView) findViewById(R.id.ivStep1);
        ivStep02 = (ImageView) findViewById(R.id.ivStep2);
        ivStep03 = (ImageView) findViewById(R.id.ivStep3);
        ivStep04 = (ImageView) findViewById(R.id.ivStep4);

        viewStep1 = findViewById(R.id.viewStep1);
        viewStep2 = findViewById(R.id.viewStep2);
        viewStep3 = findViewById(R.id.viewStep3);

        layoutStep = (LinearLayout) findViewById(R.id.layoutStep);

        ivStep03.setImageResource(R.mipmap.a5_step3);
        viewStep1.setBackgroundColor(getResources().getColor(R.color.colorText_red));
        viewStep2.setBackgroundColor(getResources().getColor(R.color.colorText_red));
        if (MyApplication.isDeposit) {
            ivStep02.setImageResource(R.mipmap.a5_step2);
        } else {
            ivStep02.setImageResource(R.mipmap.a5_step1);
        }

    }

    private void initUI() {
        //设置标题
        TextView tvHeadTitle = (TextView) findViewById(R.id.tvHeadTitle);
        tvHeadTitle.setText(R.string.real_name_authentication);
        LinearLayout layoutLeft = (LinearLayout) findViewById(R.id.layoutLeft);
        layoutLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        etIDCard = (EditText) findViewById(R.id.etIDCard);
        etName = (EditText) findViewById(R.id.etName);
        btAuthentication = (Button) findViewById(R.id.btAuthentication);
        //实名认证按钮
        btAuthentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String etIDCard2 = etIDCard.getText().toString();
                String etName2 = etName.getText().toString();

                if ("".equals(etName2)) {
                    Toast.makeText(RegisterActivity04.this, R.string.first_input_04, Toast.LENGTH_SHORT).show();
                } else if (etIDCard2.length() != 18) {
                    Toast.makeText(RegisterActivity04.this, R.string.first_input_05, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity04.this, "实名认证成功", Toast.LENGTH_SHORT).show();
                    finish();
                    MyApplication.isIdentity = true;
                }
            }
        });
    }
}
