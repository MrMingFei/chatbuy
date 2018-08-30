package com.campus.chatbuy.action;

import com.campus.chatbuy.bean.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Created by jinku on 17-7-8.
 */

@ControllerAdvice
public class ExceptionAction {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(value= HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public ResponseEntity<ResultBean> requestMethodNotSupported(HttpServletRequest req, HttpRequestMethodNotSupportedException ex) {
        return new ResponseEntity<ResultBean>(ResultBean.failure("request method is not support"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(value=HttpStatus.OK)
    public ModelAndView requestHandlingNoHandlerFound(HttpServletRequest req, NoHandlerFoundException ex) {
        ModelAndView modelAndView = new ModelAndView("error_json");
        modelAndView.addObject("exception", "请求的接口不存在");
        return modelAndView;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    public ModelAndView requestHandlingNoHandlerFound(HttpServletRequest req, MaxUploadSizeExceededException ex) {
		ModelAndView modelAndView = new ModelAndView("error_json");
		modelAndView.addObject("exception", "上传文件大小超过最大限制");
        return modelAndView;
    }

}