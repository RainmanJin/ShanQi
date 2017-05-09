package com.hztuen.shanqi.activity.myself;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hztuen.contacts.AppUrl;
import com.hztuen.model.Guide;
import com.hztuen.shanqi.MyApplication;
import com.hztuen.shanqi.R;
import com.hztuen.shanqi.activity.BaseActivity;
import com.hztuen.shanqi.adapter.GuideListViewAdapter;
import com.hztuen.util.MyLogUtil;
import com.hztuen.util.StringUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * 用户指南主界面
 */

public class GuideActivity extends BaseActivity {
    public static final String TAG = GuideActivity.class.getSimpleName();

    private List<Guide> mList;
    private GuideListViewAdapter mAdapter;
    private ListView lvGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_guide);
        initUI();
        lvGuide= (ListView) findViewById(R.id.lvGuide);
        mList=new ArrayList<>();
        mAdapter=new GuideListViewAdapter(mList,this);
        lvGuide.setAdapter(mAdapter);

        lvGuide.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent mIntent=new Intent( GuideActivity.this,GuideActivity02.class);
                mIntent.putExtra("title",mList.get(position).getTitle());
                mIntent.putExtra("url",mList.get(position).getUrl());
                startActivity(mIntent);
            }
        });
        getItemTitle();
    }


    @Override
    protected void onResume() {
        super.onResume();




    }

    private void goToActivity(int myTAG){
        Intent mIntent=new Intent(GuideActivity.this,GuideActivity02.class);

        mIntent.putExtra("TAG",myTAG);
        startActivity(mIntent);

    }


    private void initUI() {
        //设置标题
        TextView tvHeadTitle = (TextView) findViewById(R.id.tvHeadTitle);
        tvHeadTitle.setText("用户指南");
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


    public void getItemTitle() {
        String sign = "";
        List<String> attributes = new ArrayList<>();
        attributes.add("userId=" + MyApplication.userId);
        attributes.add("token=" + MyApplication.myToken);
        try {
            sign = StringUtil.sign(attributes);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        OkHttpUtils
                .post()
                .url(AppUrl.LIST)
                .addParams("userId", MyApplication.userId)
                .addParams("token", MyApplication.myToken)
                .addParams("sign", sign)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {

                        MyLogUtil.i("用户指南",response+"");


                        try {
                            JSONObject result=new JSONObject(response);
                            String resultCode=result.getString("resultCode");
                            if("200".equals(resultCode)){
                                JSONArray resultContent=result.getJSONArray("resultContent");
                                for (int i = 0; i <resultContent.length() ; i++) {

                                    JSONObject info=resultContent.getJSONObject(i);
                                    Guide mGuid=new Guide();
                                    mGuid.setTitle(info.getString("title"));
                                    mGuid.setUrl(info.getString("url"));

                                    mList.add(mGuid);

                                }
                                mAdapter.notifyDataSetChanged();


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });


    }
}
