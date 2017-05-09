package com.hztuen.shanqi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hztuen.model.Guide;
import com.hztuen.shanqi.R;

import java.util.List;

/**
 *
 */

public class GuideListViewAdapter extends BaseAdapter {

    private List<Guide> mList;
    private Context mContent;

    public GuideListViewAdapter(List<Guide> pList, Context pContext) {
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
            convertView = LayoutInflater.from(mContent).inflate(R.layout.listview_guide_item, null);

            mHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);


            convertView.setTag(mHolder);
        } else {
            mHolder = (MyViewHolder) convertView.getTag();

        }


        mHolder.tvTitle.setText(mList.get(position).getTitle());

        return convertView;
    }

   private class MyViewHolder {

        TextView tvTitle;
    }
}
