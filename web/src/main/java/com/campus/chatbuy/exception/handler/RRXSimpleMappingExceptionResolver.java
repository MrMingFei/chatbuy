package com.campus.chatbuy.exception.handler;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Map;

public class RRXSimpleMappingExceptionResolver extends SimpleMappingExceptionResolver {
	private Map<String, IExceptionHandler> exceptionHandlerMap;
	private static final String defaultKey = "default";

	public Map<String, IExceptionHandler> getExceptionHandlerMap() {
		return exceptionHandlerMap;
	}

	public void setExceptionHandlerMap(Map<String, IExceptionHandler> exceptionHandlerMap) {
		this.exceptionHandlerMap = exceptionHandlerMap;
	}

	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception exception) {
		String uri = request.getRequestURI();
		
		Iterator iter = exceptionHandlerMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String itemKey = (String)entry.getKey();
			String tmpKey = "\\/" + itemKey + "\\/";
			if(-1 == uri.indexOf(itemKey)) {
				continue;
			}
			IExceptionHandler realHandler = (IExceptionHandler)entry.getValue();
			return realHandler.exceptionHandle(request, response, handler, exception);
		}
		ModelAndView modelAndView =  exceptionHandlerMap.get(defaultKey).exceptionHandle(request, response, handler, exception);
		return modelAndView;
	}

}
