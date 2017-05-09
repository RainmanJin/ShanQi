package com.hztuen.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支付宝支付工具类
 */

public class AliPayUtil {




    /**
     * 生成支付宝  orderInfo
     * 1、获得  keyValues
     * 2、通过  keyValues 获得orderParam
     * 3、通过Json 获得 sign
     * <p>
     * 4、合成 orderInfo
     */

    public static String getOrderInfo(String data) {
        String orderInfo="";

        try {
            JSONObject result=new JSONObject(data);
            String resultCode=result.getString("resultCode");
            if("200".equals(resultCode)){

                JSONObject resultContent=result.getJSONObject("resultContent");
                //解析得到keyValues
                Map<String, String> keyValues = new HashMap<>();

                String app_id=resultContent.getString("app_id");
                String method=resultContent.getString("method");
                String charset=resultContent.getString("charset");
                String notify_url=resultContent.getString("notify_url");
                String sign_type=resultContent.getString("sign_type");
                String timestamp=resultContent.getString("timestamp");
                String version=resultContent.getString("version");
                String biz_content=resultContent.getString("biz_content");

                String sign=resultContent.getString("sign");

                keyValues.put("app_id",app_id);
                keyValues.put("notify_url",notify_url);
                keyValues.put("biz_content", biz_content);
                keyValues.put("charset", charset);
                keyValues.put("method", method);
                keyValues.put("sign_type", sign_type);
                keyValues.put("timestamp", timestamp);
                keyValues.put("version", version);

                //第二步
                String orderParam = buildOrderParam(keyValues);
                orderInfo = orderParam + "&" + "sign="+sign;
                MyLogUtil.i("orderInfo","orderInfo:"+orderInfo+"orderParam:"+orderParam);
                return orderInfo;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return orderInfo;
    }
    public static String buildOrderParam(Map<String, String> map) {
        List<String> keys = new ArrayList<String>(map.keySet());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size() - 1; i++) {
            String key = keys.get(i);
            String value = map.get(key);
            sb.append(buildKeyValue(key, value, true));
            sb.append("&");
        }

        String tailKey = keys.get(keys.size() - 1);
        String tailValue = map.get(tailKey);
        sb.append(buildKeyValue(tailKey, tailValue, true));

        return sb.toString();
    }
    /**
     * 拼接键值对
     *
     * @param key
     * @param value
     * @param isEncode
     * @return
     */
    private static String buildKeyValue(String key, String value, boolean isEncode) {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append("=");
        if (isEncode) {
            try {
                sb.append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                sb.append(value);
            }
        } else {
            sb.append(value);
        }
        return sb.toString();
    }



}
