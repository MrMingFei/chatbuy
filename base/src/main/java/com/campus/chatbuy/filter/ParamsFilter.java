package com.campus.chatbuy.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by jinku on 2017/7/21.
 *
 * 对xss 非法字符进行统一转义,controller不需要做处理
 */
public class ParamsFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)req;
		request = new FilterHttpServletRequest(request);
		chain.doFilter(request, res);
	}

	@Override
	public void destroy() {

	}

}