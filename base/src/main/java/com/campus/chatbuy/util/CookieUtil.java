package com.campus.chatbuy.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by jinku on 2017/7/19.
 */
public class CookieUtil {

	public final static String Unlogin_Id_Name = "unlogin_userid";
	public final static String Access_Token_Name ="access_token";

	private final static int expire_time = 12 * 60 * 60;//12个小时

	/**
	 * 获取cookie指定的key值
	 *
	 * @param request
	 * @param key
	 * @return
	 */
	public static String getValue(HttpServletRequest request, String key) {
		if (request == null || request.getCookies() == null) {
			return null;
		}
		String token = null;
		for (Cookie cookie : request.getCookies()) {
			String cooName = cookie.getName();
			if (key.equals(cooName)) {
				token = cookie.getValue();
				break;
			}
		}
		return token;
	}

	/**
	 * 添加cookie
	 *
	 * @param response
	 * @param name
	 * @param value
	 */
	public static void addCookie(HttpServletResponse response, String name, String value) {
		Cookie cookie = new Cookie(name, value);
		cookie.setHttpOnly(true);// 不允许客户端操作cookie
		if (ConfigUtil.isProd()) {
			// 限制https使用cookie
			// cookie.setSecure(true);
			// 线上域名
			cookie.setDomain(".chatbuying.com");
		} else {
			cookie.setDomain(".chatbuy-dev.com");
		}

		cookie.setMaxAge(expire_time);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	/**
	 * 删除指定cookie
	 *
	 * @param request
	 * @param response
	 * @param name
	 */
	public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (name.equals(cookie.getName())) {
					cookie.setValue("");
					cookie.setPath("/");
					cookie.setMaxAge(0);
					response.addCookie(cookie);
				}
			}
		}
	}

	/**
	 * 获取access_token
	 * @param request
	 * @return
	 */
	public static String getToken(HttpServletRequest request) {
		return getValue(request, Access_Token_Name);
	}

	/**
	 * 获取access_token
	 * @param request
	 * @return
	 */
	public static String getUnLoginId(HttpServletRequest request) {
		return getValue(request, Unlogin_Id_Name);
	}
}
