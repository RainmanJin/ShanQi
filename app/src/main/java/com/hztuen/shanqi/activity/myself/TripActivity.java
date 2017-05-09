package com.hztuen.shanqi.activity.myself;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hztuen.model.Trip;
import com.hztuen.shanqi.R;
import com.hztuen.shanqi.activity.BaseActivity;
import com.hztuen.shanqi.adapter.TripListViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的行程
 */

public class TripActivity extends BaseActivity {
    PullToRefreshListView plvTrip;
    private List<Trip> mList;
    private TripListViewAdapter mAdapter;
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
                    plvTrip.onRefreshComplete();
                    break;
            }
        }
    };

    private List<Trip> parseJson(String data) {
        List<Trip> tList;
        tList = new ArrayList<>();
        for (int i = 0; i <10 ; i++) {
            Trip mInfo = new Trip();
            mInfo.setAmount((int)(10*Math.random())+"");
            mInfo.setTime("2017.04.27");
            mInfo.setBikeId(""+(int)(1000000000*Math.random()));
            mInfo.setDistance((int)(10*Math.random())+"");
            mInfo.setMinutes((int)(100*Math.random())+"");
            mInfo.setSn(""+(int)(10000000*Math.random()));
            tList.add(mInfo);
        }
        return tList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trip);
        initHeadUI();

        plvTrip = (PullToRefreshListView) findViewById(R.id.plvTrip);

        mList = new ArrayList<>();
        mAdapter = new TripListViewAdapter(mList, this);
        plvTrip.setAdapter(mAdapter);
        getInfo(pageNum);
        plvTrip.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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

        plvTrip.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0){
                    position--;
                }
//                Intent mIntent = new Intent(TripActivity.this, TraceActivity.class);
//                mIntent.putExtra("mTrip", mList.get(position));
//                startActivity(mIntent);
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
        tvTitle.setText(R.string.my_trip);
    }
}
