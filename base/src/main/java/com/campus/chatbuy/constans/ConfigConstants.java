package com.campus.chatbuy.constans;

import com.campus.chatbuy.util.ConfigUtil;

/**
 * Created by jinku on 2017/7/6.
 * <p/>
 * 基础配置常量类
 */
public class ConfigConstants {

	/**
	 * redis 统一前缀
	 */
	public final static String Redis_Key_Prefix = "chat_buy_server:" + ConfigUtil.getValue("run.env.mode") + ":";

	/**
	 * token 有效时间2个小时
	 */
	public final static int PC_Token_Valid_Time = 2 * 60 * 60;

	/**
	 * token 有效时间24个小时
	 */
	public final static int Client_Token_Valid_Time = 24 * 60 * 60;

	/**
	 * 短信验证码 有效时间5分钟
	 */
	public final static int Sms_Valid_Time = 5 * 60;

}
