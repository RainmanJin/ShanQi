package com.hztuen.util;

import com.hztuen.contacts.AppUrl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import static com.upyun.library.utils.UpYunUtils.md5;

/**
 * 字符串处理工具类
 */

public class StringUtil {


    /**
     * 二维码数据返回处理
     * 处理成功返回车辆 bikeId
     * 处理失败返回 ""
     */
    public static String parseUrlString(String url) {
        //二维码扫描成功url
        //1、http://flashbike.hztuen.com/admin/index.jhtml?sid=11111111111
        //2、http://flashbike.hztuen.com/admin/index.jhtml?userId=1&sid=11111111111
        //3、http://flashbike.hztuen.com/admin/index.jhtml?userId=1&sid=11111111111&name=111

        String result;
        try {
            //截取sid=后面的字符串
            String[] string1 = url.split("sid=");
            result = string1[1];
            //判断sid=后面的字符串有无&
            //有则 继续截取
            if (result.contains("&")) {
                String[] string2 = result.split("&");
                return string2[0];
            } else {
                return result;
            }
        } catch (Exception e) {
            return "";
        }
    }


    /**
     * 获取当前倒计时时间
     * 将当前时间转化为 分:秒
     */
    public static String getCountdownTime(long time) {

        //毫秒转秒
        double sTime = time / 1000;
        //获得分
        int min = (int) (sTime / 60);
        //获得秒
        int s = (int) (sTime % 60);

        if (s < 10) {
            return min + ":" + "0" + s;
        } else {
            return min + ":" + s;
        }

    }

    /**
     * 将高德地图返回的 地址截取"市"后面的字段
     */
    public static String getAddress(String pAddress) {
        MyLogUtil.i("地址",pAddress+"");
        try {
            String[] string1 = pAddress.split("市");
            MyLogUtil.i("地址",string1[1]+"");
            return string1[1];
        } catch (Exception e) {
            MyLogUtil.i("地址",pAddress+"");
            return pAddress;
        }

    }

    /***
     * 生成 签名
     *
     * @throws UnsupportedEncodingException
     */
    public static String sign(List<String> attributes) throws UnsupportedEncodingException {
        String key = AppUrl.key;
        Object[] os = attributes.toArray();
        Arrays.sort(os);
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < os.length; i++) {
            if (i > 0) {
                stringBuffer.append("&");
            }
            stringBuffer.append(os[i]);
        }
        //key 为约定字
        stringBuffer.append(key);
        MyLogUtil.i("key", key);
        String content = stringBuffer.toString();
        String sign = "";
        //md5加密

//        			sign = DigestUtils.md5DigestAsHex(content.getBytes("UTF-8")).toString();
        MyLogUtil.i("sign", content);

        sign = md5(content);

        MyLogUtil.i("sign", sign);
        return sign;
    }

    private static String md5LowerCase(String src) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(src.getBytes("utf-8"));
        byte b[] = md.digest();
        int i;
        StringBuffer buf = new StringBuffer("");
        for (int offset = 0; offset < b.length; offset++) {
            i = b[offset];
            if (i < 0)
                i += 256;
            if (i < 16)
                buf.append("0");
            buf.append(Integer.toHexString(i));
        }
        return buf.toString();// 32位的加密
    }


}
