package com.campus.chatbuy.filter;

import com.campus.chatbuy.util.StringUtil;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CrossDomainFilter implements Filter {

	private static Logger log = Logger.getLogger(CrossDomainFilter.class);

	@Override public void init(FilterConfig arg0) throws ServletException {

	}

	@Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String origin = httpRequest.getHeader("Origin");
		if (StringUtil.isNotEmpty(origin) && (origin.endsWith("chatbuying.com") || origin.endsWith("chatbuy-dev.com"))) {
			log.info("CrossDomainFilter set header; origin [" + origin + "]");
			((HttpServletResponse) response).addHeader("Access-Control-Allow-Origin", origin);
			((HttpServletResponse) response).addHeader("Access-Control-Allow-Credentials", "true");
			((HttpServletResponse) response).addHeader("Access-Control-Allow-Methods","GET, POST, HEAD, OPTIONS");
			((HttpServletResponse) response).addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
		}

		chain.doFilter(request, response);
	}

	@Override public void destroy() {
	}

}