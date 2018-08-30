package com.campus.chatbuy.exception;

import com.campus.chatbuy.bean.ResultBean;
import com.campus.chatbuy.constans.ErrorCode;

/**
 * 业务通用异常
 */
public class BusinessException extends Exception {

    private int errorCode = ResultBean.RETURN_ERROR_CODE ;

    public BusinessException(int errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public BusinessException(int errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public BusinessException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getDesc());
        this.errorCode = errorCode.getCode();
    }

    public BusinessException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
