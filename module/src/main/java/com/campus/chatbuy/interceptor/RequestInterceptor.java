package com.campus.chatbuy.interceptor;

import com.campus.chatbuy.constans.CommonConstants;
import com.campus.chatbuy.constans.ConfigConstants;
import com.campus.chatbuy.constans.ErrorCode;
import com.campus.chatbuy.constans.RedisKey;
import com.campus.chatbuy.enums.PlatformEnum;
import com.campus.chatbuy.exception.BusinessException;
import com.campus.chatbuy.id.generator.IdGeneratorFactory;
import com.campus.chatbuy.service.RedisService;
import com.campus.chatbuy.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jinku on 2017/10/26.
 * <p/>
 * 接口次数拦截
 */
public class RequestInterceptor implements HandlerInterceptor {

    private static final Logger log = Logger.getLogger(RequestInterceptor.class);

    @Autowired
    private RedisService redisService;

    private final static List<String> urlList = new ArrayList<>();

    static {
        urlList.add("sendRegisterSms");//发送注册短信验证码
        urlList.add("register");//注册接口
        urlList.add("sendExMobileSms");//发送更换密码的短信验证码
        urlList.add("onlineNotice");//提醒卖家上线的短信通知
        urlList.add("sendSmsCode");// 短信验证码
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String platformStr = request.getParameter(CommonConstants.Platform);
        if (StringUtil.isEmpty(platformStr)) {
            platformStr = PlatformEnum.PC.remark;
        }
        String url = request.getRequestURI();
        String method = request.getMethod();
        String userId = null;
        String token = TokenUtil.getToken(request);
        if (StringUtil.isNotEmpty(token)) {
            userId = redisService.get(RedisKey.getToken2UserIdKey(token, platformStr));
        }

        if (StringUtil.isEmpty(userId)) {
            userId = getAndGenerateUnLoginId(platformStr, request, response);
        }

        if (StringUtil.isEmpty(userId)) {
            userId = IPUtil.getRequestIp(request);
            log.info("RequestInterceptor getRequestIp [" + userId + "]");
        }

        log.info("RequestInterceptor userId [" + userId + "]");

        Date date = new Date();

        // url + userId + 分/小时/天
        String minuteKey = RedisKey.URL_LIMIT_KEY + platformStr + ":" + userId + ":"
                + url + ":" + DateUtils.getMinuteStr(date);
        String hourKey = RedisKey.URL_LIMIT_KEY + platformStr + ":" + userId + ":"
                + url + ":" + DateUtils.getHourStr(date);
        String dayKey = RedisKey.URL_LIMIT_KEY + platformStr + ":" + userId + ":"
                + url + ":" + DateUtils.getDayStr(date);

        int minuteCount = StringUtil.parseInt(redisService.get(minuteKey), 0);
        int hourCount = StringUtil.parseInt(redisService.get(hourKey), 0);
        int dayCount = StringUtil.parseInt(redisService.get(dayKey), 0);

//        log.info("RequestInterceptor minuteKey=" + minuteKey + "; minuteCount=" + minuteCount);
//        log.info("RequestInterceptor hourKey=" + hourKey + "; hourCount=" + hourCount);
//        log.info("RequestInterceptor dayKey=" + dayKey + "; dayCount=" + dayCount);

        boolean isSpecial = false;
        for (String specialUrl : urlList) {
            if (url.endsWith(specialUrl)) {
                isSpecial = true;
                break;
            }
        }

        if (isSpecial) {
            if (minuteCount > 1 || hourCount > 10 || dayCount > 20) {
                log.info("RequestInterceptor isSpecial over limit minuteCount [" + minuteCount + "] hourCount ["
                        + hourCount + "] dayCount [" + dayCount + "]");
                throw new BusinessException(ErrorCode.OVER_LIMIT);
            }
        }

        if ("POST".equals(method)) {
            if (minuteCount > 20 || hourCount > 1000 || dayCount > 10000) {
                log.info("RequestInterceptor POST over limit minuteCount [" + minuteCount + "] hourCount [" + hourCount
                        + "] dayCount [" + dayCount + "]");
                throw new BusinessException(ErrorCode.OVER_LIMIT);
            }
        } else {
            if (minuteCount > 60 || hourCount > 3600 || dayCount > 200000) {
                log.info("RequestInterceptor GET over limit minuteCount [" + minuteCount + "] hourCount [" + hourCount
                        + "] dayCount [" + dayCount + "]");
                throw new BusinessException(ErrorCode.OVER_LIMIT);
            }
        }

        redisService.incr(minuteKey);
        redisService.expire(minuteKey, 60);

        redisService.incr(hourKey);
        redisService.expire(hourKey, 60 * 60);

        redisService.incr(dayKey);
        redisService.expire(dayKey, 24 * 60 * 60);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {

    }

    private String getAndGenerateUnLoginId(String platformStr, HttpServletRequest request,
                                           HttpServletResponse response) {
        String unLoginId = CookieUtil.getUnLoginId(request);
        if (StringUtil.isNotEmpty(unLoginId)) {
            log.info("RequestInterceptor get unLoginId [" + unLoginId + "]");
            return unLoginId;
        }

        if (PlatformEnum.isClient(platformStr)) {
            // TODO 获取客户端Id
            return null;
        }
        unLoginId = String.valueOf(IdGeneratorFactory.generateId());
        log.info("RequestInterceptor generate unLoginId [" + unLoginId + "]");
        CookieUtil.addCookie(response, CookieUtil.Unlogin_Id_Name, "unlogin_id_" + unLoginId);
        return unLoginId;
    }
}
