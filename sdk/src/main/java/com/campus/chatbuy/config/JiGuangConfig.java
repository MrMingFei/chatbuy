package com.campus.chatbuy.config;

/**
 * Created by jinku on 2017/7/6.
 *
 * 极光配置类
 */
public class JiGuangConfig {

	public final static String App_Key = "25431b5975709577340162e3";
	public final static String Master_Secret = "855b85c35c3ce9e3fd69209e";

	/**
	 * 发送短信验证码地址
	 */
	public final static String Send_Code_Sms_Url = "https://api.sms.jpush.cn/v1/codes";

	/**
	 * 检查验证码是否一致
	 */
	public final static String Check_Code_Url = "https://api.sms.jpush.cn/v1/codes/%s/valid";

	/**
	 * 发送参数短信
	 */
	public final static String Send_Param_Sms_Url = "https://api.sms.jpush.cn/v1/messages";
}
