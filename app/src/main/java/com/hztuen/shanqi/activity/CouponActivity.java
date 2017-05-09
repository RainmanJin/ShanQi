package com.hztuen.shanqi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hztuen.contacts.AppUrl;
import com.hztuen.model.Coupon;
import com.hztuen.shanqi.MyApplication;
import com.hztuen.shanqi.R;
import com.hztuen.shanqi.adapter.CouponListViewAdapter;
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
 * 优惠券界面
 */

public class CouponActivity extends BaseActivity {
    PullToRefreshListView plvTrip;
    private List<Coupon> mList;
    private CouponListViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
        initHeadUI();
        plvTrip = (PullToRefreshListView) findViewById(R.id.plvTrip);
        mList = new ArrayList<>();
        mAdapter = new CouponListViewAdapter(mList, this);
        plvTrip.setAdapter(mAdapter);
        plvTrip.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent data = new Intent();
                data.putExtra("amount", mList.get(position - 1).getAmount());
                data.putExtra("code", mList.get(position - 1).getCode());
                data.putExtra("name", mList.get(position - 1).getName());
                setResult(10086, data);
                finish();
            }
        });
    }

    //初始化头布局
    private void initHeadUI() {
        LinearLayout layoutLeft = (LinearLayout) findViewById(R.id.layoutLeft);
        layoutLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tvTitle = (TextView) findViewById(R.id.tvHeadTitle);
        tvTitle.setText("绿色出行体验券");
        TextView tvRight = (TextView) findViewById(R.id.tvRight);
        tvRight.setText("确定");
    }

    @Override
    protected void onResume() {
        super.onResume();

        String sign = "";
        List<String> attributes = new ArrayList<>();
        attributes.add("userId=" + MyApplication.userId);
        attributes.add("token=" + MyApplication.myToken);
        try {
            sign = StringUtil.sign(attributes);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //获取数据
        OkHttpUtils
                .post()
                .url(AppUrl.COUPON_LIST)
                .addParams("userId", MyApplication.userId)
                .addParams("token", MyApplication.myToken)
                .addParams("sign", sign)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        MyLogUtil.i("优惠券", "" + e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        MyLogUtil.i("优惠券", "" + response);
                        try {
                            JSONObject result = new JSONObject(response);
                            String resultCode = result.getString("resultCode");
                            String resultMsg = result.getString("resultMsg");
                            Toast.makeText(CouponActivity.this, resultMsg, Toast.LENGTH_SHORT).show();
                            if ("200".equals(resultCode)) {
                                JSONArray resultContent = result.getJSONArray("resultContent");
                                for (int i = 0; i < resultContent.length(); i++) {
                                    JSONObject coupon = resultContent.getJSONObject(i);
                                    int amount = coupon.getInt("amount");
                                    String name = coupon.getString("name");
                                    String endDate = coupon.getString("endDate");
                                    String code = coupon.getString("code");
                                    Coupon mCoupon = new Coupon();
                                    mCoupon.setAmount(amount);
                                    mCoupon.setName(name);
                                    mCoupon.setCode(code);
                                    mCoupon.setEndDate(endDate);
                                    mList.add(mCoupon);
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
