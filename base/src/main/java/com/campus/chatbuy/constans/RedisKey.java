package com.campus.chatbuy.constans;

import com.campus.chatbuy.enums.PlatformEnum;
import com.campus.chatbuy.util.StringUtil;

/**
 * Created by jinku on 2017/7/19.
 */
public class RedisKey {

	public final static String Register_MsgId_Key = "register_msgId2mobile:";

	public final static String UnloginId_Key = "unloginId2mobile:";

	public final static String Token_UserId_Key = "token2UserId:";

	public final static String URL_LIMIT_KEY = "url_limit:";

	// 消息相关============================================
	public final static String Session2MessageList = "session2message_list:";

	public final static String SessionLockKey = "session_lock:";

	public final static String SessionUnreadNum = "session_unread_num:";
	// 消息相关============================================

	public final static String Code_Login_Key = "code_login:";

	public final static String Code_Login_Result_Key = "code_login_result:";

	public static String getToken2UserIdKey(String token, String platform) {
		if (PlatformEnum.isClient(platform)) {
			return RedisKey.Token_UserId_Key + PlatformEnum.Client.remark + token;
		}
		return RedisKey.Token_UserId_Key + PlatformEnum.PC.remark + token;
	}
}
