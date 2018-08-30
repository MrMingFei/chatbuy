package com.campus.chatbuy.interceptor;

import com.campus.chatbuy.constans.CommonConstants;
import com.campus.chatbuy.constans.ErrorCode;
import com.campus.chatbuy.constans.RedisKey;
import com.campus.chatbuy.enums.PlatformEnum;
import com.campus.chatbuy.exception.BusinessException;
import com.campus.chatbuy.manager.IUserSecurityManager;
import com.campus.chatbuy.model.UserSecurity;
import com.campus.chatbuy.service.RedisService;
import com.campus.chatbuy.util.ConfigUtil;
import com.campus.chatbuy.util.CookieUtil;
import com.campus.chatbuy.util.StringUtil;
import com.campus.chatbuy.util.TokenUtil;
import org.apache.ibatis.ognl.Token;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by jinku on 2017/12/18.
 *
 * 运营活动管理后台拦截器:判断用户是否管理员
 */
public class ConsoleInterceptor implements HandlerInterceptor {

    private static final Logger log = Logger.getLogger(ConsoleInterceptor.class);

    @Autowired
    private RedisService redisService;

    @Autowired
    private IUserSecurityManager userSecurityManager;

    private final static boolean isProd = "prod".equals(ConfigUtil.getValue("run.env.mode"));

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
            Exception {

//        if (isProd) {
//            return true;
//        }

        String accessToken = TokenUtil.getToken(request);
        if (StringUtil.isEmpty(accessToken)) {
            throw new BusinessException(ErrorCode.UnLogin);
        }

        String userId = redisService.get(RedisKey.getToken2UserIdKey(accessToken, PlatformEnum.PC.remark));
        if (StringUtil.isEmpty(userId)) {
            throw new BusinessException(ErrorCode.UnLogin);
        }
        UserSecurity userSecurity = userSecurityManager.queryByUserId(Long.parseLong(userId));
        if (userSecurity.getRoleType() == UserSecurity.Role_Type_Admin &&
                userSecurity.getStatus() == UserSecurity.ACCOUNT_STATE_NORMAL) {
            // 管理员
            return true;
        }
        log.info("ConsoleInterceptor Not_Allowed; userId [" + userId + "]");
        throw new BusinessException(ErrorCode.Not_Allowed);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView
            modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
