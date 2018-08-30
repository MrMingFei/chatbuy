package com.campus.chatbuy.interceptor;

import com.campus.chatbuy.constans.CommonConstants;
import com.campus.chatbuy.constans.ConfigConstants;
import com.campus.chatbuy.constans.ErrorCode;
import com.campus.chatbuy.constans.RedisKey;
import com.campus.chatbuy.enums.PlatformEnum;
import com.campus.chatbuy.exception.BusinessException;
import com.campus.chatbuy.id.generator.IdGeneratorFactory;
import com.campus.chatbuy.service.RedisService;
import com.campus.chatbuy.util.CookieUtil;
import com.campus.chatbuy.util.StringUtil;
import com.campus.chatbuy.util.TokenUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by jinku on 2017/7/19.
 *
 * 校验用户登录态
 */
public class LoginInterceptor implements HandlerInterceptor {

	private static final Logger log = Logger.getLogger(LoginInterceptor.class);

	@Autowired
	private RedisService redisService;

	@Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		String platformStr = request.getParameter(CommonConstants.Platform);
		boolean isClient = PlatformEnum.isClient(platformStr);
		String accessToken = TokenUtil.getToken(request);

		if (StringUtil.isEmpty(accessToken)) {
			throw new BusinessException(ErrorCode.UnLogin);
		}

		String token2UserIdKey = RedisKey.getToken2UserIdKey(accessToken, platformStr);
		String userId = redisService.get(token2UserIdKey);
		if (StringUtil.isEmpty(userId)) {
			throw new BusinessException(ErrorCode.UnLogin);
		}
		// 更新token 存活时间
		if (isClient) {
			redisService.set(token2UserIdKey, ConfigConstants.Client_Token_Valid_Time,  userId);
		} else {
			redisService.set(token2UserIdKey, ConfigConstants.PC_Token_Valid_Time,  userId);
		}
		return true;
	}

	@Override public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}

	@Override public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) throws Exception {

	}
}
