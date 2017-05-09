package com.hztuen.model;

import java.io.Serializable;

public class Recharge implements Serializable {
	private static final long serialVersionUID = 1L;

	/*
	 *   "body": "绿炳畅充值0.01",
        "_input_charset": "utf-8",
        "total_fee": "0.01",
        "subject": "绿炳畅充值0.01",
        "sign_type": "RSA",
        "service": "mobile.securitypay.pay",
        "notify_url": "http://localhost/payment/alipayMobileNotify/async/201607131422.jhtml",
	 */

	/**
	 * sign":"EFD8002AA1BC32B6017CB8E3FAE06326","timestamp":"1468549739","noncestr":"T6cBhnbvHhl5N3Qv","partnerid":"1364340102","prepayid":"wx201607151028378408cc4b170326430684","package":"Sign=WXPay","appid":"wxccc2a0fcb33835db"
	 */

	/**字符串	交易的具体描述信息 **/
	private String body;

	/**编码格式，固定为utf-8。 **/
	private String _input_charset;

	/**订单总额单位为RMB-元。取值范围为[0.01，100000000.00]精确到小数点后两位。 **/
	private String total_fee;

	/**商品名称**/
	private String subject;

	/**签名类型，目前仅支持RSA **/
	private String sign_type;

	/**接口名称，固定值 **/
	private String service;

	/** 服务器异步通知路径**/
	private String notify_url;

	/*
	 *  "seller_id": "greentravel@126.com",
        "partner": "2088221890608815",
        "out_trade_no": "201607131422",
        "payment_type": 1
	 */
	/**卖家支付宝账号 **/
	private String seller_id;

	/** 支付宝唯一用户号**/
	private String partner;

	/**订单号 **/
	private String out_trade_no;

	/** 支付类型,1代表商品购买**/
	private String payment_type;


	///wechat

	/**
	 * sign":"EFD8002AA1BC32B6017CB8E3FAE06326",
	 * "timestamp":"1468549739",
	 * "noncestr":"T6cBhnbvHhl5N3Qv",
	 * "partnerid":"1364340102",
	 * "prepayid":"wx201607151028378408cc4b170326430684",
	 * "package":"Sign=WXPay",
	 * "appid":"wxccc2a0fcb33835db"
	 */

	/**   **/
	private String sign;

	/**   **/
	private String timestamp;

	/**   **/
	private String noncestr;

	/**   **/
	private String partnerid;

	/**   **/
	private String prepayid;

	/**   **/
	private String package0;

	/**   **/
	private String appid;








	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getNoncestr() {
		return noncestr;
	}

	public void setNoncestr(String noncestr) {
		this.noncestr = noncestr;
	}

	public String getPartnerid() {
		return partnerid;
	}

	public void setPartnerid(String partnerid) {
		this.partnerid = partnerid;
	}

	public String getPrepayid() {
		return prepayid;
	}

	public void setPrepayid(String prepayid) {
		this.prepayid = prepayid;
	}

	public String getPackage0() {
		package0 = "Sign=WXPay";
		return package0;
	}

	public void setPackage0(String package0) {
		this.package0 = package0;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String get_input_charset() {
		return _input_charset;
	}

	public void set_input_charset(String _input_charset) {
		this._input_charset = _input_charset;
	}

	public String getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSign_type() {
		return sign_type;
	}

	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	public String getSeller_id() {
		return seller_id;
	}

	public void setSeller_id(String seller_id) {
		this.seller_id = seller_id;
	}

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getPayment_type() {
		return payment_type;
	}

	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}

}
