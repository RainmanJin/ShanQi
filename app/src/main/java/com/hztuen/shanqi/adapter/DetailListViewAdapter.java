package com.hztuen.shanqi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hztuen.model.Detail;
import com.hztuen.shanqi.R;

import java.util.List;

/**
 *
 */

public class DetailListViewAdapter extends BaseAdapter {

    private List<Detail> mList;
    private Context mContent;

    public DetailListViewAdapter(List<Detail> pList, Context pContext) {
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
            convertView = LayoutInflater.from(mContent).inflate(R.layout.listview_detial_item, null);

            mHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            mHolder.tvMonet = (TextView) convertView.findViewById(R.id.tvAmount);
            mHolder.tvRight = (TextView) convertView.findViewById(R.id.tvRight);
            mHolder.tvLeft = (TextView) convertView.findViewById(R.id.tvLeft);

            convertView.setTag(mHolder);

        } else {
            mHolder = (MyViewHolder) convertView.getTag();
        }

        mHolder.tvDate.setText(mList.get(position).getDate());
        mHolder.tvMonet.setText(""+mList.get(position).getAmount()+"元");
        mHolder.tvRight.setText(mList.get(position).getRight());
        mHolder.tvLeft.setText(mList.get(position).getLeft());

        return convertView;
    }

    private class MyViewHolder {
        TextView tvMonet, tvDate, tvRight, tvLeft;

    }
}
