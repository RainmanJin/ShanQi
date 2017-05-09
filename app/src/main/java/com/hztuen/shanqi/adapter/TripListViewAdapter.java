package com.hztuen.shanqi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hztuen.model.Trip;
import com.hztuen.shanqi.R;

import java.util.List;

/**
 *
 */

public class TripListViewAdapter extends BaseAdapter {

    private List<Trip> mList;
    private Context mContent;

    public TripListViewAdapter(List<Trip> pList, Context pContext) {
        mList = pList;
        mContent = pContext;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MyViewHolder mHolder;
        if (convertView == null) {
            mHolder = new MyViewHolder();
            convertView = LayoutInflater.from(mContent).inflate(R.layout.listview_trip_item, null);

            mHolder.tvAmount = (TextView) convertView.findViewById(R.id.tvAmount);
            mHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
            mHolder.tvBikeId = (TextView) convertView.findViewById(R.id.tvBikeId);
//            mHolder.tvDistance = (TextView) convertView.findViewById(R.id.tvDistance);
            mHolder.tvMinutes = (TextView) convertView.findViewById(R.id.tvMinutes);

            convertView.setTag(mHolder);
        } else {
            mHolder = (MyViewHolder) convertView.getTag();

        }

        mHolder.tvAmount.setText(mList.get(position).getAmount());
        mHolder.tvTime.setText(mList.get(position).getTime());
        mHolder.tvBikeId.setText("自行车编号:"+mList.get(position).getBikeId());
//        mHolder.tvDistance.setText(mList.get(position).getDistance());
        mHolder.tvMinutes.setText(mList.get(position).getMinutes());

        return convertView;
    }

   private class MyViewHolder {

        TextView tvAmount, tvTime, tvBikeId, tvDistance, tvMinutes;
    }
}
