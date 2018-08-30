package com.campus.chatbuy.exception.handler.impl;

import com.campus.chatbuy.bean.ResultBean;
import com.campus.chatbuy.constans.ErrorCode;
import com.campus.chatbuy.exception.BusinessException;
import com.campus.chatbuy.exception.handler.IExceptionHandler;
import com.campus.chatbuy.util.JsonConverUtil;
import org.apache.log4j.Logger;
import org.guzz.exception.IllegalParameterException;
import org.guzz.util.StringUtil;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class DefaultExceptionHandlerImpl implements IExceptionHandler {

    private static Logger logger = Logger.getLogger(DefaultExceptionHandlerImpl.class);

    @Override
    public ModelAndView exceptionHandle(HttpServletRequest request, HttpServletResponse response, Object object,
                                        Exception exception) {
        try {
            String userMsg = "服务器处理异常";
            String errorMsg = userMsg;

            int errorCode = ResultBean.RETURN_ERROR_CODE;

            if (exception instanceof BusinessException) {
                errorCode = ((BusinessException) exception).getErrorCode();
                errorMsg = exception.getMessage();
                userMsg = exception.getMessage();
            }

            if (errorCode == ErrorCode.UnLogin.getCode()) {
                logger.warn("exceptionHandle user unlogin:" + request.getRequestURI());

            } else if (errorCode == ErrorCode.OVER_LIMIT.getCode()) {
                logger.warn("exceptionHandle over limit:" + request.getRequestURI());

            } else if (exception instanceof IllegalParameterException) { // 请求参数非法，提示具体信息, 不打印栈信息
                errorMsg = exception.getMessage();
                userMsg = exception.getMessage();
                logger.error("exceptionHandle IllegalParameterException:" + request.getRequestURI() + ", msg=" + exception.getMessage());
            } else {
                logger.error("exceptionHandle Exception:" + request.getRequestURI() + ", msg=" + exception.getMessage(), exception);
            }

            if (StringUtil.notEmpty(exception.getMessage()) && exception.getMessage().length() <= 20) {
                errorMsg = exception.getMessage();
                userMsg = exception.getMessage();
            }

            ResultBean resultBean = new ResultBean();
            resultBean.getError().returnCode = errorCode;
            resultBean.getError().returnMessage = errorMsg;
            resultBean.getError().returnUserMessage = userMsg;
            String respMsg = JsonConverUtil.bean2Json(resultBean);
            printInfo(response, respMsg);
            return new ModelAndView();
        } catch (Exception e) {
            logger.error(e);
            ResultBean resultBean = new ResultBean();
            resultBean.setData(e.getMessage());
            return new ModelAndView();
        }
    }

    private void printInfo(HttpServletResponse response, String info) {
        PrintWriter writer = null;
        try {
            response.setStatus(200);
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            writer = response.getWriter();
            writer.write(info);
            writer.flush();
        } catch (Exception e) {
            logger.error("exceptionHandle printInfo exception", e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

}
