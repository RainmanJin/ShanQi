package com.hztuen.util;

import com.hztuen.shanqi.MyApplication;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * okHttpUtils
 */

public class OkUtil {

    //传2个参数
    //url
    private String mUrl;
    private Map<String, String> mMap;


    public OkUtil(String pUrl, Map<String, String> pMap) {
        mUrl = pUrl;
        mMap = pMap;

        mMap.put("token", MyApplication.myToken);
        List<String> attributes = new ArrayList<>();
        //生成签名sign
        for (String obj : mMap.keySet()) {
            String value = mMap.get(obj);
            attributes.add(obj + "=" + value);
        }

        String sign = "";
        try {
            sign = StringUtil.sign(attributes);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mMap.put("sign", sign);


    }

    public void sendAHttp(final okUtilOnErrorListener mListener  ) {

        PostFormBuilder mBuilder = OkHttpUtils.post().url(mUrl);

        for (String obj : mMap.keySet()) {
            String value = mMap.get(obj);
            mBuilder.addParams(obj, value);
        }
        mBuilder.build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
               mListener.mOnError(call,e,id);

            }

            @Override
            public void onResponse(String response, int id) {
                mListener.mOnResponse(response,id);
            }
        });

    }

    public interface  okUtilOnErrorListener{

        void mOnError(Call call, Exception e, int id);
        void mOnResponse(String response, int id);
    }


}
