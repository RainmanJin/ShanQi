package com.hztuen.shanqi.activity.myself;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hztuen.shanqi.R;
import com.hztuen.shanqi.activity.BaseActivity;

/**
 * 用户指南每个item 点进来的界面
 */

public class GuideActivity02 extends BaseActivity {


    WebView webView;
    String url;
    String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_guide_02);

        Intent mIntent = getIntent();
        url=mIntent.getStringExtra("url");
        title=mIntent.getStringExtra("title");
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initUI() {
        //设置标题
        TextView tvHeadTitle = (TextView) findViewById(R.id.tvHeadTitle);
        tvHeadTitle.setText(title);


        webView= (WebView) findViewById(R.id.webView);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });

        //启用支持javascript
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        //优先使用缓存
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
////不使用缓存：
//        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        LinearLayout layoutLeft = (LinearLayout) findViewById(R.id.layoutLeft);
        layoutLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                if (newProgress == 100) {
                    // 网页加载完成

                } else {
                    // 加载中

                }

            }
        });
    }



    //改写物理按键——返回的逻辑
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            if(webView.canGoBack())
            {
                webView.goBack();//返回上一页面
                return true;
            }
            else
            {
                System.exit(0);//退出程序
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
