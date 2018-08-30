package com.campus.chatbuy.exception.handler;

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IExceptionHandler {
    public ModelAndView exceptionHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3);
}
