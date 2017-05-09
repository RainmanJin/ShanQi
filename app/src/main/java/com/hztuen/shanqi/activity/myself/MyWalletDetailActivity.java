package com.hztuen.shanqi.activity.myself;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hztuen.model.Detail;
import com.hztuen.shanqi.R;
import com.hztuen.shanqi.activity.BaseActivity;
import com.hztuen.shanqi.adapter.DetailListViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的钱包明细界面
 */

public class MyWalletDetailActivity extends BaseActivity {
    PullToRefreshListView plvDetail;
    private List<Detail> mList;
    private DetailListViewAdapter mAdapter;
    private final static int PULL_DOWN_TO_REFRESH = 1;//下拉刷新
    private final static int PULL_UP_TO_REFRESH = 2;//上拉加载
    private final static int HTTP = 3;//上拉加载
    private int pageNum = 1;

    private Handler mHandler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PULL_DOWN_TO_REFRESH:
                    //下拉刷新
                    pageNum = 1;
                    mList.clear();
                    getInfo(pageNum);
                    break;
                case PULL_UP_TO_REFRESH:
                    getInfo(pageNum);
                    break;
                case HTTP:
                    pageNum++;
                    mList.addAll(parseJson((String) msg.obj));
                    mAdapter.notifyDataSetChanged();
                    plvDetail.onRefreshComplete();
                    break;
            }
        }
    };

    private List<Detail> parseJson(String data) {

        List<Detail> tList;
        tList = new ArrayList<>();

        for (int i = 0; i <10 ; i++) {
            Detail mInfo = new Detail();
            mInfo.setAmount((int)(10*Math.random())+"");
            mInfo.setDate("2017.4.27");
            mInfo.setLeft("支付成功");
            mInfo.setRight("余额支付");
            tList.add(mInfo);
        }
        return tList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet_detials);
        initHeadUI();

        plvDetail = (PullToRefreshListView) findViewById(R.id.plvDetail);

        mList = new ArrayList<>();
        mAdapter = new DetailListViewAdapter(mList, this);
        plvDetail.setAdapter(mAdapter);
        getInfo(pageNum);
        plvDetail.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                //下拉刷新
                Message message = Message.obtain();
                message.what = PULL_DOWN_TO_REFRESH;
                mHandler.sendMessage(message);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {

                //上拉加载
                Message message = Message.obtain();
                message.what = PULL_UP_TO_REFRESH;
                mHandler.sendMessage(message);
            }
        });

    }

    public void getInfo(int pPageNum) {
        Message message = Message.obtain();
        message.what = HTTP;
        message.obj = "";
        mHandler.sendMessage(message);
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
        tvTitle.setText("明细");
    }
}
