package com.campus.chatbuy.filter;

import com.campus.chatbuy.constans.CommonConstants;
import com.campus.chatbuy.constans.RedisKey;
import com.campus.chatbuy.id.generator.IdGeneratorFactory;
import com.campus.chatbuy.service.RedisService;
import com.campus.chatbuy.util.CookieUtil;
import com.campus.chatbuy.util.StringUtil;
import com.campus.chatbuy.util.ThreadLocalUtil;
import com.campus.chatbuy.util.TokenUtil;
import org.apache.log4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by jinku on 17-7-22.
 */
public class ThreadFilter implements Filter {

    private RedisService redisService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ApplicationContext ctx = WebApplicationContextUtils
                .getRequiredWebApplicationContext(filterConfig.getServletContext());
        redisService = ctx.getBean(RedisService.class);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String platformStr = httpRequest.getParameter(CommonConstants.Platform);
        String token = TokenUtil.getToken(httpRequest);
        if (StringUtil.isNotEmpty(token)) {
            String userId = redisService.get(RedisKey.getToken2UserIdKey(token, platformStr));
            if (StringUtil.isNotEmpty(userId)) {
                ThreadLocalUtil.setUserId(Long.parseLong(userId));
                MDC.put("uid", String.valueOf(userId));
            }
        }
        MDC.put("requestUrl", ((HttpServletRequest) servletRequest).getRequestURI());
        MDC.put("traceId", String.valueOf(IdGeneratorFactory.generateId()));
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            ThreadLocalUtil.clearAll();
            MDC.clear();
        }
    }

    @Override
    public void destroy() {

    }
}
