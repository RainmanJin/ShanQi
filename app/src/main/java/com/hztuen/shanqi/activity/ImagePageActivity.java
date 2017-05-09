package com.hztuen.shanqi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.hztuen.shanqi.R;
import com.hztuen.shanqi.widget.PhotoViewViewPager;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 *
 */

public class ImagePageActivity extends BaseActivity {
    public static final String TAG = ImagePageActivity.class
            .getSimpleName();

    private PhotoViewViewPager vp;
    private List<String> mList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_page);

        initHeadUI();
        vp = (PhotoViewViewPager) findViewById(R.id.vp);
        mList = new ArrayList<>();
        mList = getIntent().getStringArrayListExtra("mList");
        vp.setAdapter(new MyPageAdapter(mList, this));
        vp.setCurrentItem(getIntent().getIntExtra("pos", 1));
    }

    private void initHeadUI() {

        LinearLayout layoutLeft= (LinearLayout) findViewById(R.id.layoutLeft);
        layoutLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ImagePageActivity.this.setResult(100);
                finish();
            }
        });



        LinearLayout layoutRight= (LinearLayout) findViewById(R.id.layoutRight);
        layoutRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("pos",vp.getCurrentItem());
                setResult(101, data);
                finish();


            }
        });

    }
}


class MyPageAdapter extends PagerAdapter {
    private List<String> mList;
    private Context mContent;

    MyPageAdapter(List<String> mList, Context pContext) {
        this.mList = mList;
        mContent=pContext;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {


        PhotoView photoView = new PhotoView(container.getContext());
//        photoView.setImageResource(mList.get(position));
        Glide
                .with(mContent)
                .load(mList.get(position))
                .into(photoView);

//        photoView.setImageResource(R.mipmap.ic_launcher);
        // Now just add PhotoView to ViewPager and return it
        //不仅要 添加还要返回，设置为全屏
        container.addView(photoView);
//        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return photoView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}