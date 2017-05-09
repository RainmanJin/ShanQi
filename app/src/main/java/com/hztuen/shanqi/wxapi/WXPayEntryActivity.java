package com.hztuen.shanqi.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.hztuen.shanqi.R;
import com.hztuen.util.MyLogUtil;



public class WXPayEntryActivity extends Activity {

    // IWXAPI 是第三方app和微信通信的openapi接口
//    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
        MyLogUtil.i("创建界面");
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
//        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
//        // 将该app注册到微信
//        api.registerApp(Constants.APP_ID);
//        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
//        api.handleIntent(intent, this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
//    @Override
//    public void onReq(BaseReq req) {
//        MyLogUtil.i("微信支付回调结果onReq", "resp.openId=" + req.openId + ",req.getType()=" + req.getType());
//    }

//    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
//    @Override
//    public void onResp(BaseResp resp) {
//
//        MyLogUtil.i("微信支付回调结果onResp", "resp.openId=" + resp.openId + ",resp.errCode=" + resp.errCode);
//        if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
//            Toast.makeText(this, "code = " + ((SendAuth.Resp) resp).code, Toast.LENGTH_SHORT).show();
//        }
//        int result = 0;
//        switch (resp.errCode) {
//            case BaseResp.ErrCode.ERR_OK:
//                result = R.string.errcode_success;
//
//                if (MyApplication.isGoWeChatDeposit) {
//                    MyApplication.isDeposit = true;
//
//                    if (MyApplication.isIdentity) {
//                        RegisterActivity03.mRegisterActivity03.finish();
//                    } else {
//                        Intent mIntent = new Intent(WXPayEntryActivity.this, RegisterActivity04.class);
//                        startActivity(mIntent);
//                        RegisterActivity03.mRegisterActivity03.finish();
//                    }
//                }
//
//
//                break;
//            case BaseResp.ErrCode.ERR_USER_CANCEL:
//                result = R.string.errcode_cancel;
//                //取消支付
//                break;
//            case BaseResp.ErrCode.ERR_AUTH_DENIED:
//                result = R.string.errcode_deny;
//                //发送被拒绝
//                break;
//            default:
//                result = R.string.errcode_unknown;
//                //发送被拒绝
//                break;
//        }
//
//        MyLogUtil.i("微信支付回调结果", getString(result) + "");
//        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
//        WXPayEntryActivity.this.finish();
//        overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
//    }

}

