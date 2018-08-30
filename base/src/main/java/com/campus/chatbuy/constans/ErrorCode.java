package com.campus.chatbuy.constans;

/**
 * Created by jinku on 2017/9/19.
 */
public enum ErrorCode {

    UnLogin(10000, "当前用户未登录"), OVER_LIMIT(10001, "请求过于频繁"), Not_Allowed(10002, "没有访问权限");

    private int code;
    private String desc;

    ErrorCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
