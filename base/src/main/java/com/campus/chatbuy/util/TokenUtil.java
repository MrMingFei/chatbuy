package com.campus.chatbuy.util;

import com.campus.chatbuy.constans.CommonConstants;
import com.campus.chatbuy.enums.PlatformEnum;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by jinku on 2017/7/19.
 */
public class TokenUtil {

	public static String genToken(HttpServletRequest request, long userId){
		String requestIp = IPUtil.getRequestIp(request);
		String userAgent = request.getHeader("UserAgent");

		String originStr = userId + requestIp + userAgent;
		String md5 = Md5Util.GetMD5Code(originStr);
		return md5;
	}

	public static String getToken(HttpServletRequest request){
		String platformStr = request.getParameter(CommonConstants.Platform);
		boolean isClient = PlatformEnum.isClient(platformStr);
		String accessToken = null;
		if (isClient) {
			accessToken = request.getParameter(CommonConstants.Access_Token);
		} else {
			accessToken = CookieUtil.getValue(request, CookieUtil.Access_Token_Name);
		}
		return accessToken;
	}
}
