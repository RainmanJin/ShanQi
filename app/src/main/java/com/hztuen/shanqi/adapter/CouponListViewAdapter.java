package com.hztuen.shanqi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hztuen.model.Coupon;
import com.hztuen.shanqi.R;
import com.hztuen.shanqi.widget.CustomTextView;

import java.util.List;

import static com.hztuen.shanqi.R.id.tvCoupon01;
import static com.hztuen.shanqi.R.id.tvMoney;

/**
 * 优惠券
 */

public class CouponListViewAdapter extends BaseAdapter {

    private List<Coupon> mList;
    private Context mContent;

    public CouponListViewAdapter(List<Coupon> pList, Context pContext) {
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
            convertView = LayoutInflater.from(mContent).inflate(R.layout.listview_coupon_item, null);

            mHolder.tvCoupon01 = (TextView) convertView.findViewById(tvCoupon01);
            mHolder.tvCoupon02 = (TextView) convertView.findViewById(R.id.tvCoupon02);
            mHolder.tvCoupon03 = (TextView) convertView.findViewById(R.id.tvCoupon03);
            mHolder.tvMoney = (CustomTextView) convertView.findViewById(tvMoney);

            convertView.setTag(mHolder);
        } else {

            mHolder = (MyViewHolder) convertView.getTag();
        }

        mHolder.tvMoney.setText("" + mList.get(position).getAmount() + "");
        mHolder.tvCoupon01.setText(mList.get(position).getName());
        mHolder.tvCoupon02.setText("有效期至   " + mList.get(position).getEndDate() + "");


        return convertView;
    }

    private class MyViewHolder {

        TextView tvCoupon01, tvCoupon02, tvCoupon03;
        CustomTextView tvMoney;
        ImageView ivCoupon;

    }
}
