package com.hztuen.contacts;


/**
 * 接口配置类
 */
public class AppUrl {


    //key
    public static final String key = "A-98467E7D2";
    // 生产环境
//    public  static  String BASEURL = "http://flashbike.hztuen.com";
    public static final String BASEURL = "https://app.flashbike.cn";


    //普通登录
    public static final String LOGIN_URL = BASEURL + "/app/member/login.jhtml";
    //token登录
    public static final String LOGIN_TOKEN = BASEURL + "/app/member/tokenLogin.jhtml";
    //获取用户信息
    public static final String MEMBER_DETAIL = BASEURL + "/app/member/detail.jhtml";
    //获取验证码
    public static final String VERIFICATION_CODE = BASEURL + "/app/common/sendMsgCode.jhtml";
    //实名认证
    public static final String REAL_NAME_AUTHENTICATION = BASEURL + "/app/member/check.jhtml";
    //获取余额
    public static final String GET_BALANCE = BASEURL + "/app/member/getBalance.jhtml";
    //获取周边车辆
    public static final String AROUND_SITE = BASEURL + "/app/bikeOrder/aroundSite.jhtml";
    //开锁
    public static final String OPEN = BASEURL + "/app/bikeOrder/open.jhtml";
    //获取车辆信息
    public static final String GET_BIKE_INFO = BASEURL + "/app/bikeOrder/getBikeInfo.jhtml";
    //获取订单状态
    public static final String GET_ORDER_INFO = BASEURL + "/app/bikeOrder/getOrderInfo.jhtml";
    //我的行程
    public static final String GET_ORDER_LIST = BASEURL + "/app/bikeOrder/getOrderList.jhtml";
    //获取押金
    public static final String GET_DEPOSIT = BASEURL + "/app/payRule/getDeposit.jhtml";
    //获取计费规则
    public static final String GET_RULE = BASEURL + "/app/payRule/getRule.jhtml";
    //提交故障
    public static final String PRESENT = BASEURL + "/app/fault/present.jhtml";
    //还车
    public static final String LOCK = BASEURL + "/app/bikeOrder/lock.jhtml";

    //充值
    public static final String RECHARGE = BASEURL + "/app/payment/recharge.jhtml";
    //押金充值
    public static final String DEPOSIT = BASEURL + "/app/payment/deposit.jhtml";
    //优惠券列表
    public static final String COUPON_LIST = BASEURL + "/app/coupon/couponList.jhtml";
    //支付
    public static final String PAY = BASEURL + "/app/bikeOrder/pay.jhtml";
    //明细
    public static final String GET_BILL = BASEURL + "/app/member/getBill.jhtml";

    //预约
    public static final String APPOINTMENT = BASEURL + "/app/bikeOrder/appointment.jhtml";
    //取消预约
    public static final String CANCEL_LATION = BASEURL + "/app/bikeOrder/cancellation.jhtml";
    //临时锁车
    public static final String TEMPORARY_LOCK = BASEURL + "/app/bikeOrder/temporaryLock.jhtml";
    //临时还车
    public static final String TEMPORARY_RETURN = BASEURL + "/app/bikeOrder/temporaryReturn.jhtml";
    //退还押金
    public static final String RETRUN_DEPOSIT = BASEURL + "/app/refund/retrunDeposit.jhtml";

    public static final String LIST = BASEURL + "/app/article/list.jhtml";


    public static final String WEIBO = "http://weibo.com/flashbike";
    public static final String FEEDBACK = "https://jinshuju.net/f/Nzs3mx";

    //订单轨迹
    public static final String GET_TRAJECTORYLIST = BASEURL + "/app/bikeOrder/trajectory.jhtml";

}
